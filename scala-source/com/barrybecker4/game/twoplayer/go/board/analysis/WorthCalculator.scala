/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis

import com.barrybecker4.game.common.{GameContext, Move}
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy
import com.barrybecker4.game.twoplayer.go.GoController
import com.barrybecker4.game.twoplayer.go.board.{GoBoard, GoProfiler, PositionalScore}
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap
import com.barrybecker4.game.twoplayer.go.board.analysis.territory.{TerritoryAnalyzer, TerritoryUpdater}
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.game.twoplayer.go.options.GoWeights
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * For statically evaluating the current state of a GoBoard.
 *
 * @author Barry Becker
 */
class WorthCalculator(val board_ : GoBoard, analyzerMap: GroupAnalyzerMap) {

  private val territoryAnalyzer = new TerritoryAnalyzer(board_, analyzerMap)
  private val territoryUpdater = new TerritoryUpdater(board_, analyzerMap)
  private var info: WorthInfo = _

  /**
   * Statically evaluate the board position.
   *
   * @return statically evaluated value of the board.
   *   a positive value means that player1 has the advantage.
   *   A big negative value means a good move for p2.
   */
  def worth(lastMove: Move, weights: ParameterArray): Int = {
    GoProfiler.startCalcWorth()
    val worth = calculateWorth(board_, lastMove, weights)

    GameContext.log(3, "GoController.worth: worth=" + worth)
    val result = if (worth < -GoController.WIN_THRESHOLD) {
      -SearchStrategy.WINNING_VALUE
    }
    else if (worth > GoController.WIN_THRESHOLD) {
      SearchStrategy.WINNING_VALUE
    }
    else {
      worth.toInt
    }
    GoProfiler.stopCalcWorth()
    result
  }

  /**
   * Get estimate of territory for specified player.
   * @param forPlayer1 the player to get the estimate for
   * @param isEndOfGame then we need the estimate to be more accurate.
   * @return estimate of size of territory for specified player.
   */
  def getTerritoryEstimate(forPlayer1: Boolean, isEndOfGame: Boolean): Int = {
    territoryAnalyzer.getTerritoryEstimate(forPlayer1, isEndOfGame)
  }

  /**
   * Statically evaluate the board position.
   *     The most naive thing we could do here is to simply return the sum of the captures
   * for player1 - sum of the captures for player2. However for go, since search is not likely to
   * be that useful given the huge branch factor, we need to rely heavily on sophisticated evaluation.
   *     We have every space on the board have a score representing
   * how strongly it is controlled by player1 (black).  If the score is 1.00, then that
   * position is inside or part of an unconditionally alive group owned by player1 (black)
   * or it is inside a dead white group.
   * If the score is -1.00 then its player 2's(white) unconditionally alive group
   * or black's dead group. A blank dame might have a score
   * of 0. A white stone might have a positive score if its part of a white group
   * which is considered mostly dead.
   *
   * @return statically evaluated value of the board.
   */
  private def calculateWorth(board: GoBoard, lastMove: Move, weights: ParameterArray): Double = {
    val rows = board.getNumRows
    val scaleFactor = 361.0 / (rows * rows)

    territoryUpdater.updateTerritory(false)

    val positionScores = Array.ofDim[PositionalScore](board.getNumRows, board.getNumCols)
    var totalScore = PositionalScore.createZeroScore()

    val positionalScorer = new PositionalScoreAnalyzer(board)

    for (row <- 1 to board.getNumRows) {
      for (col <- 1 to board.getNumCols) {
        val s = positionalScorer.determineScoreForPosition(row, col, weights)
        positionScores(row - 1)(col - 1) = s
        totalScore.incrementBy(s)
      }
    }

    val territoryDelta = territoryUpdater.getTerritoryDelta
    val captureScore = getCaptureScore(board, weights)
    val positionalScore = totalScore.getPositionScore
    val worth = scaleFactor * (positionalScore + captureScore + territoryDelta)

    if (GameContext.getDebugMode > 0) {
      val desc = totalScore.getDescription(worth, captureScore, territoryDelta, scaleFactor)
      lastMove.asInstanceOf[TwoPlayerMove].setScoreDescription(desc)
    }
    val blackCap = board.getNumCaptures(true)
    val whiteCap = board.getNumCaptures(false)
    this.info =
      new WorthInfo(territoryDelta, captureScore, blackCap, whiteCap,
        positionalScore, positionScores, lastMove.asInstanceOf[GoMove].getCaptures,
        worth, board.getMoveList.copy())
    worth
  }

  def getWorthInfo: WorthInfo = info

  /**
   * @return the estimated difference in territory between the 2 sides.
   */
  def updateTerritoryAtEndOfGame(): Float = {
    territoryUpdater.updateTerritory(true)
  }

  /**
   * @return score attributed to captured stones.
   */
  private def getCaptureScore(board: GoBoard, weights: ParameterArray): Double = {
    val captureWt = weights.get(GoWeights.CAPTURE_WEIGHT_INDEX).getValue
    captureWt * (board.getNumCaptures(false) - board.getNumCaptures(true))
  }
}
