// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.common.ui

import com.barrybecker4.common.concurrency.Worker
import com.barrybecker4.game.common.player.PlayerAction
import com.barrybecker4.game.multiplayer.common.MultiGameController
import com.barrybecker4.game.multiplayer.common.online.SurrogateMultiPlayer

/**
 * Waits for the next computer move in a separate thread.
 *
 * @author Barry Becker
 */
class SurrogateMoveWorker(viewer: MultiGameViewer[?, ?]) {

  private val viewer_ = viewer
  private val controller_ = viewer.getController.asInstanceOf[MultiGameController[?, ?]]

  /** this is true while the surrogate player thinks about its next move. */
  private var processing_ = false

  /**
   * Request the next move from the player that the surrogate represents.
   * Launches a separate thread to do the search for the next move so the UI is not blocked.
   * @return true if the game is over
   */
  def requestSurrogateMove(player: SurrogateMultiPlayer): Boolean = {
    /* Worker represents a separate thread for getting the next move. */
    val worker = new Worker {
      private var action: PlayerAction = null

      override def construct(): AnyRef = {
        processing_ = true
        // blocks until the move action is available
        action = player.getAction(controller_)
        action
      }

      override def finished(): Unit = {
        viewer_.applyAction(action, player.getActualPlayer)
        //viewer.sendGameChangedEvent(null);
        viewer_.refresh()
        controller_.advanceToNextPlayer()
        processing_ = false
      }
    }

    worker.start()
    false
  }

  /**
   *  @return true if the viewer is currently processing (i.e. searching)
   */
  def isProcessing: Boolean = processing_
}
