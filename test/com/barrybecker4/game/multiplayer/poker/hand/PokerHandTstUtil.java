// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand;

import com.barrybecker4.game.card.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


/**
 * author Barry Becker
 */
public class PokerHandTstUtil  {

    private PokerHandTstUtil() {}

    public static Hand createHand(String line) {
         List<Card> cards = new ArrayList<Card>(5);

        StringTokenizer tokenizer = new StringTokenizer(line, " ");

        // five entries for for the five cards
        while (tokenizer.hasMoreElements()) {
            String cardToken = (String) tokenizer.nextElement();
            cards.add(new Card(cardToken));
        }

        return new Hand(cards);
    }

}
