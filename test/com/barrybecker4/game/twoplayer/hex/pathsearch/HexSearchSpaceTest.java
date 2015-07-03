package com.barrybecker4.game.twoplayer.hex.pathsearch;

import com.barrybecker4.common.geometry.IntLocation;
import com.barrybecker4.game.twoplayer.hex.HexBoard;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Barry Becker
 */
public class HexSearchSpaceTest {

    private HexBoard board;
    private HexSearchSpace space;

    @Before
    public void setUp() {
        board = new HexBoard();
        space = new HexSearchSpace(board, true);
    }

    @Test
    public void testConstruction() {
        int distance = space.distanceFromGoal(new HexState(board, new IntLocation(5, 5)));
        assertEquals("Unexpected distance.", 6, distance);

        HexState expInitialState = new HexState(board, new IntLocation(0, 1));
        assertEquals("Unexpected initial state", expInitialState, space.initialState());
    }


    @Test
    public void testTransitionCost() {
        assertEquals("Unexpected cost",
                3, space.getCost(new HexTransition(new IntLocation(6, 6), 3)));
    }

    @Test
    public void testIsPlayer1Goal() {
        HexState state = new HexState(board, new IntLocation(12, 4));
        Assert.assertTrue("Unexpectedly not goal", space.isGoal(state));
    }

    @Test
    public void testIsNotPlayer1Goal() {
        HexState notGoal = new HexState(board, new IntLocation(4, 1));
        Assert.assertFalse("Unexpectedly goal", space.isGoal(notGoal));
    }

    @Test
    public void testIsPlayer2Goal() {
        space = new HexSearchSpace(board, false);
        HexState state = new HexState(board, new IntLocation(4, 12));
        Assert.assertTrue("Unexpectedly not goal", space.isGoal(state));
    }

    @Test
    public void testIsNotPlayer2Goal() {
        space = new HexSearchSpace(board, false);
        HexState notGoal = new HexState(board, new IntLocation(4, 1));
        Assert.assertFalse("Unexpectedly goal", space.isGoal(notGoal));
    }

}
