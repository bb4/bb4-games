/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.options

/**
 * The options for search strategies that use Monte Carlo tree search.
 *
 * @author Barry Becker
 */
class MonteCarloSearchOptions(
  maxSim: Int,
  exploreExploit: Double,
  randomLook: Int,
  style: MaximizationStyle
):

  import MonteCarloSearchOptions.*

  private var maxSimulations: Int = maxSim
  private var exploreExploitRatio: Double = exploreExploit
  private var randomLookAhead: Int = randomLook
  private var maxStyle: MaximizationStyle = style

  def this() =
    this(MonteCarloSearchOptions.DEFAULT_MAX_SIMULATIONS, MonteCarloSearchOptions.DEFAULT_EXPLORE_EXPLOIT_RATIO, MonteCarloSearchOptions.DEFAULT_RANDOM_LOOKAHEAD, MaximizationStyle.WIN_RATE)

  def this(maxSimulations: Int, exploreExploitRatio: Double, randomLookAhead: Int) =
    this(maxSimulations, exploreExploitRatio, randomLookAhead, MaximizationStyle.WIN_RATE)

  def getMaxSimulations: Int = maxSimulations

  def setMaxSimulations(maxSim: Int): Unit =
    maxSimulations = maxSim

  def getExploreExploitRatio: Double = exploreExploitRatio

  def setExploreExploitRatio(ratio: Double): Unit =
    exploreExploitRatio = ratio

  def getRandomLookAhead: Int = randomLookAhead

  def setRandomLookAhead(randomLook: Int): Unit =
    randomLookAhead = randomLook

  def getMaxStyle: MaximizationStyle = maxStyle

  def setMaxStyle(style: MaximizationStyle): Unit =
    maxStyle = style

  override def toString: String =
    s"maxSimulations:$maxSimulations  exploreExploitRatio:$exploreExploitRatio  randomLookAhead:$randomLookAhead"

object MonteCarloSearchOptions:
  private val DEFAULT_MAX_SIMULATIONS: Int = 2000
  private val DEFAULT_EXPLORE_EXPLOIT_RATIO: Double = 1.0
  private val DEFAULT_RANDOM_LOOKAHEAD: Int = 20
