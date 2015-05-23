/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy.example;

import com.barrybecker4.game.twoplayer.common.search.Searchable;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;
import com.barrybecker4.game.twoplayer.common.search.strategy.NegaMaxStrategy;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;
import com.barrybecker4.game.twoplayer.common.search.strategy.testcase.SearchResult;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Test negamax strategy independent of any particular game implementation.
 *
 * @author Barry Becker
 */
public class NegaMaxStrategyTest extends AbstractBruteSearchStrategyTst {

    @Override
    protected SearchStrategy<TwoPlayerMoveStub> createSearchStrategy(Searchable searchable, ParameterArray weights) {
        return new NegaMaxStrategy(searchable, weights);
    }

    @Override
    protected SearchResult getPruneFourLevelWithABSearchPlayer1() {
        return new SearchResult( "0", 3, 16);
    }

}