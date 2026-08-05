/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.model

import com.barrybecker4.game.common.player.PlayerAction
import PokerAction.Name

class PokerAction(playerName: String, private val name: Name, private val raiseAmount: Int)
    extends PlayerAction(playerName) {

  def getActionName: Name = name

  def getRaiseAmount: Int = raiseAmount
}

object PokerAction {
  enum Name extends java.lang.Enum[Name] {
    case FOLD, CALL, RAISE
  }
}
