/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui

import com.barrybecker4.game.common.{GameContext, GameController, GameViewModel}
import com.barrybecker4.game.common.ui.dialogs.{GameOptionsDialog, NewGameDialog}
import com.barrybecker4.game.common.ui.panel.{GameInfoPanel, GamePanel}
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.game.multiplayer.galactic.ui.dialog.{GalacticNewGameDialog, GalacticOptionsDialog}

import java.awt.Component

/**
 * This class defines the main UI for the Galactic Empire game applet.
 * It can be run as an applet or application.
 *
 * @author Barry Becker
 */
class GalacticPanel extends GamePanel:

  override def getTitle: String = GameContext.getLabel("GALACTIC_TITLE")

  override def createBoardViewer(): GameBoardViewer[?, ?] = GalaxyViewer()

  override def createNewGameDialog(parent: Component, viewer: GameViewModel): NewGameDialog =
    GalacticNewGameDialog(parent, viewer)

  override def createOptionsDialog(parent: Component, controller: GameController[?, ?]): GameOptionsDialog =
    GalacticOptionsDialog(parent, controller)

  override def createInfoPanel(controller: GameController[?, ?]): GameInfoPanel =
    GalacticInfoPanel(controller)

  override def showHelpDialog(): Unit =
    val name = getTitle
    val comments = GameContext.getLabel("GALACTIC_TITLE")
    val overview = GameContext.getLabel("GALACTIC_OVERVIEW")
    showHelpDialog(name, comments, overview)
