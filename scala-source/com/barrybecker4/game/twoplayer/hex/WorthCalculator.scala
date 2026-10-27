package com.barrybecker4.game.twoplayer.hex

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.math.MathUtil
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * Calculates the worth of the hex game board from player one's standpoint
 */
class WorthCalculator:

  /**
   * @return the worth of the hex game board from player one's standpoint
   */
  def calculateWorth(board: HexBoard, lastMove: TwoPlayerMove, weights: ParameterArray): Int =
    val pathValue = calcPathValue(board)
    val centricityValue = calcCentricityValue(board, lastMove)

    val wt1 = weights.get(HexWeights.CENTRICITY_INDEX).getValue
    val wt2 = weights.get(HexWeights.PATH_COST_INDEX).getValue
    (wt1 * centricityValue + wt2 * pathValue).toInt

  private def calcPathValue(board: HexBoard): Double =
    var pathValue = MathUtil.RANDOM.nextInt(100)

    val p1Cost = board.getP1PathCost
    val p2Cost = board.getP2PathCost
    val p1Connected = p1Cost == 0
    val p2Connected = p2Cost == 0
    
    if p1Connected || p2Connected then
      if p1Connected then
        pathValue = SearchStrategy.WINNING_VALUE
      else if p2Connected then
        pathValue = -SearchStrategy.WINNING_VALUE
    else
      pathValue = p2Cost - p1Cost
    
    pathValue

  private def calcCentricityValue(board: HexBoard, lastMove: TwoPlayerMove): Double =
    val thresh = 10
    val moveNum = board.getMoveList.size
    val sign = if lastMove.isPlayer1 then -1 else 1
    
    if moveNum < thresh then
      val center = board.getCenter
      val lastMv = lastMove.getToLocation
      val manhattanDist =
        Math.abs(center.row - lastMv.row) + Math.abs(center.col - lastMv.col)
      sign * (thresh - moveNum) * manhattanDist / thresh
    else
      0
