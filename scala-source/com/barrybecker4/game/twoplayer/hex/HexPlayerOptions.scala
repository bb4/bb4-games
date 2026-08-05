package com.barrybecker4.game.twoplayer.hex

import com.barrybecker4.game.twoplayer.common.search.options.{BestMovesSearchOptions, BruteSearchOptions, MonteCarloSearchOptions, SearchOptions}
import com.barrybecker4.game.twoplayer.common.{TwoPlayerMove, TwoPlayerPlayerOptions}

import java.awt.Color

class HexPlayerOptions(name: String, color: Color) 
    extends TwoPlayerPlayerOptions[TwoPlayerMove, HexBoard](name, color):

  override def createDefaultSearchOptions(): SearchOptions[TwoPlayerMove, HexBoard] =
    SearchOptions[TwoPlayerMove, HexBoard](
      BruteSearchOptions(HexPlayerOptions.DEFAULT_LOOK_AHEAD),
      BestMovesSearchOptions(
        HexPlayerOptions.DEFAULT_PERCENTAGE_BEST_MOVES,
        HexPlayerOptions.DEFAULT_PERCENTAGE_LESS_THAN_BEST_THRESH,
        HexPlayerOptions.DEFAULT_MIN_BEST_MOVES
      ),
      MonteCarloSearchOptions(200, 1.0, 8)
    )

object HexPlayerOptions:
  /** initial look ahead factor. */
  private val DEFAULT_LOOK_AHEAD = 3

  /** for any given ply never consider more than this percentage of the top moves. */
  private val DEFAULT_PERCENTAGE_BEST_MOVES = 100

  /** Don't consider moves that are less than this percentage less than the best move. */
  private val DEFAULT_PERCENTAGE_LESS_THAN_BEST_THRESH = 100

  /** for any given ply never consider less than this many moves. */
  private val DEFAULT_MIN_BEST_MOVES = 4
