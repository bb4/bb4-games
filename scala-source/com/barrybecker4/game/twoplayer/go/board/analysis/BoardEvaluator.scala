/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis

import com.barrybecker4.game.common.Move
import com.barrybecker4.game.twoplayer.common.cache.{ScoreCache, ScoreEntry}
import com.barrybecker4.game.twoplayer.common.search.transposition.HashKey
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.group.{GroupAnalyzer, GroupAnalyzerMap}
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * Responsible for evaluating groups and territory on a go board.
 * we keep a cache of the score for each unique board position to avoid recalculating.
 *
 * @author Barry Becker
 */
final class BoardEvaluator(val board_ : GoBoard, val scoreCache: ScoreCache) {

  private val USE_SCORE_CACHING = false

  private val analyzerMap_ = new GroupAnalyzerMap()
  private val worthCalculator = new WorthCalculator(board_, analyzerMap_)

  /**
   * @return the worth of the board from player1's perspective
   */
  def worth(lastMove: Move, weights: ParameterArray, hashKey: HashKey): Int = {
    if (USE_SCORE_CACHING) {
      cachedWorth(lastMove, weights, hashKey)
    } else {
      worthCalculator.worth(lastMove, weights)
    }
  }

  /**
   * If we have a cached worth value for this board position, then use that instead of recomputing it.
   * Why doesn't playing with caching give same results as without? I think its because we can arrive at
   * identical board positions from different routes and they have different scores (may be related to ko)
   * @return statically evaluated value for the board.
   */
  private def cachedWorth(lastMove: Move, weights: ParameterArray, key: HashKey): Int = {
    val cachedScore = scoreCache.get(key)

    val worth = worthCalculator.worth(lastMove, weights)

    if (cachedScore.isEmpty) {
      scoreCache.put(key, new ScoreEntry(key, worth, board_.toString, getWorthInfo))
    }
    else {
      if (cachedScore.get.getScore != worth) {
        val bldr = s"\ncachedScore $cachedScore" +
          s"\ndid not match $worth for \n" +
          board_.toString +
          "\ncurrent info: " +
          getWorthInfo +
          s"using current key=$key"
        println(bldr)
        System.out.flush()
      }
    }
    worth
  }

  /**
   * Used only for debugging to understand how the worth was calculated.
   * @return the worthInfo from the calculator.
   */
  private def getWorthInfo: WorthInfo = {
    worthCalculator.getWorthInfo
  }

  def getGroupAnalyzer(group: IGoGroup): GroupAnalyzer = {
    analyzerMap_.getAnalyzer(group)
  }

  def getCache: ScoreCache = scoreCache

  /**
   * Get estimate of territory for specified player.
   * @param forPlayer1 the player to get the estimate for
   * @param isEndOfGame then we need the estimate to be more accurate.
   * @return estimate of size of territory for specified player.
   */
  def getTerritoryEstimate(forPlayer1: Boolean, isEndOfGame: Boolean): Int = {
    worthCalculator.getTerritoryEstimate(forPlayer1, isEndOfGame)
  }

  /**
   * @return the estimated difference in territory between the 2 sides.
   */
  def updateTerritoryAtEndOfGame(): Float = {
    worthCalculator.updateTerritoryAtEndOfGame()
  }
}
