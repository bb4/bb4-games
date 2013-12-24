// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.online.server.connection;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.online.GameCommand;
import com.barrybecker4.game.common.online.OnlineChangeListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.AccessControlException;
import java.util.List;

/**
 * A socket allowing communication from the client to the GameServer.
 * We pass data using object serialization over the input and output streams.
 *
 * @author Barry Becker
 */
class ListenerSocket {

    private ObjectOutputStream oStream;
    private boolean isConnected;

    /**
     * Open a socket to the server to listen for, and send information.
     * Consider using executor framework.
     * @param host name of the host. Something like "192.168.1.100".
     * @param port the port number to open the connection on.
     * @param changeListeners clients listening for game state changes from the server.
     */
    void start(String host, int port, List<OnlineChangeListener> changeListeners) {
        try {
            isConnected = false;
            makeConnection(host, port, changeListeners);
            isConnected = true;
            GameContext.log(0, "connected.");
        }
        catch (ConnectException e) {
            isConnected = false;
            GameContext.log(0, "failed to get connection. "
                    + "Probably because the server is not running or is inaccessable. "
                    + "Playing a local game instead. " + e.getMessage());
        }
        catch (UnknownHostException e) {
            exceptionOccurred("Unknown host: " + host, e);
        }
        catch (IOException e) {
            exceptionOccurred("No I/O", e);
        }
        catch (AccessControlException e) {
            exceptionOccurred("Failed to createListenSocket. \n"
                   + "You don't have permission to open a socket to " + host
                   + " in the current context." + e.getMessage(), e);
        }
    }

    private void makeConnection(String host, int port, List<OnlineChangeListener> changeListeners)
        throws IOException  {

        GameContext.log(1, "Attempting to connect to Server=" + host + " port="+port);
        Socket socket = new Socket(host, port);
        oStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream iStream = new ObjectInputStream(socket.getInputStream());

        // create a thread to listen for updates from the server.
        UpdateWorker worker = new UpdateWorker(iStream, changeListeners);
        new Thread(worker).start();
    }

    /**
     * @return true if we have a live connection to the server.
     */
    boolean isConnected() {
        return isConnected;
    }

    /**
     * Send data over the socket to the server using the output stream.
     * @param cmd object to serialize over the wire.
     */
    void sendCommand(GameCommand cmd)  {

        try {
            assert(oStream != null && cmd != null) : "No socket: oStream="+ oStream +" cmd=" + cmd;
            oStream.writeObject(cmd);
            oStream.flush();
        }
        catch (IOException e) {
            exceptionOccurred("Send failed.", e);
        }
    }

    private void exceptionOccurred(String msg, Throwable t) {
        isConnected = false;
        GameContext.log(0, msg);
        t.printStackTrace();
        // Don't stop execution. Online play simply won't be available.
        // throw new RuntimeException(t);
    }
}
