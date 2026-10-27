/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis

import com.barrybecker4.game.common.{GameContext, Move}
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy
import com.barrybecker4.game.twoplayer.go.GoController
import com.barrybecker4.game.twoplayer.go.board.{GoBoard, GoProfiler, PositionalScore}
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap
import com.barrybecker4.game.twoplayer.go.board.analysis.territory.{SearchScoreCalculator, TerritoryAnalyzer, TerritoryUpdater}
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.game.twoplayer.go.options.GoOptions
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * For statically evaluating the current state of a GoBoard.
 *
 * @author Barry Becker
 */
class WorthCalculator(
  val board_ : GoBoard,
  analyzerMap: GroupAnalyzerMap,
  komi: Float = GoOptions.DEFAULT_KOMI
) {

  private val territoryAnalyzer = new TerritoryAnalyzer(board_, analyzerMap)
  private val territoryUpdater = new TerritoryUpdater(board_, analyzerMap)
  private var info: WorthInfo = scala.compiletime.uninitialized

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
      // Round so small positional penalties (e.g. first-line) are not lost to truncation.
      Math.round(worth).toInt
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
   * Black-positive Japanese points times ten, plus a shape tie-break smaller than one point.
   * Prisoners are already one point inside the point score, so captures are not weighted again.
   */
  private def calculateWorth(board: GoBoard, lastMove: Move, weights: ParameterArray): Double = {
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

    val points = new SearchScoreCalculator(board, komi).points()
    val tieBreak = positionalTieBreak(totalScore.getPositionScore)
    val worth = (points + tieBreak) * 10.0

    if (GameContext.getDebugMode > 0) {
      val desc = totalScore.getDescription(worth, 0, points, 1.0)
      lastMove.asInstanceOf[TwoPlayerMove].setScoreDescription(desc)
    }
    val blackCap = board.getNumCaptures(true)
    val whiteCap = board.getNumCaptures(false)
    this.info =
      new WorthInfo(points, 0, blackCap, whiteCap,
        tieBreak, positionScores, lastMove.asInstanceOf[GoMove].getCaptures,
        worth, board.getMoveList.copy())
    worth
  }

  /**
   * Line and bad-shape. Scaled so moving one stone from the third line to the first
   * changes the integer worth, while one such stone stays worth less than a point.
   */
  private def positionalTieBreak(raw: Double): Double =
    raw / WorthCalculator.PositionalTieBreakDivisor

  def getWorthInfo: WorthInfo = info

  /**
   * @return the estimated difference in territory between the 2 sides.
   */
  def updateTerritoryAtEndOfGame(): Float = {
    territoryUpdater.updateTerritory(true)
  }

}

object WorthCalculator {
  /** One third-line stone versus one first-line stone is a few tenths of a point. */
  private val PositionalTieBreakDivisor = 6.0
}
