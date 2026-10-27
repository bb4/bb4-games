// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis

import com.barrybecker4.game.common.GameWeights
import com.barrybecker4.game.twoplayer.gomoku.pattern.{SimplePatterns, SimpleWeights}
import org.junit.Assert.assertEquals
import org.junit.{Before, Test}

/**
 * Verify that we correctly evaluate patterns in lines on the board.
 *
 * @author Barry Becker
 */
class LineEvaluatorTest:

  private val TOL = 0.0
  private var line: StringBuilder = scala.compiletime.uninitialized
  private var lineEvaluator: LineEvaluator = scala.compiletime.uninitialized
  private var worth = 0.0

  @Before
  def setUp(): Unit =
    lineEvaluator = createLineEvaluator()

  @Test
  def testEvalLinePlayer1_X(): Unit =
    line = createLine("_X")
    var worth = lineEvaluator.evaluate(line, true, 1, 0, 1).toDouble
    assertEquals(1.0, worth, TOL)

    line = createLine("_X")
    worth = lineEvaluator.evaluate(line, true, 0, 0, 1).toDouble
    assertEquals(1.0, worth, TOL)

  @Test
  def testEvalLinePlayer1_X_simp(): Unit =
    line = createLine("_X_")
    worth = lineEvaluator.evaluate(line, true, 1, 0, 2).toDouble
    assertEquals(3.0, worth, TOL)

  @Test
  def testEvalLinePlayer1_X_(): Unit =
    line = createLine("_X_")
    worth = lineEvaluator.evaluate(line, true, 1, 0, 2).toDouble
    assertEquals(3.0, worth, TOL)

    line = createLine("_X_")
    worth = lineEvaluator.evaluate(line, true, 0, 0, 2).toDouble
    assertEquals(3.0, worth, TOL)

    // should be same if in longer line
    line = createLine("OO_X__")
    worth = lineEvaluator.evaluate(line, true, 3, 2, 4).toDouble
    assertEquals(3.0, worth, TOL)

    line = createLine("O_X_O_O")
    worth = lineEvaluator.evaluate(line, true, 2, 1, 3).toDouble
    assertEquals(3.0, worth, TOL)

  @Test
  def testEvalLinePlayer1X_(): Unit =
    line = createLine("X_")
    worth = lineEvaluator.evaluate(line, false, 0, 0, 1).toDouble
    assertEquals(0.0, worth, TOL)

    line = createLine("X_")
    worth = lineEvaluator.evaluate(line, true, 0, 0, 1).toDouble
    assertEquals(1.0, worth, TOL)

    line = createLine("X_")
    worth = lineEvaluator.evaluate(line, true, 1, 0, 1).toDouble
    assertEquals(1.0, worth, TOL)

  @Test
  def testEvalLinePlayer1XX(): Unit =
    line = createLine("XX")
    worth = lineEvaluator.evaluate(line, true, 0, 0, 1).toDouble
    assertEquals(10.0, worth, TOL)

    line = createLine("XX")
    worth = lineEvaluator.evaluate(line, true, 1, 0, 1).toDouble
    assertEquals(10.0, worth, TOL)

  @Test
  def testEvaLineSimplePlayer2(): Unit =
    line = createLine("_O")
    worth = lineEvaluator.evaluate(line, false, 1, 0, 1).toDouble
    assertEquals(-1.0, worth, TOL)

    line = createLine("_O")
    worth = lineEvaluator.evaluate(line, false, 0, 0, 1).toDouble
    assertEquals(-1.0, worth, TOL)

    line = createLine("O_")
    worth = lineEvaluator.evaluate(line, true, 0, 0, 1).toDouble
    assertEquals(0.0, worth, TOL)

    line = createLine("O_")
    worth = lineEvaluator.evaluate(line, true, 1, 0, 1).toDouble
    assertEquals(0.0, worth, TOL)

    line = createLine("O_")
    worth = lineEvaluator.evaluate(line, false, 0, 0, 1).toDouble
    assertEquals(-1.0, worth, TOL)

    line = createLine("OO")
    worth = lineEvaluator.evaluate(line, false, 0, 0, 1).toDouble
    assertEquals(-10.0, worth, TOL)

    line = createLine("OO")
    worth = lineEvaluator.evaluate(line, false, 1, 0, 1).toDouble
    assertEquals(-10.0, worth, TOL)

  /** XX is a winning pattern in this test example */
  @Test
  def testEvalLineLongerThanPattern(): Unit =
    line = createLine("X_XX")

    // the pattern is XX
    worth = lineEvaluator.evaluate(line, true, 2, 2, 3).toDouble
    assertEquals(10.0, worth, TOL)

    // the pattern is X with is not a pattern in SimplePatterns
    worth = lineEvaluator.evaluate(line, false, 2, 2, 3).toDouble
    assertEquals(0.0, worth, TOL)

    worth = lineEvaluator.evaluate(line, true, 3, 2, 3).toDouble
    assertEquals(10.0, worth, TOL)

    worth = lineEvaluator.evaluate(line, true, 1, 1, 2).toDouble
    assertEquals(1.0, worth, TOL)

    worth = lineEvaluator.evaluate(line, true, 1, 0, 3).toDouble
    assertEquals(0.0, worth, TOL)

    // X_XX is not a recognizable pattern
    worth = lineEvaluator.evaluate(line, true, 2, 0, 3).toDouble
    assertEquals(0.0, worth, TOL)

    // X_XX is not a recognizable StubPattern
    worth = lineEvaluator.evaluate(line, true, 3, 0, 3).toDouble
    assertEquals(0.0, worth, TOL)

  @Test
  def testEvalMixedLineXOXwithRecordedPatterns(): Unit =
    val evaluator = createStubLineEvaluator()

    line = createLine("XOX")
    worth = evaluator.evaluate(line, true, 2, 0, 2).toDouble
    TstUtil.checkRecordedPatterns(Array("X"), evaluator)
    assertEquals(0.0, worth, TOL)

    line = createLine("XOX")
    worth = evaluator.evaluate(line, false, 1, 0, 2).toDouble
    TstUtil.checkRecordedPatterns(Array("O"), evaluator)
    assertEquals(0.0, worth, TOL)

    line = createLine("XOX")
    worth = evaluator.evaluate(line, true, 1, 0, 2).toDouble
    TstUtil.checkRecordedPatterns(Array[String](), evaluator)
    assertEquals(0.0, worth, TOL)

  @Test
  def testEvalMixedLine_XOX_withRecordedPatterns(): Unit =
    val evaluator = createStubLineEvaluator()

    line = createLine("_XOX_")
    worth = evaluator.evaluate(line, true, 3, 0, 4).toDouble
    TstUtil.checkRecordedPatterns(Array("X_"), evaluator)
    assertEquals(1.0, worth, TOL)

    line = createLine("_XOX_")
    worth = evaluator.evaluate(line, true, 1, 0, 4).toDouble
    TstUtil.checkRecordedPatterns(Array("_X"), evaluator)
    assertEquals(1.0, worth, TOL)

    line = createLine("_XOX_")
    worth = evaluator.evaluate(line, false, 2, 0, 4).toDouble
    TstUtil.checkRecordedPatterns(Array("O"), evaluator)
    assertEquals(0.0, worth, TOL)

    line = createLine("_XOX_")
    worth = evaluator.evaluate(line, true, 2, 0, 4).toDouble
    TstUtil.checkRecordedPatterns(Array("_X", "X_"), evaluator)
    assertEquals(2.0, worth, TOL)

  @Test
  def testEvalMixedLineX_OX(): Unit =
    line = createLine("X_OX")
    worth = lineEvaluator.evaluate(line, true, 2, 0, 3).toDouble
    // X_ gets 1.
    assertEquals(1.0, worth, TOL)

    line = createLine("X_OX")
    worth = lineEvaluator.evaluate(line, false, 2, 0, 3).toDouble
    assertEquals(-1.0, worth, TOL)

  @Test
  def testEvalMixedLineX_O_X(): Unit =
    line = createLine("X_O_X")
    worth = lineEvaluator.evaluate(line, true, 2, 0, 4).toDouble
    // X_ and _X get 2.
    assertEquals(2.0, worth, TOL)

    worth = lineEvaluator.evaluate(line, false, 2, 0, 4).toDouble
    // X_ and _X get 4.
    assertEquals(-3.0, worth, TOL)

  @Test
  def testEvalMixedLineXXOXXwithRecordedPatterns(): Unit =
    val evaluator = createStubLineEvaluator()

    line = createLine("XXOXX")
    worth = evaluator.evaluate(line, true, 2, 0, 4).toDouble
    TstUtil.checkRecordedPatterns(Array("XX", "XX"), evaluator)
    assertEquals(20.0, worth, TOL)

    worth = evaluator.evaluate(line, true, 1, 0, 4).toDouble
    TstUtil.checkRecordedPatterns(Array("XX"), evaluator)
    assertEquals(10.0, worth, TOL)

    worth = evaluator.evaluate(line, false, 1, 0, 4).toDouble
    TstUtil.checkRecordedPatterns(Array("O"), evaluator)
    assertEquals(0.0, worth, TOL)

    worth = evaluator.evaluate(line, false, 2, 0, 4).toDouble
    TstUtil.checkRecordedPatterns(Array("O"), evaluator)
    assertEquals(0.0, worth, TOL)

  @Test
  def testEvalMixedLine_XO_X(): Unit =
    line = createLine("_XO_X")

    worth = lineEvaluator.evaluate(line, true, 0, 0, 4).toDouble
    assertEquals(1.0, worth, TOL)

    worth = lineEvaluator.evaluate(line, false, 0, 0, 4).toDouble
    assertEquals(0.0, worth, TOL)

    worth = lineEvaluator.evaluate(line, true, 1, 0, 4).toDouble
    assertEquals(1.0, worth, TOL)

    worth = lineEvaluator.evaluate(line, true, 2, 0, 4).toDouble
    assertEquals(2.0, worth, TOL)

    worth = lineEvaluator.evaluate(line, false, 2, 0, 4).toDouble
    assertEquals(-1.0, worth, TOL)

    worth = lineEvaluator.evaluate(line, true, 1, 0, 4).toDouble
    assertEquals(1.0, worth, TOL)

    worth = lineEvaluator.evaluate(line, false, 2, 0, 4).toDouble
    assertEquals(-1.0, worth, TOL)

    worth = lineEvaluator.evaluate(line, true, 3, 0, 4).toDouble
    assertEquals(1.0, worth, TOL)

    worth = lineEvaluator.evaluate(line, false, 3, 0, 4).toDouble
    assertEquals(-1.0, worth, TOL)

  /** @return the line */
  private def createLine(lineStr: String): StringBuilder =
    new StringBuilder(lineStr)

  private def createLineEvaluator(): LineEvaluator =
    val weights = new SimpleWeights()
    new LineEvaluator(new SimplePatterns(), weights.getDefaultWeights)

  private def createStubLineEvaluator(): StubLineEvaluator =
    val weights = new SimpleWeights()
    new StubLineEvaluator(new SimplePatterns(), weights.getDefaultWeights)
