/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers.ui

import com.barrybecker4.game.common.{GameContext, GameController, GameViewModel}
import com.barrybecker4.game.common.ui.dialogs.NewGameDialog
import com.barrybecker4.game.common.ui.panel.GameInfoPanel
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.game.twoplayer.checkers.{CheckersBoard, CheckersMove}
import com.barrybecker4.game.twoplayer.common.ui.{TwoPlayerInfoPanel, TwoPlayerPanel}

import java.awt.Component

class CheckersPanel extends TwoPlayerPanel {

  override def getTitle: String = GameContext.getLabel("CHECKERS_TITLE")

  override def createBoardViewer(): GameBoardViewer[?, ?] = new CheckersBoardViewer()

  override def createNewGameDialog(parent: Component, viewer: GameViewModel): NewGameDialog = {
    new CheckersNewGameDialog(parent, viewer)
  }

  override def createInfoPanel(controller: GameController[?, ?]): GameInfoPanel = {
    new TwoPlayerInfoPanel(controller)
  }

  override def showHelpDialog(): Unit = {
    val name = getTitle
    val comments = GameContext.getLabel("CHECKERS_COMMENTS")
    showHelpDialog(name, comments, GameContext.getLabel("CHECKERS_OVERVIEW"))
  }
}
