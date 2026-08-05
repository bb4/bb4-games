/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common

import com.barrybecker4.common.concurrency.{ThreadUtil, Worker}

/**
 * Searches for the next computer move in a separate thread.
 *
 * @author Barry Becker
 */
class TwoPlayerSearchWorker[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](
  private val controller: TwoPlayerController[M, B]
) {

  private var worker: Worker = _
  private var processing_ : Boolean = false

  /**
   * Apply whatever the best move is that we have found so far - even though we are not done.
   */
  def interrupt(): Unit = {
    if (isProcessing) {
      controller.pause()
      if (worker != null) {
        worker.interrupt()
        processing_ = false
        val move = worker.get.asInstanceOf[M]
        if (move != null) {
          controller.getViewer.computerMoved(move)
        }
      }
      ThreadUtil.sleep(100)
    }
  }

  /**
   * Request the next computer move. It will be the best move that the computer can find.
   * Launches a separate thread to do the search for the next move.
   * @param isPlayer1 true if player one to move.
   * @param synchronous if true then the method does not return until the next move has been found.
   * @return true if the game is over
   * @throws AssertionError  if something bad happened while searching.
   */
  @throws[AssertionError]
  def requestComputerMove(isPlayer1: Boolean, synchronous: Boolean): Boolean = {
    worker = new Worker() {
      private var move: M = _

      override def construct(): Object = {
        processing_ = true
        move = controller.findComputerMove(isPlayer1)
        move.asInstanceOf[Object]
      }

      override def finished(): Unit = {
        if (controller.getViewer != null) {
          controller.getViewer.computerMoved(move)
        }
        processing_ = false
      }
    }

    worker.start()

    if (synchronous) {
      val m = worker.get.asInstanceOf[M]
      return controller.getSearchable().done(m, true)
    }
    false
  }

  /**
   *  @return true if the viewer is currently processing (i.e. searching)
   */
  def isProcessing: Boolean = processing_
}
