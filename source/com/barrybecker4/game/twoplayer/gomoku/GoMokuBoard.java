/* Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku;

import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;

/**
 * Representation of a GoMoku Game Board.
 *
 * @author Barry Becker
 */
public class GoMokuBoard extends TwoPlayerBoard<TwoPlayerMove> {

    /**
     * Constructor
     * @param numRows num rows
     * @param numCols num cols
     */
    public GoMokuBoard(int numRows, int numCols) {
        setSize( numRows, numCols );
    }

    /**
     * default constructor
     */
    public GoMokuBoard() {
        setSize( 30, 30 );
    }

    protected GoMokuBoard(GoMokuBoard pb) {
        super(pb);
    }

    @Override
    public GoMokuBoard copy() {
        return new GoMokuBoard(this);
    }

    @Override
    public int getMaxNumMoves() {
        return positions_.getNumBoardSpaces();
    }

    public CandidateMoves getCandidateMoves() {
        return new CandidateMoves(this);
    }

    /**
     *  For gomoku, undoing a move is just changing that space back to a blank.
     *  @param move the move to undo
     */
    @Override
    protected void undoInternalMove( TwoPlayerMove move ) {
        getPosition(move.getToRow(), move.getToCol()).clear();
    }

    /**
     * Num different states.
     * This is used primarily for the Zobrist hash. You do not need to override if yo udo not use it.
     * States: player1, player2, empty.
     * @return number of different states this position can have.
     */
    @Override
    public int getNumPositionStates() {
        return 3;
    }
}
