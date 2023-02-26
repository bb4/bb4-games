/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.ui.ComputerMoveProgressBar;
import com.barrybecker4.game.common.ui.panel.GameChangedEvent;
import com.barrybecker4.game.common.ui.panel.GameChangedListener;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.common.ui.viewer.GamePieceRenderer;
import com.barrybecker4.game.twoplayer.common.ComputerMoveRequester;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.TwoPlayerViewModel;
import com.barrybecker4.optimization.parameter.ParameterArrayWithFitness;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.List;

/**
 * This class contains a TwoPlayerController and displays the current state of the Game.
 * The TwoPlayerController contains a Board which describes the game state.
 * The game specific TwoPlayerController is created upon construction to be used internally.
 * This class delegates to a boardRenderer to render the board and its pieces.
 * There should be no references to swing classes outside this ui subpackage.
 *    This class sends a GameChangedEvent after each move in case there are other
 * components (like the GameTreeViewer) that need to updated based on the new board state.
 * Since the computer can take a long time to think about its move before playing it, that
 * computation is handled asynchronously in a separate thread. The way it works is that the
 * TwoPlayerBoardViewer requests the next move from the controller (controller.requestComputerMove(p1)).
 * The controller spawns a new thread to actually do the search for the next best move.
 * When the next best move has been found, the controller calls computerMoved on the viewer
 * (using the TwoPlayerViewerCallbackInterface that it implements) to let it know that the move has
 * been found. The instructions in the computerMoved method are called using <br/>
 *      SwingUtilities.invokeLater()<br/>
 * so that they get executed on the event dispatch thread as soon as the event dispatch
 * thread is not busy doing something else (like refreshing the visible board).
 * A progress bar is used to show how close the computer is to playing its next move.
 * The progressbar updates by polling the controller for its search progress.
 * If you open the GameTreeDialog to see the game tree, there are buttons to pause,
 * step through, and continue processing the search as it is happening.
 *
 * This class displays the game and takes input from the user.
 * It passes the user's input to the TwoPlayerController, which in turn tells the GameViewer
 * things such as whether the user's move was legal or not, and also tells the GameViewer
 * what the computer's move is.
 *
 *  @author Barry Becker
 */
public abstract class AbstractTwoPlayerBoardViewer<M extends TwoPlayerMove, B extends TwoPlayerBoard<M>>
        extends GameBoardViewer<M, B>
        implements GameChangedListener<M>, TwoPlayerViewModel<M, B> {

    /** Responsible for showing move progress visually (with a progress bar). */
    private ComputerMoveRequester moveRequester_;

    private ComputerMoveProgressBar progressBar;

    /**
     * Show this cached board if we are in the middle of processing the next one
     * (to avoid concurrency problems)
     */
    private B cachedGameBoard_ = null;

    /** we occasionally want to show the computer's considered next moves in the ui. */
    private M[] nextMoves_;

    /** playback a sequence of moves  */
    private MoveSequencePlayback moveSequencePlayer_;


    /**
     * Construct the viewer.
     */
    protected AbstractTwoPlayerBoardViewer() {
        controller.setViewer(this);
        moveRequester_ = new ComputerMoveRequester(get2PlayerController());
        moveSequencePlayer_ = new MoveSequencePlayback(get2PlayerController());
    }

    /**
     * @return our game controller
     */
    public TwoPlayerController<M, B> get2PlayerController() {
       return (TwoPlayerController<M, B>) controller;
    }

    /**
     * Set an optional progress bar for showing progress as the computer thinks about its next move.
     */
    @Override
    public void setProgressBar(ComputerMoveProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public M[] getNextMoves() {
        return nextMoves_;
    }

    private void setNextMoves(M[] nextMoves) {
        nextMoves_ = nextMoves;
    }

    public GamePieceRenderer getPieceRenderer() {
        return getBoardRenderer().getPieceRenderer();
    }

    /**
     * run many games and use hill-climbing to find optimal weights.
     */
    private void runOptimization() {

        final GameBoardViewer viewer = this;

        get2PlayerController().runOptimization(new OptimizationDoneHandler() {
            public void done(ParameterArrayWithFitness optimizedParams) {
                JOptionPane.showMessageDialog(viewer, GameContext.getLabel("OPTIMIZED_WEIGHTS_TXT") +
                optimizedParams, GameContext.getLabel("OPTIMIZED_WEIGHTS"), JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    /**
     * return the game to its original state.
     */
    @Override
    public void reset() {
        controller.reset();  //clear what's there and start over
        B board = getBoard();
        commonReset(board);
    }

    /**
     * start over with a new game using the current options.
     */
    @Override
    public void startNewGame() {
        reset();
        TwoPlayerController<M, B> controller = get2PlayerController();
        if (get2PlayerController().getOptions().isAutoOptimize())  {
            runOptimization();
        }
        else if (controller.getPlayers().allPlayersComputer() ) {
            controller.computerMovesFirst();
            doComputerMove( false );
        }
        else if ( controller.doesComputerMoveFirst() ) {
            // computer vs human opponent
            controller.computerMovesFirst();
            refresh();
        }
        // for all other cases a human moves first
        // see the mouseClicked callback method for details
    }

    /**
     * Register the human's move.
     * @param move the move to make.
     * @return true if the game is over now.
     */
    private boolean manMoves(M move) {

        TwoPlayerController<M, B> controller = get2PlayerController();
        if ( GameContext.getUseSound() ) {
            GameContext.getMusicMaker().playNote( controller.getOptions().getPreferredTone(), 45, 0, 200, 1000 );
        }
        // need to clear the cache, otherwise we may render a stale board.
        cachedGameBoard_ = null;
        controller.manMoves(move);

        // need to refresh here to show man moves in human only game
        if (controller.getPlayers().allPlayersHuman())  {
            refresh();
        }

        // Second arg was true, but then we did final update twice.
        boolean done = controller.getSearchable().done(move, false);
        // This is needed to update the current player info
        sendGameChangedEvent(move);
        return done;
    }

    /**
     * The computer plays against itself.
     */
    @Override
    public void doComputerVsComputerGame() {

        boolean done = false;

        // if player one has not already moved, make sure they do
        if (get2PlayerController().getMoveList().isEmpty())  {
            get2PlayerController().computerMovesFirst();
            boolean isEmpty = get2PlayerController().getMoveList().isEmpty();
            assert (!isEmpty) : "Error: null before search";
        }

        // don't run this on the event dispatch thread.
        while ( !done ) {
            done = doComputerMove( false );
            // if done the final move was placed
            if ( !done ) {
                done = doComputerMove( true );
            }
        }
    }

    /**
     * Make the computer move and show it on the screen.
     * Since this can take a very long time we will show the user a progress bar
     * to give feedback.
     *   The computer needs to search through vast numbers of moves to find the best one.
     * This will happen asynchronously in a separate thread so that the event dispatch
     * thread can return immediately and not lock up the user interface (UI).
     *   Some moves can be complex (like multiple jumps in checkers). For these
     * We should animate these types of moves so the human player does not get disoriented.
     *
     * @param isPlayer1 if the computer player now moving is player 1.
     * @return done always returns false unless auto optimizing
     */
    private boolean doComputerMove( boolean isPlayer1 ) {
        setCursor(waitCursor_);

        try {
            if (progressBar != null) {
                progressBar.doComputerMove(moveRequester_);
            }
            return moveRequester_.requestComputerMove(isPlayer1);
        }
        catch  (AssertionError ae) {
            // if any errors occur during search, I want to save the state of the game to
            // a file so the error can be easily reproduced.
            assertFailed( ae );
        }

        return false;
    }

    /**
     * Currently this does not actually step forward just one search step, but instead
     * stops after PROGRESS_STEP_DELAY more milliseconds.
     */
    @Override
    public final void step() {
        progressBar.step();
    }

    /**
     * resume computation
     */
    @Override
    public final void continueProcessing()  {
        moveRequester_.continueProcessing();
    }

    /**
     * Called when the controller has found the computer's move (usually after a long asynchronous search).
     * The runnable body will run on the event-dispatch thread when the search has completed.
     * @param m the move that was selected by the computer.
     */
    @Override
    public void computerMoved(M m) {

        final Runnable postMoveCleanup = new PostMoveCleanup(m);
        SwingUtilities.invokeLater(postMoveCleanup);
    }

    /**
     * Implements the GameChangedListener interface.
     * Called when the game has changed in some way
     * @param evt change event
     */
    @Override
    public void gameChanged(GameChangedEvent<M> evt) {
        TwoPlayerController<M, B> c = get2PlayerController();
        assert c == evt.getController();

        // note: we don't show the winner dialog if we are having the computer play against itself.
        if (c.getSearchable().done(evt.getMove(), true)
                && c.getOptions().getShowGameOverDialog()) {
            showWinnerDialog();
            //c.reset();
        }
        else {
            if (get2PlayerController().getPlayers().allPlayersComputer() && evt.getMove() != null) {
                continuePlay(evt.getMove());
            }
        }
    }

    /**
     * Let the computer go next if one of the players is a computer.
     * @param move the current move (it must not be null)
     * @return false if the game is at an end, otherwise return true
     */
     public final boolean continuePlay( M move ) {
         boolean done;
         TwoPlayerController controller = get2PlayerController();
         if (controller.getPlayers().allPlayersComputer()) {
             refresh();
             done = doComputerMove(!move.isPlayer1());
         }
         else {
             if ( controller.isPlayer1sTurn() ) {
                 assert !controller.isProcessing();
                 done = manMoves( move );
                 if (!controller.isPlayer1sTurn() && !controller.getPlayers().getPlayer2().isHuman() && !done )  {
                     done = doComputerMove( false );
                 }
             }
             else { // player 2s turn
                 done = manMoves( move );
                 if (controller.isPlayer1sTurn() && !controller.getPlayers().getPlayer1().isHuman() && !done )  {
                     done = doComputerMove( true );
                 }
             }
             //refresh();    // causes rendering in intermediate state (while computer thinking)
         }
         return !done;
     }


    /**
     * some moves require that the human players be given some kind of notification.
     * @param m the last move made
     */
    protected void warnOnSpecialMoves( TwoPlayerMove m )  {
        if (m == null) {
            return;
        }
        if (m.isPassingMove() && !get2PlayerController().getPlayers().allPlayersComputer())
            JOptionPane.showMessageDialog( this,
                    GameContext.getLabel("COMPUTER_PASSES"),
                    GameContext.getLabel("INFORMATION"),
                    JOptionPane.INFORMATION_MESSAGE );
    }

    /**
     * return the game to its state before the last human move.
     */
    public void undoLastManMove()  {
        TwoPlayerController<M, B> c = get2PlayerController();
        PlayerList players = c.getPlayers();
        if ( players.allPlayersComputer() )
            return;
        M move = c.undoLastMove();
        if ( move != null ) {
            undoneMoves_.add( move );
            if ( !players.allPlayersHuman() ) {
                undoneMoves_.add( c.undoLastMove() );
            }
            refresh();
        }
        else
            JOptionPane.showMessageDialog( this,
                    GameContext.getLabel("NO_MOVES_TO_UNDO"),
                    GameContext.getLabel("WARNING"),
                    JOptionPane.WARNING_MESSAGE );
    }

    /**
     * redo the last human player's move.
     */
    public void redoLastManMove()  {
        TwoPlayerController<M, B> c = get2PlayerController();
        PlayerList players = c.getPlayers();
        if ( undoneMoves_.isEmpty() ) {
            JOptionPane.showMessageDialog( null,
                    GameContext.getLabel("NO_MOVES_TO_REDO"),
                    GameContext.getLabel("WARNING"),
                    JOptionPane.WARNING_MESSAGE );
            return;
        }
        if ( players.allPlayersComputer() )
            return;
        c.makeMove(undoneMoves_.removeLast());
        if ( !players.allPlayersHuman() ) {
            c.makeMove(undoneMoves_.removeLast());
        }
        refresh();
    }


    public final synchronized void showMoveSequence( List<M> moveSequence ) {
        showMoveSequence( moveSequence, getController().getNumMoves() );
    }

    private final synchronized void showMoveSequence( List<M> moveSequence, int numMovesToBackup) {
        showMoveSequence(moveSequence, numMovesToBackup, null);
    }

    public final synchronized void showMoveSequence( List<M> moveSequence, int numMovesToBackup,
                                                     M[] nextMoves) {
        moveSequencePlayer_.makeMoveSequence( moveSequence, numMovesToBackup);
        setNextMoves(nextMoves);
        refresh();
    }

    /**
     * @return   the message to display at the completion of the game.
     */
    @Override
    protected String getGameOverMessage() {
        return new GameOverMessage(get2PlayerController()).getText();
    }

    /**
     * @return the cached game board if we are in the middle of processing.
     */
    @Override
    public B getBoard() {
       TwoPlayerController<M, B> c = get2PlayerController();

       if (cachedGameBoard_ == null) {
           cachedGameBoard_ = (B)c.getBoard().copy();
       }
       if (c.isProcessing() && !c.getOptions().isAutoOptimize()) {
           return cachedGameBoard_;
       }
       else {
           return c.getBoard();
       }
    }

    private class PostMoveCleanup implements Runnable {
        private final M lastMove;

        public PostMoveCleanup(M lastMove) {
            this.lastMove = lastMove;
        }

        @Override
        public void run() {

            setCursor(origCursor_);
            if ( GameContext.getUseSound() )
                GameContext.getMusicMaker().playNote(
                        get2PlayerController().getOptions().getPreferredTone(), 45, 0, 200, 1000 );
            showLastMove();
            cachedGameBoard_ = null;
            if (!get2PlayerController().getOptions().isAutoOptimize()) {
                // show a pop-up for certain exceptional cases.
                // For example, in chess we warn on a checking move.
                warnOnSpecialMoves(lastMove);
                sendGameChangedEvent(lastMove);
            }
            if (progressBar != null) {
                progressBar.cleanup();
            }
       }
    }
}
