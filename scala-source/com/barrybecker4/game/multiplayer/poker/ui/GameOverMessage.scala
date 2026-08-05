// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.ui

import com.barrybecker4.game.common.player.{Player, PlayerList}
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer

class GameOverMessage(players: PlayerList) {
  private val buf = StringBuilder("Game Over\n")

  var max = -1
  var winner: PokerPlayer = null
  val it = players.iterator()
  while (it.hasNext) {
    val p = it.next()
    val pp = p.asInstanceOf[PokerPlayer]
    if (pp.getCash > max) {
      max = pp.getCash
      winner = pp
    }
  }
  assert(winner != null)
  buf.append(winner.getName).append(" won the game with $").append(winner.getCash).append('.')

  override def toString: String = buf.toString()
}
