// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku

import com.barrybecker4.game.twoplayer.common.{TwoPlayerMove, TwoPlayerPlayerOptions}
import com.barrybecker4.game.twoplayer.common.search.options.{BestMovesSearchOptions, BruteSearchOptions, MonteCarloSearchOptions, SearchOptions}

import java.awt.Color

/**
 * Player options specific to GoMoku game.
 * @author Barry Becker
 */
class GoMokuPlayerOptions(name: String, color: Color) extends TwoPlayerPlayerOptions[TwoPlayerMove, GoMokuBoard](name, color) {

  override def createDefaultSearchOptions(): SearchOptions[TwoPlayerMove, GoMokuBoard] = {
    new SearchOptions[TwoPlayerMove, GoMokuBoard](
      new BruteSearchOptions(GoMokuPlayerOptions.DEFAULT_LOOK_AHEAD, GoMokuPlayerOptions.DEFAULT_LOOK_AHEAD + 2),
      new BestMovesSearchOptions(GoMokuPlayerOptions.DEFAULT_PERCENTAGE_BEST_MOVES,
        GoMokuPlayerOptions.DEFAULT_PERCENT_LESS_THAN_BEST_THRESH, GoMokuPlayerOptions.DEFAULT_MIN_BEST_MOVES
      ),
      new MonteCarloSearchOptions(4000, 1.0, 25))
  }
}

object GoMokuPlayerOptions {
  private val DEFAULT_LOOK_AHEAD = 6
  private val DEFAULT_PERCENT_LESS_THAN_BEST_THRESH = 70
  private val DEFAULT_PERCENTAGE_BEST_MOVES = 60
  private val DEFAULT_MIN_BEST_MOVES = 5
}
