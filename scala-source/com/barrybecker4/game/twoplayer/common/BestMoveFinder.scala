/* Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.twoplayer.common.search.options.BestMovesSearchOptions
import scala.jdk.CollectionConverters._
import java.util.Collections

/**
 * Find the best moves from a list of reasonable next moves using configured search options.
 * The search options provide a set constraints which all must be minimally satisfied.
 *
 * @author Barry Becker
 */
class BestMoveFinder[M <: TwoPlayerMove](
  private val searchOptions_ : BestMovesSearchOptions
) {

  private var isPlayer1: Boolean = scala.compiletime.uninitialized

  /**
   * Take the list of all possible next moves and return just the best of them, based
   * on the constraints specified my the search options.
   */
  def getBestMoves(moveList: MoveList[M]): MoveList[M] = {
    Collections.sort(moveList)

    isPlayer1 = moveList.isEmpty || moveList.getFirstMove.isPlayer1
    if (isPlayer1) {
      Collections.reverse(moveList)
    }

    determineBestMoves(moveList)
  }

  /**
   * Select just the best moves after sorting the reasonable next moves.
   */
  private def determineBestMoves(moveList: MoveList[M]): MoveList[M] = {
    val minBest = searchOptions_.getMinBestMoves
    val requestedBest = getNumRequestedBest(moveList)
    val thresholdValue = getThresholdValue(moveList)
    val minToGet = Math.max(minBest, requestedBest)

    getBestMoves(moveList, minToGet, thresholdValue)
  }

  /**
   * @return number of moves corresponding to percent of best.
   */
  private def getNumRequestedBest(moveList: MoveList[M]): Int = {
    val numMoves = moveList.size
    val topPercent = searchOptions_.getPercentageBestMoves
    ((topPercent / 100.0 * numMoves) + 0.5).toInt
  }

  /**
   * Must consider the full range of values, not just the biggest, because values can be negative.
   * @return score that needs to be exceeded to be considered an acceptable move.
   */
  private def getThresholdValue(moveList: MoveList[M]): Double = {
    var thresholdValue = 0.0
    if (!moveList.isEmpty) {
      val highValue = moveList.getFirstMove.getValue
      val lowValue = moveList.getLastMove.getValue
      val range = Math.abs(highValue - lowValue)
      val percent = searchOptions_.getPercentLessThanBestThresh / 100.0
      val diff = range * percent
      thresholdValue = highValue + (if (isPlayer1) -diff else diff)
    }
    thresholdValue
  }

  /**
   * @param moveList list of moves to select from
   * @param minToGet return no fewer than this unless there are not that many in moveList to begin with.
   * @param thresholdValue percent of best score to exceed to get included
   * @return best moves that satisfy requirements
   */
  private def getBestMoves(moveList: MoveList[M], minToGet: Int, thresholdValue: Double): MoveList[M] = {
    val bestMoves = new MoveList[M]()

    if (!moveList.isEmpty) {
      for (move <- moveList.asScala) {
        val threshExceeded = if (isPlayer1) {
          move.getValue >= thresholdValue
        } else {
          move.getValue <= thresholdValue
        }
        if (bestMoves.size < minToGet || threshExceeded) {
          bestMoves.add(move)
        }
      }
    }
    bestMoves
  }
}
