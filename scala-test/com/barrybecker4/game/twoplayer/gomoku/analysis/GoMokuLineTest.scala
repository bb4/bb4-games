/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.analysis

import com.barrybecker4.game.common.GameWeights
import com.barrybecker4.game.twoplayer.gomoku.GoMokuTestWeights
import com.barrybecker4.game.twoplayer.gomoku.pattern.GoMokuPatterns
import org.junit.Assert.assertEquals
import org.junit.{Before, Test}

import scala.jdk.CollectionConverters.*

/**
 * Verify that we correctly evaluate patterns on the board.
 *
 * @author Barry Becker
 */
class GoMokuLineTest:

  private var weights: GameWeights = scala.compiletime.uninitialized
  private val PLAYER1_PERSP = true
  private val PLAYER2_PERSP = false

  @Before
  def setUp(): Unit =
    weights = new GoMokuTestWeights()

  /** from the perspective of X */
  @Test
  def testEvalLinePlayer1Short_p1Persp(): Unit =
    checkLine("_X_XO", 1, PLAYER1_PERSP, 2, Array("_X_X"))
    checkLine("_X_XO", 2, PLAYER1_PERSP, 2, Array("_X_X"))
    checkLine("_X_XO", 3, PLAYER1_PERSP, 2, Array("_X_X"))
    checkLine("_X_XO", 4, PLAYER1_PERSP, 0, Array(""))
    checkLine("_XX", 1, PLAYER1_PERSP, 0, Array("_XX"))
    checkLine("_XX_", 2, PLAYER1_PERSP, 8, Array("_XX_"))
    checkLine("_XX_", 3, PLAYER1_PERSP, 8, Array("_XX_"))

  /** from the perspective of O */
  @Test
  def testEvalLinePlayer1Short_p2Persp(): Unit =
    checkLine("_X_XO", 3, PLAYER2_PERSP, 0, Array("_"))
    checkLine("_X_XO", 4, PLAYER2_PERSP, 0, Array("O"))
    checkLine("_XX_", 2, PLAYER2_PERSP, 0, Array[String]())
    checkLine("_XX_", 3, PLAYER2_PERSP, 0, Array("_"))

  @Test
  def testEvalLinePlayer1Long(): Unit =
    checkLine("_XX_XX__", 1, PLAYER1_PERSP, 320, Array("_XX_XX_"))
    checkLine("_XX_XX__", 3, PLAYER1_PERSP, 320, Array("_XX_XX_"))
    checkLine("_XX_XX__", 3, PLAYER2_PERSP, 0, Array("_"))
    checkLine("_XX_XX__", 4, PLAYER1_PERSP, 320, Array("_XX_XX_"))
    checkLine("_XX_XX__", 6, PLAYER1_PERSP, 320, Array("_XX_XX_"))
    checkLine("_XX_XX__", 1, PLAYER1_PERSP, 320, Array("_XX_XX_"))
    checkLine("_XX_XOX_X__", 1, PLAYER1_PERSP, 8, Array("_XX_X"))
    checkLine("_XX_XOX_X__", 1, PLAYER2_PERSP, 0, Array(""))
    checkLine("_XX_XOX_X__", 5, PLAYER1_PERSP, 10, Array("_XX_X", "X_X_"))
    checkLine("_XX_XOX_X__", 5, PLAYER2_PERSP, 0, Array("O"))
    checkLine("_OO_OXO_O__", 5, PLAYER1_PERSP, 0, Array("X"))
    checkLine("_OO_OXO_O__", 5, PLAYER2_PERSP, -10, Array("_OO_O", "O_O_"))

  private def checkLine(linePattern: String, position: Int, player1persp: Boolean, expectedWorth: Int,
                        expectedPatternsChecked: Array[String]): Unit =
    val evaluator = new StubLineEvaluator(new GoMokuPatterns(), weights.getDefaultWeights)
    val line = new StringBuilder(linePattern)
    val worth = evaluator.evaluate(line, player1persp, position, 0, linePattern.length - 1)

    assertEquals("unexpected score for pattern " + linePattern + " pos=" + position + " player1Persp=" + player1persp,
      expectedWorth, worth)

    val checkedPats = evaluator.getPatternsChecked
    assertEquals("Unexpected number of patterns checked",
      expectedPatternsChecked.length, checkedPats.size)
    var i = 0
    for p <- checkedPats.asScala do
      assertEquals(expectedPatternsChecked(i), p)
      i += 1
