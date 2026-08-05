// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.model

import com.barrybecker4.game.card.Deck
import com.barrybecker4.game.common.player.{Player, PlayerList}
import com.barrybecker4.game.multiplayer.poker.hand.Hand
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer

class Dealer {
  def dealCardsToPlayers(players: PlayerList, numCardsToDealToEachPlayer: Int): Unit = {
    var deck = Deck()
    assert(players != null, "No players! (players is null)")
    
    val it = players.iterator()
    while (it.hasNext) {
      val p = it.next()
      if (deck.size < numCardsToDealToEachPlayer) {
        deck = Deck()
      }
      val player = p.getActualPlayer.asInstanceOf[PokerPlayer]
      player.setHand(Hand(deck, numCardsToDealToEachPlayer))
      player.resetPlayerForNewRound()
    }
  }
}
