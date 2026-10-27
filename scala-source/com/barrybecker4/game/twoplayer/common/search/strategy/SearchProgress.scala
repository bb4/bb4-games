// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.common.search.strategy

/**
 * Interface for something that can be queried for current search progress toward finding next move.
 *
 * @author Barry Becker
 */
trait SearchProgress {

  /**
   * @return the number of moves considered in the search so far.
   */
  def getNumMovesConsidered: Long

  /**
   * Approximate percent completed for the search.
   * Approximate because pruning can cause the search to speed up considerably toward the end.
   *
   * @return the approximate percentage of total search time that has been completed.
   */
  def getPercentDone: Int

  /**
   * Cause search to become paused.
   */
  def pause(): Unit

  /**
   * @return true if search is paused.
   */
  def isPaused: Boolean

  /**
   * Continue processing if the search was paused.
   */
  def continueProcessing(): Unit
}
