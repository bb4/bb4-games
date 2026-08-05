/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.options

import com.barrybecker4.game.twoplayer.common.{TwoPlayerBoard, TwoPlayerMove}
import com.barrybecker4.game.twoplayer.common.search.Searchable
import com.barrybecker4.game.twoplayer.common.search.strategy.{SearchStrategy, SearchStrategyType}
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * Encapsulate two player search options here to keep the TwoPlayerController class much simpler.
 * While some options are used for all strategies,
 * some Search strategies with different SearchAttributes use different sets of options.
 *
 * @author Barry Becker
 */
class SearchOptions[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](
  algorithm: SearchStrategyType,
  val bruteOptions: BruteSearchOptions,
  val bestMovesOptions: BestMovesSearchOptions,
  val mcOptions: MonteCarloSearchOptions
):

  import SearchOptions.*

  private var strategyMethod: SearchStrategyType = algorithm

  def this() =
    this(SearchOptions.DEFAULT_STRATEGY_METHOD, new BruteSearchOptions(), new BestMovesSearchOptions(), new MonteCarloSearchOptions())

  def this(algorithm: SearchStrategyType) =
    this(algorithm, new BruteSearchOptions(), new BestMovesSearchOptions(), new MonteCarloSearchOptions())

  def this(bruteOptions: BruteSearchOptions, bestMovesOptions: BestMovesSearchOptions, mcOptions: MonteCarloSearchOptions) =
    this(SearchOptions.DEFAULT_STRATEGY_METHOD, bruteOptions, bestMovesOptions, mcOptions)

  def this(bruteOptions: BruteSearchOptions, bestMovesOptions: BestMovesSearchOptions) =
    this(bruteOptions, bestMovesOptions, new MonteCarloSearchOptions())

  def getSearchStrategyMethod: SearchStrategyType = strategyMethod

  final def setSearchStrategyMethod(method: SearchStrategyType): Unit =
    strategyMethod = method

  def getBruteSearchOptions: BruteSearchOptions = bruteOptions

  def getBestMovesSearchOptions: BestMovesSearchOptions = bestMovesOptions

  def getMonteCarloSearchOptions: MonteCarloSearchOptions = mcOptions

  def getSearchStrategy(searchable: Searchable[M, B], weights: ParameterArray): SearchStrategy[M, B] =
    val stype =
      if strategyMethod != null then strategyMethod
      else SearchOptions.DEFAULT_STRATEGY_METHOD
    // Guard against enum/static init order leaving strategyMethod null when constructed from Java.
    require(stype != null, "SearchStrategyType was null; check SearchStrategyType / SearchOptions initialization")
    stype.createStrategy(searchable, weights).asInstanceOf[SearchStrategy[M, B]]

  override def toString: String =
    s"SearchOptions strategy=$strategyMethod\n$bruteOptions\n$bestMovesOptions\n$mcOptions\n"

object SearchOptions:
  /** Lazy to avoid circular init with SearchStrategyType enum statics. */
  lazy val DEFAULT_STRATEGY_METHOD: SearchStrategyType = SearchStrategyType.NEGASCOUT
