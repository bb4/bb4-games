// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand;

import com.barrybecker4.game.card.Deck;
import junit.framework.TestCase;

import static com.barrybecker4.game.multiplayer.poker.hand.PokerHandTstUtil.createHand;


/**
 * programming challenge to test which poker hands are better
 * see http://www.programming-challenges.com/pg.php?page=downloadproblem&probid=110202&format=html
 *
 * author Barry Becker
 */
public class PokerHandTest extends TestCase {


    /**
     * est out a bunch of hands of different sizes
     */
    public void testHands() {

        Deck deck = new Deck();

        for (int i=5; i<9; i++) {
            Hand hand = new Hand(deck, i);
            assertEquals("Unexpected size of hand.", i, hand.size());
        }
    }

}
