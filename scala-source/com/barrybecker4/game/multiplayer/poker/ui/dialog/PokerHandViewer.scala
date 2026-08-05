/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.dialog

import com.barrybecker4.game.multiplayer.poker.hand.Hand
import javax.swing.{JLabel, JPanel}

class PokerHandViewer(hand: Hand) extends JPanel {
  add(JLabel(hand.toString))
}
