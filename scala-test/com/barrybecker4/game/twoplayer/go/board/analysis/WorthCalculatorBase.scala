/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis

import com.barrybecker4.game.common.Move
import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap
import com.barrybecker4.game.twoplayer.go.options.GoWeights
import org.junit.Assert.assertEquals
import org.junit.Before

/**
 * Verify that we calculate the expected worth for a given board position.
 * @author Barry Becker
 */
abstract class WorthCalculatorBase extends GoTestCase:

  protected val PREFIX = "board/"
  protected val WEIGHTS = new GoWeights()
  protected var calculator: WorthCalculator = scala.compiletime.uninitialized

  @Before
  override def setUp(): Unit =
    super.setUp()
    val board = getBoard
    val analyzerMap = new GroupAnalyzerMap()
    calculator = new WorthCalculator(board, analyzerMap)

  protected def verifyWorth(file: String, expWorth: Int): Unit =
    restore(PREFIX + file)
    val move = controller.getLastMove
    val actWorth = calculator.worth(move, WEIGHTS.getDefaultWeights)
    assertEquals("Unexpected worth.", expWorth, actWorth)

  /**
   * If we arrive at the same exact board position from two different paths,
   * we should calculate the same worth value.
   */
  protected def compareWorths(game1: String, game2: String, expWorth: Int): Unit =
    restore(PREFIX + game1)
    var move: Move = controller.getLastMove
    val actWorthA = calculator.worth(move, WEIGHTS.getDefaultWeights)
    controller.reset()
    restore(PREFIX + game2)
    move = controller.getLastMove
    val actWorthB = calculator.worth(move, WEIGHTS.getDefaultWeights)
    assertEquals("Unexpected worth for A.", expWorth, actWorthA)
    assertEquals("Unexpected worth for B.", expWorth, actWorthB)
