/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade

import com.barrybecker4.game.common.GameWeights

object BlockadeWeights:
  val CLOSEST_WEIGHT_INDEX = 0
  val SECOND_CLOSEST_WEIGHT_INDEX = 1
  val FURTHEST_WEIGHT_INDEX = 2

  private val DEFAULT_WEIGHTS = Array(26.0, 20.0, 8.0)
  private val MAX_WEIGHTS = Array(100.0, 100.0, 50.0)
  private val MIN_WEIGHTS = Array(0.0, 0.0, 0.0)

  private val WEIGHT_SHORT_DESCRIPTIONS = Array(
    "Shorter path weight",
    "Second shortest path weight",
    "Furthest path weight"
  )

  private val WEIGHT_DESCRIPTIONS = Array(
    "Weight to associate with the shortest path to the closest opponent home",
    "Weight to associate with the shortest path to the second closest opponent home",
    "Weight to associate with the shortest path to the furthest opponent home"
  )

class BlockadeWeights extends GameWeights(
  BlockadeWeights.DEFAULT_WEIGHTS,
  BlockadeWeights.MIN_WEIGHTS,
  BlockadeWeights.MAX_WEIGHTS,
  BlockadeWeights.WEIGHT_SHORT_DESCRIPTIONS,
  BlockadeWeights.WEIGHT_DESCRIPTIONS
)
