/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.ui

import com.barrybecker4.game.common.{GameContext, Move}
import com.barrybecker4.game.common.board.Board
import com.barrybecker4.game.common.player.{Player, PlayerAction}
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer
import com.barrybecker4.game.multiplayer.common.MultiGameController
import com.barrybecker4.game.multiplayer.common.ui.MultiGameViewer
import com.barrybecker4.game.multiplayer.trivial.{ActionName, TrivialAction, TrivialController}
import com.barrybecker4.game.multiplayer.trivial.player.{TrivialPlayer, TrivialRobotPlayer}
import com.barrybecker4.game.multiplayer.trivial.ui.render.TrivialGameRenderer

import javax.swing.JOptionPane

/**
 *  Takes a TrivialController as input and displays the
 *  current state of the Game. The TrivialController contains a TrivialTable object
 *  which describes this state.
 *
 *  @author Barry Becker
 */
class TrivialGameViewer extends MultiGameViewer {

  getController.asInstanceOf[com.barrybecker4.game.multiplayer.common.MultiGameController[?, ?]].initializeData()

  override def createController(): MultiGameController[Move, Board[Move]] =
    new TrivialController().asInstanceOf[MultiGameController[Move, Board[Move]]]
  override def getBoardRenderer: GameBoardRenderer =
    TrivialGameRenderer.getRenderer

  /**
   * @return the message to display at the completion of the game.
   */
  override def getGameOverMessage: String =
    val buf = new StringBuilder("Game Over\n")

    val players = this.getController.getPlayers
    var max = -1
    var winner: TrivialPlayer = null
    
    val it = players.iterator()
    while it.hasNext do
      val p = it.next()
      val tp = p.getActualPlayer.asInstanceOf[TrivialPlayer]
      if tp.getValue > max then
        max = tp.getValue
        winner = tp

    assert(winner != null)
    buf.append(winner.getName).append(" won the game with a value of ").append(winner.getValue).append('.')
    buf.toString

  /**
   * make the computer move and show it on the screen.
   *
   * @param player computer player to move
   * @return done return true if the game is over after moving
   */
  override def doComputerMove(player: Player): Boolean =
    assert(!player.isHuman)
    val robot = player.asInstanceOf[TrivialRobotPlayer]
    val pc = this.getController.asInstanceOf[TrivialController]

    val action = robot.getAction(pc)
    GameContext.log(0, "making trivial robot move! : " + action)

    val msg = applyAction(action, robot)
    pc.addRecentRobotAction(action)

    if this.getParentFrame != null then
      JOptionPane.showMessageDialog(this.getParentFrame, msg, robot.getName, JOptionPane.INFORMATION_MESSAGE)

    this.refresh()
    pc.advanceToNextPlayerIndex()

    false

  /**
   * @param action to take
   * @param player to apply it to
   * @return message to show if on client.
   */
  override def applyAction(action: PlayerAction, player: Player): String =
    assert(action != null)
    val p = player.asInstanceOf[TrivialPlayer]
    val act = action.asInstanceOf[TrivialAction]
    var msg: String = null

    act.actionName match
      case ActionName.KEEP_HIDDEN =>
        msg = p.getName + " has opted to keep his number hidden."
      case ActionName.REVEAL =>
        p.revealValue()
        msg = p.getName + " has revealed his number."

    msg
}
