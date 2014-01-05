package com.barrybecker4.game.twoplayer.mancala;

import com.barrybecker4.game.common.board.GamePiece;

/**
 * @author Barry Becker
 */
public class MancalaBin extends GamePiece {

    /** the number of stones in the bin */
    private int numStones;

    /**
     * @param player1 true if this bin belongs to player one, else false.
     * @param numStones initial number of stones
     */
    public MancalaBin(boolean player1, int numStones) {
        super(player1);
    }

    public int getNumStones() {
       return numStones;
    }

    public void setNumStones(int numStones) {
        this.numStones = numStones;
    }

    public void increment() {
        this.numStones++;
    }

}
