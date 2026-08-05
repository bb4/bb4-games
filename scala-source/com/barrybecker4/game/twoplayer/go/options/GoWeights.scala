/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.options

import com.barrybecker4.game.common.GameWeights

/**
 * These weights determine how the computer values each term of the polynomial evaluation function.
 * if only one computer is playing, then only one of the weights arrays is used.
 *
 * @author Barry Becker Date: Feb 11, 2007
 */
class GoWeights extends GameWeights(
  GoWeights.DEFAULT_WEIGHTS,
  GoWeights.MIN_WEIGHTS,
  GoWeights.MAX_WEIGHTS,
  GoWeights.WEIGHT_SHORT_DESCRIPTIONS,
  GoWeights.WEIGHT_DESCRIPTIONS
)

object GoWeights {
  private val DEFAULT_WEIGHTS = Array(2.0, 0.9, 0.3, 10.0)
  private val MAX_WEIGHTS = Array(10.0, 5.0, 10.0, 30.0)
  private val MIN_WEIGHTS = Array(0.0, 0.0, 0.0, 0.5)

  private val WEIGHT_SHORT_DESCRIPTIONS = Array(
    "Health",
    "Position",
    "Bad shape",
    "CaptureCounts"
  )

  private val WEIGHT_DESCRIPTIONS = Array(
    "Weight to associate with the relative health of groups",
    "Weight to associate with Position",
    "Weight to associate with the Bad Shape Penalty",
    "Weight to give to CaptureCounts"
  )

  val HEALTH_WEIGHT_INDEX = 0
  val POSITIONAL_WEIGHT_INDEX = 1
  val BAD_SHAPE_WEIGHT_INDEX = 2
  val CAPTURE_WEIGHT_INDEX = 3
}
