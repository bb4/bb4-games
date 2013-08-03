/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy;

import com.barrybecker4.game.twoplayer.common.search.Searchable;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Test negascout memory strategy independent of any particular game implementation.
 *
 * @author Barry Becker
 */
public class NegaScoutMemorySearchStrategyTest extends NegaScoutSearchStrategyTest {

    @Override
    protected SearchStrategy createSearchStrategy(Searchable searchable, ParameterArray weights) {
        return new NegaScoutMemoryStrategy(searchable, weights);
    }

    @Override
    protected SearchResult getTwoLevelPlayer1Result() {
        return new SearchResult("1", 8, 6);
    }

    @Override
    protected SearchResult getTwoLevelQuiescensePlayer2Result() {
        return new SearchResult("1", 4, 9);
    }

    @Override
    protected SearchResult getPruneTwoLevelWithABSearchPlayer2() {
        return new SearchResult( "1", 4, 6);
    }

    // seems wrong
    @Override
    protected SearchResult getPruneFourLevelWithABSearchPlayer2() {
        return new SearchResult( "1", 2, 18);
    }

    @Override
    protected SearchResult getTwoLevelQuiescenseABPlayer2Result() {
        return new SearchResult("1", 4, 9);
    }

    @Override
    protected SearchResult getLadderMax3QuiescensePlayer2Result() {
        return new SearchResult("1", 4, 9);
    }

    @Override
    protected SearchResult getLadderMax4QuiescensePlayer2Result() {
        return new SearchResult("1", 4, 9);
    }

    @Override
    protected SearchResult getFourLevelPlayer1Result() {
        return new SearchResult("0", 33, 20);
    }
    @Override
    protected SearchResult getFourLevelPlayer2Result() {
        return new SearchResult("1", 22, 22);
    }

    @Override
    protected SearchResult getFourLevelABPlayer1Result() {
        return new SearchResult("0", 33, 20);
    }

    @Override
    protected SearchResult getFourLevelABPlayer2Result() {
        return new SearchResult("1", 22, 22);
    }

}