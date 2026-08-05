/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui

import com.barrybecker4.game.common.GameViewModel
import com.barrybecker4.game.multiplayer.common.ui.{MultiPlayerNewGameDialog, PlayerTable}

import java.awt.Component

class SetNewGameDialog(parent: Component, viewer: GameViewModel)
  extends MultiPlayerNewGameDialog(parent, viewer):

  override def createPlayerTable(): PlayerTable =
    SetPlayerTable(controller_.getPlayers)
