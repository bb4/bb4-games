/** Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.board;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.mancala.move.MancalaMove;

/**
 * Representation of a Mancala Game Board.
 *
 * @author Barry Becker
 */
public class MancalaBoard extends TwoPlayerBoard {

    /** traditionally each bin starts with 3 stones. */
    private static final byte INITIAL_STONES_PER_BIN = 3;

    private BinNavigator navigator;

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
        navigator = new BinNavigator(numCols);
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
        navigator = new BinNavigator(mb.getNumCols());
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
            getPosition(row, col).setPiece(new MancalaBin(row == 1, INITIAL_STONES_PER_BIN, false));
        }
        getPosition(1, 1).setPiece(new MancalaBin(true, (byte)0, true));
        getPosition(1, getNumCols()).setPiece(new MancalaBin(false, (byte)0, true));
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
     * Given a move specification, execute it on the board.
     * See rules: http://boardgames.about.com/cs/mancala/ht/play_mancala.htm
     * @param move the move to make, if possible.
     * @return false if the move is illegal.
     */
    @Override
    protected boolean makeInternalMove( Move move ) {

        MancalaMove m = (MancalaMove)move;
        Location currentLocation = m.getFromLocation();
        MancalaBin bin = getBin(currentLocation);
        if (bin.isHome() || bin.getNumStones() == 0) {
            return false;
        }
        int numStones = bin.getStones();

        // march counter-clockwise around the board dropping stones into bins (but skipping the opponent home bin)
        for (int i = 0; i<numStones; i++) {
            currentLocation = navigator.getNextLocation(currentLocation);
            MancalaBin nextBin = getBin(currentLocation);
            if (!(nextBin.isHome() && ((MancalaMove) move).isPlayer1() != nextBin.isOwnedByPlayer1())) {
                nextBin.increment();
            }
        }
        // handle some special cases. like capturing
        MancalaBin lastBin = getBin(currentLocation);
        // if the last bin seeded had no stones, capture that piece
        // and all the stones from the opposite bin and put in your store
        if (!lastBin.isHome() && lastBin.getNumStones() == 1)  {

            MancalaBin oppositeBin = getBin(navigator.getOppositeLocation(currentLocation));
            MancalaBin homeBin = getHomeBin(m.isPlayer1());
            homeBin.increment(lastBin.getStones());
            homeBin.increment(oppositeBin.getStones());
        }

        // if no stones left on players side, opponent captures all remaining stone son his side
        if (isSideClear(m.isPlayer1())) {
            clearSide(!m.isPlayer1());
        }
        if (isSideClear(!m.isPlayer1())) {
            clearSide(m.isPlayer1());
        }
        return true;
    }

    public MancalaBin getHomeBin(boolean player1) {
        return getBin(navigator.getHomeLocation(player1));
    }


    public boolean isEmpty() {
        return isSideClear(true) && isSideClear(false);
    }


    private boolean isSideClear(boolean player1) {
        int sum = 0;
        Location currentLoc = navigator.getHomeLocation(!player1);
        for (int i=0; i<getNumCols()-2; i++) {
            currentLoc = navigator.getNextLocation(currentLoc);
            sum += getBin(currentLoc).getNumStones();
        }
        return sum == 0;
    }

    /**
     * Clear off the remaining stones on the specified players side and put them in his store.
     * @param player1 player's whose side to clear.
     */
    private void clearSide(boolean player1) {
        MancalaBin homeBin = getHomeBin(player1);

        Location currentLoc = navigator.getHomeLocation(!player1);
        for (int i=0; i<getNumCols()-2; i++) {
            currentLoc = navigator.getNextLocation(currentLoc);
            homeBin.increment(getBin(currentLoc).getStones());
        }
    }

    /**
     * @return true if the players move lands the last stone in their own home bin.
     */
    public boolean moveAgainAfterMove(Move move) {
        MancalaMove m = (MancalaMove)move;
        Location lastLoc = navigator.getNthBin(m.getFromLocation(), m.getNumStonesMoved());
        MancalaBin bin = getBin(lastLoc);
        return bin.isHome() && bin.isOwnedByPlayer1() == m.isPlayer1();
    }

    public MancalaBin getBin(Location loc) {
        assert loc != null;
        MancalaBin bin =  (MancalaBin) getPosition(loc).getPiece();
        assert bin != null : " Could not find mancala bin at "+ loc;
        return bin;
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
