/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis;

import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.twoplayer.go.GoTestCase;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap;
import com.barrybecker4.game.twoplayer.go.options.GoWeights;

/**
 * Verify that we calculate the expected worth for a given board position.
 * @author Barry Becker
 */
public abstract class WorthCalculatorBase extends GoTestCase {

    protected static final String PREFIX = "board/";

    protected static final GoWeights WEIGHTS = new GoWeights();

    /** instance under test. */
    protected WorthCalculator calculator;


    /**
     * common initialization for all go test cases.
     * Override setOptionOverrides if you want different search parameters.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        GoBoard board = getBoard();
        GroupAnalyzerMap analyzerMap = new GroupAnalyzerMap();
        calculator = new WorthCalculator(board, analyzerMap);
    }

    protected void verifyWorth(String file, int expWorth) throws Exception {

        restore(PREFIX  + file);

        Move move = controller_.getLastMove();
        int actWorth = calculator.worth(move, WEIGHTS.getDefaultWeights());

        assertEquals("Unexpected worth.", expWorth, actWorth);
    }

    /**
     * If we arrive at the same exact board position from two different paths,
     * we should calculate the same worth value.
     */
    protected void compareWorths(String game1, String game2, int expWorth) throws Exception {

        restore(PREFIX  + game1);

        Move move = controller_.getLastMove();
        int actWorthA = calculator.worth(move, WEIGHTS.getDefaultWeights());

        controller_.reset();
        restore(PREFIX + game2);

        move = controller_.getLastMove();
        int actWorthB = calculator.worth(move, WEIGHTS.getDefaultWeights());

        assertEquals("Unexpected worth for A.", expWorth, actWorthA);
        assertEquals("Unexpected worth for B.", expWorth, actWorthB);
    }
}
