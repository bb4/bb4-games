/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui

import com.barrybecker4.game.common.{GameContext, GameController, GameViewModel}
import com.barrybecker4.game.common.ui.dialogs.NewGameDialog
import com.barrybecker4.game.common.ui.panel.{GameInfoPanel, GamePanel, GameToolBar}
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.game.twoplayer.common.ui.{AbstractTwoPlayerBoardViewer, TwoPlayerPanel}
import com.barrybecker4.game.twoplayer.common.ui.gametree.GameTreeDialog
import com.barrybecker4.game.twoplayer.go.ui.dialogs.GoNewGameDialog
import com.barrybecker4.game.twoplayer.go.ui.gametree.GoTreeCellRenderer
import java.awt.Component
import java.awt.event.ActionEvent

/**
 *  This class defines the main UI for the Go game panel.
 *
 *  @author Barry Becker
 */
final class GoPanel extends TwoPlayerPanel {

  /**
   * @return the title for the applet/application window
   */
  override def getTitle: String = GameContext.getLabel("GO")

  /**
   * creates a board viewer of the appropriate type given a game controller
   * @return the game board viewer
   */
  override def createBoardViewer(): GameBoardViewer[?, ?] = new GoBoardViewer()

  override def createNewGameDialog(parent: Component, viewer: GameViewModel): NewGameDialog = {
    new GoNewGameDialog(parent, viewer)
  }

  override def createInfoPanel(controller: GameController[?, ?]): GameInfoPanel = {
    new GoInfoPanel(controller)
  }

  override def createGameTreeDialog(): GameTreeDialog = {
    val v = createBoardViewer().asInstanceOf[AbstractTwoPlayerBoardViewer[?, ?]]
    v.setViewOnly(true)
    new GameTreeDialog(null, v, new GoTreeCellRenderer())
  }

  override def createToolbar(): GameToolBar = {
    new GoToolBar(GamePanel.BG_TEXTURE, this)
  }

  /**
   * Display the help dialog to give instructions
   */
  override def showHelpDialog(): Unit = {
    val name = getTitle
    val comments = GameContext.getLabel("GO_COMMENTS")
    val overview = GameContext.getLabel("GO_OVERVIEW")
    showHelpDialog(name, comments, overview)
  }

  /**
   * handle the pass button and the regular ones too.
   */
  override def actionPerformed(e: ActionEvent): Unit = {
    val source = e.getSource
    if (source == toolBar_.asInstanceOf[GoToolBar].getPassButton) {
      boardViewer_.asInstanceOf[GoBoardViewer].pass()
    } else if (source == toolBar_.asInstanceOf[GoToolBar].getResignButton) {
      boardViewer_.asInstanceOf[GoBoardViewer].resign()
    }
    super.actionPerformed(e)
  }
}
