// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.mancala

import com.barrybecker4.game.twoplayer.common.TwoPlayerPlayerOptions
import com.barrybecker4.game.twoplayer.common.search.options.{BestMovesSearchOptions, BruteSearchOptions, MonteCarloSearchOptions, SearchOptions}
import java.awt.Color

/**
  * Player options specific to Mancala game.
  * @author Barry Becker
  */
class MancalaPlayerOptions(name: String, color: Color) extends TwoPlayerPlayerOptions[com.barrybecker4.game.twoplayer.mancala.move.MancalaMove, com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard](name, color):

  override def createDefaultSearchOptions(): SearchOptions[com.barrybecker4.game.twoplayer.mancala.move.MancalaMove, com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard] =
    new SearchOptions(
      BruteSearchOptions(MancalaPlayerOptions.DEFAULT_LOOK_AHEAD, MancalaPlayerOptions.DEFAULT_LOOK_AHEAD + 2),
      BestMovesSearchOptions(
        MancalaPlayerOptions.DEFAULT_PERCENTAGE_BEST_MOVES,
        MancalaPlayerOptions.DEFAULT_PERCENT_LESS_THAN_BEST_THRESH,
        MancalaPlayerOptions.DEFAULT_MIN_BEST_MOVES
      ),
      MonteCarloSearchOptions(4000, 1.0, 25)
    )

object MancalaPlayerOptions:
  /** initial look ahead factor. */
  private val DEFAULT_LOOK_AHEAD = 1

  /** Don't consider moves that are less than this percentage less than the best move. */
  private val DEFAULT_PERCENT_LESS_THAN_BEST_THRESH = 70

  /** for any given ply never consider more than BEST_PERCENTAGE of the top moves. not used. */
  private val DEFAULT_PERCENTAGE_BEST_MOVES = 60

  /** for any given ply never consider less than this many moves. */
  private val DEFAULT_MIN_BEST_MOVES = 4
