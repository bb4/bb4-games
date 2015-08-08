package com.barrybecker4.game.twoplayer.hex.pathsearch;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
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

    /** When empty, p1 snakes down column 1 to the goal state */
    @Test
    public void testSearchForP1WhenEmpty() {
        search = new ShortestPathSearch(board, true);
        List<HexTransition> path = search.solve();
        assertEquals("Unexpected path.",
                "[[(row=0, column=3): 0], [(row=0, column=4): 0], " +
                "[(row=0, column=5): 0], [(row=0, column=6): 0], " +
                "[(row=0, column=7): 0], [(row=0, column=8): 0], " +
                "[(row=0, column=9): 0], [(row=0, column=10): 0], " +
                "[(row=0, column=11): 0], [(row=1, column=11): 10], " +
                "[(row=2, column=11): 10], [(row=3, column=11): 10], " +
                "[(row=4, column=11): 10], [(row=5, column=10): 10], " +
                "[(row=6, column=10): 10], [(row=7, column=10): 10], " +
                "[(row=8, column=9): 10], [(row=9, column=8): 10], " +
                "[(row=10, column=7): 10], [(row=11, column=6): 10], " +
                "[(row=12, column=5): 0]]",
                path.toString());
    }

    /** When empty, p2 snakes across row 2 to the goal state */
    @Test
    public void testSearchForP2WhenEmpty() {
        search = new ShortestPathSearch(board, false);
        List<HexTransition> path = search.solve();
        assertEquals("Unexpected path.",
                "[[(row=3, column=0): 0], [(row=4, column=0): 0], " +
                "[(row=5, column=0): 0], [(row=6, column=0): 0], " +
                "[(row=7, column=0): 0], [(row=8, column=0): 0], " +
                "[(row=9, column=0): 0], [(row=10, column=0): 0], " +
                "[(row=11, column=0): 0], [(row=11, column=1): 10], " +
                "[(row=11, column=2): 10], [(row=11, column=3): 10], " +
                "[(row=11, column=4): 10], [(row=11, column=5): 10], " +
                "[(row=11, column=6): 10], [(row=11, column=7): 10], " +
                "[(row=11, column=8): 10], [(row=11, column=9): 10], " +
                "[(row=11, column=10): 10], [(row=11, column=11): 10], " +
                "[(row=11, column=12): 0]]",
                path.toString());
    }

    @Test
    public void testSearchForP1WhenPathAlmostThere() {
        p1Move(3, 3);
        p2Move(3, 6);
        p1Move(3, 4);
        p2Move(3, 5);
        p1Move(4, 4);
        p2Move(4, 5);
        p1Move(5, 4);
        p2Move(5, 5);
        p1Move(6, 4);
        p2Move(6, 5);
        p1Move(7, 4);
        p2Move(7, 5);
        p1Move(8, 4);
        //System.out.println("board = " + board);

        search = new ShortestPathSearch(board, true);
        List<HexTransition> path = search.solve();

        assertEquals("Unexpected path.",
                "[[(row=0, column=3): 0], " +
                "[(row=0, column=4): 0], " +
                "[(row=1, column=4): 10], " +
                "[(row=2, column=4): 10], " +
                "[(row=3, column=4): 0], " +
                "[(row=4, column=4): 0], " +
                "[(row=5, column=4): 0], " +
                "[(row=6, column=4): 0], " +
                "[(row=7, column=4): 0], " +
                "[(row=8, column=4): 0], " +
                "[(row=9, column=3): 10], " +
                "[(row=10, column=2): 10], " +
                "[(row=11, column=1): 10], " +
                "[(row=12, column=1): 0]]",
                path.toString());
    }

    @Test
    public void testSearchForP1ThroughKeyhole() {
        p1Move(3, 3);
        p2Move(3, 6);
        p2Move(3, 5);
        p1Move(4, 4);
        p2Move(4, 5);
        p2Move(5, 5);
        p1Move(7, 4);
        p2Move(7, 1);
        p2Move(7, 2);
        p2Move(7, 3);
        p2Move(7, 4);
        p2Move(7, 5);
        p2Move(7, 6);
        p2Move(7, 7);
        p2Move(7, 8);
        p2Move(7, 9);
        p2Move(7, 11);

        System.out.println("board = " + board);

        search = new ShortestPathSearch(board, true);
        List<HexTransition> path = search.solve();

        assertEquals("Unexpected path.",
                "[[(row=0, column=3): 0], [(row=0, column=4): 0], " +
                "[(row=0, column=5): 0], [(row=0, column=6): 0], " +
                "[(row=0, column=7): 0], [(row=0, column=8): 0], " +
                "[(row=0, column=9): 0], [(row=0, column=10): 0], " +
                "[(row=0, column=11): 0], [(row=1, column=11): 10], " +
                "[(row=2, column=11): 10], [(row=3, column=11): 10], " +
                "[(row=4, column=11): 10], [(row=5, column=11): 10], " +
                "[(row=6, column=10): 10], [(row=7, column=10): 10], " +
                "[(row=8, column=10): 10], [(row=9, column=10): 10], " +
                "[(row=10, column=10): 10], [(row=11, column=9): 10], " +
                "[(row=12, column=8): 0]]",
                path.toString());
    }

    @Test
    public void testSearchForP1ThroughTwoKeyholes() {
        p1Move(3, 3);
        p2Move(3, 6);
        p2Move(3, 5);
        p1Move(4, 4);
        p2Move(4, 5);
        p2Move(5, 5);
        p2Move(4, 2);
        p2Move(4, 3);
        p2Move(4, 4);
        p2Move(4, 5);
        p2Move(4, 6);
        p2Move(4, 7);
        p2Move(4, 8);
        p2Move(4, 9);
        p2Move(4, 10);
        p2Move(4, 11);
        p1Move(7, 4);
        p2Move(7, 1);
        p2Move(7, 2);
        p2Move(7, 3);
        p2Move(7, 4);
        p2Move(7, 5);
        p2Move(7, 6);
        p2Move(7, 7);
        p2Move(7, 8);
        p2Move(7, 9);
        p2Move(7, 11);

        System.out.println("board = " + board);

        search = new ShortestPathSearch(board, true);
        List<HexTransition> path = search.solve();

        assertEquals("Unexpected path.",
                "[[(row=0, column=3): 0], [(row=1, column=3): 10], " +
                "[(row=2, column=2): 10], [(row=3, column=1): 10], " +
                "[(row=4, column=1): 10], [(row=5, column=1): 10], " +
                "[(row=6, column=1): 10], [(row=6, column=2): 10], " +
                "[(row=6, column=3): 10], [(row=6, column=4): 10], " +
                "[(row=6, column=5): 10], [(row=6, column=6): 10], " +
                "[(row=6, column=7): 10], [(row=6, column=8): 10], " +
                "[(row=6, column=9): 10], [(row=6, column=10): 10], " +
                "[(row=7, column=10): 10], [(row=8, column=9): 10], " +
                "[(row=9, column=9): 10], [(row=10, column=9): 10], " +
                "[(row=11, column=9): 10], [(row=12, column=8): 0]]",
                path.toString());
        showSolvedPath(path, true);
    }

    @Test
    public void testSearchForP1WhenColumnPathShortcutOnRight() {
        p1Move(2, 10);
        p1Move(3, 10);
        p1Move(4, 10);
        p1Move(5, 10);
        p1Move(6, 10);
        p1Move(7, 10);
        p1Move(8, 10);
        p1Move(9, 10);
        p1Move(10, 10);
        System.out.println("board = " + board);

        search = new ShortestPathSearch(board, true);
        List<HexTransition> path = search.solve();
        assertEquals("Unexpected path.",
                "[[(row=0, column=3): 0], [(row=0, column=4): 0], " +
                        "[(row=0, column=5): 0], [(row=0, column=6): 0], " +
                        "[(row=0, column=7): 0], [(row=0, column=8): 0], " +
                        "[(row=0, column=9): 0], [(row=0, column=10): 0], " +
                        "[(row=1, column=10): 10], [(row=2, column=10): 0], " +
                        "[(row=3, column=10): 0], [(row=4, column=10): 0], " +
                        "[(row=5, column=10): 0], [(row=6, column=10): 0], " +
                        "[(row=7, column=10): 0], [(row=8, column=10): 0], " +
                        "[(row=9, column=10): 0], [(row=10, column=10): 0], " +
                        "[(row=11, column=9): 10], [(row=12, column=8): 0]]",
                path.toString());
        //showSolvedPath(path, true);
    }


    @Test
    public void testSearchForP1WhenColumnPathShortcut() {
        p1Move(3, 4);
        p2Move(3, 5);
        p1Move(4, 4);
        p2Move(4, 5);
        p1Move(5, 4);
        p2Move(5, 5);
        p1Move(6, 4);
        p2Move(6, 5);
        p1Move(7, 4);
        p2Move(7, 5);
        p1Move(8, 4);
        System.out.println("board = " + board);

        search = new ShortestPathSearch(board, true);
        List<HexTransition> path = search.solve();
        assertEquals("Unexpected path.",
                "[[(row=0, column=3): 0], [(row=0, column=4): 0], " +
                        "[(row=1, column=4): 10], [(row=2, column=4): 10], " +
                        "[(row=3, column=4): 0], [(row=4, column=4): 0], " +
                        "[(row=5, column=4): 0], [(row=6, column=4): 0], " +
                        "[(row=7, column=4): 0], [(row=8, column=4): 0], " +
                        "[(row=9, column=3): 10], [(row=10, column=2): 10], " +
                        "[(row=11, column=1): 10], [(row=12, column=1): 0]]",
                path.toString());
    }

    @Test
    public void testSearchForP2WhenFull() {
        p1Move(4, 3);
        p2Move(5, 4);
        p1Move(4, 4);
        p2Move(5, 4);
        p1Move(5, 4);
        p2Move(5, 5);
        p1Move(4, 5);
        p2Move(5, 6);
        p1Move(4, 7);
        p2Move(5, 7);
        p1Move(4, 8);

        search = new ShortestPathSearch(board, false);
        List<HexTransition> path = search.solve();
        assertEquals("Unexpected path.",
                "[[(row=3, column=0): 0], [(row=4, column=0): 0], " +
                "[(row=5, column=0): 0], [(row=6, column=0): 0], " +
                "[(row=7, column=0): 0], [(row=7, column=1): 10], " +
                "[(row=7, column=2): 10], [(row=7, column=3): 10], " +
                "[(row=6, column=4): 10], [(row=5, column=5): 0], " +
                "[(row=5, column=6): 0], [(row=5, column=7): 0], " +
                "[(row=5, column=8): 10], [(row=5, column=9): 10], " +
                "[(row=5, column=10): 10], [(row=5, column=11): 10], " +
                "[(row=5, column=12): 0]]",
                path.toString());
    }


    private void showSolvedPath(List<HexTransition> path, boolean player1) {
        for (HexTransition tran : path) {
            Location loc = tran.getLocation();
            playerMove(loc.getRow(), loc.getCol(), player1);
        }
        System.out.println("board = " + board);
    }

    private void p1Move(int row, int col) {
        playerMove(row, col, true);
    }

    private void p2Move(int row, int col) {
        playerMove(row, col, false);
    }

    private void playerMove(int row, int col, boolean p1) {
        if (row > 0 && row <= board.getNumRows() && col > 0 && col <= board.getNumCols()) {
            board.makeMove(TwoPlayerMove.createMove(row, col, 0, new GamePiece(p1)));
        }
    }
}
