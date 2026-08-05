// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.mancala.move

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite

import scala.compiletime.uninitialized

/** @author Barry Becker */
class MoveMakerTest extends AnyFunSuite with BeforeAndAfterEach:

  /** instance under test. */
  private var moveMaker: MoveMaker = uninitialized
  private var board: MancalaBoard = uninitialized
  private var verifier: Verifier = uninitialized

  override def beforeEach(): Unit =
    board = new MancalaBoard()
    moveMaker = new MoveMaker(board)
    verifier = new Verifier(board)

  // 1,1  1,2  1,3  1,4  1,5  1,6  1,7  1,8
  //      2,2  2,3  2,4  2,5  2,6  2,7
  test("testMakeSimplePlayer1Move") {
    val move = new MancalaMove(true, new ByteLocation(1, 5), 3.toByte, 0)
    moveMaker.makeMove(move)

    verifier.checkOverallBoard(0, 4, 4, 4, 0, 3, 3, 0, 3, 3, 3, 3, 3, 3)
  }

  test("testMakeSimplePlayer1MoveWithLastInHome") {
    val move = new MancalaMove(true, new ByteLocation(1, 4), 3.toByte, 0)
    moveMaker.makeMove(move)

    verifier.checkOverallBoard(1, 4, 4, 0, 3, 3, 3, 0, 3, 3, 3, 3, 3, 3)
  }

  test("testMakePlayer1MoveWithManyStones") {
    val move = new MancalaMove(true, new ByteLocation(1, 4), 20.toByte, 0)
    moveMaker.makeMove(move)

    verifier.checkOverallBoard(2, 5, 5, 1, 4, 4, 4, 0, 5, 5, 5, 5, 4, 4)
  }

  test("testMakeSimplePlayer2Move") {
    val move = new MancalaMove(false, new ByteLocation(2, 6), 3.toByte, 0)
    moveMaker.makeMove(move)

    verifier.checkOverallBoard(0, 3, 3, 3, 3, 3, 4, 1, 3, 3, 3, 3, 0, 4)
  }

  /**
    * The initial move results in the last seed going into storage, so there is a secondary move.
    * 1,1  1,2  1,3  1,4  1,5  1,6  1,7  1,8
    *      2,2  2,3  2,4  2,5  2,6  2,7
    */
  test("testComboPlayer1Move") {
    val move = new MancalaMove(true, new ByteLocation(1, 4), 3.toByte, 0)
    val secondaryMove = new MancalaMove(true, new ByteLocation(1, 6), 3.toByte, 0)
    move.setFollowUpMove(secondaryMove)
    moveMaker.makeMove(move)

    verifier.checkOverallBoard(1, 4, 5, 1, 4, 0, 3, 0, 3, 3, 3, 3, 3, 3)
  }

  test("testPlayer1CapturingMove") {
    board.getBin(new ByteLocation(1, 2)).takeStones()
    val move = new MancalaMove(true, new ByteLocation(1, 4), 2.toByte, 0)
    moveMaker.makeMove(move)

    // The captures describe where the captures stones came from
    assert(
      move.getCaptures.toString == "{(row=1, column=2)=1, (row=2, column=2)=3}",
      "Unexpected captures."
    )

    // note that stones from 1, 2 and 2,2, are moved to p1's home
    verifier.checkOverallBoard(4, 0, 4, 0, 3, 3, 3, 0, 0, 3, 3, 3, 3, 3)
  }

  test("testPlayer2CapturingMove") {
    board.getBin(new ByteLocation(2, 5)).takeStones()
    val move = new MancalaMove(false, new ByteLocation(2, 3), 2.toByte, 0)
    moveMaker.makeMove(move)

    assert(
      move.getCaptures.toString == "{(row=1, column=5)=3, (row=2, column=5)=1}",
      "Unexpected captures."
    )

    // note that stones from 1,2 and 2,2, are moved to p1's home
    verifier.checkOverallBoard(0, 3, 3, 3, 0, 3, 3, 4, 3, 0, 4, 0, 3, 3)
  }

  /** opposite side should get cleared on final move when on player side */
  test("testFinalPlayer1MoveOnPlayersSide") {
    for i <- 2 until 8 do
      board.getBin(new ByteLocation(1, i)).takeStones()

    val move = new MancalaMove(true, new ByteLocation(1, 4), 1.toByte, 0)
    moveMaker.makeMove(move)

    assert(
      move.getCaptures.toString == "{(row=1, column=3)=1, (row=2, column=2)=3, (row=2, column=3)=3, (row=2, column=4)=3, " +
        "(row=2, column=5)=3, (row=2, column=6)=3, (row=2, column=7)=3}",
      "Unexpected captures."
    )

    verifier.checkOverallBoard(4, 0, 0, 0, 0, 0, 0, 15, 0, 0, 0, 0, 0, 0)
  }

  /** opposite side should get cleared on final move when on player two's side */
  test("testFinalPlayer2MoveOnPlayersSide") {
    for i <- 2 until 8 do
      board.getBin(new ByteLocation(2, i)).takeStones()

    val move = new MancalaMove(false, new ByteLocation(2, 4), 1.toByte, 0)
    moveMaker.makeMove(move)

    assert(
      move.getCaptures.toString == "{(row=1, column=2)=3, (row=1, column=3)=3, (row=1, column=4)=3, (row=1, column=5)=3, " +
        "(row=1, column=6)=3, (row=1, column=7)=3, (row=2, column=5)=1}",
      "Unexpected captures."
    )

    verifier.checkOverallBoard(15, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0)
  }

  /** opposite side should get cleared on final move */
  test("testFinalPlayer1MoveInHomeBin") {
    for i <- 2 until 8 do
      board.getBin(new ByteLocation(1, i)).takeStones()

    val move = new MancalaMove(true, new ByteLocation(1, 2), 1.toByte, 0)
    moveMaker.makeMove(move)

    assert(
      move.getCaptures.toString == "{(row=2, column=2)=3, (row=2, column=3)=3, (row=2, column=4)=3, (row=2, column=5)=3, " +
        "(row=2, column=6)=3, (row=2, column=7)=3}",
      "Unexpected captures."
    )

    verifier.checkOverallBoard(1, 0, 0, 0, 0, 0, 0, 18, 0, 0, 0, 0, 0, 0)
  }

  /** opposite side should get cleared on final move */
  test("testFinalPlayer2MoveInHomeBin") {
    for i <- 2 until 8 do
      board.getBin(new ByteLocation(2, i)).takeStones()

    val move = new MancalaMove(false, new ByteLocation(2, 7), 1.toByte, 0)
    moveMaker.makeMove(move)

    assert(
      move.getCaptures.toString == "{(row=1, column=2)=3, (row=1, column=3)=3, (row=1, column=4)=3, (row=1, column=5)=3, " +
        "(row=1, column=6)=3, (row=1, column=7)=3}",
      "Unexpected captures."
    )
    verifier.checkOverallBoard(18, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0)
  }

  /**
    * The initial move results in the last seed going into storage, so there is a secondary move.
    * 1,1  1,2  1,3  1,4  1,5  1,6  1,7  1,8
    *      2,2  2,3  2,4  2,5  2,6  2,7
    */
  test("testP1SimpleCombo") {
    // initial board state
    // 0, 1, 2, 3, 4, 5, 3, 0,
    //    3, 3, 3, 3, 3, 3
    board.getBin(new ByteLocation(1, 2)).increment(-2)
    board.getBin(new ByteLocation(1, 3)).increment(-1)
    board.getBin(new ByteLocation(1, 5)).increment(1)
    board.getBin(new ByteLocation(1, 6)).increment(2)
    val move1 = new MancalaMove(true, new ByteLocation(1, 2), 1.toByte, 0)
    val move2 = new MancalaMove(true, new ByteLocation(1, 3), 2.toByte, 0)
    move1.setFollowUpMove(move2)
    moveMaker.makeMove(move1)

    assert(move1.getCaptures.toString == "{}", "Unexpected captures.")
    println("b=" + board)

    verifier.checkOverallBoard(2, 1, 0, 3, 4, 5, 3, 0, 3, 3, 3, 3, 3, 3)
  }

  /**
    * The initial move results in the last seed going into storage, so there is a secondary move.
    * 1,1  1,2  1,3  1,4  1,5  1,6  1,7  1,8
    *      2,2  2,3  2,4  2,5  2,6  2,7
    */
  test("testP1SequenceCombo") {
    // initial board state
    // 0, 1, 2, 3, 4, 5, 3, 0,
    //    3, 3, 3, 3, 3, 3
    board.getBin(new ByteLocation(1, 2)).increment(-2)
    board.getBin(new ByteLocation(1, 3)).increment(-1)
    board.getBin(new ByteLocation(1, 5)).increment(1)
    board.getBin(new ByteLocation(1, 6)).increment(2)
    val move1 = new MancalaMove(true, new ByteLocation(1, 2), 1.toByte, 0)
    val move2 = new MancalaMove(true, new ByteLocation(1, 3), 2.toByte, 0)
    val move3 = new MancalaMove(true, new ByteLocation(1, 4), 3.toByte, 0)
    val move4 = new MancalaMove(true, new ByteLocation(1, 5), 4.toByte, 0)
    val move5 = new MancalaMove(true, new ByteLocation(1, 3), 2.toByte, 0)
    move1.setFollowUpMove(move2)
    move2.setFollowUpMove(move3)
    move3.setFollowUpMove(move4)
    move4.setFollowUpMove(move5)
    moveMaker.makeMove(move1)

    assert(move1.getCaptures.toString == "{}", "Unexpected captures.")

    verifier.checkOverallBoard(5, 4, 0, 1, 0, 5, 3, 0, 3, 3, 3, 3, 3, 3)
  }

  /**
    * The initial move results in the last seed going into storage, so there is a secondary move.
    * 1,1  1,2  1,3  1,4  1,5  1,6  1,7  1,8
    *      2,2  2,3  2,4  2,5  2,6  2,7
    */
  test("testCrazyP1Combo") {
    // initial board state
    // 0, 1, 2, 3, 4, 5, 3, 0,
    //    3, 3, 3, 3, 3, 3
    board.getBin(new ByteLocation(1, 2)).increment(-2)
    board.getBin(new ByteLocation(1, 3)).increment(-1)
    board.getBin(new ByteLocation(1, 5)).increment(1)
    board.getBin(new ByteLocation(1, 6)).increment(2)
    board.getBin(new ByteLocation(1, 7)).increment(3)
    val move1 = new MancalaMove(true, new ByteLocation(1, 2), 1.toByte, 0)
    val move2 = new MancalaMove(true, new ByteLocation(1, 3), 2.toByte, 0)
    val move3 = new MancalaMove(true, new ByteLocation(1, 4), 3.toByte, 0)
    val move4 = new MancalaMove(true, new ByteLocation(1, 5), 4.toByte, 0)
    val move5 = new MancalaMove(true, new ByteLocation(1, 3), 2.toByte, 0)
    val move6 = new MancalaMove(true, new ByteLocation(1, 4), 1.toByte, 0)
    move1.setFollowUpMove(move2)
    move2.setFollowUpMove(move3)
    move3.setFollowUpMove(move4)
    move4.setFollowUpMove(move5)
    move5.setFollowUpMove(move6)
    moveMaker.makeMove(move1)

    assert(move1.getCaptures.toString == "{}", "Unexpected captures.")

    assert(
      move6.getCaptures.toString == "{(row=1, column=3)=1, (row=2, column=3)=3}",
      "Unexpected captures."
    )

    verifier.checkOverallBoard(9, 4, 0, 0, 0, 5, 6, 0, 3, 0, 3, 3, 3, 3)
  }
