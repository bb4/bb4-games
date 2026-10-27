/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.gametree

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.common.TwoPlayerController
import com.barrybecker4.ui.components.GradientButton

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

/**
 * Set of buttons to control debug animation at the bottom fot he gameTreeDialog.
 * @author Barry Becker Date: Dec 24, 2006
 */
class GameTreeButtons(gameTreeDlg: GameTreeDialog) extends JPanel with ActionListener:

  private val pauseButton: JButton = addButton("Pause", "Pause processing", true)
  private val stepButton: JButton = addButton("Step", "Step forward through the search computation", false)
  private val continueButton: JButton = addButton("Continue", "Continue searching for the next move", false)
  private val closeButton: JButton = addButton("Close", "Hide the Game Tree Viewer", true)

  // the controller that is actually being played in the normal view.
  private var mainController: TwoPlayerController[?, ?] = null

  setLayout(FlowLayout())

  def setMainController(mainController: TwoPlayerController[?, ?]): Unit =
    this.mainController = mainController

  private def addButton(label: String, tooltip: String, enabled: Boolean): JButton =
    val b = GradientButton()
    b.setText(label)
    b.setToolTipText(tooltip)
    b.setEnabled(enabled)
    b.addActionListener(this)
    add(b)
    b

  /**
   * called when the ok button is clicked.
   */
  private def pause(): Unit =
    // if we set the root to null, then it doesn't have to build the tree
    pauseButton.setEnabled(false)
    continueButton.setEnabled(true)
    stepButton.setEnabled(true)

    mainController.pause()
    gameTreeDlg.showCurrentGameTree()

  /**
   * called when the ok button is clicked.
   */
  private def step(): Unit =
    mainController.getViewer.step()
    gameTreeDlg.showCurrentGameTree()

  /**
   * called when the ok button is clicked.
   */
  private def continueProcessing(): Unit =
    GameContext.log(1, "continue")
    pauseButton.setEnabled(true)
    continueButton.setEnabled(false)
    stepButton.setEnabled(false)
    mainController.getViewer.continueProcessing()

  /**
   * called when one of the buttons at the bottom have been pressed.
   */
  override def actionPerformed(e: ActionEvent): Unit =
    val source = e.getSource
    if source.equals(pauseButton) then
      pause()
    else if source.equals(stepButton) then
      step()
    else if source.equals(continueButton) then
      continueProcessing()
    else if source.equals(closeButton) then
      gameTreeDlg.close()
