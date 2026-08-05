/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board.path

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.blockade.BlockadeWeights
import com.barrybecker4.optimization.parameter.ParameterArray

class PlayerPathLengths:
  
  private val pathLengths: Array[PathLengths] = Array(PathLengths(), PathLengths())

  def getPathLengthsForPlayer(player1: Boolean): PathLengths =
    pathLengths(if player1 then 0 else 1)

  def isValid: Boolean =
    pathLengths(0).isValid && pathLengths(1).isValid

  def determineWorth(winningValue: Double, weights: ParameterArray): Int =
    val p1 = 0
    val p2 = 1

    val value = if pathLengths(p1).shortestLength == 0 then
      GameContext.log(3, "**WON**")
      winningValue
    else if pathLengths(p2).shortestLength == 0 then
      GameContext.log(3, "**WON**")
      -winningValue
    else
      val shortestLengthDiff = pathLengths(p2).shortestLength - pathLengths(p1).shortestLength
      val secondShortestDiff = pathLengths(p2).secondShortestLength - pathLengths(p1).secondShortestLength
      val furthestLengthDiff = pathLengths(p2).furthestLength - pathLengths(p1).furthestLength

      weights.get(BlockadeWeights.CLOSEST_WEIGHT_INDEX).getValue * shortestLengthDiff +
        weights.get(BlockadeWeights.SECOND_CLOSEST_WEIGHT_INDEX).getValue * secondShortestDiff +
        weights.get(BlockadeWeights.FURTHEST_WEIGHT_INDEX).getValue * furthestLengthDiff

    value.toInt

  override def toString: String =
    val bldr = new StringBuilder()
    bldr.append("PlayerPathLengths:")
    bldr.append(s"\n\r player1:${getPathLengthsForPlayer(player1 = true)}")
    bldr.append(s"\n\r player2:${getPathLengthsForPlayer(player1 = false)}")
    bldr.toString()
