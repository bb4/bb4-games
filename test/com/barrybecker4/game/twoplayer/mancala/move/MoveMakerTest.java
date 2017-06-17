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
public class MoveMakerTest {

    /** instance under test. */
    private MoveMaker moveMaker;

    private MancalaBoard board;


    @Before
    public void setUp() {
        board = new MancalaBoard();
        moveMaker = new MoveMaker(board);
    }

    // 1,1  1,2  1,3  1,4  1,5  1,6  1,7  1,8
    //      2,2  2,3  2,4  2,5  2,6  2,7
    @Test
    public void testMakeSimplePlayer1Move() {

        MancalaMove move = new MancalaMove(true, new ByteLocation(1, 5), (byte)3, 0);
        moveMaker.makeMove(move);

        checkStones(new ByteLocation(1, 6), 3);
        checkStones(new ByteLocation(1, 5), 0);
        checkStones(new ByteLocation(1, 4), 4);
        checkStones(new ByteLocation(1, 2), 4);
    }

    @Test
    public void testMakeSimplePlayer1MoveWithLastInHome() {

        MancalaMove move = new MancalaMove(true, new ByteLocation(1, 4), (byte)3, 0);
        moveMaker.makeMove(move);

        checkStones(new ByteLocation(1, 6), 3);
        checkStones(new ByteLocation(1, 5), 3);
        checkStones(new ByteLocation(1, 4), 0);
        checkStones(new ByteLocation(1, 2), 4);
        checkStones(new ByteLocation(1, 1), 1);  // storage
    }

    @Test
    public void testMakePlayer1MoveWithManyStones() {

        MancalaMove move = new MancalaMove(true, new ByteLocation(1, 4), (byte)20, 0);
        moveMaker.makeMove(move);

        checkStones(new ByteLocation(1, 6), 4);
        checkStones(new ByteLocation(1, 4), 1);
        checkStones(new ByteLocation(2, 4), 5);
        checkStones(new ByteLocation(1, 1), 2);  // storage
        checkStones(new ByteLocation(1, 8), 0);  // storage
    }

    @Test
    public void testMakeSimplePlayer2Move() {

        MancalaMove move = new MancalaMove(false, new ByteLocation(2, 6), (byte)3, 0);
        moveMaker.makeMove(move);

        checkStones(new ByteLocation(2, 3), 3);
        checkStones(new ByteLocation(2, 6), 0);
        checkStones(new ByteLocation(2, 7), 4);
        checkStones(new ByteLocation(1, 8), 1);  // storage
    }


    /**
     * The initial move results in the last see going into storage, so there is a secondary move.
     * 1,1  1,2  1,3  1,4  1,5  1,6  1,7  1,8
     *      2,2  2,3  2,4  2,5  2,6  2,7
     */
    @Test
    public void testComboPlayer1Move() {

        MancalaMove move = new MancalaMove(true, new ByteLocation(1, 4), (byte)3, 0);
        MancalaMove secondaryMove = new MancalaMove(true, new ByteLocation(1, 6), (byte)3, 0);
        move.setFollowUpMove(secondaryMove);
        moveMaker.makeMove(move);

        checkStones(new ByteLocation(1, 6), 0);
        checkStones(new ByteLocation(1, 5), 4);
        checkStones(new ByteLocation(1, 4), 1);
        checkStones(new ByteLocation(1, 3), 5);
        checkStones(new ByteLocation(1, 2), 4);
        checkStones(new ByteLocation(1, 1), 1);  // storage
    }

    @Test
    public void testPlayer1CapturingMove() {

        board.getBin(new ByteLocation(1, 2)).takeStones();
        MancalaMove move = new MancalaMove(true, new ByteLocation(1, 4), (byte)2, 0);

        moveMaker.makeMove(move);

        assertEquals("Unexpected captures.",
                "{(row=1, column=2)=1, (row=2, column=2)=3}",
                move.getCaptures().toString());
        checkStones(new ByteLocation(1, 4), 0);
        checkStones(new ByteLocation(1, 3), 4);
        checkStones(new ByteLocation(1, 2), 0);
        checkStones(new ByteLocation(1, 1), 4);
        checkStones(new ByteLocation(2, 2), 0);
        checkStones(new ByteLocation(2, 3), 3);
    }

    /** opposite side should get cleared on final move when on player side */
    @Test
    public void testFinalPlayer1MoveOnPlayersSide() {

        for (int i=2; i<8; i++) {
            board.getBin(new ByteLocation(1, i)).takeStones();
        }

        MancalaMove move = new MancalaMove(true, new ByteLocation(1, 4), (byte)1, 0);

        moveMaker.makeMove(move);

        assertEquals("Unexpected captures.",
                "{(row=1, column=3)=1, (row=2, column=2)=3, (row=2, column=3)=3, (row=2, column=4)=3, " +
                        "(row=2, column=5)=3, (row=2, column=6)=3, (row=2, column=7)=3}",
                move.getCaptures().toString());
        checkStones(new ByteLocation(1, 4), 0);
        checkStones(new ByteLocation(2, 4), 0);
        assertEquals("Unexpected stones in p1 home.", 4, board.getHomeBin(true).getNumStones());
        assertEquals("Unexpected stones in p2 home.", 15, board.getHomeBin(false).getNumStones());
    }


    /** opposite side should get cleared on final move when on player two's side */
    @Test
    public void testFinalPlayer2MoveOnPlayersSide() {

        for (int i=2; i<8; i++) {
            board.getBin(new ByteLocation(2, i)).takeStones();
        }

        MancalaMove move = new MancalaMove(false, new ByteLocation(2, 4), (byte)1, 0);

        moveMaker.makeMove(move);

        assertEquals("Unexpected captures.",
                "{(row=1, column=2)=3, (row=1, column=3)=3, (row=1, column=4)=3, (row=1, column=5)=3, " +
                        "(row=1, column=6)=3, (row=1, column=7)=3, (row=2, column=5)=1}",
                move.getCaptures().toString());

        checkStones(new ByteLocation(1, 4), 0);
        checkStones(new ByteLocation(2, 4), 0);
        checkStones(new ByteLocation(2, 3), 0);
        checkStones(new ByteLocation(1, 6), 0);
        checkStones(new ByteLocation(2, 6), 0);
        assertEquals("Unexpected stones in p1 home.", 15, board.getHomeBin(true).getNumStones());
        assertEquals("Unexpected stones in p2 home.", 4, board.getHomeBin(false).getNumStones());
    }


    /** opposite side should get cleared on final move */
    @Test
    public void testFinalPlayer1MoveInHomeBin() {

        for (int i=2; i<8; i++) {
            board.getBin(new ByteLocation(1, i)).takeStones();
        }

        MancalaMove move = new MancalaMove(true, new ByteLocation(1, 2), (byte)1, 0);

        moveMaker.makeMove(move);

        assertEquals("Unexpected captures.",
                "{(row=2, column=2)=3, (row=2, column=3)=3, (row=2, column=4)=3, (row=2, column=5)=3, " +
                        "(row=2, column=6)=3, (row=2, column=7)=3}",
                move.getCaptures().toString());
        assertEquals("Unexpected stones", 0, board.getBin(new ByteLocation(1, 4)).getNumStones());
        assertEquals("Unexpected stones", 0, board.getBin(new ByteLocation(2, 4)).getNumStones());
        assertEquals("Unexpected stones", 1, board.getHomeBin(true).getNumStones());
        assertEquals("Unexpected stones", 18, board.getHomeBin(false).getNumStones());
    }

    /** opposite side should get cleared on final move */
    @Test
    public void testFinalPlayer2MoveInHomeBin() {

        for (int i=2; i<8; i++) {
            board.getBin(new ByteLocation(2, i)).takeStones();
        }

        MancalaMove move = new MancalaMove(false, new ByteLocation(2, 7), (byte)1, 0);

        moveMaker.makeMove(move);

        assertEquals("Unexpected captures.",
                "{(row=1, column=2)=3, (row=1, column=3)=3, (row=1, column=4)=3, (row=1, column=5)=3, " +
                        "(row=1, column=6)=3, (row=1, column=7)=3}",
                move.getCaptures().toString());
        assertEquals("Unexpected stones", 0, board.getBin(new ByteLocation(1, 4)).getNumStones());
        assertEquals("Unexpected stones", 0, board.getBin(new ByteLocation(2, 4)).getNumStones());
        assertEquals("Unexpected stones", 18, board.getHomeBin(true).getNumStones());
        assertEquals("Unexpected stones", 1, board.getHomeBin(false).getNumStones());
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