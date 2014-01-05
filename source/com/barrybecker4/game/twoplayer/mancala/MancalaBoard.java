/** Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala;

import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;

/**
 * Representation of a Mancala Game Board.
 *
 * @author Barry Becker
 */
public class MancalaBoard extends TwoPlayerBoard {

    /**
     * Constructor
     * @param numRows num rows
     * @param numCols num cols
     */
    public MancalaBoard(int numRows, int numCols) {
        setSize( numRows, numCols );
    }

    /**
     * default constructor
     */
    public MancalaBoard() {
        setSize( 30, 30 );
    }

    protected MancalaBoard(MancalaBoard pb) {
        super(pb);
    }

    @Override
    public MancalaBoard copy() {
        return new MancalaBoard(this);
    }

    @Override
    public int getMaxNumMoves() {
        return positions_.getNumBoardSpaces();
    }

    /**
     *  For mancala, undoing a move means picking up all the stones that were
     *  placed and restoring them to their original bin.
     */
    @Override
    protected void undoInternalMove( Move move ) {
        TwoPlayerMove m = (TwoPlayerMove)move;
        getPosition(m.getToRow(), m.getToCol()).clear();
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
