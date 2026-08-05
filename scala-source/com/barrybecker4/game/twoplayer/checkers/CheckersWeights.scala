/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers

import com.barrybecker4.game.common.{MoveList, GameWeights}

class CheckersWeights extends GameWeights(
  CheckersWeights.DEFAULT_WEIGHTS,
  CheckersWeights.MIN_WEIGHTS,
  CheckersWeights.MAX_WEIGHTS,
  CheckersWeights.WEIGHT_SHORT_DESCRIPTIONS,
  CheckersWeights.WEIGHT_DESCRIPTIONS
)

object CheckersWeights {
  private val DEFAULT_WEIGHTS: Array[Double] = Array(21.0, 29.0, 1.5)
  private val MAX_WEIGHTS: Array[Double] = Array(80.0, 200.0, 20.0)
  private val MIN_WEIGHTS: Array[Double] = Array(1.0, 10.0, 0.0)

  private val WEIGHT_SHORT_DESCRIPTIONS: Array[String] = Array(
    "PawnMove weight",
    "King weight",
    "Advancement weight"
  )

  private val WEIGHT_DESCRIPTIONS: Array[String] = Array(
    "Weight to associate with the number of remaining pieces",
    "Weight to associate with the number of kings that a side has",
    "Weight to give associate with piece advancement"
  )

  val PIECE_WEIGHT_INDEX: Int = 0
  val KINGED_WEIGHT_INDEX: Int = 1
  val ADVANCEMENT_WEIGHT_INDEX: Int = 2
}
