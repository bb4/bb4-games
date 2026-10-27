/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.dialog

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.multiplayer.poker.{PokerController, PokerOptions}
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer
import com.barrybecker4.ui.components.NumberInput
import com.barrybecker4.ui.dialogs.OptionsDialog
import javax.swing.{JComponent, JPanel}
import java.awt.{BorderLayout, Component}

class RaiseDialog(pc: PokerController, parent: Component) extends OptionsDialog(parent) {
  private var raiseAmount_ : NumberInput = scala.compiletime.uninitialized

  showContent()

  override def createDialogContent: JComponent = {
    val mainPanel = JPanel()
    mainPanel.setLayout(BorderLayout())

    val player = pc.getCurrentPlayer.asInstanceOf[PokerPlayer]
    val maxRaise = pc.getOptions.asInstanceOf[PokerOptions].getMaxAbsoluteRaise
    val allInAmt = pc.getAllInAmount - player.getContribution - player.getCallAmount(pc)

    raiseAmount_ = NumberInput(GameContext.getLabel("RAISE"), 1,
                              GameContext.getLabel("RAISE_TIP"), 1, Math.min(maxRaise, allInAmt), true)

    mainPanel.add(raiseAmount_, BorderLayout.CENTER)
    mainPanel
  }

  def getRaiseAmount: Int = raiseAmount_.getIntValue
  
  override protected def createButtonsPanel: JPanel = JPanel()
}
