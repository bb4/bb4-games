/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.update

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.NeighborType
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionSet}
import com.barrybecker4.game.twoplayer.go.board.elements.string.{GoString, GoStringSet, IGoString}
import com.barrybecker4.game.twoplayer.go.board.move.{GoCaptureList, GoMove}
import scala.jdk.CollectionConverters._

/**
 * Responsible for updating a go board after making a move.
 *
 * @author Barry Becker
 */
class PostMoveUpdater(board: GoBoard, captureCounter: CaptureCounts) 
  extends PostChangeUpdater(board, captureCounter) {

  /**
   * Update the board after move has been played.
   * @param move the move that was just made.
   */
  override def update(move: GoMove): Unit = {
    clearEyes()
    val stone = getBoard.getPosition(move.getToLocation).asInstanceOf[GoBoardPosition]

    board_.adjustLiberties(stone)

    val captures = determineCaptures(stone)
    move.setCaptures(captures)

    updateStringsAfterMove(stone)
    captures.removeFromBoard(getBoard)

    assert(stone.getString.getNumLiberties(getBoard) > 0,
      s"The placed stone $stone has no liberties ${stone.getGroup}\n${getBoard.toString}")
    updateGroupsAfterMove(stone)
    captureCounter_.updateCaptures(move, true)
  }

  /**
   * Determine a list of enemy stones that are captured when this stone is played on the board.
   * In other words determine all opponent strings (at most 4) whose last liberty is at the new stone location.
   * @param stone stone that was just placed and caused stones to be captured (if any)
   * @return list of captured stones. Empty if no captures.
   */
  private def determineCaptures(stone: GoBoardPosition): GoCaptureList = {
    profiler_.startFindCaptures()
    assert(stone != null)
    val nbrs = nbrAnalyzer_.getNobiNeighbors(stone, NeighborType.ENEMY)
    val captureList = new GoCaptureList()
    // keep track of the strings captured so we don't capture the same one twice
    val capturedStrings = new GoStringSet()

    nbrs.asScala.foreach { enbr =>
      assert(enbr.isOccupied, s"enbr=$enbr")

      val str = enbr.getString
      assert(str.isOwnedByPlayer1 != stone.getPiece.isOwnedByPlayer1,
        s"The $str is not an enemy of $stone")
      assert(str.size > 0, s"String has 0 stones: $str")

      if (str.getNumLiberties(getBoard) == 0 && !capturedStrings.contains(str)) {
        capturedStrings.add(str)
        // we need to add copies so that when the original stones on the board are
        // changed we don't change the captures.
        captureList.addCaptures(str.getMembers)
      }
    }
    profiler_.stopFindCaptures()
    captureList
  }

  /**
   * Examine the neighbors of this added stone and determine how the strings have changed.
   * For strings: examine the strongly connected neighbors. If more than one string borders, then
   * we merge the strings. If only one borders, then we add this stone to that string. If no strings
   * touch the added stone, then we create a new string containing only this stone.
   * @param stone the stone that was just placed on the board.
   */
  private def updateStringsAfterMove(stone: GoBoardPosition): Unit = {
    profiler_.startUpdateStringsAfterMove()

    val nbrs = nbrAnalyzer_.getNobiNeighbors(stone, NeighborType.FRIEND)

    if (nbrs.size == 0) {
      // there are no strongly connected neighbors, create a new string
      new GoString(stone, getBoard) // stone points to the new string
    } else {
      updateNeighborStringsAfterMove(stone, nbrs)
    }
    cleanupGroups()
    profiler_.stopUpdateStringsAfterMove()
  }

  /**
   * There is at least one neighbor string, so we will join to it/them.
   * @param stone position where we just placed a stone.
   * @param nbrs neighbors
   */
  private def updateNeighborStringsAfterMove(stone: GoBoardPosition, nbrs: GoBoardPositionSet): Unit = {
    val nbrStone = nbrs.getOneMember
    val str = nbrStone.getString.asInstanceOf[GoString]
    str.addMember(stone, getBoard)
    getAllGroups.debugPrint(3, "groups before merging:", showBlack = true, showWhite = true)

    if (nbrs.size > 1) {
      mergeStringsIfNeeded(str, nbrs)
    }
  }

  /**
   * Then we probably need to merge the strings.
   * We will not, for example, if we are completing a clump of four.
   */
  private def mergeStringsIfNeeded(str: GoString, nbrs: GoBoardPositionSet): Unit = {
    nbrs.asScala.foreach { nbrStone =>
      // if its the same string then there is nothing to merge
      val nbrString = nbrStone.getString
      if (str ne nbrString) {
        str.merge(nbrString, getBoard)
      }
    }
  }

  /**
   * First remove all the groups on the board.
   * Then for each stone, find its group and add that new group to the board's group list.
   * Continue until all stone accounted for.
   * @param pos the stone that was just placed on the board.
   */
  private def updateGroupsAfterMove(pos: GoBoardPosition): Unit = {
    profiler_.startUpdateGroupsAfterMove()

    if (GameContext.getDebugMode > 1) {
      getAllGroups.confirmAllStonesInUniqueGroups()
    }
    profiler_.startRecreateGroupsAfterMove()
    recreateGroupsAfterChange()
    profiler_.stopRecreateGroupsAfterMove()

    // verify that the string to which we added the stone has at least one liberty
    assert(pos.getString.getNumLiberties(getBoard) > 0,
      s"The placed stone $pos has no liberties ${pos.getGroup}")

    profiler_.stopUpdateGroupsAfterMove()
  }
}
