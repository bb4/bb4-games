/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe

import com.barrybecker4.game.common.GameWeights

/**
 * These weights determine how the computer values each pattern.
 * These weights determine how the computer values features of the board relative to each other.
 * if only one computer is playing, then only one of the weights arrays is used.
 * use these weights if no others are provided.
 *
 * @author Barry Becker
 */
class TicTacToeWeights extends GameWeights(
  TicTacToeWeights.DEFAULT_WEIGHTS,
  TicTacToeWeights.MIN_WEIGHTS,
  TicTacToeWeights.MAX_WEIGHTS,
  TicTacToeWeights.WEIGHT_SHORT_DESCRIPTIONS,
  TicTacToeWeights.WEIGHT_DESCRIPTIONS
)

object TicTacToeWeights {
  /** If greater than this threshold, then opponent is in jeopardy. */
  val JEOPARDY_WEIGHT = 10

  /** These defaults may be overridden in by the user in the UI. */
  private val DEFAULT_WEIGHTS = Array[Double](
    1.0, 1.0, JEOPARDY_WEIGHT + 1, JEOPARDY_WEIGHT, 2 * GameWeights.ASSUMED_WINNING_VALUE
  )

  /** Don't allow the weights to go below these minimum values. Upper limit. */
  private val MIN_WEIGHTS = Array[Double](
    0.0, 0.0, 1.0, 1.0, GameWeights.ASSUMED_WINNING_VALUE
  )

  /** Don't allow the weights to exceed these maximum values. Upper limit. */
  private val MAX_WEIGHTS = Array[Double](
    50.0, 50.0, 500.0, 500.0, 3 * GameWeights.ASSUMED_WINNING_VALUE
  )

  private val WEIGHT_SHORT_DESCRIPTIONS = Array[String](
    "1a weight", "1b weight", "2a weight", "2b weight", "3 weight"
  )

  private val WEIGHT_DESCRIPTIONS = Array[String](
    "1 in a row weight (X__)",
    "1 in a row weight (_X_)",
    "open 2 in a row weight (XX_)",
    "open of 2 in a row weight (X_X)",
    "3 in a row weight (XXX)"
  )
}
