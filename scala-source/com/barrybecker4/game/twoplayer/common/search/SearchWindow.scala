/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search

import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy

/**
 * Manages alpha and beta - the search window thresholds.
 *
 * @author Barry Becker
 */
class SearchWindow(var alpha: Int, var beta: Int) {

  /**
   * Default Constructor.
   */
  def this() = {
    this(SearchStrategy.INFINITY, -SearchStrategy.INFINITY)
  }

  def copy(): SearchWindow = {
    new SearchWindow(alpha, beta)
  }

  /**
   * Negate and then swap the alpha and beta values.
   * @return a new window which swaps and negates the alpha and beta values.
   */
  def negateAndSwap(): SearchWindow = {
    new SearchWindow(-alpha, -beta)
  }

  /**
   * @return the difference between the alpha and beta values. Returns negative infinity if alpha > beta.
   */
  def getExtent: Int = {
    if (alpha > beta) {
      -Integer.MAX_VALUE
    } else {
      beta - alpha
    }
  }

  /**
   * @return a value midway between alpha and beta.
   */
  def getMidPoint: Int = {
    (alpha + beta) / 2
  }

  override def toString: String = {
    s"($alpha, $beta)"
  }

  override def equals(o: Any): Boolean = o match {
    case that: SearchWindow =>
      beta == that.beta && alpha == that.alpha
    case _ => false
  }

  override def hashCode(): Int = {
    var result = alpha
    result = 31 * result + beta
    result
  }
}
