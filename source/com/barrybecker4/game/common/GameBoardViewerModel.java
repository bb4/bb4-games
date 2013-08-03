// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common;

import com.barrybecker4.game.common.board.IBoard;
import com.barrybecker4.game.common.persistence.GameExporter;
import com.barrybecker4.game.common.ui.SgfFileFilter;
import com.barrybecker4.game.common.ui.panel.GameChangedEvent;
import com.barrybecker4.game.common.ui.panel.GameChangedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains a GameController and potentially displays the current state of the game by
 * sending message to the IGameBoardViewer.
 * The GameController contains a Board which describes this state.
 * The game specific GameController is created upon construction to be used internally.
 * This code was separated from the GameBoardViewer to decouple ui and game logic so this class could be instantiated
 * on a server.
 *
 *  @author Barry Becker
 */
public abstract class GameBoardViewerModel
                implements GameViewModel, GameChangedListener {

    /** Every GameBoardViewer must contain a controller for controlling game state. */
    protected GameController controller_;

    /** for restoring undone moves. */
    protected final MoveList undoneMoves_ = new MoveList();

    /** list of listeners for handling those events. */
    private final List<GameChangedListener> gameListeners_ = new ArrayList<GameChangedListener>();

    /** optional UI manifestation of this viewer model */
    private IGameBoardViewer viewer;

    /**
     * Construct the viewer.
     */
    public GameBoardViewerModel() {
        controller_ = createController();
        controller_.setViewer(this);

        // add a listener so that we realize when the computer (or human) has finished making his move
        addGameChangedListener(this);
    }

    /**
     * Optionally set a view so the game will show onscreen.
     * @param viewer viewer to display the game in the UI
     */
    public void setViewer(IGameBoardViewer viewer) {
        this.viewer = viewer;
    }

    /**
     * @return the game specific controller for this viewer.
     */
    protected abstract GameController createController();

    /**
     * @return our game controller.
     */
    @Override
    public GameController getController() {
       return controller_;
    }

    /**
     * restore a game from a previously saved file (in SGF = Smart Game Format)
     * Derived classes should implement the details of the open.
     * @param file file to load state from
     */
    public void openGame(File file) {
        controller_.restoreFromFile(file.getAbsolutePath());
        sendGameChangedEvent(controller_.getLastMove());
        refresh();
    }

    /**
     * save the current game to the specified file (in SGF = Smart Game Format)
     * Derived classes should implement the details of the save
     * @param file file to save game state to.  If it does not have the .sgf extension already then add it.
     * @param ae assertion error if an error prompted this save. Null if there was no error.
     */
    public void saveGame(File file, AssertionError ae) {
        String fPath = file.getAbsolutePath();
        fPath = SgfFileFilter.addExtIfNeeded(fPath, SgfFileFilter.SGF_EXTENSION);
        GameExporter exporter = controller_.getExporter();
        exporter.saveToFile(fPath, ae);
    }

    /**
     *  Animate the last move so the player does not lose orientation.
     *  By default this just redraws the board, but for games with complex moves,
     *  we may want to do more.
     */
    protected void showLastMove() {
        refresh();
    }

    /**
     * Cause the board UI to draw itself based on the current state of the game.
     */
    public void refresh() {
        if (viewer != null) viewer.refresh();
    }

    /**
     * return the game to its original state.
     */
    @Override
    public void reset() {
        controller_.reset();  //clear what's there and start over
        if (viewer != null) {
            viewer.reset(controller_.getBoard());
        }
    }

    /**
     * start over with a new game using the current options.
     */
    public abstract void startNewGame();


    /**
     * in some cases the viewer is used to show games only.
     */
    public void setViewOnly( boolean viewOnly ) {
        if (viewer != null)  {
            viewer.setViewOnly(viewOnly);
        }
    }

    /**
     * Implements the GameChangedListener interface.
     * Called when the game has changed in some way
     * @param evt
     */
    @Override
    public void gameChanged(GameChangedEvent evt) {
        GameContext.log(1, "game changed" );
        refresh();
    }

    /**
     * This method gets called when the game has changed in some way.
     * Most likely because a move has been played.
     */
    public void sendGameChangedEvent(Move m) {
        GameChangedEvent gce = new GameChangedEvent( m, controller_, this );
        for (GameChangedListener gcl : gameListeners_) {
            gcl.gameChanged(gce);
        }
    }

    /**
     * @return true if there is a move to undo.
     */
    public final boolean canUndoMove() {
        return  (controller_.getLastMove() != null);
    }

    /**
     * @return true if there is a move to redo.
     */
     public final boolean canRedoMove() {
         return  !undoneMoves_.isEmpty();
     }

    /**
     * @return   the message to display at the completion of the game.
     */
    public abstract String getGameOverMessage();

    /**
     * This is how the client can register itself to receive these events.
     * @param gcl the listener to add
     */
    public void addGameChangedListener(GameChangedListener gcl) {
        gameListeners_.add(gcl);
    }

    /**
     * This is how the client can unregister itself to receive these events.
     * @param gcl the listener  to remove
     */
    public void removeGameChangedListener(GameChangedListener gcl) {
        gameListeners_.remove(gcl);
    }

    /**
     * @return the cached game board if we are in the middle of processing.
     */
    public IBoard getBoard() {
        return controller_.getBoard();
    }
}