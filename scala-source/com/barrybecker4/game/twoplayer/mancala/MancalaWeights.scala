/* Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala

import com.barrybecker4.game.common.GameWeights

/**
  * These weights determine how the computer values each board position.
  * If only one computer is playing, then only one of the weights arrays is used.
  *
  * @author Barry Becker
  */
class MancalaWeights(weights: Array[Double]) extends GameWeights(
  weights,
  MancalaWeights.MIN_WEIGHTS,
  MancalaWeights.MAX_WEIGHTS,
  MancalaWeights.WEIGHT_SHORT_DESCRIPTIONS,
  MancalaWeights.WEIGHT_DESCRIPTIONS
):
  def this() = this(MancalaWeights.DEFAULT_WEIGHTS)

object MancalaWeights:
  val STORAGE_WEIGHT_INDEX = 0

  /** If greater than this threshold, then opponent is in jeopardy. */
  val JEOPARDY_WEIGHT = 80

  /**
    * These defaults may be overridden in by the user in the UI.
    */
  private val DEFAULT_WEIGHTS = Array[Double](1.0)

  /** Don't allow the weights to exceed these maximum values. Upper limit. */
  private val MAX_WEIGHTS = Array[Double](10.0)

  /** Don't allow the weights to go below these minimum values. Upper limit. */
  private val MIN_WEIGHTS = Array[Double](0.0)

  private val WEIGHT_SHORT_DESCRIPTIONS = Array[String]("storage weight")

  private val WEIGHT_DESCRIPTIONS = Array[String](
    "The difference between player one's stones in storage versus player two's"
  )
