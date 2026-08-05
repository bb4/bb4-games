/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common

import com.barrybecker4.math.MathUtil
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy
import com.barrybecker4.optimization.parameter.{NumericParameterArray, ParameterArray}
import com.barrybecker4.optimization.parameter.redistribution.RedistributionFunction

/**
 * The GameWeights define the coefficients to use by the
 * evaluation polynomial used by each computer player.
 * Optimizing these weights is the primary way that the game
 * learns to play better.
 *
 * @author Barry Becker
 */
class GameWeights(val defaultWeights: ParameterArray) {

  private val numWeights: Int = defaultWeights.size
  private val SCALE: Double = SearchStrategy.WINNING_VALUE / GameWeights.ASSUMED_WINNING_VALUE

  private var p1Weights: ParameterArray = defaultWeights
  private var p2Weights: ParameterArray = defaultWeights

  private val names: Array[String] = Array.tabulate(numWeights)(i => s"Weight $i")
  private val descriptions: Array[String] = Array.tabulate(numWeights)(i =>
    s"The weighting coefficient for the ${i}th term of the evaluation polynomial"
  )

  def this(defaultWeights: Array[Double], minWeights: Array[Double], maxWeights: Array[Double],
           names: Array[String], descriptions: Array[String]) = {
    this({
      val numWeights = defaultWeights.length
      val SCALE = SearchStrategy.WINNING_VALUE / GameWeights.ASSUMED_WINNING_VALUE
      val minVals = minWeights.map(_ * SCALE)
      val defaultVals = defaultWeights.map(_ * SCALE)
      val maxVals = maxWeights.map(_ * SCALE)
      new NumericParameterArray(defaultVals, minVals, maxVals, names, MathUtil.RANDOM)
    })
  }

  final def getPlayer1Weights: ParameterArray = p1Weights

  final def getPlayer2Weights: ParameterArray = p2Weights

  final def setPlayer1Weights(p1Weights: ParameterArray): Unit = {
    verify(p1Weights)
    this.p1Weights = p1Weights
  }

  final def setPlayer2Weights(p2Weights: ParameterArray): Unit = {
    verify(p2Weights)
    this.p2Weights = p2Weights
  }

  private def verify(wts: ParameterArray): Unit = {
    assert(wts.size == numWeights,
      s"Incorrect number of weights: ${wts.size} you need $numWeights")
  }

  final def getDefaultWeights: ParameterArray = defaultWeights

  final def getName(i: Int): String = names(i)

  final def getDescription(i: Int): String = descriptions(i)

  final def getMaxWeight(i: Int): Double = defaultWeights.get(i).maxValue

  override def toString: String =
    s"Player1's weights are:$p1Weights\nPlayer2's weights are $p2Weights"
}

object GameWeights {
  final val ASSUMED_WINNING_VALUE: Double = 1024
  final val NONE: scala.Option[RedistributionFunction] = scala.Option.apply(null)
}
