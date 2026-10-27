/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.dialog

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer
import javax.swing.{JPanel, JTextArea}
import java.awt.BorderLayout

class GameInstructionsPanel(player: PokerPlayer, callAmount: Int) extends JPanel {
  setLayout(BorderLayout())
  val textArea = JTextArea(5, 20)
  textArea.setEditable(false)
  textArea.setLineWrap(true)
  textArea.setWrapStyleWord(true)
  val instructions = s"${player.getName} has $$${player.getCash}. Call amount: $$$callAmount"
  textArea.setText(instructions)
  add(textArea, BorderLayout.CENTER)
}
