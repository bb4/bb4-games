/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye

import com.barrybecker4.game.twoplayer.go.GoTestCase
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.{EyeInformation, EyeStatus, EyeType}
import com.barrybecker4.game.twoplayer.go.board.analysis.group.{GroupAnalyzer, GroupAnalyzerMap}
import com.barrybecker4.game.twoplayer.go.board.elements.group.{GoGroup, IGoGroup}
import org.junit.Assert.assertEquals
import scala.jdk.CollectionConverters._

/**
 * Test that we can get the correct type and status for all the different eyes that can arise.
 * @author Barry Becker
 */
abstract class TestEyeTypeAnalyzer extends GoTestCase:

  protected enum GroupType:
    case BIGGEST, SURROUNDED

  protected def initializeBoard(eyesProblemFile: String): GoBoard =
    initializeBoard(eyesProblemFile, 2)

  /** @return Place where test SGF files are stored. */
  protected val PATH_PREFIX = "board/analysis/eye/information/"

  protected def getPathPrefix: String =
    PATH_PREFIX + getEyeType.toString + "/"

  protected def getEyeType: EyeType

  /**
   * @param eyesProblemFile saved sgf game file to load
   * @return the initialized board. Must have 2 groups.
   */
  protected def initializeBoard(eyesProblemFile: String, expectedNumGroups: Int): GoBoard =
    System.out.println("finding eyes for " + eyesProblemFile + "...")
    restore(getPathPrefix + eyesProblemFile)
    val board = getBoard
    val groups = board.getGroups
    assertEquals("Unexpected number of groups.", expectedNumGroups, groups.size)
    board

  protected def checkBlackEye(board: GoBoard, expectedInfo: EyeInformation, expectedStatus: EyeStatus): Unit =
    checkEyeInfo(board, expectedInfo, expectedStatus, true, false, false, GroupType.BIGGEST)

  protected def checkWhiteEye(board: GoBoard, expectedInfo: EyeInformation, expectedStatus: EyeStatus): Unit =
    checkEyeInfo(board, expectedInfo, expectedStatus, false, false, false, GroupType.BIGGEST)

  protected def checkEdgeBlackEye(board: GoBoard, expectedInfo: EyeInformation, expectedStatus: EyeStatus): Unit =
    checkEyeInfo(board, expectedInfo, expectedStatus, true, false, true, GroupType.BIGGEST)

  protected def checkEdgeWhiteEye(board: GoBoard, expectedInfo: EyeInformation, expectedStatus: EyeStatus): Unit =
    checkEyeInfo(board, expectedInfo, expectedStatus, false, false, true, GroupType.BIGGEST)

  protected def checkCornerBlackEye(board: GoBoard, expectedInfo: EyeInformation, expectedStatus: EyeStatus): Unit =
    checkEyeInfo(board, expectedInfo, expectedStatus, true, true, true, GroupType.BIGGEST)

  protected def checkCornerWhiteEye(board: GoBoard, expectedInfo: EyeInformation, expectedStatus: EyeStatus): Unit =
    checkEyeInfo(board, expectedInfo, expectedStatus, false, true, true, GroupType.BIGGEST)

  /** Check the eye of the surrounded group, not the biggest group as we usually do. */
  protected def checkBlackEyeSurrounded(board: GoBoard, expectedInfo: EyeInformation, expectedStatus: EyeStatus): Unit =
    checkEyeInfo(board, expectedInfo, expectedStatus, true, false, false, GroupType.SURROUNDED)

  /** Check the eye of the surrounded group, not the biggest group as we usually do. */
  protected def checkWhiteEyeSurrounded(board: GoBoard, expectedInfo: EyeInformation, expectedStatus: EyeStatus): Unit =
    checkEyeInfo(board, expectedInfo, expectedStatus, false, false, false, GroupType.SURROUNDED)

  protected def countGroupsWithOneEye(board: GoBoard, isBlack: Boolean): Int =
    board.getGroups.asScala.count { group =>
      val stones = group.asInstanceOf[GoGroup].getStones
      stones.iterator.next.getPiece.isOwnedByPlayer1 == isBlack &&
        new GroupAnalyzer(group, new GroupAnalyzerMap()).getEyes(board).size == 1
    }

  /** Check information and status for specified eye. */
  protected def checkEyeInfo(
    board: GoBoard,
    expectedInfo: EyeInformation,
    expectedStatus: EyeStatus,
    isBlack: Boolean,
    isInCorner: Boolean,
    isOnEdge: Boolean,
    groupType: GroupType
  ): Unit =
    val group = getGroupToCheck(isBlack, groupType, board)
    val groupAnalyzer = new GroupAnalyzer(group, new GroupAnalyzerMap())
    val eyes = groupAnalyzer.getEyes(board)
    assertEquals("The group\n" + group + "\n did not have one eye.", 1, eyes.size)
    val firstEye = groupAnalyzer.getEyes(board).iterator.next()
    val eyeAnalyzer = new EyeTypeAnalyzer(firstEye, board, groupAnalyzer)
    val information = eyeAnalyzer.determineEyeInformation()
    val eyeColor = if isBlack then "black" else "white"
    assertEquals("Unexpected information found for " + eyeColor + " eye.", expectedInfo, information)
    val status = information.determineStatus(firstEye, board)
    assertEquals("Unexpected status found for " + eyeColor + " eye in group=" + group, expectedStatus, status)
    assertEquals("Corner status unexpected.", isInCorner, information.isInCorner(firstEye))
    assertEquals("Edge status unexpected.", isOnEdge, information.isOnEdge(firstEye))

  private def getGroupToCheck(isBlack: Boolean, groupType: GroupType, board: GoBoard): IGoGroup =
    groupType match
      case GroupType.BIGGEST => getBiggestGroup(isBlack)
      case GroupType.SURROUNDED => getSurroundedGroup(isBlack, board)

  /**
   * @param isBlack true if black
   * @return a large group of the specified side with 2 or fewer liberties.
   */
  protected def getSurroundedGroup(isBlack: Boolean, board: GoBoard): IGoGroup =
    val groups = getBoard.getGroups
    var surroundedGroup: IGoGroup = null
    for group <- groups.asScala do
      val stones = group.asInstanceOf[GoGroup].getStones
      if stones.iterator.next.getPiece.isOwnedByPlayer1 == isBlack then
        if surroundedGroup == null ||
          (group.getNumStones > 5 && group.getLiberties(board).size < 3) then
          surroundedGroup = group
    surroundedGroup
