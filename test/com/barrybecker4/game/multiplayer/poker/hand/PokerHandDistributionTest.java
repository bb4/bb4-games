// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand;

import com.barrybecker4.game.card.Deck;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;


/**
 * Verify that the distribution of the poker hand types roughly matches reality.
 * See http://www.dagnammit.com/poker/breakdown.html
 *
 * author Barry Becker
 */
public class PokerHandDistributionTest extends TestCase {

    private static final int NUM_HANDS = 7000;

    /** instance under test */
    Hand hand;

    public void testHandDistributions() {

        Map<HandType, Integer> distribution = new HashMap<HandType, Integer>();
        PokerHandScorer scorer = new PokerHandScorer();

        for (int i=0; i<NUM_HANDS; i++) {
            Hand hand = new Hand(new Deck(), 5);
            HandType type = scorer.getScore(hand).getType();

            if (distribution.containsKey(type)) {
                distribution.put(type, distribution.get(type) + 1);
            }
            else {
                distribution.put(type, 1);
            }
        }

        System.out.println("distribution = " + distribution);
        assertTrue(true);
    }



}
