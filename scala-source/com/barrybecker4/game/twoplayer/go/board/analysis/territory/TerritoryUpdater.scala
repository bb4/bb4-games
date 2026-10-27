// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.board.analysis.territory

import com.barrybecker4.game.twoplayer.go.GoController
import com.barrybecker4.game.twoplayer.go.board.{GoBoard, GoProfiler}
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import scala.jdk.CollectionConverters._

/**
 * Analyzes territory on board.
 *
 * @author Barry Becker
 */
class TerritoryUpdater(val board_ : GoBoard, val analyzerMap_ : GroupAnalyzerMap) {

  private var territoryDelta_ = 0.0f

  def getTerritoryDelta: Float = territoryDelta_

  /**
   * Loops through the groups to determine the territorial difference between the players.
   * Then it loops through and determines a score for positions that are not part of groups.
   * If a position is part of an area that borders only a living group, then it is considered
   * territory for that group's side. If, however, the position borders living groups from
   * both sides, then the score is weighted according to the proportion of the perimeter
   * that borders each living group and how alive those bordering groups are.
   * This is the primary factor in evaluating the board position for purposes of search.
   * This method and the methods it calls are the crux of this go playing program.
   *
   * @return the estimated difference in territory between the 2 sides.
   *  A large positive number indicates black is winning, while a negative number indicates that white has the edge.
   */
  def updateTerritory(isEndOfGame: Boolean): Float = {
    clearScores()

    val prof = GoProfiler.getInstance
    prof.startUpdateTerritory()
    analyzerMap_.clear()

    var delta = calcAbsoluteHealth()
    if (GoController.USE_RELATIVE_GROUP_SCORING) {
      delta = calcRelativeHealth(prof, delta)
    }
    prof.startUpdateEmpty()
    if (isEndOfGame) {
      val emptyUpdater = new EmptyRegionUpdater(board_, analyzerMap_)
      delta += emptyUpdater.updateEmptyRegions()
    }
    prof.stopUpdateEmpty()

    prof.stopUpdateTerritory()
    territoryDelta_ = delta

    delta
  }

  /**
   * Clear whatever cached score state we might have before recomputing.
   */
  private def clearScores(): Unit = {
    for (i <- 1 to board_.getNumRows) {
      for (j <- 1 to board_.getNumCols) {
        val pos = board_.getPosition(i, j).asInstanceOf[GoBoardPosition]
        pos.setScoreContribution(0)

        if (pos.isOccupied) {
          val stone = pos.getPiece.asInstanceOf[GoStone]
          stone.setHealth(0)
          val string = pos.getString
          if (string != null) {
            string.setUnconditionallyAlive(false)
          }
        }
      }
    }
  }

  /**
   * First calculate the absolute health of the groups so that measure can
   * be used in the more accurate relative health computation.
   * @return total health of all stones in all groups in absolute terms.
   */
  private def calcAbsoluteHealth(): Float = {
    var delta = 0.0f
    GoProfiler.getInstance.startAbsoluteTerritory()

    for (g <- board_.getGroups.asScala) {
      val health = analyzerMap_.getAnalyzer(g).calculateAbsoluteHealth(board_)

      if (!GoController.USE_RELATIVE_GROUP_SCORING) {
        g.updateTerritory(health)
        delta += health * g.getNumStones
      }
    }
    GoProfiler.getInstance.stopAbsoluteTerritory()
    delta
  }

  /**
   * @param initDelta initial value.
   * @return total health of all stones in all groups in relative terms.
   */
  private def calcRelativeHealth(prof: GoProfiler, initDelta: Float): Float = {
    var delta = initDelta

    prof.startRelativeTerritory()
    for (g <- board_.getGroups.asScala) {
      val health = analyzerMap_.getAnalyzer(g).calculateRelativeHealth(board_)
      g.updateTerritory(health)
      delta += health * g.getNumStones
    }

    prof.stopRelativeTerritory()

    delta
  }
}
