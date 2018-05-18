// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.model;

import com.barrybecker4.game.card.Deck;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.multiplayer.poker.hand.Hand;
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer;

/**
 * Poker dealer deals the cards to the players from a new shuffled deck.
 *
 * @author Barry Becker
 */
public class Dealer  {

    /**
     * Deal the cards. Give the default players some cards.
     * @param players the players to deal to.
     * @param numCardsToDealToEachPlayer number of cards to deal
     */
    public void dealCardsToPlayers(PlayerList players, int numCardsToDealToEachPlayer) {

        Deck deck = new Deck();
        assert (players != null) : "No players! (players is null)";
        for (Player p : players) {
            if (deck.size() < numCardsToDealToEachPlayer) {
                // ran out of cards. start a new shuffled deck.
                deck = new Deck();
            }
            PokerPlayer player = (PokerPlayer) p.getActualPlayer();
            player.setHand(new Hand(deck, numCardsToDealToEachPlayer));
            player.resetPlayerForNewRound();
        }
    }

}
