/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis;

import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.twoplayer.go.board.move.GoMove;

/**
 * Verify that we calculate the expected worth for a given board position.
 * @author Barry Becker
 */
public class TestWorthCalculator5 extends WorthCalculatorBase {

    @Override
    protected int getBoardSize() {
        return 5;
    }

    /** verify that we get the expected worth value. */
    public void testFindSimpleWorth() throws Exception {
        verifyWorth("worth5x5", 30); //33);
    }

    /** verify that we get the expected worth value after a move redo. */
    public void testFindWorthAfterRedo() throws Exception {

        restore(PREFIX  + "worth5x5");

        GoMove move = controller_.undoLastMove();
        controller_.makeMove(move);

        int actWorth = calculator.worth(move, WEIGHTS.getDefaultWeights());

        assertEquals("Unexpected worth.", 30, actWorth);
    }

    /**
     * If we arrive at the same exact board position from two different paths,
     * we should calculate the same worth value.
     */
    public void testSamePositionFromDifferentPathsEqual() throws Exception {

        compareWorths("worth5x5_A", "worth5x5_B", -64); //-70); // -61);
    }

}
