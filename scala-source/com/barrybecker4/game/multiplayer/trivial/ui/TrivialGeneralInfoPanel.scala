// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.trivial.ui

import com.barrybecker4.game.common.{GameContext, GameController}
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.common.ui.panel.GeneralInfoPanel

import javax.swing.JPanel
import javax.swing.border.TitledBorder
import java.awt.Color
import java.text.MessageFormat

/**
 *  Show information and statistics about the game.
 *
 *  @author Barry Becker
 */
class TrivialGeneralInfoPanel(player: Player, commandPanel: JPanel) extends GeneralInfoPanel(player):

  private val commandPanel_ = commandPanel
  setCommandPanelTitle(player)

  /**
   * set the appropriate text and color for the player label.
   */
  override def setPlayerLabel(player: Player): Unit =
    val playerName = player.getName
    playerLabel.setText(s" $playerName ")

    val pColor = player.getColor

    playerLabel.setBorder(getPlayerLabelBorder(pColor))

    if commandPanel_ != null then
      commandPanel_.setForeground(pColor)
      setCommandPanelTitle(player)
    
    this.repaint()

  private def setCommandPanelTitle(player: Player): Unit =
    val args = Array[AnyRef](player.getName)
    val title = MessageFormat.format(GameContext.getLabel("MAKE_YOUR_MOVE"), args*)

    val b = commandPanel_.getBorder.asInstanceOf[TitledBorder]
    b.setTitle(title)

  override def update(controller: GameController[?, ?]): Unit =
    val mgController = controller.asInstanceOf[com.barrybecker4.game.multiplayer.common.MultiGameController[?, ?]]
    setPlayerLabel(controller.getPlayers.get(mgController.currentPlayerIndex))
    val lastMove = controller.getLastMove
    if lastMove != null then
      moveNumLabel.setText(s"${controller.getNumMoves + 2} ")
    else
      moveNumLabel.setText("1 ")
