/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.analysis

import com.barrybecker4.game.common.GameWeights
import com.barrybecker4.game.common.board.{BoardPosition, GamePiece}
import com.barrybecker4.game.twoplayer.gomoku.pattern.{SimplePatterns, SimpleWeights}
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Verify that we correctly evaluate lines on the board.
 *
 * @author Barry Becker
 */
class LineTest:

  private val weights: GameWeights = new SimpleWeights()

  @Test
  def testAppendEmpty(): Unit =
    val line = new Line(createLineEvaluator())
    val pos = new BoardPosition(2, 2, null)
    line.append(pos)
    assertEquals("_", line.toString)

  @Test
  def testAppendPlayer(): Unit =
    val line = new Line(createLineEvaluator())
    var pos = new BoardPosition(2, 2, new GamePiece(true))
    line.append(pos)
    assertEquals("X", line.toString)

    pos = new BoardPosition(4, 3, new GamePiece(false))
    line.append(pos)
    assertEquals("XO", line.toString)

  @Test
  def testComputeValueDifferenceXX_Integraction(): Unit =
    val line = createLine("XX")

    var diff = line.computeValueDifference(0)
    assertEquals(9, diff)

    diff = line.computeValueDifference(1)
    assertEquals(9, diff)

  @Test
  def testComputeValueDifferenceOO_Integraction(): Unit =
    val line = createLine("OO")

    var diff = line.computeValueDifference(0)
    assertEquals(-9, diff)

    diff = line.computeValueDifference(1)
    assertEquals(-9, diff)

  @Test
  def testComputeValueDifference_X_Integraction(): Unit =
    val line = createLine("_X")
    assertEquals("_X", line.toString)

    val diff = line.computeValueDifference(1)
    assertEquals(1, diff)

  @Test
  def testComputeValueDifferenceXX_TooShort(): Unit =
    val line = createLineWithMock("XX")

    var diff = line.computeValueDifference(0)
    assertEquals(0, diff)

    diff = line.computeValueDifference(1)
    assertEquals(0, diff)

  @Test
  def testComputeValueDifference_XXX(): Unit =
    val line = createLineWithMock("_XXX")

    var diff = line.computeValueDifference(2)
    assertEquals(2, diff)

    diff = line.computeValueDifference(3)
    assertEquals(2, diff)

  /** The mock evaluator returns 1 whenever the position matches the player symbol (or -1 if opponent) */
  @Test
  def testComputeValueDifference_XOX(): Unit =
    val line = createLineWithMock("_XOX")

    var diff = line.computeValueDifference(2)
    assertEquals(-2, diff)

    diff = line.computeValueDifference(3)
    assertEquals(2, diff)

  @Test
  def testComputeValueDifference_XOXrecorded(): Unit =
    val line = createLineWithRecorder("_XOX")

    var diff = line.computeValueDifference(2)
    assertEquals(1, diff)
    TstUtil.checkRecordedPatterns(Array("_", "_X_X", "O", "_X"), line)

    diff = line.computeValueDifference(3)
    assertEquals(1, diff)
    TstUtil.checkRecordedPatterns(Array("_", "O_", "X", ""), line)

  /** shows a more typical pattern */
  @Test
  def testComputeValueDifferenceXX_XO_X_X_recorded(): Unit =
    val line = createLineWithRecorder("XX_XO_X_X_")

    // The first two patterns are when we set the position to unoccupied, then check both points of view
    // The second two are when we set the players piece again and check both points of view.
    var diff = line.computeValueDifference(0)
    assertEquals(0, diff)
    TstUtil.checkRecordedPatterns(Array("_X_X", "_", "XX_X", ""), line)

    diff = line.computeValueDifference(1)
    assertEquals(-1, diff)
    TstUtil.checkRecordedPatterns(Array("X_", "_", "XX_X", "_"), line)

    diff = line.computeValueDifference(3)
    assertEquals(2, diff)
    TstUtil.checkRecordedPatterns(Array("_", "_O_", "XX_X", "_", "O_"), line)

    diff = line.computeValueDifference(4)
    assertEquals(-1, diff)
    TstUtil.checkRecordedPatterns(Array("_", "XX_X_", "O_", "XX_X", "_X_X_"), line)

    diff = line.computeValueDifference(6)
    assertEquals(-1, diff)
    TstUtil.checkRecordedPatterns(Array("_", "_", "_X_X_", "O_", "_"), line)

    diff = line.computeValueDifference(8)
    assertEquals(0, diff)
    TstUtil.checkRecordedPatterns(Array("_", "_", "_X_X_", "_"), line)

  /** Cannot check where no symbol was played */
  @Test(expected = classOf[IllegalStateException])
  def testComputeValueDifferenceXX_XO_X_X_emptyPosition(): Unit =
    val line = createLineWithRecorder("XX_XO_X_X_")
    line.computeValueDifference(2)

  /**
   * @param linePattern some sequence of X, O, _
   * @return the line
   */
  private def createLine(linePattern: String): Line =
    TstUtil.createLine(linePattern, createLineEvaluator())

  /**
   * @param linePattern some sequence of X, O, _
   * @return the line
   */
  private def createLineWithMock(linePattern: String): Line =
    TstUtil.createLine(linePattern, new MockLineEvaluator(3))

  /**
   * @param linePattern some sequence of X, O, _
   * @return the line
   */
  private def createLineWithRecorder(linePattern: String): Line =
    val evaluator = new StubLineEvaluator(new SimplePatterns(), weights.getDefaultWeights)
    TstUtil.createLine(linePattern, evaluator)

  private def createLineEvaluator(): LineEvaluator =
    new LineEvaluator(new SimplePatterns(), weights.getDefaultWeights)
