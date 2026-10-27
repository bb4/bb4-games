/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess.ui

import com.barrybecker4.game.common.{GameContext, GameViewModel}
import com.barrybecker4.game.twoplayer.common.ui.dialogs.TwoPlayerNewGameDialog

import java.awt.Component
import java.awt.event.ActionListener

class ChessNewGameDialog(parent: Component, viewer: GameViewModel)
    extends TwoPlayerNewGameDialog(parent, viewer)
    with ActionListener {

  override final def getTitle: String = GameContext.getLabel("CHESS_OPTIONS")
}
