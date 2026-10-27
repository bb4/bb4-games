/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.ui

import com.barrybecker4.game.common.player.Player

import javax.swing.{JLabel, JPanel}
import java.awt.Dimension

/**
 * UI element for showing the players name.
 * @author Barry Becker
 */
class PlayerLabel extends JPanel {

  private val swatch = new JPanel()
  private val playerLabel = new JLabel()

  swatch.setPreferredSize(new Dimension(10, 10))
  add(swatch)
  add(playerLabel)

  def setPlayer(player: Player): Unit = {
    swatch.setBackground(player.getColor)
    playerLabel.setText(player.getName)
  }
}
