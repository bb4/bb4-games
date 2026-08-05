// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.tictactoe

import com.barrybecker4.game.twoplayer.common.{TwoPlayerMove, TwoPlayerPlayerOptions}
import com.barrybecker4.game.twoplayer.common.search.options.{BestMovesSearchOptions, BruteSearchOptions, MonteCarloSearchOptions, SearchOptions}

import java.awt.Color

/**
 * @author Barry Becker
 */
class TicTacToePlayerOptions(name: String, color: Color) extends TwoPlayerPlayerOptions[TwoPlayerMove, TicTacToeBoard](name, color) {

  override def createDefaultSearchOptions(): SearchOptions[TwoPlayerMove, TicTacToeBoard] = {
    new SearchOptions(
      new BruteSearchOptions(TicTacToePlayerOptions.DEFAULT_LOOK_AHEAD),
      new BestMovesSearchOptions(
        TicTacToePlayerOptions.DEFAULT_PERCENTAGE_BEST_MOVES,
        TicTacToePlayerOptions.DEFAULT_PERCENTAGE_LESS_THAN_BEST_THRESH,
        TicTacToePlayerOptions.DEFAULT_MIN_BEST_MOVES
      ),
      new MonteCarloSearchOptions(200, 1.0, 8)
    )
  }
}

object TicTacToePlayerOptions {
  /** initial look ahead factor. */
  private val DEFAULT_LOOK_AHEAD = 4

  /** for any given ply never consider more than this percentage of the top moves. */
  private val DEFAULT_PERCENTAGE_BEST_MOVES = 100

  /** Don't consider moves that are less than this percentage less than the best move. */
  private val DEFAULT_PERCENTAGE_LESS_THAN_BEST_THRESH = 100

  /** for any given ply never consider less than this many moves. */
  private val DEFAULT_MIN_BEST_MOVES = 4
}
