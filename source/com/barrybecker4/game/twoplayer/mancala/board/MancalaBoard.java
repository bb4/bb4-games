/** Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.board;

import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;

/**
 * Representation of a Mancala Game Board.
 *
 * @author Barry Becker
 */
public class MancalaBoard extends TwoPlayerBoard {

    /** traditionally each bin starts with 3 stones. */
    private static final int INITIAL_STONES_PER_BIN = 3;

    /**
     * Constructor
     * A Mancala board always has 2 rows.
     * It typically has 8 columns (six of which are for the ordinary bins)
     * e.g. suppose there are 2 players a and b, then
     * aH   a6  a5  a4  a3  a2  a1   bH
     * aH   b8  b9  b10 b11 b12 b13  bH
     *
     * @param numCols num cols
     */
    public MancalaBoard(int numCols) {
        setSize( 2, numCols );
    }

    /**
     * default constructor
     * The first and last columns are for the home bases.
     */
    public MancalaBoard() {
        setSize( 2, 8 );
    }

    protected MancalaBoard(MancalaBoard mb) {
        super(mb);
    }

    @Override
    public MancalaBoard copy() {
        return new MancalaBoard(this);
    }

    /**
     *  Reset the board to its initial state.
     */
    @Override
    public void reset() {
        super.reset();
        for (int row = 1; row <= getNumRows(); row++)
        for (int col = 2; col < getNumCols(); col++) {
            getPosition(row, col).setPiece(new MancalaBin(row == 1, INITIAL_STONES_PER_BIN));
        }
        getPosition(1, 1).setPiece(new MancalaBin(true, 0));
        getPosition(1, getNumCols()).setPiece(new MancalaBin(false, 0));
    }

    protected BoardPosition getPositionPrototype() {
        return new BoardPosition(1, 1, null);
    }


    /**
     * This is just a conservative rough guess.
     * My reasoning is that the end of round is inevitable because
     * once a stone enters the home bin, it can never come out.
     * However, a stone does not always go in a home every turn (but they always move closer), and
     * some turns more than one may go in.
     * So my estimate is 3 times the number of columns times the starting number
     * of stones in each bin. Multiplying by 2 instead of 3 would probably be a more
     * accurate estimate, but we want an upper limit.
     */
    @Override
    public int getMaxNumMoves() {
        return getNumCols() * INITIAL_STONES_PER_BIN * 3 + 1;
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
     * This is used primarily for the Zobrist hash. You do not need to override if you do not use it.
     * For mancala there are a lot of potential states. A bin can be empty, or it can have any number of stones.
     * @return number of different states this position can have.
     */
    @Override
    public int getNumPositionStates() {
        return getNumCols() * INITIAL_STONES_PER_BIN;
    }
}
