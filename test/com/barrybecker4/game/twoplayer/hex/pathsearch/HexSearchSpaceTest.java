package com.barrybecker4.game.twoplayer.hex.pathsearch;

import com.barrybecker4.common.geometry.IntLocation;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.hex.HexBoard;
import org.junit.Before;
import org.junit.Test;
import scala.collection.immutable.Seq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        assertEquals("Unexpected distance.", 63, distance);

        HexState expInitialState = new HexState(board, new IntLocation(0, 2));
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
        assertTrue("Unexpectedly not goal", space.isGoal(state));
    }

    @Test
    public void testIsNotPlayer1Goal() {
        HexState notGoal = new HexState(board, new IntLocation(4, 1));
        assertFalse("Unexpectedly goal", space.isGoal(notGoal));
    }

    @Test
    public void testIsPlayer2Goal() {
        space = new HexSearchSpace(board, false);
        HexState state = new HexState(board, new IntLocation(4, 12));
        assertTrue("Unexpectedly not goal", space.isGoal(state));
    }

    @Test
     public void testIsNotPlayer2Goal() {
        space = new HexSearchSpace(board, false);
        HexState notGoal = new HexState(board, new IntLocation(4, 1));
        assertFalse("Unexpectedly goal", space.isGoal(notGoal));
    }

    @Test
    public void testLegalTransitionsFromMiddleForP1() {
        HexState state = new HexState(board, new IntLocation(4, 1));
        Seq<HexTransition> transitions = space.legalTransitions(state);

        assertEquals("Unexpected number of transitions.", 4, transitions.size());

        assertEquals("Unexpected transitions.",
                "[(row=4, column=2): 10], " +
                        "[(row=3, column=2): 10], " +
                        "[(row=3, column=1): 10], " +
                        "[(row=5, column=1): 10]",
                transitions.mkString(", "));
    }

    @Test
    public void testLegalTransitionsFromMiddleForP2() {
        space = new HexSearchSpace(board, false);

        HexState state = new HexState(board, new IntLocation(4, 1));
        Seq<HexTransition> transitions = space.legalTransitions(state);

        assertEquals("Unexpected transitions.",
                "[(row=4, column=0): 0], [(row=5, column=0): 0], [(row=4, column=2): 10], " +
                "[(row=3, column=2): 10], [(row=3, column=1): 10], [(row=5, column=1): 10]",
                transitions.mkString(", "));
    }

    @Test
    public void testLegalTransitionsFromTopEdgeForP1() {

        HexState state = new HexState(board, new IntLocation(0, 6));
        Seq<HexTransition> transitions = space.legalTransitions(state);

        assertEquals("Unexpected transitions.",
                "[(row=0, column=7): 0], " +
                        "[(row=0, column=5): 0], " +
                        "[(row=1, column=5): 10], " +
                        "[(row=1, column=6): 10]",
                transitions.mkString(", "));
    }

    @Test
    public void testLegalTransitionsFromLeftEdgeForP1() {

        HexState state = new HexState(board, new IntLocation(4, 0));
        Seq<HexTransition> transitions = space.legalTransitions(state);

        assertEquals("Unexpected transitions.",
                "[(row=4, column=1): 10], " +
                        "[(row=3, column=1): 10]",
                transitions.mkString(", "));
    }

    @Test
    public void testLegalTransitionsFromLeftEdgeForP2() {
        space = new HexSearchSpace(board, false);

        HexState state = new HexState(board, new IntLocation(4, 0));
        Seq<HexTransition> transitions = space.legalTransitions(state);

        assertEquals("Unexpected transitions.",
                "[(row=3, column=0): 0], [(row=5, column=0): 0], " +
                        "[(row=4, column=1): 10], [(row=3, column=1): 10]",
                transitions.mkString(", "));
    }

    @Test
    public void testLegalTransitionsFromMiddleForP1WithSurrounding() {

        board.makeMove(TwoPlayerMove.createMove(3, 1, 0, new GamePiece(true)));
        board.makeMove(TwoPlayerMove.createMove(4, 2, 0, new GamePiece(false)));

        HexState state = new HexState(board, new IntLocation(4, 1));
        Seq<HexTransition> transitions = space.legalTransitions(state);

        assertEquals("Unexpected transitions.",
                        "[(row=3, column=1): 0], [(row=3, column=2): 10], [(row=5, column=1): 10]",
                transitions.mkString(", "));
    }

    @Test
    public void testLegalTransitionsFromMiddleForP2WithSurrounding() {

        board.makeMove(TwoPlayerMove.createMove(3, 1, 0, new GamePiece(true)));
        board.makeMove(TwoPlayerMove.createMove(4, 2, 0, new GamePiece(false)));

        space = new HexSearchSpace(board, false);

        HexState state = new HexState(board, new IntLocation(4, 1));
        Seq<HexTransition> transitions = space.legalTransitions(state);

        assertEquals("Unexpected transitions.",
                "[(row=4, column=2): 0], [(row=4, column=0): 0], [(row=5, column=0): 0], " +
                "[(row=3, column=2): 10], [(row=5, column=1): 10]",
                transitions.mkString(", "));
    }

    @Test
    public void testLegalTransitionsFromTopEdgeForP1WithSurrounding() {

        board.makeMove(TwoPlayerMove.createMove(3, 1, 5, new GamePiece(true)));
        board.makeMove(TwoPlayerMove.createMove(4, 1, 6, new GamePiece(false)));

        HexState state = new HexState(board, new IntLocation(0, 6));
        Seq<HexTransition> transitions = space.legalTransitions(state);

        assertEquals("Unexpected transitions.",
                "[(row=0, column=7): 0], " +
                        "[(row=0, column=5): 0], " +
                        "[(row=1, column=5): 10], " +
                        "[(row=1, column=6): 10]",
                transitions.mkString(", "));
    }

    @Test
    public void testLegalTransitionsFromLeftEdgeForP1WithSurrounding() {

        board.makeMove(TwoPlayerMove.createMove(3, 1, 0, new GamePiece(true)));
        board.makeMove(TwoPlayerMove.createMove(4, 2, 0, new GamePiece(false)));

        HexState state = new HexState(board, new IntLocation(4, 0));
        Seq<HexTransition> transitions = space.legalTransitions(state);

        assertEquals("Unexpected transitions.",
                "[(row=3, column=1): 0], [(row=4, column=1): 10]",
                transitions.mkString(", "));
    }

    @Test
    public void testLegalTransitionsFromLeftEdgeForP2WithSurrounding() {

        board.makeMove(TwoPlayerMove.createMove(3, 1, 0, new GamePiece(true)));
        board.makeMove(TwoPlayerMove.createMove(4, 2, 0, new GamePiece(false)));

        space = new HexSearchSpace(board, false);

        HexState state = new HexState(board, new IntLocation(4, 0));
        Seq<HexTransition> transitions = space.legalTransitions(state);

        assertEquals("Unexpected transitions.",
                "[(row=3, column=0): 0], [(row=5, column=0): 0], [(row=4, column=1): 10]",
                transitions.mkString(", "));
    }
}
