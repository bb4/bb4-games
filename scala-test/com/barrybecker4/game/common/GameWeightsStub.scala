/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common

import com.barrybecker4.optimization.parameter.NumericParameterArray
import com.barrybecker4.optimization.parameter.types.DoubleParameter

import scala.util.Random

/** Stub GameWeights for testing */
class GameWeightsStub extends GameWeights(
  new NumericParameterArray(
    IndexedSeq(DoubleParameter(1, 0, 10, "paramName", GameWeights.NONE)),
    5,
    new Random(1)
  )
)
