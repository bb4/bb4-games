// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.mancala.board;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.twoplayer.mancala.move.Captures;
import com.barrybecker4.game.twoplayer.mancala.move.MancalaMove;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Barry Becker
 */
public class MancalaBoardTest {

    /** instance under test. */
    private MancalaBoard board;

    @Before
    public void setUp() {
        board = new MancalaBoard();
    }

    @Test
    public void testConstructed() {

        assertEquals("Unexpected stones", 3, board.getBin(new ByteLocation(2, 2)).getNumStones());

        // check home storages
        MancalaBin p1Storage = board.getBin(new ByteLocation(1, 1));
        MancalaBin p2Storage = board.getBin(new ByteLocation(1, 8));
        assertEquals("Unexpected p1 home", p1Storage, board.getHomeBin(true));
        assertEquals("Unexpected p2 home", p2Storage, board.getHomeBin(false));
        assertEquals("Unexpected stones", 0, p1Storage.getNumStones());
        assertEquals("Unexpected stones", 0, p2Storage.getNumStones());
        assertEquals("Unexpected stones", 3, board.getBin(new ByteLocation(1, 4)).getNumStones());
        assertEquals("Unexpected stones", 3, board.getBin(new ByteLocation(2, 5)).getNumStones());
    }

    @Test
    public void testIsSideClearWhenNot() {
        assertFalse("Unexpected isSideClear", board.isSideClear(true));
        assertFalse("Unexpected isSideClear", board.isSideClear(false));
    }

    @Test
    public void testIsSideClear() {

        Captures captures = new Captures();
        board.clearSide(true, captures);
        assertTrue("Unexpected isSideClear", board.isSideClear(true));
        assertEquals("Unexpected number of captures", 6, captures.size());

        board.clearSide(false, captures);
        assertTrue("Unexpected isSideClear", board.isSideClear(false));
        assertEquals("Unexpected number of captures", 12, captures.size());
    }

    @Test
    public void testMoveAgainPlayer1() {
        MancalaMove move = new MancalaMove(true, new ByteLocation(1, 4), (byte)3, 0);
        boolean moveAgain = board.moveAgainAfterMove(move);
        assertTrue("Should move again", moveAgain);
    }

    @Test
    public void testNotMoveAgainPlayer1() {
        MancalaMove move = new MancalaMove(true, new ByteLocation(1, 5), (byte)3, 0);
        boolean moveAgain = board.moveAgainAfterMove(move);
        assertFalse("Should not move again", moveAgain);
    }

    @Test
    public void testMoveAgainPlayer2() {
        MancalaMove move = new MancalaMove(false, new ByteLocation(2, 4), (byte)4, 0);
        boolean moveAgain = board.moveAgainAfterMove(move);
        assertTrue("Should move again", moveAgain);
    }

    @Test
    public void testNotMoveAgainPlayer2() {
        MancalaMove move = new MancalaMove(false, new ByteLocation(2, 5), (byte)4, 0);
        boolean moveAgain = board.moveAgainAfterMove(move);
        assertFalse("Should not move again", moveAgain);
    }
}