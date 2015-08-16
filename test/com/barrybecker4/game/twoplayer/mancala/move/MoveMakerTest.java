// Copyright by Barry G. Becker, 2014-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
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

    private Verifier verifier;


    @Before
    public void setUp() {
        board = new MancalaBoard();
        moveMaker = new MoveMaker(board);
        verifier = new Verifier(board);
    }

    // 1,1  1,2  1,3  1,4  1,5  1,6  1,7  1,8
    //      2,2  2,3  2,4  2,5  2,6  2,7
    @Test
    public void testMakeSimplePlayer1Move() {

        MancalaMove move = new MancalaMove(true, new ByteLocation(1, 5), (byte)3, 0);
        moveMaker.makeMove(move);

        verifier.checkOverallBoard(0, 4, 4, 4, 0, 3, 3, 0,
                3, 3, 3, 3, 3, 3);
    }

    @Test
    public void testMakeSimplePlayer1MoveWithLastInHome() {

        MancalaMove move = new MancalaMove(true, new ByteLocation(1, 4), (byte)3, 0);
        moveMaker.makeMove(move);

        verifier.checkOverallBoard(1, 4, 4, 0, 3, 3, 3, 0,
                3, 3, 3, 3, 3, 3);
    }

    @Test
    public void testMakePlayer1MoveWithManyStones() {

        MancalaMove move = new MancalaMove(true, new ByteLocation(1, 4), (byte)20, 0);
        moveMaker.makeMove(move);

        verifier.checkOverallBoard(2, 5, 5, 1, 4, 4, 4, 0,
                5, 5, 5, 5, 4, 4);
    }

    @Test
    public void testMakeSimplePlayer2Move() {

        MancalaMove move = new MancalaMove(false, new ByteLocation(2, 6), (byte)3, 0);
        moveMaker.makeMove(move);

        verifier.checkOverallBoard(0, 3, 3, 3, 3, 3, 4, 1,
                3, 3, 3, 3, 0, 4);
    }

    /**
     * The initial move results in the last seed going into storage, so there is a secondary move.
     * 1,1  1,2  1,3  1,4  1,5  1,6  1,7  1,8
     *      2,2  2,3  2,4  2,5  2,6  2,7
     */
    @Test
    public void testComboPlayer1Move() {

        MancalaMove move = new MancalaMove(true, new ByteLocation(1, 4), (byte)3, 0);
        MancalaMove secondaryMove = new MancalaMove(true, new ByteLocation(1, 6), (byte)3, 0);
        move.setFollowUpMove(secondaryMove);
        moveMaker.makeMove(move);

        verifier.checkOverallBoard(1, 4, 5, 1, 4, 0, 3, 0,
                3, 3, 3, 3, 3, 3);
    }

    @Test
    public void testPlayer1CapturingMove() {
        board.getBin(new ByteLocation(1, 2)).takeStones();
        MancalaMove move = new MancalaMove(true, new ByteLocation(1, 4), (byte)2, 0);
        moveMaker.makeMove(move);

        // The captures describe where the captures stones came from
        assertEquals("Unexpected captures.",
                "{(row=1, column=2)=1, (row=2, column=2)=3}",
                move.getCaptures().toString());

        // note that stones from 1, 2 and 2,2, are moved to p1's home
        verifier.checkOverallBoard(4, 0, 4, 0, 3, 3, 3, 0,
                0, 3, 3, 3, 3, 3);
    }

    @Test
    public void testPlayer2CapturingMove() {
        board.getBin(new ByteLocation(2, 5)).takeStones();
        MancalaMove move = new MancalaMove(false, new ByteLocation(2, 3), (byte)2, 0);
        moveMaker.makeMove(move);

        assertEquals("Unexpected captures.",
                "{(row=2, column=5)=1, (row=1, column=5)=3}",
                move.getCaptures().toString());

        // note that stones from 1,2 and 2,2, are moved to p1's home
        verifier.checkOverallBoard(0, 3, 3, 3, 0, 3, 3, 4,
                3, 0, 4, 0, 3, 3);
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
                "{(row=2, column=5)=3, (row=2, column=4)=3, (row=1, column=3)=1, " +
                 "(row=2, column=7)=3, (row=2, column=6)=3, (row=2, column=3)=3, (row=2, column=2)=3}",
                move.getCaptures().toString());

        verifier.checkOverallBoard(4, 0, 0, 0, 0, 0, 0, 15,
                0, 0, 0, 0, 0, 0);
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
                "{(row=1, column=2)=3, (row=2, column=5)=1, (row=1, column=3)=3, " +
                "(row=1, column=6)=3, (row=1, column=7)=3, (row=1, column=4)=3, (row=1, column=5)=3}",
                move.getCaptures().toString());

        verifier.checkOverallBoard(15, 0, 0, 0, 0, 0, 0, 4,
                0, 0, 0, 0, 0, 0);
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
                "{(row=2, column=5)=3, (row=2, column=4)=3, (row=2, column=7)=3, " +
                        "(row=2, column=6)=3, (row=2, column=3)=3, (row=2, column=2)=3}",
                move.getCaptures().toString());

        verifier.checkOverallBoard(1, 0, 0, 0, 0, 0, 0, 18,
                0, 0, 0, 0, 0, 0);
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
                "{(row=1, column=2)=3, (row=1, column=3)=3, (row=1, column=6)=3, " +
                        "(row=1, column=7)=3, (row=1, column=4)=3, (row=1, column=5)=3}",
                move.getCaptures().toString());
        verifier.checkOverallBoard(18, 0, 0, 0, 0, 0, 0, 1,
                0, 0, 0, 0, 0, 0);
    }


    /**
     * The initial move results in the last seed going into storage, so there is a secondary move.
     * 1,1  1,2  1,3  1,4  1,5  1,6  1,7  1,8
     *      2,2  2,3  2,4  2,5  2,6  2,7
     */
    @Test
    public void testP1SimpleCombo() {

        // initial board state
        // 0, 1, 2, 3, 4, 5, 3, 0,
        //    3, 3, 3, 3, 3, 3
        board.getBin(new ByteLocation(1, 2)).increment(-2);
        board.getBin(new ByteLocation(1, 3)).increment(-1);
        board.getBin(new ByteLocation(1, 5)).increment(1);
        board.getBin(new ByteLocation(1, 6)).increment(2);
        MancalaMove move1 = new MancalaMove(true, new ByteLocation(1, 2), (byte)1, 0);
        MancalaMove move2 = new MancalaMove(true, new ByteLocation(1, 3), (byte)2, 0);
        move1.setFollowUpMove(move2);
        moveMaker.makeMove(move1);

        assertEquals("Unexpected captures.",
                "{}",
                move1.getCaptures().toString());
        System.out.println("b="+ board);

        verifier.checkOverallBoard(2, 1, 0, 3, 4, 5, 3, 0,
                                      3, 3, 3, 3, 3, 3);
    }

    /**
     * The initial move results in the last seed going into storage, so there is a secondary move.
     * 1,1  1,2  1,3  1,4  1,5  1,6  1,7  1,8
     *      2,2  2,3  2,4  2,5  2,6  2,7
     */
    @Test
    public void testP1SequenceCombo() {

        // initial board state
        // 0, 1, 2, 3, 4, 5, 3, 0,
        //    3, 3, 3, 3, 3, 3
        board.getBin(new ByteLocation(1, 2)).increment(-2);
        board.getBin(new ByteLocation(1, 3)).increment(-1);
        board.getBin(new ByteLocation(1, 5)).increment(1);
        board.getBin(new ByteLocation(1, 6)).increment(2);
        MancalaMove move1 = new MancalaMove(true, new ByteLocation(1, 2), (byte)1, 0);
        MancalaMove move2 = new MancalaMove(true, new ByteLocation(1, 3), (byte)2, 0);
        MancalaMove move3 = new MancalaMove(true, new ByteLocation(1, 4), (byte)3, 0);
        MancalaMove move4 = new MancalaMove(true, new ByteLocation(1, 5), (byte)4, 0);
        MancalaMove move5 = new MancalaMove(true, new ByteLocation(1, 3), (byte)2, 0);
        move1.setFollowUpMove(move2);
        move2.setFollowUpMove(move3);
        move3.setFollowUpMove(move4);
        move4.setFollowUpMove(move5);
        moveMaker.makeMove(move1);

        assertEquals("Unexpected captures.",
                "{}",
                move1.getCaptures().toString());
        System.out.println("b=" + board);

        verifier.checkOverallBoard(5, 4, 0, 1, 0, 5, 3, 0,
                3, 3, 3, 3, 3, 3);
    }

    /**
     * The initial move results in the last seed going into storage, so there is a secondary move.
     * 1,1  1,2  1,3  1,4  1,5  1,6  1,7  1,8
     *      2,2  2,3  2,4  2,5  2,6  2,7
     */
    @Test
    public void testCrazyP1Combo() {

        // initial board state
        // 0, 1, 2, 3, 4, 5, 3, 0,
        //    3, 3, 3, 3, 3, 3
        board.getBin(new ByteLocation(1, 2)).increment(-2);
        board.getBin(new ByteLocation(1, 3)).increment(-1);
        board.getBin(new ByteLocation(1, 5)).increment(1);
        board.getBin(new ByteLocation(1, 6)).increment(2);
        board.getBin(new ByteLocation(1, 7)).increment(3);
        MancalaMove move1 = new MancalaMove(true, new ByteLocation(1, 2), (byte)1, 0);
        MancalaMove move2 = new MancalaMove(true, new ByteLocation(1, 3), (byte)2, 0);
        MancalaMove move3 = new MancalaMove(true, new ByteLocation(1, 4), (byte)3, 0);
        MancalaMove move4 = new MancalaMove(true, new ByteLocation(1, 5), (byte)4, 0);
        MancalaMove move5 = new MancalaMove(true, new ByteLocation(1, 3), (byte)2, 0);
        MancalaMove move6 = new MancalaMove(true, new ByteLocation(1, 4), (byte)1, 0);
        move1.setFollowUpMove(move2);
        move2.setFollowUpMove(move3);
        move3.setFollowUpMove(move4);
        move4.setFollowUpMove(move5);
        move5.setFollowUpMove(move6);
        moveMaker.makeMove(move1);

        assertEquals("Unexpected captures.", "{}",  move1.getCaptures().toString());

        assertEquals("Unexpected captures.",
                "{(row=1, column=3)=1, (row=2, column=3)=3}",
                move6.getCaptures().toString());

        System.out.println("b=" + board);
        verifier.checkOverallBoard(9, 4, 0, 0, 0, 5, 6, 0,
                                      3, 0, 3, 3, 3, 3);
    }
}