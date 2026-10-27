/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.online

import com.barrybecker4.game.common.GameOptions
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer
import com.barrybecker4.game.multiplayer.common.online.ui.MultiPlayerOnlineTable
import com.barrybecker4.game.multiplayer.poker.PokerOptions
import com.barrybecker4.game.multiplayer.poker.player.{PokerPlayer, PokerRobotPlayer}
import java.awt.Color

class OnlinePokerTable(name: String, initialPlayer: Player, options: GameOptions) 
    extends MultiPlayerOnlineTable(name, initialPlayer, options) {

  override def addRobotPlayer(i: Int): Unit = {
    val newColor = MultiGamePlayer.getNewPlayerColor(getPlayers)

    val opts = getGameOptions.asInstanceOf[PokerOptions]
    val name = s"Robot ${i + 1}"
    val robot = PokerRobotPlayer.getRandomRobotPlayer(name, opts.getInitialCash, newColor)
    this.addPlayer(robot)
  }
}
