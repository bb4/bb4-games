/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers.ui

import com.barrybecker4.game.common.GameViewModel
import com.barrybecker4.game.twoplayer.common.ui.dialogs.TwoPlayerNewGameDialog

import java.awt.Component
import java.awt.event.ActionListener

class CheckersNewGameDialog(parent: Component, viewer: GameViewModel)
    extends TwoPlayerNewGameDialog(parent, viewer)
    with ActionListener
