/* Copyright by Barry G. Becker, 2000-2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.options

object BestMovesSearchOptions:
  /**
   * Of the total number of reasonable candidate moves, this will limit X percent of the most promising ones.
   * The larger this percent is, the more moves that will be included.
   * If 100%, then all candidates will be considered.
   */
  private val DEFAULT_BEST_PERCENTAGE = 50

  /**
   * Select the best moves whose value is greater than this percent less than the highest value in the set.
   * For example if the best score is 4000 and this percent threshold is 50%, then only moves with a
   * score of 2000 or higher will be kept.
   * When computing the percentage the range between the maximum and minimum value must be considered since
   * can could go negative.
   */
  private val DEFAULT_PERCENT_LESS_THAN_BEST_THRESH = 60

  /**
   * No matter what the percentBestMoves is, we should not prune if less than this number remaining.
   * For example, suppose this constant is 10, and only 50% of the best moves are kept, and there are 12 considered,
   * then 10 will still be returned instead of 6. This rule has the highest precedence.
   */
  private val DEFAULT_MIN_BEST_MOVES = 10

/**
 * Options to limit the number of moves searched at each ply to just the ones most likely to be selected.
 * There is no guarantee that the subset returned will contain the actual best move, but it's a trade-off
 * for search time. The options are for search strategies that use brute-force minimax search
 * like MiniMax, NegaMax, NegaScout, and also the memory and aspiration variations of these strategies.
 * These methods usually use a search window to do pruning of tree branches.
 * The different options define a number of constraints on limiting the number of moves returned.
 * All the constraints must be satisfied in the sense that there will never be fewer moves returned than
 * any one of them dictates unless there are simple not that many moves available.
 *
 * @author Barry Becker
 */
class BestMovesSearchOptions(
  bestPercentage: Int = BestMovesSearchOptions.DEFAULT_BEST_PERCENTAGE,
  percentLessThanBestThresh: Int = BestMovesSearchOptions.DEFAULT_PERCENT_LESS_THAN_BEST_THRESH,
  minBestMoves: Int = BestMovesSearchOptions.DEFAULT_MIN_BEST_MOVES
):

  import BestMovesSearchOptions.*

  /** percent of the best moves to consider */
  private var bestPercentage_ : Int = bestPercentage

  /** consider moves that are percent less than best or greater */
  private var percentLessThanBestThreshold_ : Int = percentLessThanBestThresh

  /** highest priority criteria */
  private var minBestMoves_ : Int = minBestMoves

  /**
   * Default Constructor with default values for all constraints.
   */
  def this() =
    this(BestMovesSearchOptions.DEFAULT_BEST_PERCENTAGE,
         BestMovesSearchOptions.DEFAULT_PERCENT_LESS_THAN_BEST_THRESH,
         BestMovesSearchOptions.DEFAULT_MIN_BEST_MOVES)

  /**
   * @return the percentage of top moves considered at each ply
   */
  final def getPercentageBestMoves: Int = bestPercentage_

  /**
   * @param bestPercentage the percentage of top moves considered at each ply
   */
  final def setPercentageBestMoves(bestPercentage: Int): Unit =
    bestPercentage_ = bestPercentage

  /**
   * @return never return fewer than this many best moves.
   */
  def getMinBestMoves: Int = minBestMoves_

  def setMinBestMoves(minBest: Int): Unit =
    if minBest <= 0 then
      throw new IllegalArgumentException(s"minBest must be greater than 0. It was $minBest")
    minBestMoves_ = minBest

  /**
   * @return the "percent less than best" threshold percentage.
   */
  def getPercentLessThanBestThresh: Int = percentLessThanBestThreshold_

  def setPercentLessThanBestThresh(percent: Int): Unit =
    assert(percent >= 0 && percent <= 100, s"percent out of range : $percent")
    percentLessThanBestThreshold_ = percent

  override def toString: String =
    s"bestPercentage: $bestPercentage_  minBestMoves:$minBestMoves_  percentLessThanBestThreshold:$percentLessThanBestThreshold_"
