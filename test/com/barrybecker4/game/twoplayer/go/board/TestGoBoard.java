/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.twoplayer.go.GoTestCase;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;

/**
 * Verify that all the methods in GoBoard work as expected
 * @author Barry Becker
 */
public class TestGoBoard extends GoTestCase {

    private static final String PREFIX = "board/";

    /** verify that the right stones are captured by a given move. */
    public void testFindCaptures1() throws Exception {
        verifyCaptures("findCaptures1", new ByteLocation(5, 6), 6);
    }

    public void testFindCaptures2() throws Exception {
        verifyCaptures("findCaptures2", new ByteLocation(6, 6), 9);
    }

    public void testFindCaptures3() throws Exception {
        verifyCaptures("findCaptures3", new ByteLocation(5, 4), 7);
    }

    public void testFindCaptures4() throws Exception {
        verifyCaptures("findCaptures4", new ByteLocation(4, 8), 16);
    }

    public void testFindCaptures5() throws Exception {
        verifyCaptures("findCaptures5", new ByteLocation(10, 2), 11);
    }

    private void verifyCaptures(String file, Location moveLocation, int expNnumCaptures) throws Exception {

        restore(PREFIX  + file);

        GoMove move = new GoMove(moveLocation, 0, new GoStone(true));
        GoBoard board = getBoard();
        int numWhiteStonesBefore = board.getNumStones(false);
        controller.makeMove(move);

        int numWhiteStonesAfter = board.getNumStones(false);

        int actualNumCaptures = move.getNumCaptures();

        assertTrue("move.captures=" + actualNumCaptures + " expected "+expNnumCaptures,
                              actualNumCaptures == expNnumCaptures);
        int diffWhite = numWhiteStonesBefore - numWhiteStonesAfter;
        assertTrue("diff in num white stones ("+ diffWhite
                + ") not = numcaptures (" + expNnumCaptures
                + ')', diffWhite == expNnumCaptures);

        controller.undoLastMove();
        // verify that all the captured stones get restored to the board
        numWhiteStonesAfter = board.getNumStones(false);
        assertTrue("numWhiteStonesBefore="+numWhiteStonesBefore
                + " not equal numWhiteStonesAfter="+numWhiteStonesAfter,
                numWhiteStonesBefore == numWhiteStonesAfter );
    }


    public void testCausedAtari1() throws Exception {
        restore(PREFIX + "causedAtari1");

        GoMove m = new GoMove(new ByteLocation(4, 4), 0, new GoStone(false));
        int numInAtari = m.numStonesAtaried(getBoard());
        assertTrue("numInAtri="+numInAtari+" expected="+4, numInAtari == 4);
    }


    public void testCausedAtari2() throws Exception {
        restore(PREFIX + "causedAtari2");

        GoMove m = new GoMove(new ByteLocation(2, 12), 0, new GoStone(true));
        controller.makeMove(m);
        GoBoard board = (GoBoard) controller.getBoard();
        int numInAtari = m.numStonesAtaried(board);
        assertTrue("numInAtri="+numInAtari+" expected="+12, numInAtari == 12);
    }


    public void testNumLiberties1() throws Exception {
        verifyGroupLiberties("causedAtari2", 2, 9, 14,  2, 10, 2);
    }

    public void testNumLiberties2() throws Exception {
        verifyGroupLiberties("numLiberties2", 1, 2, 17,   3, 6, 16);
    }

    public void testCopy() {
        GoBoard board = new GoBoard(3, 0);
        Location center = new ByteLocation(2, 2);
        board.makeMove(new GoMove(center, 1, new GoStone(true)));
        GoBoard boardCopy = board.copy();


        assertEquals("Center positions not equal.", board.getPosition(center), boardCopy.getPosition(center));
    }

    private void verifyGroupLiberties(String file,
              int bRow, int bCol, int expectedBlackLiberties,
              int wRow, int wCol, int expectedWhiteLiberties) throws Exception {
        restore(PREFIX + file);
        GoBoard board = getBoard();

        GoBoardPosition pos = (GoBoardPosition)board.getPosition(bRow, bCol);
        int numGroupLiberties = pos.getGroup().getLiberties(board).size();
        assertTrue("numGroupLiberties="+numGroupLiberties+" expected="+expectedBlackLiberties,
                numGroupLiberties == expectedBlackLiberties);

        pos = (GoBoardPosition)board.getPosition(wRow, wCol);
        numGroupLiberties = pos.getGroup().getLiberties(board).size();
        assertTrue("numGroupLiberties="+numGroupLiberties+" expected="+expectedWhiteLiberties,
                numGroupLiberties == expectedWhiteLiberties);
    }

}
