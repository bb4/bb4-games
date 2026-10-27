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
class NegaScoutConfigurations extends SearchOptionsConfigList {

  private val DEFAULT_USE_QUIESCENCE = false
  private val DEFAULT_QUIESCENT_LOOK_AHEAD = 6

  initialize()

  def initialize(): Unit = {
    add(new SearchOptionsConfig("NegaMax1", createNegaScoutSearchOptions(1)))
    add(new SearchOptionsConfig("Negamax2", createNegaScoutSearchOptions(2)))
    add(new SearchOptionsConfig("Negamax3", createNegaScoutSearchOptions(3)))
    add(new SearchOptionsConfig("Negamax4", createNegaScoutSearchOptions(4)))
    add(new SearchOptionsConfig("Negamax4Q", createNegaScoutSearchOptions(4, true)))
  }

  private def createNegaScoutSearchOptions(level: Int): SearchOptions[?, ?] =
    new SearchOptions(
      SearchStrategyType.NEGASCOUT,
      createBruteOptions(level, DEFAULT_USE_QUIESCENCE),
      createBestMoveOptions(), new MonteCarloSearchOptions())

  private def createNegaScoutSearchOptions(level: Int, useQiescence: Boolean): SearchOptions[?, ?] =
    new SearchOptions(
      SearchStrategyType.NEGASCOUT,
      createBruteOptions(level, useQiescence), createBestMoveOptions(), new MonteCarloSearchOptions())

  private def createBruteOptions(level: Int, useQuiescence: Boolean): BruteSearchOptions = {
    val bsOpts = new BruteSearchOptions(level, DEFAULT_QUIESCENT_LOOK_AHEAD)
    bsOpts.setQuiescence(useQuiescence)
    bsOpts
  }

  private def createBestMoveOptions(): BestMovesSearchOptions =
    new BestMovesSearchOptions(100, 20, 40)
}
