/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.neighbor;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.twoplayer.go.GoTestCase;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.GoBoardConfigurator;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionList;

/**
 * Verify that all our neighbor analysis methods work.
 * @author Barry Becker
 */
public class TestNobiNeighborAnalyzer extends GoTestCase {

    private static final String PREFIX = "board/analysis/neighbor/";

    /** instance under test */
    private NobiNeighborAnalyzer nobiAnalyzer_;


    public void testNoOccupiedNobiNbrs() throws Exception {
        verifyOccupiedNobiNbrs("nobiNbr_NoneOccupied", new Location[] {new ByteLocation(5, 5)}, 0);
    }

    public void testBlackOccupiedNobiNbrs() throws Exception {
        verifyOccupiedNobiNbrs("nobiNbr_BlackOccupied", new Location[] {new ByteLocation(7, 7)}, 3);
    }

    public void testMixOccupiedNobiNbrs() throws Exception {
        verifyOccupiedNobiNbrs("nobiNbr_MixOccupied", new Location[] {new ByteLocation(7, 7)}, 3);
    }

    public void testMixOccupiedNobiNbrsOnEdge() throws Exception {
        verifyOccupiedNobiNbrs("nobiNbr_MixOccupiedOnEdge", new Location[] {new ByteLocation(5, 1)}, 2);
    }

    public void testNobiNbrsOnEdge() throws Exception {
        verifyOccupiedNobiNbrs("nobiNbr_MixOccupiedOnEdge", new Location[] {new ByteLocation(5, 1)}, 2);
    }


    public void testUnoccupiedNobiNbrs() throws Exception {
        verifyNobiNbrs("nobiNbr_Unoccupied", 5, 5, false, NeighborType.UNOCCUPIED, 2);
    }

    public void testUnoccupiedNobiNbrsInCorner() throws Exception {
        verifyNobiNbrs("nobiNbr_UnoccupiedInCorner", 1, 1, false, NeighborType.UNOCCUPIED, 2);
    }

    public void testFriendNobiNbrsOneBlack() throws Exception {
        verifyNobiNbrs("nobiNbr_OneBlackFriend", 5, 5, true, NeighborType.FRIEND, 1);
    }

    public void testFriendNobiNbrsFourWhite() throws Exception {
        verifyNobiNbrs("nobiNbr_FourWhiteFriends", 5, 5, false, NeighborType.FRIEND, 4);
    }

    public void testEnemyNobiNbrsOneWhite() throws Exception {
        verifyNobiNbrs("nobiNbr_OneWhiteEnemy", 5, 5, true, NeighborType.ENEMY, 1);
    }

    public void testEnemyNobiNbrsFourBlack() throws Exception {
        verifyNobiNbrs("nobiNbr_FourBlackEnemies", 5, 5, false, NeighborType.ENEMY, 4);
    }

    public void testNotFriendNobiNbrsTwo() throws Exception {
        verifyNobiNbrs("nobiNbr_TwoNotFriends", 5, 5, false, NeighborType.NOT_FRIEND, 2);
    }

    public void testNotFriendNobiNbrsThree() throws Exception {
        verifyNobiNbrs("nobiNbr_ThreeNotFriends", 5, 5, true, NeighborType.NOT_FRIEND, 3);
    }

    private void verifyOccupiedNobiNbrs(
            String file, Location[] empties, int expectedNumOccupiedNbrs) throws Exception {
        restore(PREFIX +file);

        GoBoard board = getBoard();
        nobiAnalyzer_ = new NobiNeighborAnalyzer(board);
        GoBoardPositionList emptyList = GoBoardConfigurator.createPositionList(empties);

        int numOccupiedNbrs = nobiAnalyzer_.findOccupiedNobiNeighbors(emptyList).size();

        assertEquals("Unexpected number of occupied neigbors.",
                expectedNumOccupiedNbrs, numOccupiedNbrs);
    }

    private void verifyNobiNbrs(String file, int row, int col,
                                boolean friendOwnedByP1, NeighborType type,
                                int expectedNumNbrs) throws Exception {
        restore(PREFIX +file);

        GoBoard board = getBoard();
        nobiAnalyzer_ = new NobiNeighborAnalyzer(board);
        GoBoardPosition pos = (GoBoardPosition) board.getPosition(row, col);

        int numNbrs = nobiAnalyzer_.getNobiNeighbors(pos, friendOwnedByP1, type).size();

        assertEquals("Unexpected number of neigbors.",
                expectedNumNbrs, numNbrs);
    }
}