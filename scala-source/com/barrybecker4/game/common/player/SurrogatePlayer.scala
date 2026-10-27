/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.player

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.online.{GameCommand, OnlineChangeListener}
import com.barrybecker4.game.common.online.server.IServerConnection

/**
 * On the server, all players are surrogates except for the robot players.
 * On the client, all players are surrogates except for the human player that is controlling that client.
 *
 * @author Barry Becker
 */
class SurrogatePlayer(private val player_ : Player, connection: IServerConnection) 
    extends Player(player_.getName, player_.getColor, player_.isHuman) 
    with OnlineChangeListener {

  connection.addOnlineChangeListener(this)

  override def handleServerUpdate(cmd: GameCommand): Boolean = {
    if (cmd.getName == GameCommand.Name.DO_ACTION) {
      val action = cmd.getArgument.asInstanceOf[PlayerAction]
      GameContext.log(0, s"in SurrogatePlayer handleServerUpdate (currently ignored) action =$action")
      return true
    }
    false
  }

  override def getActualPlayer: Player = player_

  override def isSurrogate: Boolean = true
}
