/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy.example;

import com.barrybecker4.game.twoplayer.common.search.Searchable;
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;
import com.barrybecker4.game.twoplayer.common.search.strategy.UctStrategy;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Test minimax strategy independent of any particular game implementation.
 *
 * @author Barry Becker
 */
public class UctStrategyTest extends MonteCarloSearchStrategyTst {

    @Override
    protected SearchStrategy<TwoPlayerMoveStub> createSearchStrategy(Searchable searchable, ParameterArray weights) {
        return new UctStrategy(searchable, weights);
    }

}

