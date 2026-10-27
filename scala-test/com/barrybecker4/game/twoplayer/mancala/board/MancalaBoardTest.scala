// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.mancala.board

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.twoplayer.mancala.move.{Captures, MancalaMove}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite

import scala.compiletime.uninitialized

/** @author Barry Becker */
class MancalaBoardTest extends AnyFunSuite with BeforeAndAfterEach:

  /** instance under test. */
  private var board: MancalaBoard = uninitialized

  override def beforeEach(): Unit =
    board = new MancalaBoard()

  test("testConstructed") {
    assert(board.getBin(new ByteLocation(2, 2)).getNumStones == 3, "Unexpected stones")

    // check home storages
    val p1Storage = board.getBin(new ByteLocation(1, 1))
    val p2Storage = board.getBin(new ByteLocation(1, 8))
    assert(board.getHomeBin(true) == p1Storage, "Unexpected p1 home")
    assert(board.getHomeBin(false) == p2Storage, "Unexpected p2 home")
    assert(p1Storage.getNumStones == 0, "Unexpected stones")
    assert(p2Storage.getNumStones == 0, "Unexpected stones")
    assert(board.getBin(new ByteLocation(1, 4)).getNumStones == 3, "Unexpected stones")
    assert(board.getBin(new ByteLocation(2, 5)).getNumStones == 3, "Unexpected stones")
  }

  test("testIsSideClearWhenNot") {
    assert(!board.isSideClear(true), "Unexpected isSideClear")
    assert(!board.isSideClear(false), "Unexpected isSideClear")
  }

  test("testIsSideClear") {
    val captures = new Captures()
    board.clearSide(true, captures)
    assert(board.isSideClear(true), "Unexpected isSideClear")
    assert(captures.size() == 6, "Unexpected number of captures")

    board.clearSide(false, captures)
    assert(board.isSideClear(false), "Unexpected isSideClear")
    assert(captures.size() == 12, "Unexpected number of captures")
  }

  test("testMoveAgainPlayer1") {
    val move = new MancalaMove(true, new ByteLocation(1, 4), 3.toByte, 0)
    val moveAgain = board.moveAgainAfterMove(move)
    assert(moveAgain, "Should move again")
  }

  test("testNotMoveAgainPlayer1") {
    val move = new MancalaMove(true, new ByteLocation(1, 5), 3.toByte, 0)
    val moveAgain = board.moveAgainAfterMove(move)
    assert(!moveAgain, "Should not move again")
  }

  test("testMoveAgainPlayer2") {
    val move = new MancalaMove(false, new ByteLocation(2, 4), 4.toByte, 0)
    val moveAgain = board.moveAgainAfterMove(move)
    assert(moveAgain, "Should move again")
  }

  test("testNotMoveAgainPlayer2") {
    val move = new MancalaMove(false, new ByteLocation(2, 5), 4.toByte, 0)
    val moveAgain = board.moveAgainAfterMove(move)
    assert(!moveAgain, "Should not move again")
  }
