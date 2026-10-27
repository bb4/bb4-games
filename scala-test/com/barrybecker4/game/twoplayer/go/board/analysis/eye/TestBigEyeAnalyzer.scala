/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye

import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.{EyeInformation, EyeType}
import com.barrybecker4.game.twoplayer.go.board.analysis.group.{GroupAnalyzer, GroupAnalyzerMap}
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Test that we can get the correct type and status for all the different eyes that can arise.
 * @author Barry Becker
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
    assertEquals("The group\n" + group + "\n did not have one eye", 1, eyes.size)
    val firstEye = groupAnalyzer.getEyes(board).iterator.next()
    val eyeAnalyzer = new BigEyeAnalyzer(firstEye)
    val information = eyeAnalyzer.determineEyeInformation()
    val eyeColor = if isBlack then "black" else "white"
    assertEquals("Unexpected information found for " + eyeColor + " eye.", expectedInfo, information)
    assertEquals("Corner status unexpected", isInCorner, information.isInCorner(firstEye))
    assertEquals("Edge status unexpected", isOnEdge, information.isOnEdge(firstEye))
