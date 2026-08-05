package com.barrybecker4.game.twoplayer.hex.ui

import com.barrybecker4.game.common.{GameContext, GameController, GameViewModel}
import com.barrybecker4.game.common.ui.dialogs.NewGameDialog
import com.barrybecker4.game.common.ui.panel.GameInfoPanel
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.ui.{TwoPlayerInfoPanel, TwoPlayerPanel}
import com.barrybecker4.game.twoplayer.hex.HexBoard

import java.awt.Component

/**
 *  This class defines the main UI for the TicTacToe game applet or application.
 */
class HexPanel extends TwoPlayerPanel:

  override def getTitle: String =
    GameContext.getLabel("HEX_TITLE")

  override def createBoardViewer(): GameBoardViewer[?, ?] =
    HexBoardViewer()

  override def createNewGameDialog(parent: Component, viewer: GameViewModel): NewGameDialog =
    HexNewGameDialog(parent, viewer)

  override def createInfoPanel(controller: GameController[?, ?]): GameInfoPanel =
    TwoPlayerInfoPanel(controller)

  /**
   * Display the help dialog to give instructions
   */
  override def showHelpDialog(): Unit =
    val name = getTitle
    val comments = GameContext.getLabel("HEX_TITLE")
    val overview = GameContext.getLabel("HEX_OVERVIEW")
    showHelpDialog(name, comments, overview)
