/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.dialog

import com.barrybecker4.game.common.GameViewModel
import com.barrybecker4.game.common.online.ui.OnlineGameManagerPanel
import com.barrybecker4.game.multiplayer.common.ui.{MultiPlayerNewGameDialog, PlayerTable}
import com.barrybecker4.game.multiplayer.poker.online.ui.OnlinePokerManagerPanel
import java.awt.Component

class PokerNewGameDialog(parent: Component, viewer: GameViewModel) 
    extends MultiPlayerNewGameDialog(parent, viewer) {

  override def createPlayerTable(): PlayerTable = 
    PokerPlayerTable(controller_.getPlayers)

  override def createPlayOnlinePanel(): OnlineGameManagerPanel = 
    OnlinePokerManagerPanel(viewer_, this)
}
