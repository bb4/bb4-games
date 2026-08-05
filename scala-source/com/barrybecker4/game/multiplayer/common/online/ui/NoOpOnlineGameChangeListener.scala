// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.common.online.ui

import com.barrybecker4.game.common.online.{GameCommand, OnlineChangeListener}
import com.barrybecker4.game.common.player.PlayerAction
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer

/**
 * Use this when you want the command handled, but nothing needs to happen.
 * @author Barry Becker
 */
class NoOpOnlineGameChangeListener(player: MultiGamePlayer) extends OnlineChangeListener {

  /**
   * Simple returns if the command is for the player passed to the constructor.
   * @return true if the command is for the player passed to the constructor.
   */
  override def handleServerUpdate(cmd: GameCommand): Boolean = {
    if (cmd.getName == GameCommand.Name.DO_ACTION) {
      val action = cmd.getArgument.asInstanceOf[PlayerAction]
      if (action.getPlayerName == player.getName) {
        return true
      }
    }
    true
  }
}
