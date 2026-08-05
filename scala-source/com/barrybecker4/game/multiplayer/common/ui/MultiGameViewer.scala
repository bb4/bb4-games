// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.common.ui

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.{GameContext, Move}
import com.barrybecker4.game.common.board.{Board, BoardPosition, IRectangularBoard}
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.common.player.PlayerAction
import com.barrybecker4.game.common.ui.panel.GameChangedEvent
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.game.multiplayer.common.MultiGameController
import com.barrybecker4.game.multiplayer.common.online.SurrogateMultiPlayer

import javax.swing.JOptionPane
import java.awt.event.MouseEvent

/**
 * Takes a Controller as input and displays the current state of the game.
 * For example, the TrivialController contains a TrivialTable object
 * which describes this state.
 *
 * @author Barry Becker
 */
abstract class MultiGameViewer[M <: Move, B <: Board[M]] extends GameBoardViewer[M, B] {

  private var winnerDialogShown_ = false

  override def createController(): MultiGameController[M, B]

  /**
   * start over with a new game using the current options.
   */
  override def startNewGame(): Unit = {
    reset()
    winnerDialogShown_ = false
    sendGameChangedEvent(null.asInstanceOf[M]) // get the info panel to refresh with 1st players name
    val firstPlayer = getController.getPlayers.getFirstPlayer

    if (firstPlayer.isSurrogate) {
      doSurrogateMove(firstPlayer.asInstanceOf[SurrogateMultiPlayer])
    } else if (!firstPlayer.isHuman) {
      getController.computerMovesFirst()
    }
  }

  /**
   * display a dialog at the end of the game showing who won and other relevant
   * game specific information.
   */
  override def showWinnerDialog(): Unit = {
    val message = getGameOverMessage
    JOptionPane.showMessageDialog(
      this, message, GameContext.getLabel("GAME_OVER"),
      JOptionPane.INFORMATION_MESSAGE)
  }

  def getGameOverMessage: String

  /**
   * make the computer move and show it on the screen.
   *
   * @param player computer player to move
   * @return done return true if the game is over after moving
   */
  def doComputerMove(player: Player): Boolean

  /**
   * Wait for the surrogate player to move and show it on the screen when done.
   *
   * @param player surrogate player to move
   * @return done return true if the game is over after moving
   */
  def doSurrogateMove(player: SurrogateMultiPlayer): Boolean = {
    val worker = new SurrogateMoveWorker(this)
    worker.requestSurrogateMove(player)
    false
  }

  /**
   * Does nothing by default.
   * @param action to take
   * @param player to apply it to
   * @return message to show if on client.
   */
  def applyAction(action: PlayerAction, player: Player): String = {
    GameContext.log(0, "ignoring action " + action)
    ""
  }

  /**
   * Implements the GameChangedListener interface.
   * Called when the game has changed in some way
   */
  override def gameChanged(evt: GameChangedEvent[M]): Unit = {
    if (getController.isDone && !winnerDialogShown_) {
      winnerDialogShown_ = true
      showWinnerDialog()
    } else if (!winnerDialogShown_) {
      super.gameChanged(evt)
    }
  }

  /**
   * Many multiplayer games don't use this.
   * @param lastMove the move to show (but now record)
   */
  def createMove(lastMove: Move): Move =
    null

  /**
   * @return the tooltip for the panel given a mouse event
   */
  override def getToolTipText(e: MouseEvent): String = {
    val loc = getBoardRenderer.createLocation(e)
    val sb = new StringBuilder("<html><font=-3>")

    if (getController.getBoard != null) {
      val space = getController.getBoard.asInstanceOf[IRectangularBoard[M]].getPosition(loc)
      if (space != null && space.isOccupied && GameContext.getDebugMode >= 0) {
        sb.append("<br>")
        sb.append(loc)
      }
      sb.append("</font></html>")
    }
    sb.toString
  }
}
