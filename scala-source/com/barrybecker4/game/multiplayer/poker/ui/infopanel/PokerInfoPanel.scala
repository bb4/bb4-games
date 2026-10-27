/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.infopanel

import com.barrybecker4.game.common.{GameContext, GameController, Move}
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.common.ui.panel.{GameChangedEvent, GameChangedListener, GameInfoPanel, GeneralInfoPanel, SectionPanel}
import com.barrybecker4.game.multiplayer.poker.model.{PokerAction, PokerRound}
import com.barrybecker4.game.multiplayer.poker.PokerController
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer
import com.barrybecker4.game.multiplayer.poker.ui.dialog.BettingDialog
import com.barrybecker4.ui.components.GradientButton
import javax.swing.{JButton, JOptionPane, JPanel}
import javax.swing.border.TitledBorder
import java.awt.event.{ActionEvent, ActionListener}
import java.text.MessageFormat

class PokerInfoPanel(controller: GameController[?, ?]) extends GameInfoPanel(controller) 
    with ActionListener {

  private var commandButton_ : JButton = scala.compiletime.uninitialized
  private var commandPanel_ : JPanel = scala.compiletime.uninitialized

  override def createSubPanels(): Unit = {
    val customInfoPanel = createCustomInfoPanel()
    generalInfoPanel_ = createGeneralInfoPanel(controller_.getCurrentPlayer)
    add(generalInfoPanel_)

    if (!controller_.getPlayers.allPlayersComputer) {
      add(customInfoPanel)
    }

    add(createChipLegendPanel())
  }

  override def createCustomInfoPanel(): JPanel = {
    commandPanel_ = SectionPanel("")
    setCommandPanelTitle()

    val bp = createPanel()
    bp.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5))

    commandButton_ = GradientButton(GameContext.getLabel("ORDERS"))
    commandButton_.addActionListener(this)
    bp.add(commandButton_)

    commandPanel_.add(bp)
    commandPanel_
  }

  override def createGeneralInfoPanel(player: Player): GeneralInfoPanel = 
    PokerGeneralInfoPanel(player, commandPanel_)

  def createChipLegendPanel(): JPanel = {
    val legendPanel = ChipLegendPanel()
    SectionPanel.styleSectionPanel(legendPanel, "Chip Values")
    legendPanel
  }

  private def setCommandPanelTitle(): Unit = {
    val args = Array[Object](controller_.getCurrentPlayer.getName)
    val title = MessageFormat.format(GameContext.getLabel("MAKE_YOUR_MOVE"), args*)

    val b = commandPanel_.getBorder.asInstanceOf[TitledBorder]
    b.setTitle(title)
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    if (e.getSource == commandButton_) {
      val pc = controller_.asInstanceOf[PokerController]
      if (!pc.getCurrentPlayer.isHuman) {
        JOptionPane.showMessageDialog(this, "It's not your turn", "Warning", JOptionPane.INFORMATION_MESSAGE)
        return
      }
      gameChanged(null)

      val currentPlayer = pc.getCurrentPlayer.getActualPlayer.asInstanceOf[PokerPlayer]

      if (currentPlayer.hasFolded) {
        pc.advanceToNextPlayer()
      }

      val bettingDialog = BettingDialog(pc, getParent)

      val canceled = bettingDialog.showDialog
      if (!canceled) {
        val action = currentPlayer.getAction(pc).asInstanceOf[PokerAction]
        applyPokerAction(action, currentPlayer)

        pc.advanceToNextPlayer()
      }
    }
  }

  private def applyPokerAction(action: PokerAction, currentPlayer: PokerPlayer): Unit = {
    val pc = controller_.asInstanceOf[PokerController]
    val callAmount = pc.getCurrentMaxContribution - currentPlayer.getContribution

    action.getActionName match {
      case PokerAction.Name.FOLD =>
        currentPlayer.setFold(true)
      case PokerAction.Name.CALL =>
        if (callAmount <= currentPlayer.getCash) {
          currentPlayer.contributeToPot(pc.getRound, callAmount)
        } else {
          currentPlayer.setFold(true)
          assert(false, s"callAmount=$callAmount currentPlayer cash=${currentPlayer.getCash}")
        }
      case PokerAction.Name.RAISE =>
        currentPlayer.contributeToPot(pc.getRound, callAmount)
        val raise = action.getRaiseAmount
        currentPlayer.contributeToPot(pc.getRound, raise)
    }
    if (controller_.isOnlinePlayAvailable) {
      controller_.getServerConnection.playerActionPerformed(action)
    }
  }

}
