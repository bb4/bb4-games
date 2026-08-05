/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.pattern

import com.barrybecker4.game.common.GameWeights
import com.barrybecker4.optimization.parameter.NumericParameterArray
import com.barrybecker4.optimization.parameter.types.DoubleParameter

import scala.util.Random

/**
 * Simple weights for unit testing.
 *
 * @author Barry Becker
 */
class SimpleWeights extends GameWeights(
  new NumericParameterArray(
    IndexedSeq(
      DoubleParameter(1.0, 0.0, 5.0, "weight 1", GameWeights.NONE),
      DoubleParameter(3.0, 1.0, 100.0, "weight 2", GameWeights.NONE),
      DoubleParameter(10.0, 5.0, 1000.0, "won weight 3", GameWeights.NONE)
    ),
    5,
    new Random(1)
  )
)
