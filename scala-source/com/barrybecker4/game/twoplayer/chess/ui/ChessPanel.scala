/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess.ui

import com.barrybecker4.game.common.{GameContext, GameController, GameViewModel}
import com.barrybecker4.game.common.ui.dialogs.NewGameDialog
import com.barrybecker4.game.common.ui.panel.GameInfoPanel
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.game.twoplayer.common.ui.{TwoPlayerInfoPanel, TwoPlayerPanel}

import java.awt.Component

class ChessPanel extends TwoPlayerPanel {

  override def getTitle: String = GameContext.getLabel("CHESS_TITLE")

  override def createBoardViewer(): GameBoardViewer[_, _] = new ChessBoardViewer()

  override def createNewGameDialog(parent: Component, viewer: GameViewModel): NewGameDialog = {
    new ChessNewGameDialog(parent, viewer)
  }

  override def createInfoPanel(controller: GameController[_, _]): GameInfoPanel = {
    new TwoPlayerInfoPanel(controller)
  }

  override def showHelpDialog(): Unit = {
    val name = getTitle
    val comments = GameContext.getLabel("CHESS_COMMENTS")
    val overview = GameContext.getLabel("CHESS_OVERVIEW")
    showHelpDialog(name, comments, overview)
  }
}
