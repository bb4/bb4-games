/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe.ui

import com.barrybecker4.game.common.{GameContext, GameController, GameViewModel}
import com.barrybecker4.game.common.ui.dialogs.NewGameDialog
import com.barrybecker4.game.common.ui.panel.GameInfoPanel
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerInfoPanel
import com.barrybecker4.game.twoplayer.gomoku.ui.GoMokuPanel

import java.awt.Component

/**
 * This class defines the main UI for the TicTacToe game applet or application.
 *
 * @author Barry Becker
 */
class TicTacToePanel extends GoMokuPanel:

  override def getTitle: String =
    GameContext.getLabel("TICTACTOE_TITLE")

  override def createBoardViewer(): GameBoardViewer[?, ?] =
    new TicTacToeBoardViewer()

  override def createNewGameDialog(parent: Component, viewer: GameViewModel): NewGameDialog =
    new TicTacToeNewGameDialog(parent, viewer)

  override def createInfoPanel(controller: GameController[?, ?]): GameInfoPanel =
    new TwoPlayerInfoPanel(controller)

  override def showHelpDialog(): Unit =
    val name = getTitle
    val comments = GameContext.getLabel("TICTACTOE_TITLE")
    val overview = GameContext.getLabel("TICTACTOE_OVERVIEW")
    showHelpDialog(name, comments, overview)
