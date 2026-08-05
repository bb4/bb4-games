/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.online

import com.barrybecker4.game.common.GameOptions
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer
import com.barrybecker4.game.multiplayer.common.online.ui.MultiPlayerOnlineTable
import com.barrybecker4.game.multiplayer.trivial.player.TrivialRobotPlayer

import java.awt.Color

/**
 * @author Barry Becker
 */
class OnlineTrivialTable(name: String, initialPlayer: Player, options: GameOptions)
  extends MultiPlayerOnlineTable(name, initialPlayer, options):

  /**
   * add robot player i to the table.
   */
  override def addRobotPlayer(i: Int): Unit =
    val name = "Robot " + (i + 1)
    val newColor = MultiGamePlayer.getNewPlayerColor(getPlayers)

    val robot = new TrivialRobotPlayer(name, newColor)
    this.addPlayer(robot)
