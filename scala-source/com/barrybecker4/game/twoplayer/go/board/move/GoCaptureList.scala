/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.move

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.{Board, BoardPosition, CaptureList}
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer
import com.barrybecker4.game.twoplayer.go.board.elements.group.{GoGroup, IGoGroup}
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionList, GoBoardPositionLists, GoBoardPositionSet}
import com.barrybecker4.game.twoplayer.go.board.elements.string.{GoString, IGoString}
import scala.jdk.CollectionConverters._

/**
 * This class represents a linked list of captured pieces.
 * It provides convenience methods for removing and restoring those
 * pieces to a game board.
 *
 * @author Barry Becker
 */
class GoCaptureList extends CaptureList {

  /**
   * copy constructor
   */
  def this(captureList: CaptureList) = {
    this()
    this.addAll(captureList)
  }

  /**
   * @return a deep copy of the capture list.
   */
  override def copy(): GoCaptureList = {
    new GoCaptureList(this)
  }

  /**
   * we need to add copies so that when the original stones on the board are
   * changed we don't change the captures
   * @param set set of go stones to add to the capture list. If null, then none will be added.
   * @return true if set is not null or 0 sized.
   */
  def addCaptures(set: GoBoardPositionSet): Boolean = {
    if (set == null) {
      return false
    }

    set.asScala.foreach { capture =>
      // make sure none of the captures are blanks
      assert(capture.isOccupied)
      add(capture.copy())
    }
    !set.isEmpty
  }

  /**
   * remove the captured pieces from the board.
   */
  override def removeFromBoard(board: Board[?]): Unit = {
    val goBoard = board.asInstanceOf[GoBoard]
    this.asScala.foreach { c =>
      val capStone = c.asInstanceOf[GoBoardPosition]
      val stoneOnBoard = goBoard.getPosition(capStone.getLocation).asInstanceOf[GoBoardPosition]
      stoneOnBoard.clear(goBoard)
    }

    adjustStringLiberties(goBoard)
  }

  /**
   * Restore the captured pieces on the board.
   */
  override def restoreOnBoard(board: Board[?]): Unit = {
    val goBoard = board.asInstanceOf[GoBoard]
    super.modifyCaptures(goBoard, false)

    GameContext.log(3, s"GoMove: restoring these captures: $this")

    val strings = getRestoredStringLists(goBoard) // XXX should remove
    adjustStringLiberties(goBoard)

    // XXX should remove next lines
    val group = getRestoredGroup(strings, goBoard)

    assert(group != null, s"no group was formed when restoring $this the list of strings was $strings")
    goBoard.getGroups.add(group)
  }

  /**
   * Update the liberties of the surrounding strings.
   */
  def adjustStringLiberties(board: GoBoard): Unit = {
    this.asScala.foreach { capture =>
      val captured = capture.asInstanceOf[GoBoardPosition]
      val newLiberty = board.getPosition(captured.getLocation).asInstanceOf[GoBoardPosition]
      board.adjustLiberties(newLiberty)
    }
  }

  /**
   * There may have been more than one string in the captureList
   * @return list of strings that were restored on the board.
   */
  private def getRestoredStringLists(b: GoBoard): GoBoardPositionLists = {
    val restoredList = getRestoredList(b)
    val strings = new GoBoardPositionLists()
    val nbrAnalyzer = new NeighborAnalyzer(b)
    restoredList.asScala.foreach { s =>
      if (!s.isVisited) {
        val string1 = nbrAnalyzer.findStringFromInitialPosition(s, false)
        strings.add(string1)
      }
    }
    strings
  }

  /**
   * @return list of captured stones that were restored on the board.
   */
  private def getRestoredList(b: GoBoard): GoBoardPositionList = {
    val restoredList = new GoBoardPositionList()
    this.asScala.foreach { pos =>
      val capStone = pos.asInstanceOf[GoBoardPosition]
      val stoneOnBoard = b.getPosition(capStone.getRow, capStone.getCol).asInstanceOf[GoBoardPosition]
      stoneOnBoard.setVisited(false) // make sure in virgin unvisited state

      restoredList.add(stoneOnBoard)
    }
    restoredList
  }

  /**
   * @return the group that was restored when the captured stones were replaced on the board.
   */
  private def getRestoredGroup(strings: GoBoardPositionLists, b: GoBoard): IGoGroup = {
    var firstString = true
    var group: IGoGroup = null
    strings.asScala.foreach { stringList =>
      val string = new GoString(stringList, b)
      if (firstString) {
        group = new GoGroup(string)
        firstString = false
      } else {
        group.addMember(string)
      }
      string.setVisited(false)
    }
    group
  }
}
