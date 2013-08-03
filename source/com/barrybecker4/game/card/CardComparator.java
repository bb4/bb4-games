// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.card;

import java.util.Comparator;

/**
 * Used to sort a set of cards in ascending order.
 * @author Barry Becker
 */
public class CardComparator implements Comparator<Card> {

    @Override
    public int compare(Card card1, Card card2) {

        if (card1.rank() == card2.rank())   {
            return card1.suit().ordinal() - card2.suit().ordinal();
        }
        else {
            return card1.rank().ordinal() - card2.rank().ordinal();
        }
    }
}
