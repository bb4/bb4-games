// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade

import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.common.TwoPlayerPlayerOptions
import com.barrybecker4.game.twoplayer.common.search.options.{BestMovesSearchOptions, BruteSearchOptions, MonteCarloSearchOptions, SearchOptions}
import java.awt.Color

object BlockadePlayerOptions:
  private val DEFAULT_LOOK_AHEAD = 3
  private val DEFAULT_PERCENT_LESS_THAN_BEST_THRESH = 50
  private val DEFAULT_PERCENTAGE_BEST_MOVES = 0
  private val DEFAULT_MIN_BEST_MOVES = 5

class BlockadePlayerOptions(name: String, color: Color) extends TwoPlayerPlayerOptions[BlockadeMove, BlockadeBoard](name, color):

  override def createDefaultSearchOptions(): SearchOptions[BlockadeMove, BlockadeBoard] =
    SearchOptions(
      BruteSearchOptions(BlockadePlayerOptions.DEFAULT_LOOK_AHEAD),
      BestMovesSearchOptions(
        BlockadePlayerOptions.DEFAULT_PERCENTAGE_BEST_MOVES,
        BlockadePlayerOptions.DEFAULT_PERCENT_LESS_THAN_BEST_THRESH,
        BlockadePlayerOptions.DEFAULT_MIN_BEST_MOVES
      ),
      MonteCarloSearchOptions(50, 1.0, 10)
    )
