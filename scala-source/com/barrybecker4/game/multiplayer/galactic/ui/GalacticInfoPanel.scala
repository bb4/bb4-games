/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui

import com.barrybecker4.game.common.{GameContext, GameController, Move}
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.common.ui.panel.{
  GameChangedEvent, GameInfoPanel, GeneralInfoPanel, SectionPanel
}
import com.barrybecker4.game.multiplayer.galactic.{GalacticController, Galaxy}
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer
import com.barrybecker4.game.multiplayer.galactic.ui.dialog.OrdersDialog
import com.barrybecker4.ui.components.GradientButton

import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.{JButton, JPanel}
import scala.compiletime.uninitialized

/**
 * Show information and statistics about the game.
 * Also allow the player to enter their commands for the turn.
 *
 * @author Barry Becker
 */
class GalacticInfoPanel(controller: GameController[?, ?]) extends GameInfoPanel(controller)
  with ActionListener:

  private var commandButton: JButton = uninitialized
  private var passButton: JButton = uninitialized
  private var commandPanel: JPanel = uninitialized

  override def createSubPanels(): Unit =
    val customInfoPanel = createCustomInfoPanel()
    generalInfoPanel_ = createGeneralInfoPanel(controller_.getCurrentPlayer)
    add(generalInfoPanel_)

    if !controller_.getPlayers.allPlayersComputer then
      add(customInfoPanel)

  override def createGeneralInfoPanel(player: Player): GeneralInfoPanel =
    GalacticGeneralInfoPanel(player, commandPanel)

  override def createCustomInfoPanel(): JPanel =
    commandPanel = SectionPanel()

    val bp = createPanel()
    bp.setBorder(GameInfoPanel.createMarginBorder())

    commandButton = GradientButton(GameContext.getLabel("ORDERS"))
    commandButton.addActionListener(this)
    bp.add(commandButton)

    passButton = GradientButton(GameContext.getLabel("PASS"))
    passButton.addActionListener(this)
    bp.add(passButton)

    commandPanel.add(bp)
    commandPanel

  override def actionPerformed(e: ActionEvent): Unit =
    val gc = controller_.asInstanceOf[GalacticController]
    gameChanged(null)

    if e.getSource eq commandButton then
      if Galaxy.getPlanets(gc.getCurrentPlayer.asInstanceOf[GalacticPlayer]).size() == 0 then
        gc.advanceToNextPlayer()
      showOrdersDialog(gc)
    else if e.getSource eq passButton then
      gc.advanceToNextPlayer()

  private def showOrdersDialog(gc: GalacticController): Unit =
    val currentPlayer = gc.getCurrentPlayer.asInstanceOf[GalacticPlayer]

    val ordersDialog = OrdersDialog(null, currentPlayer, gc.getNumberOfYearsRemaining)
    val p = getParent.getLocationOnScreen

    ordersDialog.setLocation(
      (p.getX + 0.7 * getParent.getWidth).toInt,
      (p.getY + getParent.getHeight / 3.0).toInt)

    val canceled = ordersDialog.showDialog
    if !canceled then
      currentPlayer.setOrders(ordersDialog.getOrders)
      gc.advanceToNextPlayer()

  override def gameChanged(gce: GameChangedEvent[Move]): Unit =
    if controller_ != null then
      generalInfoPanel_.update(controller_)
