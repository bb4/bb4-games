/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.dialog

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.multiplayer.common.ui.PlayerLabel
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer
import com.barrybecker4.ui.components.GradientButton
import com.barrybecker4.ui.dialogs.OptionsDialog
import javax.swing.{BorderFactory, JComponent, JLabel, JPanel}
import java.awt.{BorderLayout, Component, FlowLayout}
import java.awt.event.ActionEvent
import java.text.NumberFormat

class RoundOverDialog(parent: Component, winners_ : java.util.List[PokerPlayer], winnings_ : Int) 
    extends OptionsDialog(parent) {

  private var closeButton_ : GradientButton = scala.compiletime.uninitialized
  private var winLabel_ : JLabel = scala.compiletime.uninitialized

  showContent()

  override def createDialogContent: JComponent = {
    setResizable(true)
    val mainPanel = JPanel()
    mainPanel.setLayout(BorderLayout())
    mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
                                                          BorderFactory.createEmptyBorder(10, 10, 10, 10)))
    val buttonsPanel = createButtonsPanel
    val instructions = createInstructionsPanel()

    mainPanel.add(instructions, BorderLayout.CENTER)
    mainPanel.add(buttonsPanel, BorderLayout.SOUTH)

    mainPanel
  }

  private def createInstructionsPanel(): JPanel = {
    val panel = JPanel()
    panel.setLayout(BorderLayout())

    val fmt = NumberFormat.getCurrencyInstance()
    val winMsg = if (winners_.size > 1) {
      s"${winners_.size} players tied and split ${fmt.format(winnings_)}"
    } else {
      s"${winners_.get(0).getName} won ${fmt.format(winnings_)}"
    }
    winLabel_ = JLabel(winMsg)
    panel.add(winLabel_, BorderLayout.NORTH)

    val winnersPanel = JPanel(FlowLayout())
    for (i <- 0 until winners_.size) {
      val label = PlayerLabel()
      label.setPlayer(winners_.get(i))
      winnersPanel.add(label)
    }
    panel.add(winnersPanel, BorderLayout.CENTER)

    panel
  }

  override def createButtonsPanel: JPanel = {
    val buttonsPanel = JPanel(FlowLayout())
    closeButton_ = GradientButton(GameContext.getLabel("CLOSE"))
    initBottomButton(closeButton_, GameContext.getLabel("CLOSE"), GameContext.getLabel("CLOSE_TIP"))
    buttonsPanel.add(closeButton_)
    buttonsPanel
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    if (e.getSource == closeButton_) {
      this.setVisible(false)
    }
  }
}
