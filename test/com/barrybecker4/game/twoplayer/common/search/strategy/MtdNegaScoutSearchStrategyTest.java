/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy;

import com.barrybecker4.game.twoplayer.common.search.Searchable;
import com.barrybecker4.game.twoplayer.common.search.examples.OneLevelGameTreeExample;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Test mtd strategy independent of any particular game implementation.
 *
 * @author Barry Becker
 */
public class MtdNegaScoutSearchStrategyTest extends NegaScoutMemorySearchStrategyTest {

    @Override
    protected SearchStrategy createSearchStrategy(Searchable searchable, ParameterArray weights) {
        return new MtdStrategy(new NegaScoutMemoryStrategy(searchable, weights));
    }

    /**
     * Look ahead one level and get the best move.
     */
    @Override
    public void testOneLevelLookAheadPlayer1Search() {
        bruteSearchOptions.setLookAhead(1);
        verifyResult(new OneLevelGameTreeExample(true, getEvaluationPerspective()),
                getOneLevelLookAheadPlayer1Result());
    }


    @Override
    protected SearchResult getOneLevelLookAheadPlayer1Result() {
        //return new SearchResult("0", -2, 4);
        return new SearchResult("0", 0, 1);
    }

    @Override
    protected SearchResult getOneLevelLookAheadPlayer2Result() {
        //return new SearchResult("0", -8, 4);
        return new SearchResult("0", 0, 1);
    }


    @Override
    protected SearchResult getTwoLevelPlayer1Result() {
        //return new SearchResult("0", 7, 12);
        return new SearchResult("0", 0, 2);
    }

    @Override
    protected SearchResult getTwoLevelPlayer2Result() {
        return new SearchResult("0", 0, 2);
    }

    @Override
    protected SearchResult getTwoLevelQuiescensePlayer1Result() {
        return new SearchResult("0", 0, 3);
    }
    @Override
    protected SearchResult getTwoLevelQuiescensePlayer2Result() {
        return new SearchResult("0", 0, 3);
    }
    @Override
    protected SearchResult getTwoLevelQuiescenseABPlayer1Result() {
        return new SearchResult("0", 0, 3);
    }
    @Override
    protected SearchResult getTwoLevelQuiescenseABPlayer2Result() {
        return new SearchResult("0", 0, 3);
    }

    @Override
    protected SearchResult getLadderMax3QuiescensePlayer1Result() {
        return new SearchResult("0",  0, 3);
    }
    @Override
    protected SearchResult getLadderMax3QuiescensePlayer2Result() {
        return new SearchResult("0", 0, 3);
    }
    @Override
    protected SearchResult getLadderMax4QuiescensePlayer1Result() {
        return new SearchResult("0", 0, 3);
    }
    @Override
    protected SearchResult getLadderMax4QuiescensePlayer2Result() {
        return new SearchResult("0", 0, 3);
    }


    @Override
    protected SearchResult getPruneTwoLevelWithoutABResultPlayer1() {
        //return new SearchResult("0", -5, 12);
        return new SearchResult("0", 0, 2);
    }


    @Override
    protected SearchResult getPruneTwoLevelWithABSearchPlayer1() {
        //return new SearchResult("0", -5, 7);
        return new SearchResult("0", 0, 2);
    }

    @Override
    protected SearchResult getPruneTwoLevelWithABSearchPlayer2() {
        //return new SearchResult("1", 4, 9);
        return new SearchResult("0", 0, 2);
    }

    @Override
    protected SearchResult getPruneFourLevelWithABSearchPlayer1() {
        return new SearchResult( "0", 0, 4);
    }
    @Override
    protected SearchResult getPruneFourLevelWithABSearchPlayer2() {
        return new SearchResult( "0", 0, 4);
    }

    @Override
    protected SearchResult getThreeLevelPlayer1Result() {
        //return new SearchResult("0", -5, 28);
        return new SearchResult("0", 0, 3);
    }

    @Override
    protected SearchResult getThreeLevelPlayer2Result() {
        return new SearchResult("0", 0, 3);
    }

    @Override
    protected SearchResult getThreeLevelPlayer1WithABResult() {
        //return new SearchResult("0", -5, 15);
        return new SearchResult("0", 0, 3);
    }
    @Override
    protected SearchResult getThreeLevelPlayer2WithABResult() {
        //return new SearchResult("0", -5, 15);
        return new SearchResult("0", 0, 3);
    }
    @Override
    protected SearchResult getFourLevelPlayer1Result() {
        return new SearchResult("0", 0, 4);
    }
    @Override
    protected SearchResult getFourLevelPlayer2Result() {
        return new SearchResult("0", 0, 4);
    }
    @Override
    protected SearchResult getFourLevelABPlayer1Result() {
        return new SearchResult("0", 0, 4);
    }
    @Override
    protected SearchResult getFourLevelABPlayer2Result() {
        return new SearchResult("0", 0, 4);
    }
}