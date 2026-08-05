// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.galactic.ui

import com.barrybecker4.game.common.{GameContext, GameController, Move}
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.common.ui.panel.GeneralInfoPanel

import javax.swing.JPanel
import javax.swing.border.TitledBorder
import java.awt.Color
import java.text.MessageFormat

/**
 * Show information and statistics about the game.
 * Also allow the player to enter their commands for the turn.
 *
 * @author Barry Becker
 */
class GalacticGeneralInfoPanel(player: Player, commandPanel: JPanel) extends GeneralInfoPanel(player):

  setCommandPanelTitle(player)

  override def setPlayerLabel(player: Player): Unit =
    val playerName = player.getName
    playerLabel.setText(s" $playerName ")

    val pColor = player.getColor
    playerLabel.setBorder(getPlayerLabelBorder(pColor))

    if commandPanel != null then
      commandPanel.setForeground(pColor)
      setCommandPanelTitle(player)
    repaint()

  override def update(controller: GameController[?, ?]): Unit =
    setPlayerLabel(controller.getCurrentPlayer)
    val lastMove = controller.getLastMove
    if lastMove != null then
      moveNumLabel.setText(s"${controller.getPlayers.getNumPlayers + 2} ")
    else
      moveNumLabel.setText("1 ")

  private def setCommandPanelTitle(player: Player): Unit =
    val args = Array[AnyRef](player.getName)
    val title = MessageFormat.format(GameContext.getLabel("GIVE_YOUR_ORDERS"), args*)
    val b = commandPanel.getBorder.asInstanceOf[TitledBorder]
    b.setTitle(title)
