// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.model.config.data

import com.barrybecker4.game.twoplayer.common.search.options.{BestMovesSearchOptions, BruteSearchOptions, MaximizationStyle, MonteCarloSearchOptions, SearchOptions}
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType
import com.barrybecker4.game.twoplayer.comparison.model.config.{SearchOptionsConfig, SearchOptionsConfigList}

/**
 * A default list of search config options so we do not have to enter them every time.
 *
 * @author Barry Becker
 */
class UCTSearchConfigurations extends SearchOptionsConfigList {

  private val DEFAULT_LOOK_AHEAD = 4
  private val DEFAULT_QUIESCENT_LOOK_AHEAD = 5
  private val NUM_RAND_GAME_LOOK_AHEAD = 18

  initialize()

  def initialize(): Unit = {
    add(new SearchOptionsConfig("Negamax", createNegaOptions(false)))
    add(new SearchOptionsConfig("UCT winrate 500", createUCTOptions(500, MaximizationStyle.WIN_RATE)))
    add(new SearchOptionsConfig("UCT winrate 1000", createUCTOptions(1000, MaximizationStyle.WIN_RATE)))
    add(new SearchOptionsConfig("UCT winrate 2000", createUCTOptions(2000, MaximizationStyle.WIN_RATE)))
    add(new SearchOptionsConfig("UCT nmoves 500", createUCTOptions(500, MaximizationStyle.NUM_VISITS)))
    add(new SearchOptionsConfig("UCT nmoves 1000", createUCTOptions(1000, MaximizationStyle.NUM_VISITS)))
    add(new SearchOptionsConfig("UCT nmoves 2000", createUCTOptions(2000, MaximizationStyle.NUM_VISITS)))
  }

  private def createNegaOptions(useQuiescence: Boolean): SearchOptions[?, ?] = {
    val bruteOpts = new BruteSearchOptions(DEFAULT_LOOK_AHEAD, DEFAULT_QUIESCENT_LOOK_AHEAD)
    bruteOpts.setQuiescence(useQuiescence)
    new SearchOptions(
      SearchStrategyType.NEGAMAX,
      bruteOpts,
      new BestMovesSearchOptions(90, 50, 20),
      new MonteCarloSearchOptions())
  }

  private def createUCTOptions(maxSimulations: Int, style: MaximizationStyle): SearchOptions[?, ?] =
    new SearchOptions(
      SearchStrategyType.UCT,
      new BruteSearchOptions(),
      new BestMovesSearchOptions(),
      new MonteCarloSearchOptions(maxSimulations, 0.5, NUM_RAND_GAME_LOOK_AHEAD))
}
