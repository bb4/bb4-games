// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.mancala.move

import com.barrybecker4.common.geometry.ByteLocation
import org.scalatest.funsuite.AnyFunSuite

/** @author Barry Becker */
class MancalaMoveTest extends AnyFunSuite:

  test("testSerializeSimpleMove") {
    val move = new MancalaMove(true, new ByteLocation(1, 5), 3.toByte, 0)

    assert(
      move.toString == "Player 1 val:0 inhrtd:0 piece: X((row=1, column=5)) \n" +
        "stones moved = 3",
      "Unexpected serialized form."
    )
  }

  test("testSerializeMoveWithCaptures") {
    val move = new MancalaMove(true, new ByteLocation(1, 5), 3.toByte, 0)
    move.getCaptures.put(new ByteLocation(2, 6), 2.toByte)

    assert(
      move.toString == "Player 1 val:0 inhrtd:0 piece: X((row=1, column=5)) \n" +
        "stones moved = 3\n" +
        "captures = {(row=2, column=6)=2}",
      "Unexpected serialized form."
    )
  }

  test("testSerializeCompoundMove") {
    val move = new MancalaMove(true, new ByteLocation(1, 4), 3.toByte, 0)
    val followUp = new MancalaMove(true, new ByteLocation(1, 6), 3.toByte, 0)
    val followUp2 = new MancalaMove(true, new ByteLocation(1, 2), 4.toByte, 0)
    followUp.setFollowUpMove(followUp2)
    move.setFollowUpMove(followUp)

    assert(
      move.toString == "Player 1 val:0 inhrtd:0 piece: X((row=1, column=4)) \n" +
        "stones moved = 3\n" +
        "[\n" +
        "   Player 1 val:0 inhrtd:0 piece: X((row=1, column=6)) \n" +
        "   stones moved = 3\n" +
        "   [\n" +
        "      Player 1 val:0 inhrtd:0 piece: X((row=1, column=2)) \n" +
        "      stones moved = 4\n" +
        "   ]\n" +
        "]",
      "Unexpected serialized form."
    )
  }
