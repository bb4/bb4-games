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
        boolean useUpdateTable = true;

        switch (cmd.getName()) {
            case ENTER_ROOM :
                GameContext.log(2, "Entering room.");
                break;
            case LEAVE_ROOM :
                GameContext.log(0, "Player "+cmd.getArgument()+" is now leaving the room.");
                tableManager.removePlayer((String) cmd.getArgument());
                break;
            case ADD_TABLE :
                tableManager.addTable((OnlineGameTable) cmd.getArgument());
                break;
            case JOIN_TABLE :
                tableManager.joinTable((OnlineGameTable) cmd.getArgument());
                break;
            case CHANGE_NAME :
                String[] names = ((String)cmd.getArgument()).split(GameCommand.CHANGE_TO);
                if (names.length > 1) {
                    tableManager.changeName(names[0], names[1]);
                }
                break;
            case UPDATE_TABLES :
                break;
            case CHAT_MESSAGE :
                GameContext.log(2, "chat message=" + cmd.getArgument());
                useUpdateTable = false;
                responses.add(cmd);
                break;
            case START_GAME:
                OnlineGameTable tableToStart = (OnlineGameTable) cmd.getArgument();
                startGame(tableToStart);
                useUpdateTable = false;
                tableManager.removeTable(tableToStart);
                break;
            case DO_ACTION :
                doPlayerAction(cmd, responses);
                useUpdateTable = false;
                break;
            default:
                assert false : "Unhandled command: "+ cmd;
        }

        if (useUpdateTable) {
            GameCommand response = new GameCommand(GameCommand.Name.UPDATE_TABLES, getTables());
            responses.add(0, response);  // add as first command in response.
        }

        return responses;
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

        // get all robot player actions until the next human player
        List<PlayerAction> robotActions = ((MultiGameController) controller).getRecentRobotActions();

        for (PlayerAction act : robotActions) {
            GameCommand robotCmd = new GameCommand(GameCommand.Name.DO_ACTION, act);
            GameContext.log(0, "adding response command for robot action on server :" + robotCmd);
            responses.add(robotCmd);
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
