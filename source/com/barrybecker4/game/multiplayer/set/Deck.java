// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.set;

import com.barrybecker4.game.common.GameContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static com.barrybecker4.game.multiplayer.set.Card.AttributeValue;

/**
 * @author Barry Becker
 */
public class Deck extends ArrayList<Card> {

    private static final List<Card> protoDeck = new ArrayList<>();

    // Initialize prototype deck
    static {
        for (AttributeValue color : Card.AttributeValue.values())  {
            for (AttributeValue shape : AttributeValue.values())  {
                 for (AttributeValue number : AttributeValue.values()) {
                     for (AttributeValue texture : AttributeValue.values())  {
                        protoDeck.add(new Card(color, shape, number, texture));
                     }
                 }
            }
        }
    }

    /** constructor */
    public Deck() {
        super(protoDeck);
        Collections.shuffle(this);
    }


    public static void main(String[] args) {
        GameContext.log(0, "deck=" + new Deck());
    }
}