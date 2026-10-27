// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.options

import com.barrybecker4.game.twoplayer.common.TwoPlayerPlayerOptions
import com.barrybecker4.game.twoplayer.common.search.options.{BestMovesSearchOptions, BruteSearchOptions, MonteCarloSearchOptions, SearchOptions}
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.move.GoMove

import java.awt.Color

/**
 * @author Barry Becker
 */
class GoPlayerOptions(
  name: String,
  color: Color,
  searchOptions: SearchOptions[GoMove, GoBoard]
) extends TwoPlayerPlayerOptions[GoMove, GoBoard](name, color, searchOptions):

  def this(name: String, color: Color) =
    this(name, color, GoPlayerOptions.defaultSearchOptions)

  def this(name: String, searchOptions: SearchOptions[GoMove, GoBoard]) =
    this(name, null, searchOptions)

  override def createDefaultSearchOptions(): SearchOptions[GoMove, GoBoard] =
    GoPlayerOptions.defaultSearchOptions

object GoPlayerOptions:
  private val DEFAULT_LOOK_AHEAD = 4
  private val DEFAULT_PERCENT_LESS_THAN_BEST_THRESH = 0
  private val DEFAULT_PERCENTAGE_BEST_MOVES = 60
  private val DEFAULT_MIN_BEST_MOVES = 6

  private def defaultSearchOptions: SearchOptions[GoMove, GoBoard] =
    val opts = new SearchOptions[GoMove, GoBoard](
      new BruteSearchOptions(DEFAULT_LOOK_AHEAD, 16),
      new BestMovesSearchOptions(
        DEFAULT_PERCENTAGE_BEST_MOVES,
        DEFAULT_PERCENT_LESS_THAN_BEST_THRESH,
        DEFAULT_MIN_BEST_MOVES
      ),
      new MonteCarloSearchOptions(200, 0.9, 10)
    )
    opts.setSearchStrategyMethod(SearchStrategyType.NEGASCOUT)
    opts.getBruteSearchOptions.setQuiescence(true)
    opts
