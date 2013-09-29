// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.model.data;

import com.barrybecker4.game.twoplayer.common.search.options.BestMovesSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.BruteSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.MonteCarloSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.barrybecker4.game.twoplayer.comparison.model.SearchOptionsConfig;
import com.barrybecker4.game.twoplayer.comparison.model.SearchOptionsConfigList;

/**
 * A default list of search config options so we do not have to enter them every time.
 *
 * @author Barry Becker
 */
public class DefaultSearchConfigurations extends SearchOptionsConfigList {

    private static final int DEFAULT_LOOK_AHEAD = 3;
    private static final int DEFAULT_QUIESCENT_LOOK_AHEAD = 5;

    public DefaultSearchConfigurations()  {
        initialize();
    }

    protected void initialize() {
        add(new SearchOptionsConfig("Minimax", new SearchOptions(SearchStrategyType.MINIMAX)));
        add(new SearchOptionsConfig("Negamax", new SearchOptions(SearchStrategyType.NEGAMAX)));
        add(new SearchOptionsConfig("Negascout", new SearchOptions(SearchStrategyType.NEGASCOUT)));
        //add(new SearchOptionsConfig("Negamax w/mem", new SearchOptions(SearchStrategyType.NEGAMAX_W_MEMORY)));
        //add(new SearchOptionsConfig("Negascout w/mem", new SearchOptions(SearchStrategyType.NEGASCOUT_W_MEMORY)));
        add(new SearchOptionsConfig("UCT", new SearchOptions(SearchStrategyType.UCT)));
        //add(new SearchOptionsConfig("MTD Negamax", new SearchOptions(SearchStrategyType.MTD_NEGAMAX)));
        add(new SearchOptionsConfig("MTD Negascout", new SearchOptions(SearchStrategyType.MTD_NEGASCOUT)));
    }

    private BruteSearchOptions createBruteOptions() {
        return new BruteSearchOptions(DEFAULT_LOOK_AHEAD, DEFAULT_QUIESCENT_LOOK_AHEAD);
    }

    private BestMovesSearchOptions createBestMoveOptions() {
        return new BestMovesSearchOptions(100, 20, 40);
    }

    private MonteCarloSearchOptions createMonteCarloOptions() {
        return new MonteCarloSearchOptions(200, 0.9, 10);
    }

}
