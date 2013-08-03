// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a standard deck of playing cards without jokers.
 * @author Barry Becker
 */
public class Deck extends ArrayList<Card> {

    /** A prototype deck that gets statically initialized. */
    private static final List<Card> protoDeck = new ArrayList<Card>();

    static {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                protoDeck.add(new Card(rank, suit));
            }
        }
    }

    public Deck() {
        super(protoDeck);
        Collections.shuffle(this);
    }
}