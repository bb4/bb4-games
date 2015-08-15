package com.barrybecker4.game.twoplayer.mancala.board;

import com.barrybecker4.game.common.board.GamePiece;

/**
 * @author Barry Becker
 */
public class MancalaBin extends GamePiece {

    /** the number of stones in the bin */
    private byte numStones;

    /** if true, then this is the players home bin. */
    private boolean isHome;

    /**
     * @param player1 true if this bin belongs to player one, else false.
     * @param numStones initial number of stones
     */
    public MancalaBin(boolean player1, byte numStones, boolean isHome) {
        super(player1);
        this.numStones = numStones;
        this.isHome = isHome;
    }

    /**
     * @return create a deep copy
     */
    public MancalaBin copy() {
        return new MancalaBin(isOwnedByPlayer1(), numStones, isHome);
    }

    public byte getNumStones() {
       return numStones;
    }

    /** get and then clear this bins stones */
    public int takeStones() {
        int num = numStones;
        numStones = 0;
        return num;
    }

    public boolean isHome() {
        return isHome;
    }

    public void increment() {
        this.numStones++;
    }

    /**
     * @param num the number of stones to increment this bin by. May be negative.
     */
    public void increment(int num) {
        this.numStones += num;
        assert this.numStones >= 0 : "The number of stones (" + numStones
                + ") went below 0 when we added " + num + ".";
    }

    public String toString() {
        return (this.isHome() ? "Home" : "")  + "Bin(" + getNumStones() + ")";
    }

}
