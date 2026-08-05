/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.online.ui

import com.barrybecker4.game.common.{GameContext, GameOptions}
import com.barrybecker4.game.common.online.OnlineGameTable
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.multiplayer.common.MultiGameOptions

/**
 * Some number of players sitting around a virtual game table online.
 * @author Barry Becker
 */
abstract class MultiPlayerOnlineTable(name: String, initialPlayer: Player, options: GameOptions)
  extends OnlineGameTable(name, initialPlayer, options) {

  private val numRobots = options.asInstanceOf[MultiGameOptions].getNumRobotPlayers
  var i = 0
  while (i < numRobots) {
    addRobotPlayer(i)
    i += 1
  }

  def addRobotPlayer(i: Int): Unit
}
