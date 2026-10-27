/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.ui

import com.barrybecker4.game.common.{GameContext, GameController}
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.common.ui.panel.{GameChangedEvent, GameChangedListener, GameInfoPanel, GeneralInfoPanel, SectionPanel}
import com.barrybecker4.game.multiplayer.trivial.{ActionName, TrivialAction, TrivialController}
import com.barrybecker4.game.multiplayer.trivial.player.TrivialPlayer
import com.barrybecker4.game.multiplayer.trivial.ui.dialog.RevealDialog
import com.barrybecker4.ui.components.GradientButton

import javax.swing.{JButton, JPanel}
import java.awt.event.{ActionEvent, ActionListener}

/**
 *  Show information and statistics about the game.
 *
 *  @author Barry Becker
 */
class TrivialInfoPanel(controller: GameController[?, ?]) extends GameInfoPanel(controller):

  private var commandButton: JButton = scala.compiletime.uninitialized
  private var commandPanel: JPanel = scala.compiletime.uninitialized

  override def createSubPanels(): Unit =
    val customPanel = createCustomInfoPanel()
    generalInfoPanel_ = createGeneralInfoPanel(controller_.getCurrentPlayer)
    add(generalInfoPanel_)

    if !controller_.getPlayers.allPlayersComputer then
      add(customPanel)

  /**
   * This panel shows information that is specific to the game type.
   * For Trivial, we have a button that allows the current player to enter his commands
   */
  override def createCustomInfoPanel(): JPanel =
    commandPanel = new SectionPanel()

    val bp = createPanel()
    bp.setBorder(com.barrybecker4.game.common.ui.panel.GameInfoPanel.createMarginBorder())

    commandButton = new GradientButton(GameContext.getLabel("ORDERS"))
    commandButton.addActionListener(new java.awt.event.ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = TrivialInfoPanel.this.actionPerformed(e)
    })
    bp.add(commandButton)

    commandPanel.add(bp)
    commandPanel

  override def createGeneralInfoPanel(player: Player): GeneralInfoPanel =
    new TrivialGeneralInfoPanel(player, commandPanel)

  /**
   * The Orders button was pressed.
   * open the Orders dialog to get the players directives for their move.
   */
  def actionPerformed(e: ActionEvent): Unit =
    if e.getSource == commandButton then
      val pc = controller_.asInstanceOf[TrivialController]
      // Refresh the UI
      generalInfoPanel_.update(controller_)

      val p = pc.getPlayers.get(pc.currentPlayerIndex)
      val currentPlayer = p.getActualPlayer.asInstanceOf[TrivialPlayer]

      val bettingDialog = new RevealDialog(pc, getParent)

      val canceled = bettingDialog.showDialog
      if !canceled then
        val action = currentPlayer.getAction(pc).asInstanceOf[TrivialAction]
        action.actionName match
          case ActionName.KEEP_HIDDEN =>
          case ActionName.REVEAL =>
            currentPlayer.revealValue()

        if controller_.isOnlinePlayAvailable then
          controller_.getServerConnection.playerActionPerformed(action)
        pc.advanceToNextPlayerIndex()

    val mgController = controller_.asInstanceOf[com.barrybecker4.game.multiplayer.common.MultiGameController[?, ?]]
    val enabled = !controller_.isDone && !controller_.getPlayers.get(mgController.currentPlayerIndex).isSurrogate
    commandButton.setEnabled(enabled)
