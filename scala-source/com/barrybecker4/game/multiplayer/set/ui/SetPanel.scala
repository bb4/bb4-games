/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui

import com.barrybecker4.game.common.{GameContext, GameController, GameViewModel, Move}
import com.barrybecker4.game.common.ui.dialogs.{GameOptionsDialog, NewGameDialog}
import com.barrybecker4.game.common.ui.panel.{GameChangedEvent, GameInfoPanel, GamePanel, GameToolBar}
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.game.multiplayer.set.SetController

import java.awt.Component
import java.awt.event.ActionEvent

class SetPanel extends GamePanel:

  override def getTitle: String = "Set Game"

  override def createToolbar(): GameToolBar =
    SetToolBar(GamePanel.BG_TEXTURE, this)

  override def createBoardViewer(): GameBoardViewer[?, ?] = SetGameViewer()

  override def createNewGameDialog(parent: Component, viewer: GameViewModel): NewGameDialog =
    SetNewGameDialog(parent, viewer)

  override def createOptionsDialog(parent: Component, controller: GameController[?, ?]): GameOptionsDialog =
    SetOptionsDialog(parent, controller)

  override def createInfoPanel(controller: GameController[?, ?]): GameInfoPanel =
    SetInfoPanel(controller)

  override def gameChanged(gce: GameChangedEvent[Move]): Unit = {}

  override def showHelpDialog(): Unit =
    showHelpDialog(getTitle, GameContext.getLabel("SET_COMMENTS"), GameContext.getLabel("SET_OVERVIEW"))

  override def actionPerformed(e: ActionEvent): Unit =
    val setToolBar = toolBar_.asInstanceOf[SetToolBar]
    val c = boardViewer_.getController.asInstanceOf[SetController]
    val source = e.getSource

    if source eq setToolBar.getAddButton then
      c.addCards(1)
    else if source eq setToolBar.getRemoveButton then
      c.removeCard()
    else if source eq setToolBar.getSolveButton then
      val solutionDialog = SolutionDialog(null, boardViewer_.getController.asInstanceOf[SetController])
      solutionDialog.setVisible(true)
      solutionDialog.pack()

    setToolBar.getRemoveButton.setEnabled(c.canRemoveCards)
    setToolBar.getAddButton.setEnabled(c.hasCardsToAdd)

    super.actionPerformed(e)
    boardViewer_.repaint()
