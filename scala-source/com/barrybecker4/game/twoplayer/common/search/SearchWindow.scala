/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search

import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy

/**
 * Manages alpha and beta - the search window thresholds.
 */
class SearchWindow(var alpha: Int, var beta: Int):

  /** Default Constructor. */
  def this() =
    this(SearchStrategy.INFINITY, -SearchStrategy.INFINITY)

  def copy(): SearchWindow =
    new SearchWindow(alpha, beta)

  /**
   * Negate and then swap the alpha and beta values.
   * Classic NegaMax / PVS child window is (-beta, -alpha).
   * @return a new window which swaps and negates the alpha and beta values.
   */
  def negateAndSwap(): SearchWindow =
    new SearchWindow(-beta, -alpha)

  /**
   * @return a value midway between alpha and beta.
   */
  def getMidPoint: Int =
    (alpha + beta) / 2

  override def toString: String =
    s"($alpha, $beta)"

  override def equals(o: Any): Boolean = o match
    case that: SearchWindow =>
      beta == that.beta && alpha == that.alpha
    case _ => false

  override def hashCode(): Int =
    var result = alpha
    result = 31 * result + beta
    result
