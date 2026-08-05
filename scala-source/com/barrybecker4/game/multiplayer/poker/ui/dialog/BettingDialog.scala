/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.dialog

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.multiplayer.common.ui.ActionDialog
import com.barrybecker4.game.multiplayer.poker.model.PokerAction
import com.barrybecker4.game.multiplayer.poker.{PokerController, PokerOptions}
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer
import com.barrybecker4.ui.components.GradientButton
import javax.swing.JPanel
import java.awt.Component
import java.awt.event.ActionEvent

class BettingDialog(pc: PokerController, parent: Component) extends ActionDialog(pc, parent) {
  private var foldButton: GradientButton = scala.compiletime.uninitialized
  private var callButton: GradientButton = scala.compiletime.uninitialized
  private var raiseButton: GradientButton = scala.compiletime.uninitialized
  private val callAmount: Int = player_.asInstanceOf[PokerPlayer].getCallAmount(pc)
  private var raiseAmount = 0

  override def createPersonalInfoPanel(): JPanel = 
    PokerHandViewer(player_.asInstanceOf[PokerPlayer].getHand)

  override def createGameInstructionsPanel(): JPanel = 
    GameInstructionsPanel(player_.asInstanceOf[PokerPlayer], callAmount)

  override def createButtonsPanel: JPanel = {
    val buttonsPanel = JPanel(true)
    foldButton = GradientButton()
    initBottomButton(foldButton, GameContext.getLabel("FOLD"), GameContext.getLabel("FOLD_TIP"))
    callButton = GradientButton()
    initBottomButton(callButton, GameContext.getLabel("CALL"), GameContext.getLabel("CALL_TIP"))
    raiseButton = GradientButton()
    initBottomButton(raiseButton, GameContext.getLabel("RAISE"), GameContext.getLabel("RAISE_TIP"))
    buttonsPanel.add(foldButton)
    buttonsPanel.add(callButton)
    buttonsPanel.add(raiseButton)
    buttonsPanel
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    val src = e.getSource
    val action = if (src == foldButton) {
      PokerAction(player_.getName, PokerAction.Name.FOLD, 0)
    } else if (src == callButton) {
      PokerAction(player_.getName, PokerAction.Name.CALL, 0)
    } else {
      val raiseDialog = RaiseDialog(pc, this)
      val canceled = raiseDialog.showDialog
      if (!canceled) {
        raiseAmount = raiseDialog.getRaiseAmount
        PokerAction(player_.getName, PokerAction.Name.RAISE, raiseAmount)
      } else {
        null
      }
    }
    if (action != null) {
      player_.setAction(action)
      this.setVisible(false)
    }
  }
}
