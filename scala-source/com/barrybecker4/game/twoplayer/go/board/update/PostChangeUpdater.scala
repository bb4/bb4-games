/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.update

import com.barrybecker4.game.twoplayer.go.board.{BoardValidator, GoBoard, GoProfiler}
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer
import com.barrybecker4.game.twoplayer.go.board.elements.group.{GoGroup, GoGroupSet, IGoGroup}
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionList}
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import scala.jdk.CollectionConverters._

/**
 * Base class for classes responsible for updating a go board after making or undoing a move.
 *
 * @author Barry Becker
 */
abstract class PostChangeUpdater(protected val board_ : GoBoard, protected val captureCounter_ : CaptureCounts) {

  protected val nbrAnalyzer_ : NeighborAnalyzer = new NeighborAnalyzer(board_)
  protected val validator_ : BoardValidator = new BoardValidator(board_)
  protected val profiler_ : GoProfiler = GoProfiler.getInstance

  /**
   * Update the strings and groups on the board after a move change (move or remove).
   * @param move the move that was just made or undone.
   */
  def update(move: GoMove): Unit

  def getBoard: GoBoard = board_

  def getAllGroups: GoGroupSet = board_.getGroups

  /**
   * The structure of the groups can change after a move.
   * First remove all the groups, and then find them again.
   */
  def recreateGroupsAfterChange(): Unit = {
    val groups = new GoGroupSet()

    addFluxGroups(groups)

    board_.setGroups(groups)
    board_.unvisitAll()
  }

  /**
   * Add back the groups that are in flux due to the changed position.
   */
  private def addFluxGroups(groups: GoGroupSet): Unit = {
    for (i <- 1 to getBoard.getNumRows) {
      for (j <- 1 to getBoard.getNumCols) {
        val seed = getBoard.getPosition(i, j).asInstanceOf[GoBoardPosition]
        if (seed.isOccupied && !seed.isVisited) {
          val newGroup = nbrAnalyzer_.findGroupFromInitialPosition(seed, false)
          val g = new GoGroup(newGroup)
          groups.add(g)
        }
      }
    }
  }

  /**
   * remove groups that have no stones in them.
   */
  def cleanupGroups(): Unit = {
    val newGroups = new GoGroupSet()

    getAllGroups.asScala.foreach { group =>
      if (group.getNumStones > 0) {
        newGroups.add(group)
      }
    }
    board_.setGroups(newGroups)
  }

  /**
   * clear all the eyes from all the stones on the board
   */
  def clearEyes(): Unit = {
    for (i <- 1 to board_.getNumRows) {
      for (j <- 1 to board_.getNumCols) {
        val space = board_.getPosition(i, j).asInstanceOf[GoBoardPosition]
        if (space.isInEye) {
          // remove reference to the owning group so it can be garbage collected.
          space.getEye.clear()
          space.setEye(null)
        }
      }
    }
  }
}
