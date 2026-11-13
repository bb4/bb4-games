// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.board.analysis.territory

import com.barrybecker4.game.twoplayer.go.GoController
import com.barrybecker4.game.twoplayer.go.board.{GoBoard, GoProfiler}
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap
import scala.jdk.CollectionConverters._

/**
 * Analyzes territory on board.
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
    // Reuse GroupAnalyzers across same-board re-evals so GroupEyeCache can hit.
    // After a move, groups are recreated and getAnalyzer builds new analyzers.
    // FORCE_NEW_ANALYZERS restores the old nuclear clear for bisecting regressions.
    if (TerritoryUpdater.FORCE_NEW_ANALYZERS) {
      analyzerMap_.clear()
    }

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
    board_.clearTransientAnalysisMarkers()
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

object TerritoryUpdater {
  /**
   * When true, discard all group analyzers before each territory update (pre-issue-17 behavior).
   * Leave false so eye/health caches survive same-board re-evaluation.
   */
  var FORCE_NEW_ANALYZERS: Boolean = false
}
