/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui

import com.barrybecker4.game.common.Move
import com.barrybecker4.game.common.player.{Player, PlayerAction}
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer
import com.barrybecker4.game.multiplayer.common.ui.MultiGameViewer
import com.barrybecker4.game.multiplayer.poker.PokerController
import com.barrybecker4.game.multiplayer.poker.model.{PokerAction, PokerRound}
import com.barrybecker4.game.multiplayer.poker.player.{PokerPlayer, PokerRobotPlayer}
import com.barrybecker4.game.multiplayer.poker.ui.dialog.RoundOverDialog
import com.barrybecker4.game.multiplayer.poker.ui.render.PokerGameRenderer
import javax.swing.JOptionPane
import java.awt.Point

class PokerGameViewer extends MultiGameViewer[Move, com.barrybecker4.game.common.board.Board[Move]] {

  override def createController() = PokerController().asInstanceOf[com.barrybecker4.game.multiplayer.common.MultiGameController[Move, com.barrybecker4.game.common.board.Board[Move]]]

  override def getBoardRenderer: GameBoardRenderer = PokerGameRenderer.getRenderer

  override def getGameOverMessage: String = 
    GameOverMessage(controller.getPlayers).toString

  override def doComputerMove(player: Player): Boolean = {
    assert(!player.isHuman)
    val robot = player.getActualPlayer.asInstanceOf[PokerRobotPlayer]
    val pc = controller.asInstanceOf[PokerController]

    val action = robot.getAction(pc)
    val msg = applyAction(action, robot)
    pc.addRecentRobotAction(action)

    JOptionPane.showMessageDialog(getParentFrame, msg, robot.getName, JOptionPane.INFORMATION_MESSAGE)
    refresh()
    pc.advanceToNextPlayer()

    false
  }

  override def applyAction(action: PlayerAction, player: Player): String = {
    val p = player.asInstanceOf[PokerPlayer]
    val act = action.asInstanceOf[PokerAction]
    val pc = controller.asInstanceOf[PokerController]

    var msg: String = null
    val callAmount = pc.getCurrentMaxContribution - p.getContribution
    val round = pc.getRound

    act.getActionName match {
      case PokerAction.Name.FOLD =>
        p.setFold(true)
        msg = s"${p.getName} folded."
      case PokerAction.Name.CALL =>
        if (callAmount <= p.getCash) {
          p.contributeToPot(round, callAmount)
          msg = s"${p.getName} has called by adding $callAmount to the pot."
        } else {
          p.setFold(true)
          msg = s"${p.getName} folded."
        }
      case PokerAction.Name.RAISE =>
        p.contributeToPot(round, callAmount)
        val raise = act.getRaiseAmount
        p.contributeToPot(round, raise)
        msg = s"${p.getName} has met the $callAmount, and raised the pot by $raise"
    }
    msg
  }

  override def createMove(lastMove: Move): PokerRound = {
    assert(false, "did not expect to call this")
    PokerRound()
  }

  def showRoundOver(winners: java.util.List[PokerPlayer], winnings: Int): Unit = {
    val players = controller.getPlayers
    val it = players.iterator()
    while (it.hasNext) {
      val p = it.next()
      val player = p.getActualPlayer.asInstanceOf[PokerPlayer]
      player.getHand.setFaceUp(true)
    }
    refresh()

    val roundOverDlg = RoundOverDialog(null, winners, winnings)

    val p = this.getParent.getLocationOnScreen

    roundOverDlg.setLocation((p.getX + 0.9 * getParent.getWidth).toInt,
                             (p.getY + getParent.getHeight / 3.0).toInt)

    roundOverDlg.setVisible(true)
  }
}
