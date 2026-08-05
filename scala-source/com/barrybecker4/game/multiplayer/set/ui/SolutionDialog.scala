/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.multiplayer.set.SetController
import com.barrybecker4.ui.components.GradientButton
import com.barrybecker4.ui.dialogs.OptionsDialog

import java.awt.{BorderLayout, Component, FlowLayout, event}
import java.awt.event.ActionEvent
import javax.swing.{JComponent, JPanel}
import scala.compiletime.uninitialized

class SolutionDialog(parent: Component, controller: SetController) extends OptionsDialog(parent):

  private val okButton_ = GradientButton()
  private var solutionPanel_ : SolutionPanel = uninitialized

  showContent()

  override def createDialogContent: JComponent =
    val mainPanel = JPanel()
    mainPanel.setLayout(BorderLayout())
    val buttonsPanel = createButtonsPanel
    val sets = controller.getSetsOnBoard
    solutionPanel_ = SolutionPanel(sets, controller.getViewer.asInstanceOf[SetGameViewer])
    val solutionsHolder = JPanel()
    solutionsHolder.add(solutionPanel_)
    mainPanel.add(solutionsHolder, BorderLayout.CENTER)
    mainPanel.add(buttonsPanel, BorderLayout.SOUTH)
    mainPanel

  override def getTitle: String = GameContext.getLabel("SETS_ON_BOARD")

  override def createButtonsPanel: JPanel =
    val buttonsPanel = JPanel(FlowLayout())
    initBottomButton(okButton_, GameContext.getLabel("OK"), GameContext.getLabel("OK"))
    buttonsPanel.add(okButton_)
    buttonsPanel

  private def ok(): Unit =
    solutionPanel_.closed()
    setVisible(false)

  override def actionPerformed(e: ActionEvent): Unit =
    if e.getSource eq okButton_ then ok()
