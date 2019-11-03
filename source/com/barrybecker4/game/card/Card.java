/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.card;

import java.io.Serializable;

/**
 * Represents a standard playing card.
 */
public class Card implements Serializable {

    private static final long serialVersionUID = 1;

    private final Rank rank;
    private final Suit suit;

    /**
     * Constructor.
     * @param rank 2 - Ace
     * @param suit - space, diamond, clubs, hearts
     */
    Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    /**
     * Create a card instance from a string
     * @param cardToken string representation of the card (e.g. "JD", or "10H")
     */
    public Card(String cardToken) {
        int len = cardToken.length();
        assert (len < 4);

        this.rank = Rank.getRankForSymbol(cardToken.substring(0, len-1));
        this.suit = Suit.getSuitForSymbol(cardToken.substring(len-1));
    }

    public Rank rank() { return rank; }

    public Suit suit() { return suit; }

    @Override
    public String toString() { return rank + " of " + suit; }
}
