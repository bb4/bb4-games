/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.pattern

import com.barrybecker4.game.common.GameWeights

/**
 * These weights determine how the computer values each pattern
 * if only one computer is playing, then only one of the weights arrays is used.
 *
 * These weights determine how the computer values features of the board
 * if only one computer is playing, then only one of the weights arrays is used.
 * use these weights if no others are provided.
 *
 * @author Barry Becker
 */
class GoMokuWeights(weights: Array[Double]) extends GameWeights(
  weights, GoMokuWeights.MIN_WEIGHTS, GoMokuWeights.MAX_WEIGHTS,
  GoMokuWeights.WEIGHT_SHORT_DESCRIPTIONS, GoMokuWeights.WEIGHT_DESCRIPTIONS
) {

  def this() = this(GoMokuWeights.DEFAULT_WEIGHTS)
}

object GoMokuWeights {
  val JEOPARDY_WEIGHT = 80

  private val DEFAULT_WEIGHTS = Array[Double](
    0.0, 0.0, 0.0, 0.5, 2.0, 8.0,
    JEOPARDY_WEIGHT, JEOPARDY_WEIGHT + 7.0, 1.2 * GameWeights.ASSUMED_WINNING_VALUE,
    2.1 * GameWeights.ASSUMED_WINNING_VALUE, 2.1 * GameWeights.ASSUMED_WINNING_VALUE, 3 * GameWeights.ASSUMED_WINNING_VALUE
  )

  private val MAX_WEIGHTS = Array[Double](
    10.0, 10.0, 10.0, 20.0, JEOPARDY_WEIGHT / 2, JEOPARDY_WEIGHT,
    2 * JEOPARDY_WEIGHT, 2 * JEOPARDY_WEIGHT, 2 * GameWeights.ASSUMED_WINNING_VALUE,
    3 * GameWeights.ASSUMED_WINNING_VALUE, 4 * GameWeights.ASSUMED_WINNING_VALUE, 5 * GameWeights.ASSUMED_WINNING_VALUE
  )

  private val MIN_WEIGHTS = Array[Double](
    0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 10.0,
    GameWeights.ASSUMED_WINNING_VALUE, GameWeights.ASSUMED_WINNING_VALUE, GameWeights.ASSUMED_WINNING_VALUE
  )

  private val WEIGHT_SHORT_DESCRIPTIONS = Array(
    "1a weight", "1b weight", "1c weight", "2a weight",
    "2b weight", "3a weight", "3b weight", "4a  weight",
    "5 weight", "6 weight", "7 weight", "8 weight"
  )

  private val WEIGHT_DESCRIPTIONS = Array(
    "Open ended two in a row (_XX)",
    "Closed three in a row (XXX)",
    "Three in a row with chance to make 4 (_XXX or X_XX)",
    "Can be blocked after next move (_X_X, X_X_X)",
    "Chance to have win after next move (_XX_, _X_X_, _X_XX, _XX_X)",
    "Likely win if play next (_XXX_, _X_XX_, _X_XX_X ...)",
    "Guaranteed to win on next move (_XXXX, X_XXX, XX_XX, _X_XXX, _XX_XX, _XXX_X, ...)",
    "Guaranteed win even if not moving next (_XXXX_, _XXXX_X, ...)",
    "Already won. Arrangements of 5 in a row weight",
    "Already won. Arrangements of 6 in a row weight",
    "Already won. Arrangements of 7 in a row weight",
    "Already won. Arrangements of 8 in a row weight"
  )
}
