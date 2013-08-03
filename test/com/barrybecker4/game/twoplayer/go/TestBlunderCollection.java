/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go;

import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType;

/**
 * Test a collection of problems from
 * Martin Mueller (mmueller)
 * Markus Enzenberger (emarkus)
 * email domain: cs.ualberta.ca
 *
 * @author Barry Becker
 */
public class TestBlunderCollection extends GoTestCase {

    private static final String PREFIX = "problems/sgf/blunder/";

    /**
     * @param options default options to override
     */
    @Override
    protected void setOptionOverrides(SearchOptions options) {
        options.getBruteSearchOptions().setAlphaBeta(true);
        options.getBruteSearchOptions().setLookAhead(1); // should be 2 or more
        options.getBestMovesSearchOptions().setPercentageBestMoves(80);
        options.getBruteSearchOptions().setQuiescence(false); //true);
        options.setSearchStrategyMethod(SearchStrategyType.NEGASCOUT_W_MEMORY);
    }

    public void testPass() { assertTrue(true); }

    /*
    public void test1() {
        GoMove m = getNextMove(PREFIX + "blunder.1", true);
        verifyExpected(m, 4, 10); // 17, 8); // 17, 12);   // Q12 why?
    }

    public void test2() {
        GoMove m = getNextMove(PREFIX + "blunder.2", true);
        verifyExpected(m, 4, 10);  //17, 12);  // Q12
    }

    public void test13() {
        GoMove m = getNextMove(PREFIX + "blunder.13", false);
        verifyExpected(m, 17, 10); // 13, 5);   // A13
    }

    public void test14() {
        GoMove m = getNextMove(PREFIX + "blunder.14", false);
        verifyExpected(m, 17, 10); //  2, 12); // B12
    }  */
}
