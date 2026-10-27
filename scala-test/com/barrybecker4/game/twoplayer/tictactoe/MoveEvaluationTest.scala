/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe

import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.gomoku.analysis.{Direction, MoveEvaluator}
import org.junit.Assert.assertEquals
import org.junit.{Before, Test}

import scala.compiletime.uninitialized

/**
 * Verify that we correctly evaluate patterns on the board.
 * @author Barry Becker
 */
class MoveEvaluationTest:

  private var evaluator: MoveEvaluator = uninitialized
  private var weights: TicTacToeWeights = uninitialized
  private var board: TicTacToeBoard = uninitialized

  private val PLAYER1_PIECE = new GamePiece(true)
  private val PLAYER2_PIECE = new GamePiece(false)

  @Before
  def setUp(): Unit =
    board = new TicTacToeBoard()
    weights = new TicTacToeWeights()
    evaluator = new MoveEvaluator(new TicTacToePatterns())

  @Test def test__OEvaluation(): Unit =
    verifyAllDirectionsResult(2, 1, false, -16, -12)
    verifyAllDirectionsResult(2, 3, false, -16, -12)

  @Test def test_O_Evaluation(): Unit =
    verifyAllDirectionsResult(2, 2, false, -16, -16)

  @Test def test_X_Evaluation(): Unit =
    verifyAllDirectionsResult(2, 2, true, 16, 16)

  @Test def test_XOEvaluation(): Unit =
    board.makeMove(TwoPlayerMove.createMove(2, 2, 0, PLAYER1_PIECE))
    verifyAllDirectionsResult(2, 1, false, -16, -12)
    verifyAllDirectionsResult(2, 3, false, -16, -12)

  @Test def test_OOEvaluation(): Unit =
    board.makeMove(TwoPlayerMove.createMove(2, 2, 0, PLAYER2_PIECE))
    verifyAllDirectionsResult(2, 1, false, -52, -48)
    verifyAllDirectionsResult(2, 3, false, -52, -48)

  @Test def test_XXEvaluation(): Unit =
    board.makeMove(TwoPlayerMove.createMove(2, 2, 0, PLAYER1_PIECE))
    verifyAllDirectionsResult(2, 1, true, 52, 48)
    verifyAllDirectionsResult(2, 3, true, 52, 48)

  @Test def testOOOEvaluation(): Unit =
    board.makeMove(TwoPlayerMove.createMove(2, 2, 0, PLAYER2_PIECE))

    board.makeMove(TwoPlayerMove.createMove(2, 1, 0, PLAYER2_PIECE))
    verifyResult(Direction.HORIZONTAL, 2, 3, false, -8160)
    board.undoMove()

    board.makeMove(TwoPlayerMove.createMove(1, 2, 0, PLAYER2_PIECE))
    verifyResult(Direction.VERTICAL, 3, 2, false, -8160)
    board.undoMove()

    board.makeMove(TwoPlayerMove.createMove(3, 1, 0, PLAYER2_PIECE))
    verifyResult(Direction.UP_DIAGONAL, 1, 3, false, -8156)
    board.undoMove()

    board.makeMove(TwoPlayerMove.createMove(1, 1, 0, PLAYER2_PIECE))
    verifyResult(Direction.DOWN_DIAGONAL, 3, 3, false, -8156)
    board.undoMove()

  @Test def testXXXEvaluation(): Unit =
    board.makeMove(TwoPlayerMove.createMove(2, 2, 0, PLAYER1_PIECE))

    board.makeMove(TwoPlayerMove.createMove(2, 1, 0, PLAYER1_PIECE))
    verifyResult(Direction.HORIZONTAL, 2, 3, true, 8160)
    board.undoMove()

    board.makeMove(TwoPlayerMove.createMove(1, 2, 0, PLAYER1_PIECE))
    verifyResult(Direction.VERTICAL, 3, 2, true, 8160)
    board.undoMove()

    board.makeMove(TwoPlayerMove.createMove(3, 1, 0, PLAYER1_PIECE))
    verifyResult(Direction.UP_DIAGONAL, 1, 3, true, 8156)
    board.undoMove()

    board.makeMove(TwoPlayerMove.createMove(1, 1, 0, PLAYER1_PIECE))
    verifyResult(Direction.DOWN_DIAGONAL, 3, 3, true, 8156)
    board.undoMove()

  private def verifyAllDirectionsResult(
      row: Int, col: Int, player1: Boolean,
      expectedStraightWorth: Int, expectedDiagonalWorth: Int
  ): Unit =
    verifyStraightDirectionsResult(row, col, player1, expectedStraightWorth)
    verifyDiagonalDirectionsResult(row, col, player1, expectedDiagonalWorth)

  private def verifyStraightDirectionsResult(row: Int, col: Int, player1: Boolean, expectedWorth: Int): Unit =
    verifyResult(Direction.HORIZONTAL, row, col, player1, expectedWorth)
    verifyResult(Direction.VERTICAL, col, row, player1, expectedWorth)

  private def verifyDiagonalDirectionsResult(row: Int, col: Int, player1: Boolean, expectedWorth: Int): Unit =
    verifyResult(Direction.UP_DIAGONAL, col, TicTacToePatterns.WIN_RUN_LENGTH + 1 - col, player1, expectedWorth)
    verifyResult(Direction.DOWN_DIAGONAL, col, col, player1, expectedWorth)

  private def verifyResult(dir: Direction, row: Int, col: Int, player1: Boolean, expectedWorth: Int): Unit =
    val lastMove = TwoPlayerMove.createMove(row, col, 0, new GamePiece(player1))
    board.makeMove(lastMove)
    val worth = evaluator.worth(board, lastMove, weights.getDefaultWeights)
    assertEquals("[" + dir + "] Unexpected worth for move " + lastMove, expectedWorth, worth)
    board.undoMove()
