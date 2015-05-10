// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand;

import com.barrybecker4.game.card.Card;
import com.barrybecker4.game.card.Deck;

import java.util.ArrayList;
import java.util.List;

/**
 * A poker hand typically has 5 cards from a deck of normal playing cards.
 * @author Barry Becker
 */
public class Hand {

    /** internal list of cards in the hand. Always sorted from high to low */
    private final List<Card> hand;

    /** true if the hand is visible to all players */
    private boolean faceUp;


    /**
     * Constructor
     * @param hand  the initial poker hand (not necessarily 5 cards)
     */
    public Hand(final List<Card> hand) {
        this.hand = hand;
        faceUp = false;
    }

    /**
     * Deal numCards from the deck and make the poker hand from that.
     * @param deck to deal from
     * @param numCards number of cards to deal from the deck
     */
    public Hand(final Deck deck, int numCards) {
        this(dealCards(deck, numCards));
    }

    /** @return a copy so the client cannot change our state out from under us. */
    public List<Card> getCards() {
        return new ArrayList<>(hand);
    }

    /**
     * whether or not the cards are showing to the rest of the players
     * @param faceUp true if the card is face up.
     */
    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public int size() {
        return hand.size();
    }

    private static List<Card> dealCards(final Deck deck, int numCards) {
        List<Card> hand = new ArrayList<>();
        assert(numCards <= deck.size()) : "you can't deal more cards than you have in the deck";
        for (int i = 0; i < numCards; i++)  {
            hand.add(deck.remove(0));
        }
        return hand;
    }

    public String toString() { return "[" + hand + "]"; }
}
