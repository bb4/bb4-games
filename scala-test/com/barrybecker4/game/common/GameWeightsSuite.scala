// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common

import com.barrybecker4.game.twoplayer.tictactoe.TicTacToeWeights
import org.scalatest.funsuite.AnyFunSuite

/** Verify GameWeights preserves caller-supplied names/descriptions and NONE is empty. */
class GameWeightsSuite extends AnyFunSuite:

  test("secondary constructor preserves names and descriptions") {
    val weights = new TicTacToeWeights()
    assert(weights.getName(0) == "1a weight")
    assert(weights.getName(2) == "2a weight")
    assert(weights.getDescription(0) == "1 in a row weight (X__)")
    assert(weights.getDescription(4) == "3 in a row weight (XXX)")
  }

  test("primary constructor derives names from parameters") {
    val weights = new GameWeightsStub()
    assert(weights.getName(0) == "paramName")
  }

  test("NONE is empty Option") {
    assert(GameWeights.NONE.isEmpty)
  }

  test("player weight accessors round-trip") {
    val weights = new TicTacToeWeights()
    val p1 = weights.getPlayer1Weights
    weights.setPlayer1Weights(p1)
    assert(weights.getPlayer1Weights eq p1)
  }
