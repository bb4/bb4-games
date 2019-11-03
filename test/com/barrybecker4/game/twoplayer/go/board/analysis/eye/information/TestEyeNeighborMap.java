/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.twoplayer.go.board.GoBoardConfigurator;
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import junit.framework.TestCase;

import java.util.List;


/**
 * @author Barry Becker
 */
public class TestEyeNeighborMap extends TestCase {

    private EyeNeighborMap nbrMap;

    private static final Location[] RABBITY_SIX = new ByteLocation[] {
            new ByteLocation(4, 2), new ByteLocation(3, 2), new ByteLocation(2, 3),
            new ByteLocation(3, 3), new ByteLocation(4, 3), new ByteLocation(3, 4)
    };

    private static final Location[] BLOCK_OF_SIX = new ByteLocation[] {
            new ByteLocation(13, 6), new ByteLocation(12, 5), new ByteLocation(12, 4),
            new ByteLocation(13, 4), new ByteLocation(13, 5), new ByteLocation(12, 6)
    };

    public void testSingleEyeSpace() {

        IGoEye eye =
                new StubGoEye(GoBoardConfigurator.createPositionList(new ByteLocation[]{new ByteLocation(2, 2)}));
        nbrMap = new EyeNeighborMap(eye);

        assertEquals("unexpected size", 1, nbrMap.keySet().size());
    }

    public void testTwoSpaceEye() {

        Location[] positions = new ByteLocation[] {new ByteLocation(2, 2), new ByteLocation(2, 3)};
        IGoEye eye = new StubGoEye(GoBoardConfigurator.createPositionList(positions));
        nbrMap = new EyeNeighborMap(eye);

        assertEquals("unexpected size", 2, nbrMap.keySet().size());
    }

    /**
     * Negative case
     */
    public void testDisjointEye() {

        Location[] positions = new ByteLocation[] {new ByteLocation(2, 2), new ByteLocation(3, 3)};
        IGoEye eye = new StubGoEye(GoBoardConfigurator.createPositionList(positions));
        try{
            nbrMap = new EyeNeighborMap(eye);
            fail();
        }
        catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testThreeSpaceEye() {

        Location[] positions = new ByteLocation[] {new ByteLocation(2, 2), new ByteLocation(2, 3), new ByteLocation(3, 3)};
        List<GoBoardPosition> spaces = GoBoardConfigurator.createPositionList(positions);
        IGoEye eye = new StubGoEye(spaces);
        nbrMap = new EyeNeighborMap(eye);

        assertEquals("unexpected size", 3, nbrMap.keySet().size());
        verifyNumEyeNbrs( new int[]{1, 2, 1}, spaces);
    }


    public void testRabbittySixEye() {

        List<GoBoardPosition> spaces = GoBoardConfigurator.createPositionList(RABBITY_SIX);
        IGoEye eye = new StubGoEye(spaces);
        nbrMap = new EyeNeighborMap(eye);

        assertEquals("unexpected size", 6, nbrMap.keySet().size());

        verifyNumEyeNbrs( new int[]{2, 2, 1, 4, 2, 1}, spaces);
    }

    public void testRabbittySixSpecialPoints() {

        List<GoBoardPosition> spaces = GoBoardConfigurator.createPositionList(RABBITY_SIX);
        IGoEye eye = new StubGoEye(spaces);
        nbrMap = new EyeNeighborMap(eye);

        // we expect 3,3 and 4,2 to be special points.
        float[] specialPoints = new float[] {4.06f, 2.04f};

        assertTrue(nbrMap.isSpecialPoint(spaces.get(0), specialPoints));
        assertFalse(nbrMap.isSpecialPoint(spaces.get(1), specialPoints));
        assertFalse(nbrMap.isSpecialPoint(spaces.get(2), specialPoints));
        assertTrue(nbrMap.isSpecialPoint(spaces.get(3), specialPoints));

        verifyNumEyeNbrs( new int[]{2, 2, 1, 4, 2, 1}, spaces);
    }

    public void testBlockOfSixEye() {

        List<GoBoardPosition> spaces = GoBoardConfigurator.createPositionList(BLOCK_OF_SIX);
        IGoEye eye = new StubGoEye(spaces);
        nbrMap = new EyeNeighborMap(eye);

        assertEquals("unexpected size", 6, nbrMap.keySet().size());

        verifyNumEyeNbrs( new int[]{2, 3, 2, 2, 3, 2}, spaces);
    }


    private void verifyNumEyeNbrs(int[] expectedNumNbrs, List<GoBoardPosition> spaces) {

        for (int i=0; i<expectedNumNbrs.length; i++) {
            assertEquals("Unexpected number of neighbors for position " + i,
                expectedNumNbrs[i], nbrMap.getNumEyeNeighbors(spaces.get(i)));
        }
    }
}
