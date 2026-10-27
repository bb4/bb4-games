/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.ui

import com.barrybecker4.game.multiplayer.common.{MultiGameController, MultiGamePlayer}
import com.barrybecker4.ui.dialogs.OptionsDialog

import javax.swing._
import java.awt._

/**
 * Allow the user to specify an action - like which planets to attack
 * @author Barry Becker
 */
abstract class ActionDialog(gc: MultiGameController[?, ?], parent: Component)
  extends OptionsDialog(parent) {

  val controller_ : MultiGameController[?, ?] = gc
  val player_ : MultiGamePlayer = controller_.getCurrentPlayer.getActualPlayer.asInstanceOf[MultiGamePlayer]

  {
    val p = parent.getLocationOnScreen
    // offset the dlg so the board is visible as a reference
    setLocation(
      (p.getX + 0.7 * getParent.getWidth).toInt,
      (p.getY + getParent.getHeight / 3.0).toInt)
    showContent()
  }

  /**
   * ui initialization of the tree control.
   */
  override def createDialogContent: JComponent = {
    setResizable(true)
    val mainPanel = new JPanel()

    mainPanel.setLayout(new BorderLayout())
    mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5))

    val personalInfoPanel = createPersonalInfoPanel()
    val buttonsPanel = createButtonsPanel

    val instructions = createInstructionsPanel()

    mainPanel.add(personalInfoPanel, BorderLayout.NORTH)
    mainPanel.add(instructions, BorderLayout.CENTER)
    mainPanel.add(buttonsPanel, BorderLayout.SOUTH)

    mainPanel
  }

  def createPersonalInfoPanel(): JPanel

  private def createInstructionsPanel(): JPanel = {
    val panel = new JPanel(new BorderLayout())
    panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)))
    val playerLabel_ = new PlayerLabel()
    playerLabel_.setPlayer(player_)

    val gameSpecificInstructions = createGameInstructionsPanel()

    panel.add(playerLabel_, BorderLayout.NORTH)
    panel.add(gameSpecificInstructions, BorderLayout.CENTER)

    panel
  }

  def createGameInstructionsPanel(): JPanel

  override def createButtonsPanel: JPanel
}
