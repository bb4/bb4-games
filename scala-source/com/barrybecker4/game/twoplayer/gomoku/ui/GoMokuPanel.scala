/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.ui

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.{GameController, GameViewModel}
import com.barrybecker4.game.common.ui.dialogs.NewGameDialog
import com.barrybecker4.game.common.ui.panel.GameInfoPanel
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.game.twoplayer.common.ui.{TwoPlayerInfoPanel, TwoPlayerPanel}

import java.awt.Component

/**
 *  This class defines the main UI for the GoMoku game applet.
 *  It can be run as an applet or application.
 *
 *  @author Barry Becker
 */
class GoMokuPanel extends TwoPlayerPanel {

  override def getTitle: String = {
    GameContext.getLabel("GO_MOKU_TITLE")
  }

  override def createBoardViewer(): GameBoardViewer[?, ?] = {
    new GoMokuBoardViewer()
  }

  override def createNewGameDialog(parent: Component, viewer: GameViewModel): NewGameDialog = {
    new GoMokuNewGameDialog(parent, viewer)
  }

  override def createInfoPanel(controller: GameController[?, ?]): GameInfoPanel = {
    new TwoPlayerInfoPanel(controller)
  }

  override def showHelpDialog(): Unit = {
    val name = getTitle
    val comments = GameContext.getLabel("GO_MOKU_TITLE")
    val overview = GameContext.getLabel("GO_MOKU_OVERVIEW")
    showHelpDialog(name, comments, overview)
  }
}
