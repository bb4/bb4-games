package com.barrybecker4.game.twoplayer.hex.pathsearch;

import com.barrybecker4.game.twoplayer.hex.HexBoard;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ShortestPathSearchTest {

    private HexBoard board;
    private ShortestPathSearch search;

    @Before
    public void setUp() {
        board = new HexBoard();
    }

    @Test
    public void testSearchForP1WhenEmpty() {
        search = new ShortestPathSearch(board, true);
        List<HexTransition> path = search.solve();
        assertEquals("Unexpected path.",
                "[[(row=0, column=3): 0], " +
                "[(row=1, column=2): 1], " +
                "[(row=2, column=1): 1], " +
                "[(row=3, column=1): 1], " +
                "[(row=4, column=1): 1], " +
                "[(row=5, column=1): 1], " +
                "[(row=6, column=1): 1], " +
                "[(row=7, column=1): 1], " +
                "[(row=8, column=1): 1], " +
                "[(row=9, column=1): 1], " +
                "[(row=10, column=1): 1], " +
                "[(row=11, column=1): 1], " +
                "[(row=12, column=1): 0]]",
                path.toString());
    }

    @Test
    public void testSearchForP2WhenEmpty() {
        search = new ShortestPathSearch(board, false);
        List<HexTransition> path = search.solve();
        assertEquals("Unexpected path.",
                "[[(row=2, column=1): 1], " +
                "[(row=2, column=2): 1], " +
                "[(row=2, column=3): 1], " +
                "[(row=2, column=4): 1], " +
                "[(row=2, column=5): 1], " +
                "[(row=2, column=6): 1], " +
                "[(row=2, column=7): 1], " +
                "[(row=2, column=8): 1], " +
                "[(row=2, column=9): 1], " +
                "[(row=2, column=10): 1], " +
                "[(row=2, column=11): 1], " +
                "[(row=2, column=12): 0]]",
                path.toString());
    }

}
