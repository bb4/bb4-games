// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.mancala.move

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite

import scala.compiletime.uninitialized

/** @author Barry Becker */
class MoveUndoerTest extends AnyFunSuite with BeforeAndAfterEach:

  /** instance under test. */
  private var moveUndoer: MoveUndoer = uninitialized
  private var board: MancalaBoard = uninitialized
  private var verifier: Verifier = uninitialized

  override def beforeEach(): Unit =
    board = new MancalaBoard()
    moveUndoer = new MoveUndoer(board)
    verifier = new Verifier(board)

  // 1,1  1,2  1,3  1,4  1,5  1,6  1,7  1,8
  //      2,2  2,3  2,4  2,5  2,6  2,7
  test("testUndoSimplePlayer1Move") {
    val move = new MancalaMove(true, new ByteLocation(1, 5), 3.toByte, 0)
    moveUndoer.undoMove(move)

    verifier.checkOverallBoard(0, 2, 2, 2, 6, 3, 3, 0, 3, 3, 3, 3, 3, 3)
  }

  /**
    * First make the move and then undo it.
    * At that point we should be in the original state.
    */
  test("testUndoPlayer1MoveWithManyStones") {
    val move = new MancalaMove(true, new ByteLocation(1, 4), 20.toByte, 0)
    new MoveMaker(board).makeMove(move)
    moveUndoer.undoMove(move)

    verifier.checkOverallBoard(0, 3, 3, 20, 3, 3, 3, 0, 3, 3, 3, 3, 3, 3)
  }

  test("testUndoCompoundPlayer1Move") {
    val move = new MancalaMove(true, new ByteLocation(1, 4), 3.toByte, 0)
    val followUp = new MancalaMove(true, new ByteLocation(1, 5), 3.toByte, 0)
    move.setFollowUpMove(followUp)

    new MoveMaker(board).makeMove(move)
    moveUndoer.undoMove(move)

    checkOriginalState()
  }

  test("testUndoCompoundPlayer2Move") {
    val move = new MancalaMove(false, new ByteLocation(2, 4), 3.toByte, 0)
    val followUp = new MancalaMove(false, new ByteLocation(2, 5), 4.toByte, 0)
    move.setFollowUpMove(followUp)

    new MoveMaker(board).makeMove(move)
    moveUndoer.undoMove(move)

    checkOriginalState()
  }

  test("testUndoCompoundPlayer2aMove") {
    val move = new MancalaMove(false, new ByteLocation(2, 4), 3.toByte, 0)
    val followUp = new MancalaMove(false, new ByteLocation(2, 3), 3.toByte, 0)
    move.setFollowUpMove(followUp)

    new MoveMaker(board).makeMove(move)
    moveUndoer.undoMove(move)

    checkOriginalState()
  }

  test("testUndoPlayer1CapturingMove") {
    board.getBin(new ByteLocation(1, 2)).takeStones()
    val move = new MancalaMove(true, new ByteLocation(1, 5), 3.toByte, 0)

    // this fills in the captures
    new MoveMaker(board).makeMove(move)

    val expCaptures = new Captures()
    expCaptures.put(new ByteLocation(1, 2), 1.toByte)
    expCaptures.put(new ByteLocation(2, 2), 3.toByte)
    assert(move.getCaptures == expCaptures, "Unexpected captures.")

    // put back the 3 stones originally removed.
    board.getBin(new ByteLocation(1, 2)).increment(3)
    moveUndoer.undoMove(move)

    checkOriginalState()
  }

  test("testUndoPlayer2CapturingMove") {
    board.getBin(new ByteLocation(2, 5)).takeStones()
    val move = new MancalaMove(false, new ByteLocation(2, 2), 3.toByte, 0)

    // this fills in the captures
    new MoveMaker(board).makeMove(move)

    val expCaptures = new Captures()
    expCaptures.put(new ByteLocation(1, 5), 3.toByte)
    expCaptures.put(new ByteLocation(2, 5), 1.toByte)
    assert(move.getCaptures == expCaptures, "Unexpected captures.")

    // put back the 3 stones originally removed.
    board.getBin(new ByteLocation(2, 5)).increment(3)
    moveUndoer.undoMove(move)

    checkOriginalState()
  }

  /** verify that the board was restored to its original state */
  private def checkOriginalState(): Unit =
    assert(board.getHomeBin(true).getNumStones == 0, "P1 home not empty")
    assert(board.getHomeBin(false).getNumStones == 0, "P2 home not empty")
    for i <- 2 until board.getNumCols - 1 do
      assert(
        board.getBin(new ByteLocation(1, i)).getNumStones == 3,
        "Unexpected p1 stones at col " + i
      )
      assert(
        board.getBin(new ByteLocation(2, i)).getNumStones == 3,
        "Unexpected p2 stones at col " + i
      )
