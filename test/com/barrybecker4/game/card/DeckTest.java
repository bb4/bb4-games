// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.card;

import junit.framework.TestCase;
import java.util.Collections;

/**
 * @author Barry Becker
 */
public class DeckTest extends TestCase {

    /**
     * Verify that we can sort the cards in a shuffled deck.
     */
    @SuppressWarnings("HardCodedStringLiteral")
    public void testSortShuffledDeck() {

        Deck deck = new Deck();

        Collections.sort(deck, new CardComparator());

        assertEquals("The cards in the deck were not properly sorted.",
              "[DEUCE of SPADES, DEUCE of CLUBS, DEUCE of DIAMONDS, DEUCE of HEARTS, " +
                "THREE of SPADES, THREE of CLUBS, THREE of DIAMONDS, THREE of HEARTS, " +
                "FOUR of SPADES, FOUR of CLUBS, FOUR of DIAMONDS, FOUR of HEARTS, " +
                "FIVE of SPADES, FIVE of CLUBS, FIVE of DIAMONDS, FIVE of HEARTS, " +
                "SIX of SPADES, SIX of CLUBS, SIX of DIAMONDS, SIX of HEARTS, " +
                "SEVEN of SPADES, SEVEN of CLUBS, SEVEN of DIAMONDS, SEVEN of HEARTS, " +
                "EIGHT of SPADES, EIGHT of CLUBS, EIGHT of DIAMONDS, EIGHT of HEARTS, " +
                "NINE of SPADES, NINE of CLUBS, NINE of DIAMONDS, NINE of HEARTS, " +
                "TEN of SPADES, TEN of CLUBS, TEN of DIAMONDS, TEN of HEARTS, " +
                "JACK of SPADES, JACK of CLUBS, JACK of DIAMONDS, JACK of HEARTS, " +
                "QUEEN of SPADES, QUEEN of CLUBS, QUEEN of DIAMONDS, QUEEN of HEARTS, " +
                "KING of SPADES, KING of CLUBS, KING of DIAMONDS, KING of HEARTS, " +
                "ACE of SPADES, ACE of CLUBS, ACE of DIAMONDS, ACE of HEARTS]",
                deck.toString());
    }
}
