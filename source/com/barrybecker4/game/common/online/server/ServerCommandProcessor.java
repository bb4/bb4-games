/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online.server;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.online.GameCommand;
import com.barrybecker4.game.common.online.OnlineGameTable;
import com.barrybecker4.game.common.online.OnlineGameTableList;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerAction;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.plugin.GamePlugin;
import com.barrybecker4.game.common.plugin.PluginManager;
import com.barrybecker4.game.common.ui.panel.GamePanel;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.multiplayer.common.MultiGameController;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Handles the processing of all commands send to the online game server from human players.
 * All the human players will be surrogates but the robots are here and the server needs to
 * issue their actions to their corresponding surrogates on the clients.
 *
 * @author Barry Becker
 */
class ServerCommandProcessor {

    /** Maintains the active list of game tables. */
    private GameTableManager tableManager;

    /** Maintain the master game state on the server. */
    private GameController controller;

    /**
     * Create the online game server to serve all online clients.
     */
    public ServerCommandProcessor(String gameName) {

        createController(gameName);
        tableManager = new GameTableManager();
    }

    /**
     * Package-private for tests: supply controller and table manager directly.
     */
    ServerCommandProcessor(GameController<?, ?> controller, GameTableManager tableManager) {
        this.controller = Objects.requireNonNull(controller);
        this.tableManager = Objects.requireNonNull(tableManager);
    }

    public OnlineGameTableList getTables() {
        return tableManager.getTables();
    }

    /**
     * Factory method to create the game controller via reflection.
     * The server should not have a ui component.
     */
    private void createController(String gameName) {

        GamePlugin plugin = PluginManager.getInstance().getPlugin(gameName);
        GameContext.loadResources(gameName);

        // for now, also create a corresponding viewer. The server should really not have knowledge of a UI component.
        // fix this by doing plugin.getModelInstance, then getting the controller from that.
        GamePanel panel  = plugin.getPanelInstance();
        panel.init(null);
        GameBoardViewer viewer = panel.getViewer();

        controller = viewer.getController();
    }

    public int getPort() {
        return controller.getServerPort();
    }

    /**
     * Update our internal game table list, or server controller given the cmd from the client.
     * @param cmd to process. The command that the player has issued.
     * @return the response command(s) to send to all the clients.
     */
    public List<GameCommand> processCommand(GameCommand cmd) {

        List<GameCommand> responses = new LinkedList<>();
        boolean useUpdateTable = applyCommand(cmd, responses);

        if (useUpdateTable) {
            GameCommand response = new GameCommand(GameCommand.Name.UPDATE_TABLES, getTables());
            responses.add(0, response);  // add as first command in response.
        }

        return responses;
    }

    private boolean applyCommand(GameCommand cmd, List<GameCommand> responses) {
        return switch (cmd.getName()) {
            case ENTER_ROOM -> {
                handleEnterRoom();
                yield true;
            }
            case LEAVE_ROOM -> {
                handleLeaveRoom(cmd);
                yield true;
            }
            case ADD_TABLE -> {
                tableManager.addTable((OnlineGameTable) cmd.getArgument());
                yield true;
            }
            case JOIN_TABLE -> {
                tableManager.joinTable((OnlineGameTable) cmd.getArgument());
                yield true;
            }
            case CHANGE_NAME -> {
                handleChangeName(cmd);
                yield true;
            }
            case UPDATE_TABLES -> true;
            case CHAT_MESSAGE -> {
                handleChatMessage(cmd, responses);
                yield false;
            }
            case START_GAME -> {
                handleStartGame(cmd);
                yield false;
            }
            case DO_ACTION -> {
                doPlayerAction(cmd, responses);
                yield false;
            }
        };
    }

    private void handleEnterRoom() {
        GameContext.log(2, "Entering room.");
    }

    private void handleLeaveRoom(GameCommand cmd) {
        GameContext.log(0, "Player " + cmd.getArgument() + " is now leaving the room.");
        tableManager.removePlayer((String) cmd.getArgument());
    }

    private void handleChangeName(GameCommand cmd) {
        String[] names = ((String) cmd.getArgument()).split(GameCommand.CHANGE_TO);
        if (names.length > 1) {
            tableManager.changeName(names[0], names[1]);
        }
    }

    private void handleChatMessage(GameCommand cmd, List<GameCommand> responses) {
        GameContext.log(2, "chat message=" + cmd.getArgument());
        responses.add(cmd);
    }

    private void handleStartGame(GameCommand cmd) {
        OnlineGameTable tableToStart = (OnlineGameTable) cmd.getArgument();
        startGame(tableToStart);
        tableManager.removeTable(tableToStart);
    }

    /**
     * One of the client players has acted. We need to apply this to the server controller
     * and then broadcast out the same command so the surrogate(s) on the client can be updated.
     * When a robot (on the server) moves, then that action is broadcast to the clients so
     * the surrogates on the clients can be updated.
     * @param cmd the client players command/action
     * @param responses at the least this will be the players action that we received, but it may contain robot
     *       actions for the robots on the server that play immediately after the player.
     */
    private void doPlayerAction(GameCommand cmd, List<GameCommand> responses) {

        PlayerAction action = (PlayerAction) cmd.getArgument();
        GameContext.log(0, "ServerCmdProc: doPlayerAction (" + action + "). Surrogates to handle");
        controller.handlePlayerAction(action);

        responses.add(cmd);

        if (controller instanceof MultiGameController<?, ?> multi) {
            for (PlayerAction act : multi.getRecentRobotActions()) {
                GameCommand robotCmd = new GameCommand(GameCommand.Name.DO_ACTION, act);
                GameContext.log(0, "adding response command for robot action on server :" + robotCmd);
                responses.add(robotCmd);
            }
        } else {
            GameContext.log(1, "DO_ACTION received but controller is not MultiGameController; skipping robot actions");
        }
    }

    /**
     * When all the conditions are met for starting a new game, we create a new game controller of the
     * appropriate type and start the game here on the server.
     * All human players will be surrogates and robots will be themselves.
     * @param table the table to start a game for
     */
    private void startGame(OnlineGameTable table) {

        GameContext.log(0, "Now starting game on Server! "+ table);

        // Create players from the table and start.
        PlayerList players = table.getPlayers();
        assert (players.size() == table.getNumPlayersNeeded());
        PlayerList newPlayers = new PlayerList();
        for (Player player : players) {
            if (player.isHuman()) {
                newPlayers.add(player.createSurrogate(controller.getServerConnection()));
            } else {
                newPlayers.add(player);
            }
        }
        controller.reset();
        controller.setPlayers(newPlayers);

        // if getFirstPlayer returns null, then it is not a turn based game
        Player firstPlayer = controller.getPlayers().getFirstPlayer();
        if (firstPlayer != null && !firstPlayer.isHuman()) {
            controller.computerMovesFirst();
        }
    }
}
