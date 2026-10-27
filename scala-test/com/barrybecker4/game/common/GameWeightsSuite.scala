// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common

import com.barrybecker4.game.twoplayer.gomoku.pattern.SimpleWeights
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite

import scala.compiletime.uninitialized

/** Verify that we correctly evaluate patterns on the board. */
class GameWeightsSuite extends AnyFunSuite with BeforeAndAfterEach:

  private var weights: GameWeights = uninitialized

  override def beforeEach(): Unit =
    weights = new SimpleWeights()

  test("construction") {
    assert(weights.getName(1) == "Weight 1", "unexpected name ")
    assert(
      weights.getDescription(1) == "The weighting coefficient for the 1th term of the evaluation polynomial",
      "unexpected description "
    )
    assert(weights.getPlayer1Weights.get(2).getValue == 10.0, "unexpected param 2 value ")
  }
