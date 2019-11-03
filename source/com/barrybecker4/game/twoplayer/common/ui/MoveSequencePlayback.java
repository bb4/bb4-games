/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui;

import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;

import java.util.List;

/**
 * Helps to play back a sequence of moves on the board.
 *
 * @author Barry Becker
 */
class MoveSequencePlayback {

    /** Piece transparency for moves that have not been played yet. */
    private static final short FUTURE_MOVE_TRANSP = 190;

    private TwoPlayerController controller_;


    /**
     * Construct the viewer.
     */
    public MoveSequencePlayback(TwoPlayerController controller) {
        controller_ = controller;
    }

    /**
     * perform a sequence of moves from somewhere in the game;
     * not necessarily the start. We do, however,
     * assume the moves are valid. It is for display purposes only.
     *
     * @param moveSequence the list of moves to make
     * @param numMovesToBackup number of moves to undo before playing this move sequence
     */
    public void makeMoveSequence( List moveSequence,
                                        int numMovesToBackup ) {
        if ( moveSequence == null || moveSequence.size() == 0 ){
            return;
        }
        Move firstMove = (Move) moveSequence.get( 0 );
        // the first time we click on a row in the tree, the controller has no moves.
        Move lastMove = controller_.getLastMove();

        if ( lastMove == null ) {
            controller_.reset(); // was AbsTwoPlayerBoardViewer.reset()
        }
        else {
            backupNMoves(moveSequence, numMovesToBackup, firstMove);
        }

        performMoveSequence(moveSequence);
    }

    /**
     * We keep the original moves and just back up to firstMove.moveNumber.
     * number of steps to backup is # of most recent real moves minus
     * the first move in the sequence.
     */
    private void backupNMoves(List moveSequence, int numMovesToBackup, Move firstMove) {

        int ct = 0;
        if (firstMove != null ) {
            while ( ct < numMovesToBackup ) {
                controller_.undoLastMove();
                // I suppose this is possible
                if (controller_.getLastMove() == null) {
                    throw new IllegalArgumentException("Reached the end after backing up "
                            + ct + " out of " + numMovesToBackup + " steps." +
                            "\n moveSequence=" + moveSequence);
                }
                ct++;
            }
        }
    }

    /**
     * Perform the specified sequence of moves.
     * @param moveSequence the sequence of consecutive moves to play on the board.
     */
    private synchronized void performMoveSequence(List moveSequence) {
        int firstFuture = 0;
        for ( int i = 0; i < moveSequence.size(); i++ ) {
            TwoPlayerMove m =  (TwoPlayerMove) moveSequence.get( i );
            if (m.isFuture()) {
                if (firstFuture == 0) {
                    firstFuture = i;
                }
                m.getPiece().setAnnotation(Integer.toString(i - firstFuture + 1));
                m.getPiece().setTransparency(FUTURE_MOVE_TRANSP);
            }
            controller_.makeMove(m);
        }
    }
}
