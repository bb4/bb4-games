/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy.example;

import com.barrybecker4.game.twoplayer.common.search.Searchable;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;
import com.barrybecker4.game.twoplayer.common.search.examples.GameTreeExample;
import com.barrybecker4.game.twoplayer.common.search.examples.TwoLevelQuiescentExample;
import com.barrybecker4.game.twoplayer.common.search.strategy.NegaMaxMemoryStrategy;
import com.barrybecker4.game.twoplayer.common.search.strategy.testcase.SearchResult;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Test negamax memory strategy independent of any particular game implementation.
 *
 * @author Barry Becker
 */
public class NegaMaxMemoryStrategyTest extends NegaMaxStrategyTest {

    @Override
    protected SearchStrategy<TwoPlayerMoveStub> createSearchStrategy(Searchable searchable, ParameterArray weights) {
        return new NegaMaxMemoryStrategy(searchable, weights);
    }

    // suspect result
    @Override
    public void testTwoLevelQuiescenseABPlayer2Search() {
        bruteSearchOptions.setLookAhead(2);
        bruteSearchOptions.setQuiescence(true);
        bruteSearchOptions.setAlphaBeta(true);
        GameTreeExample eg = new TwoLevelQuiescentExample(false);
        verifyResult(eg, getTwoLevelQuiescenseABPlayer2Result());
    }

    @Override
    protected SearchResult getTwoLevelQuiescenseABPlayer1Result() {
        return new SearchResult("0", 3, 4);
    }
    // Suspect result. Should be "1" 4 or close to it.
    @Override
    protected SearchResult getTwoLevelQuiescenseABPlayer2Result() {
        return new SearchResult("0", 2, 9);  // seems wrong
    }

    @Override
    protected SearchResult getThreeLevelPlayer1WithABResult() {
        return new SearchResult( "0", -4, 8);
    }
    @Override
    protected SearchResult getThreeLevelPlayer2WithABResult() {
        return new SearchResult( "0", -5, 5);
    }

    // seems wrong
    @Override
    protected SearchResult getPruneFourLevelWithABSearchPlayer1() {
        return new SearchResult( "0", 3, 8);
    }
    // seems wrong
    @Override
    protected SearchResult getPruneFourLevelWithABSearchPlayer2() {
        return new SearchResult( "0", 2, 12);
    }

    @Override
    protected SearchResult getLadderMax4QuiescensePlayer2Result() {
        return new SearchResult("1", 4, 15);
    }

    @Override
    protected SearchResult getPruneTwoLevelWithABSearchPlayer1() {
        return new SearchResult( "0", 5, 3);
    }
    @Override
    protected SearchResult getPruneTwoLevelWithABSearchPlayer2() {
        return new SearchResult( "1", 4, 4);
    }

    @Override
    protected SearchResult getFourLevelABPlayer1Result() {
        return new SearchResult("0", 6, 9);
    }
    @Override
    protected SearchResult getFourLevelABPlayer2Result() {
        return new SearchResult("1", 14, 12);
    }

}