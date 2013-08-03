// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import junit.framework.TestCase;

/**
 * @author Barry Becker
 */
public class DirectionTest extends TestCase {


    public void  testGetEastOffset() {
        assertEquals(new ByteLocation(0, 1), Direction.EAST.getOffset());
    }

    public void testGetEastDir() {
        assertEquals(Direction.EAST, Direction.getDirection(0, 1));
    }

    public void  testGetNorhtNorthOffset() {
        assertEquals(new ByteLocation(-2, 0), Direction.NORTH_NORTH.getOffset());
    }

    public void testGetNorthNorthDir() {
        assertEquals(Direction.NORTH_NORTH, Direction.getDirection(-2, 0));
    }

    public void  testGetSourthOffset() {
        assertEquals(new ByteLocation(1, 0), Direction.SOUTH.getOffset());
    }

    public void testGetSouthDir() {
        assertEquals(Direction.SOUTH, Direction.getDirection(1, 0));
    }

    /** verify that get direction is the inverse of getOffset */
    public void testAllDirections() {
        for (Direction dir: Direction.values()) {
            Location offset = dir.getOffset();
            assertEquals(dir, Direction.getDirection(offset.getRow(), offset.getCol()));
        }
    }
}
