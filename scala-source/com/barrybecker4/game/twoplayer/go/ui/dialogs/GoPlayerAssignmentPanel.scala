// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.ui.dialogs

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.common.TwoPlayerController
import com.barrybecker4.game.twoplayer.common.ui.dialogs.PlayerAssignmentPanel
import java.awt.Component

/**
 * Use this modal dialog to initialize the required game parameters that
 * are needed in order to start play in a 2 player game.
 *
 * @author Barry Becker
 */
class GoPlayerAssignmentPanel(controller: TwoPlayerController[_, _], parent: Component) 
  extends PlayerAssignmentPanel(controller, parent) {

  private val BLACK_IS = GameContext.getLabel("BLACK_IS")
  private val WHITE_IS = GameContext.getLabel("WHITE_IS")

  override def getPlayer1Label(): String = BLACK_IS

  override def getPlayer2Label(): String = WHITE_IS
}
