/* Copyright by Barry G. Becker, 20014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.ui

import com.barrybecker4.game.common.{GameContext, GameController, GameViewModel}
import com.barrybecker4.game.common.ui.dialogs.NewGameDialog
import com.barrybecker4.game.common.ui.panel.GameInfoPanel
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.game.twoplayer.common.ui.{TwoPlayerInfoPanel, TwoPlayerPanel}
import java.awt.Component

/**
  * This class defines the main UI for the Mancala game applet.
  * It can be run as an applet or application.
  *
  * @author Barry Becker
  */
class MancalaPanel extends TwoPlayerPanel:

  override def getTitle: String =
    GameContext.getLabel("MANCALA_TITLE")

  override def createBoardViewer(): GameBoardViewer[?, ?] =
    MancalaBoardViewer()

  override def createNewGameDialog(parent: Component, viewer: GameViewModel): NewGameDialog =
    MancalaNewGameDialog(parent, viewer)

  override def createInfoPanel(controller: GameController[?, ?]): GameInfoPanel =
    TwoPlayerInfoPanel(controller)

  override def showHelpDialog(): Unit =
    val name = getTitle
    val comments = GameContext.getLabel("MANCALA_TITLE")
    val overview = GameContext.getLabel("MANCALA_OVERVIEW")
    showHelpDialog(name, comments, overview)
