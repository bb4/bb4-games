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
public class UCTSearchConfigurations extends SearchOptionsConfigList {

    private static final int DEFAULT_LOOK_AHEAD = 4;
    private static final int DEFAULT_QUIESCENT_LOOK_AHEAD = 5;
    private static final int NUM_RAND_GAME_LOOK_AHEAD = 18;

    public UCTSearchConfigurations()  {
        initialize();
    }

    protected void initialize() {
        add(new SearchOptionsConfig("Negamax",  createNegaOptions(false)));
        //add(new SearchOptionsConfig("Negamax Q", createNegaOptions(true)));
        add(new SearchOptionsConfig("UCT winrate 500", createUCTOptions(500, MonteCarloSearchOptions.MaximizationStyle.WIN_RATE)));
        add(new SearchOptionsConfig("UCT winrate 1000", createUCTOptions(1000, MonteCarloSearchOptions.MaximizationStyle.WIN_RATE)));
        add(new SearchOptionsConfig("UCT winrate 2000", createUCTOptions(2000, MonteCarloSearchOptions.MaximizationStyle.WIN_RATE)));
        add(new SearchOptionsConfig("UCT nmoves 500", createUCTOptions(500, MonteCarloSearchOptions.MaximizationStyle.NUM_VISITS)));
        add(new SearchOptionsConfig("UCT nmoves 1000", createUCTOptions(1000, MonteCarloSearchOptions.MaximizationStyle.NUM_VISITS)));
        add(new SearchOptionsConfig("UCT nmoves 2000", createUCTOptions(2000, MonteCarloSearchOptions.MaximizationStyle.NUM_VISITS)));
    }


    private SearchOptions createNegaOptions(boolean useQuiescence) {
        BruteSearchOptions bruteOpts = new BruteSearchOptions(DEFAULT_LOOK_AHEAD, DEFAULT_QUIESCENT_LOOK_AHEAD);
        bruteOpts.setQuiescence(useQuiescence);
        return new SearchOptions(SearchStrategyType.NEGAMAX,
                             bruteOpts,
                             new BestMovesSearchOptions(90, 20, 50),
                             new MonteCarloSearchOptions());
    }

    private SearchOptions createUCTOptions(int maxSimulations, MonteCarloSearchOptions.MaximizationStyle sytle) {
        return new SearchOptions(SearchStrategyType.UCT,
                             new BruteSearchOptions(),
                             new BestMovesSearchOptions(),
                             new MonteCarloSearchOptions(maxSimulations, 0.5, NUM_RAND_GAME_LOOK_AHEAD));
    }

}
