/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.twoplayer.go.GoTestCase;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;

import java.util.ArrayList;
import java.util.List;

/**
 *Test that candidate moves can be generated appropriately.
 *
 * @author Barry Becker
 */
public class TestCandidateMoveAnalyzer extends GoTestCase {

    /** we can just reuse one of the other file sets */
    private static final String PREFIX = "scoring/";


    public void testCandidateMoves1() throws Exception {
        verifyCandidateMoves("problem_score1", 114, null);
    }

    public void testCandidateMoves2() throws Exception{
        verifyCandidateMoves("problem_score2", 108/*53*/, null);
    }

    public void testCandidateMoves3() throws Exception {
        List<Location> expCandidates = new ArrayList<Location>(10);
        expCandidates.add(new ByteLocation(2, 5));
        expCandidates.add(new ByteLocation(1, 4));
        expCandidates.add(new ByteLocation(4, 3));
        expCandidates.add(new ByteLocation(5, 2));

        verifyCandidateMoves("problem_score55a", 10/*4*/, expCandidates);
    }

    /** XXXX sometimes passes sometimes fails. odd  */
    public void testCandidateMoves4() throws Exception {

        System.out.println("------------cm4 --------------------------");
        List<Location> expCandidates = new ArrayList<Location>(20);
        expCandidates.add(new ByteLocation(1, 1));
        expCandidates.add(new ByteLocation(1, 5));
        expCandidates.add(new ByteLocation(2, 1));
        expCandidates.add(new ByteLocation(2, 3));
        expCandidates.add(new ByteLocation(2, 4));
        expCandidates.add(new ByteLocation(3, 2));
        expCandidates.add(new ByteLocation(4, 1));
        expCandidates.add(new ByteLocation(5, 1));
        expCandidates.add(new ByteLocation(5, 4));
        expCandidates.add(new ByteLocation(5, 5));

        verifyCandidateMoves("problem_score55b", 10, expCandidates);
    }


    /**
     * Verify candidate move generation.
     */
    private void verifyCandidateMoves(
            String file, int expNumCandidates, List<Location> expCandidates) throws Exception {
        restore(PREFIX + file);

        GoBoard board = getBoard();
        CandidateMoveAnalyzer cma = new CandidateMoveAnalyzer(board);

        int actNumCandidates = cma.getNumCandidates();
        assertEquals("Unexpected number of candidate moves for case  "+ file, expNumCandidates, actNumCandidates);

        if (expCandidates != null) {
            System.out.println("actual candidates="+ actNumCandidates);
            for (Location loc : expCandidates) {
                assertTrue("Invalid candidate position:" + loc, cma.isCandidateMove(loc.row(), loc.col()));
            }
        }
    }

}
