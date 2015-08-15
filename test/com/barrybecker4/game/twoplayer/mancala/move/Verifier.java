package com.barrybecker4.game.twoplayer.mancala.move;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard;

import static org.junit.Assert.assertEquals;

/**
 * Used by tests to verify board state.
 * @author Barry Becker
 */
public class Verifier {

    private MancalaBoard board;

    Verifier(MancalaBoard board) {
        this.board = board;
    }

    void checkOverallBoard(int stonesAt11,
        int stonesAt12, int stonesAt13, int stonesAt14, int stonesAt15, int stonesAt16, int stonesAt17, int stonesAt18,
        int stonesAt22, int stonesAt23, int stonesAt24, int stonesAt25, int stonesAt26, int stonesAt27) {
        checkStones(new ByteLocation(1, 1), stonesAt11);
        checkStones(new ByteLocation(1, 2), stonesAt12);
        checkStones(new ByteLocation(1, 3), stonesAt13);
        checkStones(new ByteLocation(1, 4), stonesAt14);
        checkStones(new ByteLocation(1, 5), stonesAt15);
        checkStones(new ByteLocation(1, 6), stonesAt16);
        checkStones(new ByteLocation(1, 7), stonesAt17);
        checkStones(new ByteLocation(1, 8), stonesAt18);
        checkStones(new ByteLocation(2, 2), stonesAt22);
        checkStones(new ByteLocation(2, 3), stonesAt23);
        checkStones(new ByteLocation(2, 4), stonesAt24);
        checkStones(new ByteLocation(2, 5), stonesAt25);
        checkStones(new ByteLocation(2, 6), stonesAt26);
        checkStones(new ByteLocation(2, 7), stonesAt27);
    }

    /**
     * @param loc bin location to check
     * @param expNumStones expected number of stones at bin location
     */
    private void checkStones(Location loc, int expNumStones) {
        assertEquals("Unexpected stones at location " + loc, expNumStones, board.getBin(loc).getNumStones());
    }
}
