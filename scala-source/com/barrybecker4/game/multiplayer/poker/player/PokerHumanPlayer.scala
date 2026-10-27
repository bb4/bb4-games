/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.player

import com.barrybecker4.game.common.player.PlayerAction
import com.barrybecker4.game.multiplayer.common.MultiGameController
import com.barrybecker4.game.multiplayer.poker.model.PokerAction
import java.awt.Color

class PokerHumanPlayer(name: String, money: Int, color: Color) 
    extends PokerPlayer(name, money, color, true) {

  override def getAction(pc: MultiGameController[?, ?]): PlayerAction = action

  override def setAction(action: PlayerAction): Unit = 
    this.action = action.asInstanceOf[PokerAction]
}
