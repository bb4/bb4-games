/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.pente;

import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;

/**
 * Representation of a Pente Game Board.
 *
 * @author Barry Becker
 */
public class PenteBoard extends TwoPlayerBoard {

    /**
     * Constructor
     * @param numRows num rows
     * @param numCols num cols
     */
    public PenteBoard( int numRows, int numCols ) {
        setSize( numRows, numCols );
    }

    /**
     * default constructor
     */
    public PenteBoard() {
        setSize( 30, 30 );
    }

    protected PenteBoard(PenteBoard pb) {
        super(pb);
    }

    @Override
    public PenteBoard copy() {
        return new PenteBoard(this);
    }

    @Override
    public int getMaxNumMoves() {
        return positions_.getNumBoardSpaces();
    }

    public CandidateMoves getCandidateMoves() {
        return new CandidateMoves(this);
    }

    /**
     *  For pente, undoing a move is just changing that space back to a blank.
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
