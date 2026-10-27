/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.ui.dialog

import com.barrybecker4.game.multiplayer.common.MultiGameController
import com.barrybecker4.game.multiplayer.common.ui.ActionDialog
import com.barrybecker4.game.multiplayer.trivial.{ActionName, TrivialAction, TrivialController}
import com.barrybecker4.game.multiplayer.trivial.player.TrivialPlayer
import com.barrybecker4.ui.components.GradientButton

import javax.swing.*
import java.awt.{BorderLayout, Component, FlowLayout, GridLayout}
import java.awt.event.ActionEvent

/**
 * Allow the user to specify a trivial action: keep hidden or reveal.
 * @author Barry Becker
 */
class RevealDialog(pc: TrivialController, parent: Component) extends ActionDialog(pc.asInstanceOf[MultiGameController[?, ?]], parent):

  private var revealButton: JRadioButton = scala.compiletime.uninitialized
  private var okButton: GradientButton = scala.compiletime.uninitialized

  override def createPersonalInfoPanel(): JPanel =
    val p = new JPanel(new BorderLayout())
    val label = new JLabel("Value = " + player_.asInstanceOf[TrivialPlayer].getValue)
    p.add(label, BorderLayout.CENTER)
    p

  override def createGameInstructionsPanel(): JPanel =
    val keepHiddenButton = new JRadioButton("Keep value hidden")
    keepHiddenButton.setSelected(true)

    revealButton = new JRadioButton("Reveal value")

    val group = new ButtonGroup()
    group.add(keepHiddenButton)
    group.add(revealButton)

    val panel = new JPanel(new BorderLayout())
    val choicesPanel = new JPanel(new GridLayout(1, 2))
    choicesPanel.add(keepHiddenButton)
    choicesPanel.add(revealButton)

    panel.add(choicesPanel, BorderLayout.CENTER)

    panel

  /**
   * Create the OK/Cancel buttons that go at the bottom.
   */
  override def createButtonsPanel: JPanel =
    val buttonsPanel = new JPanel(new FlowLayout())

    okButton = new GradientButton()
    initBottomButton(okButton, "OK", "Click when you have made your choice")
    buttonsPanel.add(okButton)

    buttonsPanel

  override def getTitle: String = "Hide or Reveal?"

  /**
   * called when one of the buttons at the bottom have been pressed.
   */
  override def actionPerformed(e: ActionEvent): Unit =
    val source = e.getSource
    var actionName = ActionName.KEEP_HIDDEN
    if source == okButton then
      if revealButton.isSelected then
        actionName = ActionName.REVEAL
        player_.asInstanceOf[TrivialPlayer].revealValue()
      this.setVisible(false)

    player_.setAction(new TrivialAction(player_.getName, actionName))
