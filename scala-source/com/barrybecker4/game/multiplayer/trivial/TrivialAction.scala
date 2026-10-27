/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial

import com.barrybecker4.game.common.player.PlayerAction

enum ActionName:
  case KEEP_HIDDEN, REVEAL

/**
 * This is what will get sent between client and server as an action for a particular player.
 * Encapsulates the state change.
 *
 * @author Barry Becker
 */
class TrivialAction(playerName: String, val actionName: ActionName) extends PlayerAction(playerName):

  override def toString: String =
    s"[TrivialAction name=$actionName for $getPlayerName]"
