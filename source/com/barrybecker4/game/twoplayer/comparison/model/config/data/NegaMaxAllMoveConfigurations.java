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
 * Test negamax lookahead configurations where we always consider all possible moves.
 * Good for testing TicTacToe or Pente.
 *
 * @author Barry Becker
 */
public class NegaMaxAllMoveConfigurations extends SearchOptionsConfigList {

    private static final int DEFAULT_QUIESCENT_LOOK_AHEAD = 5;

    public NegaMaxAllMoveConfigurations()  {
        initialize();
    }

    protected void initialize() {
        add(new SearchOptionsConfig("NegaMax2", createNegaMaxSearchOptions(2)));
        add(new SearchOptionsConfig("Negamax3", createNegaMaxSearchOptions(3)));
        add(new SearchOptionsConfig("Negamax4", createNegaMaxSearchOptions(4)));
        add(new SearchOptionsConfig("Negamax5", createNegaMaxSearchOptions(5)));
    }

    private SearchOptions createNegaMaxSearchOptions(int level)  {
        return new SearchOptions(SearchStrategyType.NEGAMAX,
                createBruteOptions(level), createBestMoveOptions(), new MonteCarloSearchOptions());
    }

    private BruteSearchOptions createBruteOptions(int level) {
        return new BruteSearchOptions(level, DEFAULT_QUIESCENT_LOOK_AHEAD);
    }

    private BestMovesSearchOptions createBestMoveOptions() {
        return new BestMovesSearchOptions(100, 100, 40);
    }
}
