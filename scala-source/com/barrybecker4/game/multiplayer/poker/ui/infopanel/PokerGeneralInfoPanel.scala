// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.ui.infopanel

import com.barrybecker4.game.common.{GameContext, GameController, Move}
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.common.ui.panel.GeneralInfoPanel
import javax.swing.JPanel
import javax.swing.border.TitledBorder
import java.awt.Color
import java.text.MessageFormat

class PokerGeneralInfoPanel(player: Player, private val commandPanel_ : JPanel) 
    extends GeneralInfoPanel(player) {

  setCommandPanelTitle(player)

  override def setPlayerLabel(player: Player): Unit = {
    val playerName = player.getName
    playerLabel.setText(s" $playerName ")

    val pColor = player.getColor

    playerLabel.setBorder(getPlayerLabelBorder(pColor))

    if (commandPanel_ != null) {
      commandPanel_.setForeground(pColor)
      setCommandPanelTitle(player)
    }
    this.repaint()
  }

  private def setCommandPanelTitle(player: Player): Unit = {
    val args = Array[Object](player.getName)
    val title = MessageFormat.format(GameContext.getLabel("MAKE_YOUR_MOVE"), args*)

    val b = commandPanel_.getBorder.asInstanceOf[TitledBorder]
    b.setTitle(title)
  }

  override def update(controller: GameController[?, ?]): Unit = {
    setPlayerLabel(controller.getCurrentPlayer)
    val lastMove = controller.getLastMove
    if (lastMove != null) {
      moveNumLabel.setText(s"${controller.getNumMoves + 2} ")
    } else {
      moveNumLabel.setText("1 ")
    }
  }
}
