/* Copyright by Barry G. Becker, 2000-2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common;

import com.barrybecker4.game.common.board.IBoard;
import com.barrybecker4.game.common.online.server.IServerConnection;
import com.barrybecker4.game.common.persistence.GameExporter;
import com.barrybecker4.game.common.player.PlayerAction;
import com.barrybecker4.game.common.player.PlayerList;


/**
 * This is an abstract base class for a Game Controller.
 * It contains the key logic for n player games.
 * Instance of this class process requests from the GameViewer.
 *
 *  @author Barry Becker
 */
public abstract class GameController<M extends Move, B extends IBoard<M>>
           implements IGameController<M, B> {

    /** the board has the layout of the pieces. */
    private B board;

    /** Use this to draw directly to the ui while thinking (for debugging purposes) . */
    protected GameViewModel viewer;

    /** the list of players actively playing the game, in the order that they move. */
    private PlayerList players;

    /** collections of game specific options.  They may be modified through the ui (see GameOptionsDialog)*/
    protected GameOptions gameOptions;

    /**
     * Optional. Only present if we are online
     * this allows us to talk with the game server (if it is available). null if not
     */
    private IServerConnection serverConnection;


    /**
     * Construct the game controller
     */
    public GameController() {
        GameContext.log( 3, " mem=" + Runtime.getRuntime().freeMemory() );
    }

    /**
     * optionally set a viewer for the controller.
     * @param viewer view model
     */
    public void setViewer(GameViewModel viewer) {
       this.viewer = viewer;
    }

    /**
     * Return the game board back to its initial opening state
     */
    public void reset() {
        getBoard().reset();
    }

    /**
     * @return the last move played.
     */
    public M getLastMove() {
        return getMoveList().getLastMove();
    }

    /**
     * @return number of moves made so far.
     */
    @Override
    public int getNumMoves() {
        return getMoveList().getNumMoves();
    }

    /**
     * @return the class which shows the current state of the game board.
     * May be null if the viewer was never set.
     */
    @Override
    public GameViewModel getViewer() {
        return viewer;
    }

    /**
     * If called before the end of the game it just returns 0 - same as it does in the case of a tie.
     * @return some measure of how overwhelming the win was. May need to negate based on which player one.
     */
    public int getStrengthOfWin() {
        return 0;
    }

    /**
     * @return the board representation object.
     */
    @Override
    public B getBoard() {
        if (board == null) {
            board = createBoard();
        }
        return board;
    }

    protected abstract B createBoard();

    /**
     * Setup the initial game state.
     */
    protected abstract void initializeData();


    @Override
    public void makeMove(M move) {
        getBoard().makeMove(move);
    }

    /**
     * retract the most recently played move
     * @return  the move which was undone (null returned if no prior move)
     */
    @Override
    public M undoLastMove() {
        return getBoard().undoMove();
    }

    /**
     * Retrieve an exporter that ca same the current state of the game to a file
     * Use this version when an error occurred and you want to dump the state.
     * There is no default implementation (other than to say it is not implemented).
     * You must override if you want it to work.
     * @return the game exporter
     */
    public GameExporter getExporter() {

        GameContext.log(0, "Error: saveToFile(name, rte) not implemented yet for " + getClass().getName());
        return null;
    }

    /**
     * Restore the current state of the game from a file.
     * @param fileName file to load from
     */
    public void restoreFromFile( String fileName) {
        GameContext.log(0,  "Error: restoreFromFile(" + fileName + ") not implemented yet" );
    }

    /**
     *
     * @return a list of the players playing the game (in the order that they move).
     */
    @Override
    public PlayerList getPlayers() {
        return players;
    }

    /**
     * Maybe use list of players rather than array.
     * @param players the players currently playing the game
     */
    public void setPlayers( PlayerList players ) {
       this.players = players;
       initializeData();
    }


    public void setOptions(GameOptions options) {
        gameOptions = options;
    }

    public abstract GameOptions getOptions();


    protected AbstractGameProfiler getProfiler() {
        return GameProfiler.getInstance();
    }

    /**
     * You should probably check to see if online play is available before calling this.
     * @return a server connection if it is possible to get one.
     */
    public IServerConnection getServerConnection() {

        if (serverConnection == null) {
            serverConnection = createServerConnection();
        }
        return serverConnection;
    }

    /**
     * Most games do not support online play so returning null is the default
     * @return the server connection if one can be created, else null.
     */
    protected IServerConnection createServerConnection() {
        GameContext.log(0, "Cannot create a server connection for " + this.getClass().getName()
                           + ". Online play not supported");
        return null;
    }

    /**
     *
     * @param cmd containing an action for one of the players
     *
    public void handleServerUpdate(GameCommand cmd) {
        // @@ need to put something here for.
        GameContext.log(2, "Need controller implementation for handleServerUpdate");
    }*/

    /**
     *
     * @return true if online pay is supported, and the server is available.
     */
    public abstract boolean isOnlinePlayAvailable();

    /**
     * If a derived class supports online play it must override this.
     * @return server port number
     */
    public int getServerPort() {
        return -1;
    }

    /**
     * Someday 2 player games should use actions rather than moves so
     * that they too can be run over the game server.
     * @param action some action
     */
    public void handlePlayerAction(PlayerAction action) {
        assert false : "handlePlayerAction not implemented for " + this.getClass().getName();
    }

}
