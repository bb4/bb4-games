/* Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common

import com.barrybecker4.game.twoplayer.common.search.strategy.SearchProgress
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy

/**
 * Requests the next move to be found.
 *
 * @author Barry Becker
 */
class ComputerMoveRequester[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](private val controller_ : TwoPlayerController[M, B]) extends SearchProgress:

  /**
   * make the computer move and show it on the screen.
   * Since this can take a very long time we will show the user a progress bar
   * to give feedback.
   *
   * @param isPlayer1 if the computer player now moving is player 1.
   * @return true if done. Always returns false unless auto optimizing
   */
  def requestComputerMove(isPlayer1: Boolean): Boolean =
    // this will spawn the worker thread and return immediately (unless autoOptimize on)
    controller_.requestComputerMove(isPlayer1)

  override def getNumMovesConsidered: Long =
    val strategy = controller_.getSearchStrategy
    if strategy == null then 0 else strategy.getNumMovesConsidered

  override def getPercentDone: Int =
    if controller_.getSearchStrategy != null then
      controller_.getSearchStrategy.getPercentDone
    else 0

  override def pause(): Unit =
    controller_.pause()

  override def isPaused: Boolean =
    controller_.isPaused()

  override def continueProcessing(): Unit =
    if controller_.getSearchStrategy != null then
      controller_.getSearchStrategy.continueProcessing()
