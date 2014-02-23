// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.mancala.board;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Barry Becker
 */
public class BinNavigatorTest {

    /** instance under test. */
    private BinNavigator navigator;

    @Before
    public void setUp() {
        navigator = new BinNavigator(8);
    }

    /**
     * Next locations move counter clockwise
     * The grid positions are something like
     * 1,1  1,2  1,3  1,4  1,5  1,6  1,7  1,8
     *      2,2  2,3  2,4  2,5  2,6  2,7
     */
    @Test
    public void testGetNextLocation8() {

        checkNext(new ByteLocation(1, 1), new ByteLocation(2, 2));
        checkNext(new ByteLocation(2, 4), new ByteLocation(2, 5));
        checkNext(new ByteLocation(1, 6), new ByteLocation(1, 5));
        checkNext(new ByteLocation(2, 7), new ByteLocation(1, 8));
    }

    @Test
    public void testGetNextLocation3() {

        navigator = new BinNavigator(3);
        checkNext(new ByteLocation(1, 1), new ByteLocation(2, 2));
        checkNext(new ByteLocation(1, 2), new ByteLocation(1, 1));
        checkNext(new ByteLocation(2, 2), new ByteLocation(1, 3));
        checkNext(new ByteLocation(1, 3), new ByteLocation(1, 2));
    }

    @Test
    public void testGetNthLocation8() {

        checkNthLocation(new ByteLocation(1, 1), 1, new ByteLocation(2, 2));
        checkNthLocation(new ByteLocation(1, 1), -1, new ByteLocation(1, 2));
        checkNthLocation(new ByteLocation(2, 4), 2, new ByteLocation(2, 6));
        checkNthLocation(new ByteLocation(2, 4), -2, new ByteLocation(2, 2));
        checkNthLocation(new ByteLocation(2, 4), 4, new ByteLocation(1, 8));
        checkNthLocation(new ByteLocation(2, 4), -4, new ByteLocation(1, 2));
    }

    @Test
    public void testGetOppositeLocation() {
        checkOppositeLocation(new ByteLocation(1, 2), new ByteLocation(2, 2));
        checkOppositeLocation(new ByteLocation(2, 3), new ByteLocation(1, 3));
        checkOppositeLocation(new ByteLocation(1, 7), new ByteLocation(2, 7));
        checkOppositeLocation(new ByteLocation(1, 1), new ByteLocation(2, 1));
    }

    @Test
    public void testGetOppositeLocation1() {
        checkOppositeLocation(new ByteLocation(1, 2), new ByteLocation(2, 2));
        checkOppositeLocation(new ByteLocation(2, 3), new ByteLocation(1, 3));
        checkOppositeLocation(new ByteLocation(1, 7), new ByteLocation(2, 7));
        checkOppositeLocation(new ByteLocation(1, 1), new ByteLocation(2, 1));
    }

    @Test
    public void testGetHome() {
        assertEquals("Unexpected player 1 home" ,
                new ByteLocation(1, 1), navigator.getHomeLocation(true));
        assertEquals("Unexpected player 2 home" ,
                new ByteLocation(1, 8), navigator.getHomeLocation(false));
    }

    private void checkNext(Location current, Location expNext) {
        assertEquals("Unexpected next location", expNext, navigator.getNextLocation(current));
    }

    private void checkNthLocation(Location current, int numSteps, Location expNth) {
        assertEquals("Unexpected ("+numSteps+")th location from " + current,
                expNth, navigator.getNthLocation(current, numSteps));
    }

    private void checkOppositeLocation(Location current, Location expOpposite) {
        assertEquals("Unexpected opposite location" ,
                expOpposite, navigator.getOppositeLocation(current));
    }

}