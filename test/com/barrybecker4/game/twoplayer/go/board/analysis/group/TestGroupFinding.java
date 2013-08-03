/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.twoplayer.go.GoTestCase;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionSet;

import java.util.List;

/**
 * Check that we can identify groups on the board.
 * @author Barry Becker
 */
public class TestGroupFinding extends GoTestCase {

    /** where to go for the test files. */
    private static final String PREFIX = "board/analysis/eye/information/FalseEye/";


    // ----------- check that we can find group neighbors -------

    public void testFindKoGroupNeighbors() throws Exception {
        GoBoard b = initializeBoard("false_ko_eye1");

        // white group neighbors
        verifyGroupNeighbors(b, new ByteLocation(4, 5), 3);
        verifyGroupNeighbors(b, new ByteLocation(5, 6), 6);
        verifyGroupNeighbors(b, new ByteLocation(6, 7), 5);
        verifyGroupNeighbors(b, new ByteLocation(7, 5), 7);   // 8?

        // black group neighbors
        verifyGroupNeighbors(b, new ByteLocation(4, 6), 3);
        verifyGroupNeighbors(b, new ByteLocation(8, 8), 6);
        verifyGroupNeighbors(b, new ByteLocation(6, 7), 5);
        verifyGroupNeighbors(b, new ByteLocation(9, 7), 5);
        verifyGroupNeighbors(b, new ByteLocation(8, 6), 5);
    }

    public void testFindKoGroupNeighbors2() throws Exception {
        GoBoard b = initializeBoard("false_ko_eye2");

        // white group neighbors
        verifyGroupNeighbors(b, new ByteLocation(13, 7), 6);
        verifyGroupNeighbors(b, new ByteLocation(12, 8), 3);

        // black group neighbors
        verifyGroupNeighbors(b, new ByteLocation(10, 8), 9);
        verifyGroupNeighbors(b, new ByteLocation(12, 7), 1);

    }

    // ----------- check that we can find groups ----------------

    /**
     * Negative test.
     * The position we give to look from does not contain a stone.
     */
    public void testFindNoGroup() throws Exception {
        GoBoard b = initializeBoard("false_ko_eye1");
        try {
            verifyGroup(b, new ByteLocation(2, 3), 6);
            fail();
        } catch (NullPointerException e) {
            // expected, but maybe we should throw illegal argument exception instead.
        }
    }

    public void testFindFalseEyeGroup1() throws Exception {
        GoBoard b = initializeBoard("false_ko_eye1");
        verifyGroup(b, new ByteLocation(5, 5), 10);
        verifyGroup(b, new ByteLocation(6, 8), 11);
    }

    public void testFindFalseEyeGroup2() throws Exception {
        GoBoard b = initializeBoard("false_ko_eye2");
        verifyGroup(b, new ByteLocation(8, 8), 18);
        verifyGroup(b, new ByteLocation(13, 10), 18);
    }


    private GoBoard initializeBoard(String file) throws Exception  {
        restore(PREFIX  + file);
        return getBoard();
    }

    private void verifyGroupNeighbors(GoBoard board, Location loc, int expectedNumNeighbors)  {
        NeighborAnalyzer na = new NeighborAnalyzer(board);
        GoBoardPosition position = (GoBoardPosition)board.getPosition(loc);
        GoBoardPositionSet group = na.findGroupNeighbors(position, true);
        assertEquals("Unexpected number of group neighbors for : "+ position +" \n" + group + "\n",
                expectedNumNeighbors, group.size());
    }

    private void verifyGroup(GoBoard board, Location loc, int expectedNumStonesInGroup)  {
        NeighborAnalyzer na = new NeighborAnalyzer(board);
        GoBoardPosition position = (GoBoardPosition)board.getPosition(loc);
        List<GoBoardPosition> group = na.findGroupFromInitialPosition(position);
        assertEquals("Unexpected number of stones in group: \n" + group + "\n",
                expectedNumStonesInGroup, group.size());
    }
}