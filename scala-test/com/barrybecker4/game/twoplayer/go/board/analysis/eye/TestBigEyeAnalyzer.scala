/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye

import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.{EyeInformation, EyeType}
import com.barrybecker4.game.twoplayer.go.board.analysis.group.{GroupAnalyzer, GroupAnalyzerMap}
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Test that we can get the correct type and status for all the different eyes that can arise.
 */
class TestBigEyeAnalyzer extends GoTestCase:

  private val PATH_PREFIX = "board/analysis/eye/"

  @Test
  def testE111223(): Unit =
    restore(PATH_PREFIX + "BigEye_E111223")
    val board = getBoard
    checkEyeType(board, EyeType.E6.getInformation("E111223"), true, true, true)

  @Test
  def testE111223b(): Unit =
    restore(PATH_PREFIX + "BigEye_E111223b")
    val board = getBoard
    checkEyeType(board, EyeType.E6.getInformation("E111223"), true, true, true)

  /** Check information for specified eye. */
  protected def checkEyeType(
    board: GoBoard,
    expectedInfo: EyeInformation,
    isBlack: Boolean,
    isInCorner: Boolean,
    isOnEdge: Boolean
  ): Unit =
    val group = getBiggestGroup(isBlack)
    val groupAnalyzer = new GroupAnalyzer(group, new GroupAnalyzerMap())
    val eyes = groupAnalyzer.getEyes(board)
    assertEquals(1, eyes.size, "The group\n" + group + "\n did not have one eye")
    val firstEye = groupAnalyzer.getEyes(board).iterator.next()
    val eyeAnalyzer = new BigEyeAnalyzer(firstEye)
    val information = eyeAnalyzer.determineEyeInformation()
    val eyeColor = if isBlack then "black" else "white"
    assertEquals(expectedInfo, information, "Unexpected information found for " + eyeColor + " eye.")
    assertEquals(isInCorner, information.isInCorner(firstEye), "Corner status unexpected")
    assertEquals(isOnEdge, information.isOnEdge(firstEye), "Edge status unexpected")
