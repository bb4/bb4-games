// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis.differencers

import com.barrybecker4.game.twoplayer.gomoku.{GoMokuBoard, GoMokuController}
import com.barrybecker4.game.twoplayer.gomoku.pattern.{GoMokuPatterns, Patterns, SimpleWeights}
import com.barrybecker4.optimization.parameter.ParameterArray
import org.junit.Assert.assertEquals
import org.junit.Before

/**
 * Base class for all differencer tests.
 *
 * @author Barry Becker
 */
abstract class ValueDifferencerTst:

  /** instance under test */
  protected var differencer: ValueDifferencer = scala.compiletime.uninitialized
  protected var differencerFactory: ValueDifferencerFactory = scala.compiletime.uninitialized
  protected var weights: ParameterArray = new SimpleWeights().getDefaultWeights
  protected var lineFactory: StubLineFactory = scala.compiletime.uninitialized

  @Before
  def setUp(): Unit =
    val board = restoreBoard("exampleBoard")
    val patterns: Patterns = new GoMokuPatterns()
    lineFactory = new StubLineFactory()
    differencerFactory = new ValueDifferencerFactory(patterns, lineFactory)
    differencer = createDifferencer(board, patterns)

  protected def createDifferencer(board: GoMokuBoard, patterns: Patterns): ValueDifferencer

  /**
   * Restore a game file
   * @param problemFileBase the saved game to restore and test.
   */
  private def restoreBoard(problemFileBase: String): GoMokuBoard =
    val controller = new GoMokuController[GoMokuBoard]()
    val path = ValueDifferencerTst.TEST_CASE_DIR + problemFileBase + ".sgf"
    val iStream = getClass.getResourceAsStream(path)
    if iStream == null then
      throw new IllegalArgumentException("bad path: " + path)

    controller.restoreFromStream(getClass.getResourceAsStream(path))
    controller.getBoard.asInstanceOf[GoMokuBoard]

  protected def verifyLine(row: Int, col: Int, expectedLineContent: String): Unit =
    val diff = differencer.findValueDifference(row, col, weights)
    assertEquals("Unexpected difference", 0, diff)
    assertEquals("Unexpected line created", expectedLineContent, lineFactory.getLineContent)

object ValueDifferencerTst:
  private val TEST_CASE_DIR =
    "/com/barrybecker4/game/twoplayer/gomoku/cases/analysis/differencers/"
