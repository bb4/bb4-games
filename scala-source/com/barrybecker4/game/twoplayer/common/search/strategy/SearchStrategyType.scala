/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.common.{TwoPlayerBoard, TwoPlayerMove}
import com.barrybecker4.game.twoplayer.common.search.{SearchAttribute, Searchable}
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * Currently supported search strategies.
 * Extends java.lang.Enum for Java interop.
 *
 * @author Barry Becker
 */
enum SearchStrategyType(
  private val labelKey: String,
  private val attributes: Seq[SearchAttribute]
) extends java.lang.Enum[SearchStrategyType]:

  case MINIMAX extends SearchStrategyType("MINIMAX_SEARCH", Seq(SearchAttribute.BRUTE_FORCE))
  case NEGAMAX extends SearchStrategyType("NEGAMAX_SEARCH", Seq(SearchAttribute.BRUTE_FORCE))
  case NEGAMAX_W_MEMORY extends SearchStrategyType(
    "NEGAMAX_W_MEMORY_SEARCH", Seq(SearchAttribute.BRUTE_FORCE, SearchAttribute.MEMORY))
  case NEGASCOUT extends SearchStrategyType(
    "NEGASCOUT_SEARCH", Seq(SearchAttribute.BRUTE_FORCE, SearchAttribute.ASPIRATION))
  case NEGASCOUT_W_MEMORY extends SearchStrategyType(
    "NEGASCOUT_W_MEMORY_SEARCH",
    Seq(SearchAttribute.BRUTE_FORCE, SearchAttribute.MEMORY, SearchAttribute.ASPIRATION))
  case MTD_NEGASCOUT extends SearchStrategyType(
    "MTD_NEGASCOUT_SEARCH",
    Seq(SearchAttribute.BRUTE_FORCE, SearchAttribute.MEMORY, SearchAttribute.ASPIRATION))
  case MTD_NEGAMAX extends SearchStrategyType(
    "MTD_NEGAMAX_SEARCH",
    Seq(SearchAttribute.BRUTE_FORCE, SearchAttribute.MEMORY, SearchAttribute.ASPIRATION))
  case UCT extends SearchStrategyType("UCT_SEARCH", Seq(SearchAttribute.MONTE_CARLO))

  /** @return localized description. */
  def getLabel: String = GameContext.getLabel(labelKey)

  /** @return localized description. */
  def getTooltip: String = GameContext.getLabel(labelKey + "_TIP")

  /**
   * @param attribute attribute to check for presence of
   * @return true if this search type has the specified attribute.
   */
  def hasAttribute(attribute: SearchAttribute): Boolean = attributes.contains(attribute)

  /**
   * Factory method for creating the search strategy to use.
   * Do not call the constructor directly.
   *
   * @return the search method to use
   */
  def createStrategy[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](
    s: Searchable[M, B],
    weights: ParameterArray
  ): SearchStrategy[M, B] = this match
    case SearchStrategyType.MINIMAX => new MiniMaxStrategy(s, weights)
    case SearchStrategyType.NEGAMAX => new NegaMaxStrategy(s, weights)
    case SearchStrategyType.NEGAMAX_W_MEMORY => new NegaMaxMemoryStrategy(s, weights)
    case SearchStrategyType.NEGASCOUT => new NegaScoutStrategy(s, weights)
    case SearchStrategyType.NEGASCOUT_W_MEMORY => new NegaScoutMemoryStrategy(s, weights)
    case SearchStrategyType.MTD_NEGASCOUT => new MtdStrategy(new NegaScoutMemoryStrategy(s, weights))
    case SearchStrategyType.MTD_NEGAMAX => new MtdStrategy(new NegaMaxMemoryStrategy(s, weights))
    case SearchStrategyType.UCT => new UctStrategy(s, weights)
