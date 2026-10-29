/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

/**
 * Scores for various sorts of prototypical eye types.
 * 0 means dead. 1 means unconditionally alive.
 */
object EyeShapeScores {
  val FALSE_EYE: Float = 0.19f
  val SINGLE_EYE: Float = 1.0f
  val BIG_EYE: Float = 1.2f
  val GUARANTEED_TWO_EYES: Float = 2.0f
}
