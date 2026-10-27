/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.analysis

import com.barrybecker4.game.common.GameWeights
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.gomoku.GoMokuBoard
import com.barrybecker4.game.twoplayer.gomoku.GoMokuTestWeights
import com.barrybecker4.game.twoplayer.gomoku.analysis.differencers.ValueDifferencerFactory
import com.barrybecker4.game.twoplayer.gomoku.pattern.GoMokuPatterns
import org.junit.Assert.assertEquals
import org.junit.{Before, Test}

import scala.jdk.CollectionConverters.*

/**
 * Verify that we correctly evaluate patterns on the board.
 *
 * @author Barry Becker
 */
class MoveEvaluatorTest:

  private var evaluator: MoveEvaluator = scala.compiletime.uninitialized
  private var weights: GameWeights = scala.compiletime.uninitialized
  private var board: GoMokuBoard = scala.compiletime.uninitialized
  private var lastMove: TwoPlayerMove = scala.compiletime.uninitialized
  private var lineFactory: LineFactoryRecorder = scala.compiletime.uninitialized

  /**
   * Common initialization for all test cases.
   */
  @Before
  def setUp(): Unit =
    board = new GoMokuBoard()
    weights = new GoMokuTestWeights()
    val patterns = new GoMokuPatterns()
    evaluator = new MoveEvaluator(patterns)
    lineFactory = new LineFactoryRecorder()
    val differencerFactory = new ValueDifferencerFactory(patterns, lineFactory)
    evaluator.setValueDifferencerFactory(differencerFactory)

  /**
   * Verify error if piece is not where last move was played.
   */
  @Test(expected = classOf[IllegalStateException])
  def testInvalidEvaluation(): Unit =
    initRow(board, 1, "___")
    lastMove = TwoPlayerMove.createMove(1, 1, 0, new GamePiece(true))
    checkResultLines(lastMove, 0, Array("______", "______", "_", "______"))

  @Test
  def testX__Evaluation(): Unit =
    initRow(board, 1, "X__")
    lastMove = TwoPlayerMove.createMove(1, 1, 0, new GamePiece(true))
    checkResultLines(lastMove, 0, Array("X_____", "X_____", "X", "X_____"))

  @Test
  def test___X__Evaluation(): Unit =
    initRow(board, 1, "___X__")
    lastMove = TwoPlayerMove.createMove(1, 4, 0, new GamePiece(true))
    checkResultLines(lastMove, 0, Array("___X_____", "X_____", "___X", "X_____"))

  /**
   * Board looks like this:
   *   _Xx____  where X is the last move played.
   *   _______
   *   _______
   *   _______
   *
   *   Lines are :
   *     horizontal: _XX____
   *     vertical :  X_____
   *     upDiag   :   _X
   *     downDiag :  X_____
   */
  @Test
  def test_XX__Evaluation(): Unit =
    initRow(board, 1, "_XX__")
    lastMove = TwoPlayerMove.createMove(1, 2, 0, new GamePiece(true))
    checkResultLines(lastMove, 8, Array("_XX____", "X_____", "_X", "X_____"))

  @Test
  def test_OO__Evaluation(): Unit =
    initRow(board, 1, "_OO__")
    lastMove = TwoPlayerMove.createMove(1, 2, 0, new GamePiece(false))
    checkResultLines(lastMove, -8, Array("_OO____", "O_____", "_O", "O_____"))

  /** the side of the last move played must match the piece on the board or we get an exception. */
  @Test(expected = classOf[IllegalStateException])
  def test_XO__PlayerMismatch(): Unit =
    initRow(board, 1, "_XO__")
    lastMove = TwoPlayerMove.createMove(1, 2, 0, new GamePiece(false))
    checkResultLines(lastMove, 0, Array("_XO____", "X_____", "_X", "X_____"))

  @Test
  def test_XO__Evaluation(): Unit =
    initRow(board, 1, "_XO__")
    lastMove = TwoPlayerMove.createMove(1, 3, 0, new GamePiece(false))
    checkResultLines(lastMove, 0, Array("_XO_____", "O_____", "__O", "O_____"))

  @Test
  def test_XX__2Evaluation(): Unit =
    initRow(board, 2, "_XX__") // on second row
    lastMove = TwoPlayerMove.createMove(2, 2, 0, new GamePiece(true))
    checkResultLines(lastMove, 8, Array("_XX____", "_X_____", "_X_", "_X_____"))

  @Test
  def test_XX_X_X_Row1Evaluation(): Unit =
    initRow(board, 1, "_XX_X__X_")
    lastMove = TwoPlayerMove.createMove(1, 2, 0, new GamePiece(true))
    checkResultLines(lastMove, 24, Array("_XX_X__", "X_____", "_X", "X_____"))

    lastMove = TwoPlayerMove.createMove(1, 3, 0, new GamePiece(true))
    checkResultLines(lastMove, 32, Array("_XX_X__X", "X_____", "__X", "X_____"))

    lastMove = TwoPlayerMove.createMove(1, 5, 0, new GamePiece(true))
    checkResultLines(lastMove, 32, Array("_XX_X__X__", "X_____", "____X", "X_____"))

    lastMove = TwoPlayerMove.createMove(1, 8, 0, new GamePiece(true))
    checkResultLines(lastMove, 0, Array("X_X__X_____", "X_____", "_____X", "X_____"))

  @Test
  def test_XX_X_X_Row5Evaluation(): Unit =
    initRow(board, 5, "_XX_X__X_")
    lastMove = TwoPlayerMove.createMove(5, 2, 0, new GamePiece(true))
    checkResultLines(lastMove, 24, Array("_XX_X__", "____X_____", "_X____", "_X_____"))

    lastMove = TwoPlayerMove.createMove(5, 3, 0, new GamePiece(true))
    checkResultLines(lastMove, 32, Array("_XX_X__X", "____X_____", "__X____", "__X_____"))

    lastMove = TwoPlayerMove.createMove(5, 5, 0, new GamePiece(true))
    checkResultLines(lastMove, 32, Array("_XX_X__X__", "____X_____", "____X____", "____X_____"))

    lastMove = TwoPlayerMove.createMove(5, 8, 0, new GamePiece(true))
    checkResultLines(lastMove, 0, Array("X_X__X_____", "____X_____", "_____X____", "____X_____"))

  @Test
  def test_XO_X_X_Row1Evaluation(): Unit =
    initRow(board, 1, "_XO_X__X_")

    lastMove = TwoPlayerMove.createMove(1, 2, 0, new GamePiece(true))
    checkResultLines(lastMove, 0, Array("_XO_X__", "X_____", "_X", "X_____"))

    // we check that the moved game piece matches what is on the board.
    lastMove = TwoPlayerMove.createMove(1, 3, 0, new GamePiece(false))
    checkResultLines(lastMove, 0, Array("_XO_X__X", "O_____", "__O", "O_____"))

    lastMove = TwoPlayerMove.createMove(1, 5, 0, new GamePiece(true))
    checkResultLines(lastMove, 0, Array("_XO_X__X__", "X_____", "____X", "X_____"))

    lastMove = TwoPlayerMove.createMove(1, 8, 0, new GamePiece(true))
    checkResultLines(lastMove, 0, Array("O_X__X_____", "X_____", "_____X", "X_____"))

  @Test
  def test3RowEvaluation(): Unit =
    initRow(board, 2, "_OX_X__X_")
    initRow(board, 3, "_XX_X__X_")
    initRow(board, 4, "_XX_X__X_")
    var lastMove = TwoPlayerMove.createMove(2, 2, 0, new GamePiece(false))
    checkResultLines(lastMove, -14, Array("_OX_X__", "_OXX___", "_O_", "_OX____"))

    lastMove = TwoPlayerMove.createMove(3, 3, 0, new GamePiece(true))
    checkResultLines(lastMove, 64, Array("_XX_X__X", "_XXX____", "_XX__", "_OX_____"))

    lastMove = TwoPlayerMove.createMove(3, 8, 0, new GamePiece(true))
    checkResultLines(lastMove, 24, Array("X_X__X_____", "_XXX____", "_____X__", "__X_____"))

  /**
   * The 4 expected lines are - | / \
   */
  private def checkResultLines(lastMove: TwoPlayerMove, expectedWorth: Int, expectedLines: Array[String]): Unit =
    lineFactory.clearLines()
    val worth = evaluator.worth(board, lastMove, weights.getDefaultWeights)
    val lines = lineFactory.getCreatedLines
    TstUtil.printLines(lines)

    assertEquals("Unexpected worth for move " + lastMove, expectedWorth, worth)
    assertEquals(expectedLines.length, lines.size)
    var i = 0
    for expPattern <- expectedLines do
      assertEquals(expPattern, lines.get(i).toString)
      i += 1

  private def initRow(board: GoMokuBoard, row: Int, pattern: String): Unit =
    assert(row > 0)
    for i <- 0 until pattern.length do
      val c = pattern.charAt(i)
      if c == 'X' || c == 'O' then
        val player1 = c == 'X'
        val move = TwoPlayerMove.createMove(row, i + 1, 0, new GamePiece(player1))
        board.makeMove(move)
