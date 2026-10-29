// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.board.analysis.territory

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.{NeighborAnalyzer, NeighborType}
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionList, GoBoardPositionLists, GoBoardPositionSet}

/**
 * Updates the empty regions on the board at the end of the game.
 * Used to determine ownership of those empty regions.
 * Does it handle dame's correctly?
 */
class EmptyRegionUpdater(val board_ : GoBoard, val analyzerMap_ : GroupAnalyzerMap) {

  private val STONE_IN_EYE_CONTRIB = 0.1f

  private val nbrAnalyzer_ = new NeighborAnalyzer(board_)

  /**
   * Need to loop over the board and determine for each space if it is territory for the specified player.
   * We will first mark visited all the stones that are "controlled" by the specified player.
   * The unoccupied "controlled" positions will be territory.
   * @return the change in score after updating the empty regions.
   */
  def updateEmptyRegions(): Float = {
    var diffScore = 0.0f

    if (board_.getMoveList.getNumMoves <= 2 * board_.getNumRows) {
      return diffScore
    }

    val min = 1
    val rMax = board_.getNumRows
    val cMax = board_.getNumCols
    val box = new Box(new com.barrybecker4.common.geometry.ByteLocation(min, min),
      new com.barrybecker4.common.geometry.ByteLocation(rMax, cMax))

    val emptyLists = new GoBoardPositionLists()

    for (i <- min to rMax) {
      for (j <- min to cMax) {
        val pos = board_.getPosition(i, j).asInstanceOf[GoBoardPosition]
        diffScore += updateEmptyRegionFromSeed(box, emptyLists, pos)
      }
    }

    emptyLists.unvisitPositionsInLists()
    diffScore
  }

  /**
   * Update diffScore for the string connected to pos and mark it visited.
   * If pos is in an eye, update the score contribution for that eye space.
   * @return diffScore value.
   */
  private def updateEmptyRegionFromSeed(box: Box, emptyLists: GoBoardPositionLists,
                                        pos: GoBoardPosition): Float = {
    var diffScore = 0.0f
    if (pos.getString == null && !pos.isInEye) {
      assert(pos.isUnoccupied)
      if (!pos.isVisited) {
        val empties =
          nbrAnalyzer_.findStringFromInitialPosition(pos, false, false, NeighborType.UNOCCUPIED, box)
        emptyLists.add(empties)

        val nbrs = nbrAnalyzer_.findOccupiedNobiNeighbors(empties)
        val avg = calcAverageScore(nbrs)

        val score = avg * nbrs.size.toFloat / Math.max(1, Math.max(nbrs.size, empties.size))
        assert(score <= 1.0 && score >= -1.0, s"score=$score avg=$avg")

        for (space <- empties.toArray(new Array[GoBoardPosition](0))) {
          space.setScoreContribution(score)
          diffScore += score
        }
      }
    }
    else if (pos.isInEye) {
      pos.setScoreContribution(if (pos.getGroup.isOwnedByPlayer1) STONE_IN_EYE_CONTRIB else -STONE_IN_EYE_CONTRIB)
    }
    diffScore
  }

  /**
   * @param stones actually the positions containing the stones.
   * @return the average scores of the stones in the list.
   */
  private def calcAverageScore(stones: GoBoardPositionSet): Float = {
    var totalScore = 0.0f

    for (stone <- stones.toArray(new Array[GoBoardPosition](0))) {
      val group = stone.getString.getGroup
      val useCached = false
      totalScore += analyzerMap_.getAnalyzer(group).getRelativeHealth(board_, useCached)
    }
    totalScore / stones.size
  }
}
