// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.mancala.move;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Barry Becker
 */
public class MoveUndoerTest {

    /** instance under test. */
    private MoveUndoer moveUndoer;

    private MancalaBoard board;


    @Before
    public void setUp() {
        board = new MancalaBoard();
        moveUndoer = new MoveUndoer(board);
    }

    // 1,1  1,2  1,3  1,4  1,5  1,6  1,7  1,8
    //      2,2  2,3  2,4  2,5  2,6  2,7
    @Test
    public void testUndoSimplePlayer1Move() {

        MancalaMove move = new MancalaMove(true, new ByteLocation(1, 5), (byte)3, 0);
        moveUndoer.undoMove(move);

        checkStones(new ByteLocation(1, 6), 3);
        checkStones(new ByteLocation(1, 5), 6);
        checkStones(new ByteLocation(1, 4), 2);
        checkStones(new ByteLocation(1, 3), 2);
        checkStones(new ByteLocation(1, 2), 2);
        checkStones(new ByteLocation(1, 1), 0);
    }


    /**
     *
     * @param loc bin location to check
     * @param expNumStones expected number of stones at bin location
     */
    private void checkStones(Location loc, int expNumStones) {
        assertEquals("Unexpected stones", expNumStones, board.getBin(loc).getNumStones());
    }

}