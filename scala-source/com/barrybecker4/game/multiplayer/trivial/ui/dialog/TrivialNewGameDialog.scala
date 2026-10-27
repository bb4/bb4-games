/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.ui.dialog

import com.barrybecker4.game.common.GameViewModel
import com.barrybecker4.game.common.online.ui.OnlineGameManagerPanel
import com.barrybecker4.game.multiplayer.common.ui.{MultiPlayerNewGameDialog, PlayerTable}
import com.barrybecker4.game.multiplayer.trivial.online.ui.OnlineTrivialManagerPanel

import java.awt.Component

/**
 * @author Barry Becker
 */
class TrivialNewGameDialog(parent: Component, viewer: GameViewModel) 
  extends MultiPlayerNewGameDialog(parent, viewer):

  override def createPlayerTable(): PlayerTable =
    new TrivialPlayerTable(controller_.getPlayers)

  override def createPlayOnlinePanel(): OnlineGameManagerPanel =
    new OnlineTrivialManagerPanel(viewer_, this)
