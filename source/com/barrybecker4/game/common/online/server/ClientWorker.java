// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.online.server;

import com.barrybecker4.common.concurrency.ThreadUtil;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.online.GameCommand;
import com.barrybecker4.ui.components.Appendable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * A client worker is created for each client player connection to this server.
 * It runns on the server and sends commands to its particular client.
 */
class ClientWorker implements Runnable {

    private Socket clientConnection_;
    private Appendable text;
    private volatile ObjectOutputStream oStream_;
    private volatile ServerCommandProcessor cmdProcessor;
    private List<ClientWorker> clientConnections;
    private volatile boolean stopped = false;

    /** Constructor */
    ClientWorker(Socket clientConnection, Appendable text,
                 ServerCommandProcessor cmdProcessor, List<ClientWorker> clientConnections) {
        clientConnection_ = clientConnection;
        this.text = text;
        this.cmdProcessor = cmdProcessor;
        this.clientConnections = clientConnections;
    }

    public void stop() {
        stopped = true;
    }

    @Override
    public void run() {

        ObjectInputStream iStream;
        try {
            iStream = new ObjectInputStream(clientConnection_.getInputStream());
            oStream_ = new ObjectOutputStream(clientConnection_.getOutputStream());
        }
        catch (IOException e) {
            GameContext.log(0, "in or out stream creation failed.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try {
            // initial update to the game tables for someone entering the room.
            GameCommand cmd = new GameCommand(GameCommand.Name.UPDATE_TABLES, cmdProcessor.getTables());
            update(cmd);

            while (!stopped) {
                // receive the serialized commands that are sent and process them.
                cmd = (GameCommand) iStream.readObject();
                processCommand(cmd);
                ThreadUtil.sleep(100);
            }
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            GameContext.log(0, "Read failed.");
            e.printStackTrace();
        }

        GameContext.log(1, "Connection closed removing thread");
        clientConnections.remove(this);
    }

    /**
     * We got a change to the tables, update internal structure and broadcast new list.
     * @param cmd the command to process.
     */
    private void processCommand(GameCommand cmd) throws IOException {

        List<GameCommand> responses = cmdProcessor.processCommand(cmd);

        for (GameCommand response: responses) {
            for (ClientWorker worker : clientConnections) {
                GameContext.log(0, "sending resonse to client: " + response);
                worker.update(response);
                ThreadUtil.sleep(100);
            }
        }

        if (text == null)  {
           GameContext.log(0, cmd.toString());
        }  else {
           text.append(cmd.toString() + '\n');
        }
    }

    /**
     * Broadcast the current list of tables to all the online clients.
     * Must reset the stream first, otherwise tables_ will always be the same as first sent.
     */
    public synchronized void update(GameCommand response) throws IOException {

        oStream_.reset();
        oStream_.writeObject(response);
        oStream_.flush();

    }

    @Override
    protected void finalize() {
        try {
           super.finalize();
           oStream_.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

