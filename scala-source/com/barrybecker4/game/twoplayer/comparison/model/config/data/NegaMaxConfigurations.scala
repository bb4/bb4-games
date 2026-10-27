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
class NegaMaxConfigurations extends SearchOptionsConfigList {

  private val DEFAULT_QUIESCENT_LOOK_AHEAD = 5

  initialize()

  def initialize(): Unit = {
    add(new SearchOptionsConfig("NegaMax2", createNegaMaxSearchOptions(2)))
    add(new SearchOptionsConfig("Negamax3", createNegaMaxSearchOptions(3)))
  }

  private def createNegaMaxSearchOptions(level: Int): SearchOptions[?, ?] =
    new SearchOptions(
      SearchStrategyType.NEGAMAX,
      createBruteOptions(level), createBestMoveOptions(), new MonteCarloSearchOptions())

  private def createBruteOptions(level: Int): BruteSearchOptions =
    new BruteSearchOptions(level, DEFAULT_QUIESCENT_LOOK_AHEAD)

  private def createBestMoveOptions(): BestMovesSearchOptions =
    new BestMovesSearchOptions(100, 20, 40)
}
