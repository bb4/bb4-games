/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.ui

import com.barrybecker4.game.common.{GameContext, GameController, GameViewModel}
import com.barrybecker4.game.common.ui.dialogs.{GameOptionsDialog, NewGameDialog}
import com.barrybecker4.game.common.ui.panel.{GameInfoPanel, GamePanel}
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.game.multiplayer.trivial.ui.dialog.{TrivialNewGameDialog, TrivialOptionsDialog}

import java.awt.Component

/**
 *  This class defines the main UI for the Trivial game applet.
 *  It can be run as an applet or application.
 *
 *  @author Barry Becker
 */
class TrivialPanel extends GamePanel:

  override def getTitle: String =
    GameContext.getLabel("TRIVIAL_TITLE")

  override def createBoardViewer(): GameBoardViewer[?, ?] =
    new TrivialGameViewer()

  override def createNewGameDialog(parent: Component, viewer: GameViewModel): NewGameDialog =
    new TrivialNewGameDialog(parent, viewer)

  override def createOptionsDialog(parent: Component, controller: GameController[?, ?]): GameOptionsDialog =
    new TrivialOptionsDialog(parent, controller)

  override def createInfoPanel(controller: GameController[?, ?]): GameInfoPanel =
    new TrivialInfoPanel(controller)

  override def showHelpDialog(): Unit =
    val name = getTitle
    val comments = GameContext.getLabel("TRIVIAL_TITLE")
    val overview = GameContext.getLabel("TRIVIAL_OVERVIEW")
    showHelpDialog(name, comments, overview)
