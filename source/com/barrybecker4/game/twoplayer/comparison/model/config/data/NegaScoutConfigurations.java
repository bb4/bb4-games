// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.model.config.data;

import com.barrybecker4.game.twoplayer.common.search.options.BestMovesSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.BruteSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.MonteCarloSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.barrybecker4.game.twoplayer.comparison.model.config.SearchOptionsConfig;
import com.barrybecker4.game.twoplayer.comparison.model.config.SearchOptionsConfigList;

/**
 * A default list of search config options so we do not have to enter them every time.
 *
 * @author Barry Becker
 */
public class NegaScoutConfigurations extends SearchOptionsConfigList {

    private static final boolean DEFAULT_USE_QUIESCENCE = false;
    private static final int DEFAULT_QUIESCENT_LOOK_AHEAD = 6;

    public NegaScoutConfigurations()  {
        initialize();
    }

    protected void initialize() {
        add(new SearchOptionsConfig("NegaMax1", createNegaScoutSearchOptions(1)));
        add(new SearchOptionsConfig("Negamax2", createNegaScoutSearchOptions(2)));
        add(new SearchOptionsConfig("Negamax3", createNegaScoutSearchOptions(3)));
        add(new SearchOptionsConfig("Negamax4", createNegaScoutSearchOptions(4)));
        add(new SearchOptionsConfig("Negamax4Q", createNegaScoutSearchOptions(4, true)));
    }

    private SearchOptions createNegaScoutSearchOptions(int level)  {
        return new SearchOptions(SearchStrategyType.NEGASCOUT,
                createBruteOptions(level, DEFAULT_USE_QUIESCENCE),
                createBestMoveOptions(), new MonteCarloSearchOptions());
    }

    private SearchOptions createNegaScoutSearchOptions(int level, boolean useQiescence)  {
        return new SearchOptions(SearchStrategyType.NEGASCOUT,
                createBruteOptions(level, useQiescence), createBestMoveOptions(), new MonteCarloSearchOptions());
    }

    private BruteSearchOptions createBruteOptions(int level, boolean useQuiescence) {
        BruteSearchOptions bsOpts = new BruteSearchOptions(level, DEFAULT_QUIESCENT_LOOK_AHEAD);
        bsOpts.setQuiescence(useQuiescence);
        return bsOpts;
    }

    private BestMovesSearchOptions createBestMoveOptions() {
        return new BestMovesSearchOptions(100, 20, 40);
    }

}
