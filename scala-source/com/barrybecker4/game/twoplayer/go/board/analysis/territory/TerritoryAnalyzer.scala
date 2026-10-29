/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.territory

import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition

/**
 * Analyzes territory on board.
 */
class TerritoryAnalyzer(val board: GoBoard, val analyzerMap: GroupAnalyzerMap) {

  /**
   * Get an estimate of the territory for the specified player.
   * This estimate is computed by summing all spaces in eyes with dead opponent stones that are still on the board.
   * Empty spaces are weighted by how likely they are to eventually be territory of one side or the other.
   * At the end of the game this + the number of pieces captured so far should give the true score.
   * @param forPlayer1 player to get the estimate for.
   * @param isEndOfGame use 0 or 1 instead of pos.scoreContribution if true.
   * @return estimate of territory for forPlayer1
   */
  def getTerritoryEstimate(forPlayer1: Boolean, isEndOfGame: Boolean): Int = {
    var territoryEstimate = 0.0f

    for (i <- 1 to board.getNumRows) {
      for (j <- 1 to board.getNumCols) {
        val pos = board.getPosition(i, j).asInstanceOf[GoBoardPosition]
        territoryEstimate += getTerritoryEstimateForPosition(pos, forPlayer1, isEndOfGame)
      }
    }
    territoryEstimate.toInt
  }

  /**
   * @return the estimate for a single position.
   */
  private def getTerritoryEstimateForPosition(pos: GoBoardPosition, forPlayer1: Boolean,
                                              isEndOfGame: Boolean): Float = {

    val v = if (isEndOfGame) (if (forPlayer1) 1.0 else -1.0) else pos.getScoreContribution
    var territoryEstimate = 0.0f

    if (pos.isUnoccupied) {
      if (forPlayer1 && pos.getScoreContribution > 0) {
        territoryEstimate += v.toFloat
      }
      else if (!forPlayer1 && pos.getScoreContribution < 0) {
        territoryEstimate -= v.toFloat
      }
    }
    else {
      val piece = pos.getPiece
      val group = pos.getGroup
      assert(piece != null)
      if (group != null) {
        val relHealth = analyzerMap.getAnalyzer(group).getRelativeHealth(board, isEndOfGame)
        if (forPlayer1 && !piece.isOwnedByPlayer1 && relHealth >= 0) {
          territoryEstimate += v.toFloat
        }
        else if (!forPlayer1 && piece.isOwnedByPlayer1 && relHealth <= 0) {
          territoryEstimate -= v.toFloat
        }
      }
    }
    territoryEstimate
  }
}
