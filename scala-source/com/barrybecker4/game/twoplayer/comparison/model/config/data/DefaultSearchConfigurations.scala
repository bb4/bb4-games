// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.model.config.data

import com.barrybecker4.game.twoplayer.common.search.options.{BestMovesSearchOptions, BruteSearchOptions, MonteCarloSearchOptions, SearchOptions}
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType
import com.barrybecker4.game.twoplayer.comparison.model.config.{SearchOptionsConfig, SearchOptionsConfigList}

/**
 * A default list of search config options so we do not have to enter them every time.
 *
 * @author Barry Becker
 */
class DefaultSearchConfigurations extends SearchOptionsConfigList {

  initialize()

  def initialize(): Unit = {
    add(new SearchOptionsConfig("Minimax", new SearchOptions(SearchStrategyType.MINIMAX)))
    add(new SearchOptionsConfig("Negamax", new SearchOptions(SearchStrategyType.NEGAMAX)))
    add(new SearchOptionsConfig("Negascout", new SearchOptions(SearchStrategyType.NEGASCOUT)))
    add(new SearchOptionsConfig("UCT", new SearchOptions(SearchStrategyType.UCT)))
    add(new SearchOptionsConfig("MTD Negascout", new SearchOptions(SearchStrategyType.MTD_NEGASCOUT)))
  }
}
