/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online.server;

import com.barrybecker4.common.app.CommandLineOptions;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.ui.components.Appendable;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;

/**
 * The abstract server for online games.
 * This can be run from within a ui or from a command line.
 * If running from the command line use:
 *       java OnlineGameServer -game <gameName>
 * e.g.  java OnlineGameServer -game poker
 *
 * If running from the UI, see
 *   OnlineGameServerFrame
 *
 * Manages the tables for the game room.
 * Has a GameController for each table.
 * Consider using executor framework to manage threads.
 *
 * @author Barry Becker
 */
public class OnlineGameServer  {

    public static final String GAME_OPTION = "game";  // NON-NLS

    private Appendable text;
    private ServerSocket server;
    private volatile boolean stopped = false;

    /** keep a list of the threads that we have for each client connection.  */
    private List<ClientWorker> clientConnections;

    /**
     * Create the online game server to serve all online clients.
     * @param gameType - one of the supported games (see plugin file)
     * @param textArea - may be null if not running in UI, else shows traffic messages.
     */
    public OnlineGameServer(String gameType, Appendable textArea) {

        this.text = textArea;
        clientConnections = new LinkedList<ClientWorker>();
        openListenSocket(gameType);
    }

    public void shutDown() {
        stopped = true;
        for (ClientWorker worker : clientConnections) {
            worker.stop();
        }
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * open a server socket to listen on our assigned port for
     * requests from clients. Updates will be broadcast on this socket.
     * Maintain a list of clientConnections corresponding to the players
     * that we need to broadcast to when something changes.
     */
    void openListenSocket(String gameType) {

        ServerCommandProcessor cmdProcessor = new ServerCommandProcessor(gameType);
        int port = cmdProcessor.getPort();

        server = createServerSocket(port);
        acceptClientConnections(cmdProcessor);
    }

    /**
     * Allows the server to listen for client messages.
     * @param port port to listen on.
     * @return new server socket
     */
    private ServerSocket createServerSocket(int port) {
        ServerSocket socket;
        try {
            socket = new ServerSocket(port);
        }
        catch (BindException e) {
            GameContext.log(0, "Address already in use! " +
                    "Perhaps there is already another game server sunning on this port:" + port);
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            GameContext.log(0, "Could not listen on port " + port);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return socket;
    }

    private void acceptClientConnections(ServerCommandProcessor cmdProcessor) {

        while (!stopped) {
            try {
                // accept new connections from players wanting to join.
                ClientWorker worker = new ClientWorker(server.accept(), text, cmdProcessor, clientConnections);
                clientConnections.add(worker);
                new Thread(worker).start();
            }
            catch (IOException e) {
                GameContext.log(0, "Accept failed on port " + server.getLocalPort());
                e.printStackTrace();
                break;
            }
        }
    }

    /**
     * Objects created in run method are finalized when
     * program terminates and thread exits.
     * Also shuts down all client workers so they do not keep waiting for commands to process.
     */
    @Override
    protected void finalize() {
        try {
            shutDown();
            super.finalize();
        }
        catch (IOException e) {
            GameContext.log(0, "Could not close socket");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * @param options command line options to verify
     * @return true if valid.
     */
    public static boolean verifyCmdLineOptions(CommandLineOptions options) {
        if (options.contains("help") || !options.contains(GAME_OPTION)) {
           GameContext.log(0, "Usage: -game <game name>\n");
            return false;
        }
        if (options.getValueForOption(GAME_OPTION) == null) {
            GameContext.log(0,"You must specify a valid game. See plugins.xml for list of available games.");
            return false;
        }
        return true;
    }

     /**
      * Implements OnlineGameServerInterface which is also implemented by GtpTesujiSoftGoServer.
      * not currently used, but I'm trying to have a consistent game server interface.
      *
     public boolean handleCommand(String cmdLine, StringBuilder response) {
         String[] cmdArray = StringUtils.tokenize(cmdLine);
         String cmdStr = cmdArray[0];
         boolean status = true;
         GameCommand cmd = new GameCommand(GameCommand.Name.valueOf(cmdStr), cmdStr);
         return processCmd(cmd);
    } */

    /**
     * create and show the server.
     */
    public static void main(String[] args) {

        CommandLineOptions options = new CommandLineOptions(args);

        if (verifyCmdLineOptions(options))  {
            String gameName = options.getValueForOption(GAME_OPTION);
            new OnlineGameServer(gameName, null);
        }
    }
}
