/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui

import com.barrybecker4.game.common.{GameContext, GameController, GameViewModel}
import com.barrybecker4.game.common.ui.dialogs.{GameOptionsDialog, NewGameDialog}
import com.barrybecker4.game.common.ui.panel.{GameInfoPanel, GamePanel}
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.game.multiplayer.poker.ui.dialog.{PokerNewGameDialog, PokerOptionsDialog}
import com.barrybecker4.game.multiplayer.poker.ui.infopanel.PokerInfoPanel
import java.awt.Component

class PokerPanel extends GamePanel {

  override def getTitle: String = GameContext.getLabel("POKER_TITLE")

  override def createBoardViewer(): GameBoardViewer[?, ?] = PokerGameViewer()

  override def createNewGameDialog(parent: Component, viewer: GameViewModel): NewGameDialog = 
    PokerNewGameDialog(parent, viewer)

  override def createOptionsDialog(parent: Component, controller: GameController[?, ?]): GameOptionsDialog = 
    PokerOptionsDialog(parent, controller)

  override def createInfoPanel(controller: GameController[?, ?]): GameInfoPanel = 
    PokerInfoPanel(controller)

  override def showHelpDialog(): Unit = {
    val name = getTitle
    val comments = GameContext.getLabel("POKER_TITLE")
    val overview = GameContext.getLabel("POKER_OVERVIEW")
    showHelpDialog(name, comments, overview)
  }
}
