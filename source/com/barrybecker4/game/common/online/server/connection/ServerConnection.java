/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online.server.connection;

import com.barrybecker4.game.common.online.GameCommand;
import com.barrybecker4.game.common.online.OnlineChangeListener;
import com.barrybecker4.game.common.online.OnlineGameTable;
import com.barrybecker4.game.common.online.server.IServerConnection;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerAction;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Opens a socket to the Game server from the client so we can talk to it.
 * We pass data using object serialization over the input and output streams.
 *
 * @author Barry Becker
 */
public class ServerConnection implements IServerConnection {

    /**
     * Hardcoded for now, but should be configurable.
     * localhost "127.0.0.1";
     * or maybe "192.168.1.100";
     */
    private static final String DEFAULT_HOST = "192.168.1.66";

    private ListenerSocket socket;

    /** a list of things that want to hear about broadcasts from the server about changed game state. */
    private List<OnlineChangeListener> changeListeners_;

    /**
     * Constructor
     * Note that the list of listeners is a CopyOnWriteArrayList to
     * avoid ConcurrentModificationErrors when we iterate over the list when
     * new listeners may be added concurrently.
     * @param port to open the connection on.
     */
    public ServerConnection(int port) {
        changeListeners_ = new CopyOnWriteArrayList<OnlineChangeListener>();
        socket = new ListenerSocket();
        socket.start(DEFAULT_HOST, port, changeListeners_);
    }

    /**
     * @return true if we have a live connection to the server.
     */
    @Override
    public boolean isConnected() {
        return socket.isConnected();
    }

    /**
     * Send data over the socket to the server using the output stream.
     * @param cmd object to serialize over the wire.
     */
    @Override
    public void sendCommand(GameCommand cmd)  {
        socket.sendCommand(cmd);
    }

    @Override
    public void addOnlineChangeListener(OnlineChangeListener listener) {
        changeListeners_.add(listener);
    }

    public void removeOnlineChangeListener(OnlineChangeListener listener) {
        changeListeners_.remove(listener);
    }

    /**
     * Request an initial update when we enter the room with the game tables.
     */
    @Override
    public void enterRoom() {
        sendCommand(new GameCommand(GameCommand.Name.ENTER_ROOM, ""));
    }

    /**
     * Tell the server to add another game table to the list that is available.
     * @param newTable  to add.
     */
    @Override
    public void addGameTable(OnlineGameTable newTable) {
        sendCommand(new GameCommand(GameCommand.Name.ADD_TABLE, newTable));
    }

    @Override
    public void nameChanged(String oldName, String newName) {
        String changer = oldName + GameCommand.CHANGE_TO + newName;
        sendCommand(new GameCommand(GameCommand.Name.CHANGE_NAME, changer));
    }

    /**
     * Tell the server to add player p to this table.
     * The server will look at the most recently added player to this table to determine who was added.
     */
    @Override
    public void joinTable(Player p, OnlineGameTable table) {
        table.addPlayer(p);
        sendCommand(new GameCommand(GameCommand.Name.JOIN_TABLE, table));
    }

    @Override
    public void leaveRoom(String playerName) {
        sendCommand(new GameCommand(GameCommand.Name.LEAVE_ROOM, playerName));
    }

    @Override
    public void playerActionPerformed(PlayerAction action) {
        sendCommand(new GameCommand(GameCommand.Name.DO_ACTION, action));
    }
}
