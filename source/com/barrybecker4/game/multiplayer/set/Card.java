/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Barry Becker
 */
public class Card {

    public enum AttributeValue {
        FIRST, SECOND, THIRD
    }

    public enum Attribute {
        COLOR, SHAPE, NUMBER, TEXTURE
    }

    private final AttributeValue[] attributes_ = new AttributeValue[Attribute.values().length];

    private boolean isHighlighted_ = false;
    private boolean isSelected_ = false;

    Card(AttributeValue color, AttributeValue shape, AttributeValue number, AttributeValue texture) {
        attributes_[Attribute.COLOR.ordinal()] = color;
        attributes_[Attribute.SHAPE.ordinal()] = shape;
        attributes_[Attribute.NUMBER.ordinal()] = number;
        attributes_[Attribute.TEXTURE.ordinal()] = texture;
    }


    AttributeValue valueOfAttribute(Attribute a) {
         return attributes_[a.ordinal()];
    }

    public AttributeValue color() { return attributes_[Attribute.COLOR.ordinal()]; }
    public AttributeValue shape() { return attributes_[Attribute.SHAPE.ordinal()]; }
    public AttributeValue number() { return attributes_[Attribute.NUMBER.ordinal()]; }
    public AttributeValue texture() { return attributes_[Attribute.TEXTURE.ordinal()]; }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < attributes_.length; i++) {
            AttributeValue value = attributes_[i];
            buf.append(Attribute.values()[i]).append(":").append(value).append("  ");
        }
        return buf.toString();
    }

    public boolean isSelected() {
        return isSelected_;
    }

    public void setSelected(boolean selected) {
        isSelected_ = selected;
    }

    public boolean isHighlighted() {
        return isHighlighted_;
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted_ = highlighted;
    }

    public void toggleSelect() {
        isSelected_ = !isSelected_;
    }

    /**
     * reutrn true if the set of cards passed in is a set
     */
    public static boolean isSet(List<Card> cards) {
        if (cards == null || cards.size() != 3) {
            return false;
        }
        // for each attribute, verify that the values are either all the same or all different.
        for (int i = 0; i < Attribute.values().length; i++) {

            Attribute attribute = Attribute.values()[i];
            AttributeValue val0 = cards.get(0).valueOfAttribute(attribute);
            AttributeValue val1 = cards.get(1).valueOfAttribute(attribute);
            AttributeValue val2 = cards.get(2).valueOfAttribute(attribute);
            boolean allSame = (val0 == val1) && (val1 == val2);
            boolean allDifferent = (val0 != val1) && (val1 != val2) && (val0 != val2);

            if (!(allSame || allDifferent)) {
               return false;
            }
        }
        return true;
    }

    /**
     * Usually used to determine if a set game is over.
     *
     * @param cards set of cards to check for a set within
     * @return true if a set of cards contains a set
     */
     public static boolean hasSet(List<Card> cards) {
        return getSetsInternal(cards, true).size() / 3 > 0;
     }

    /**
     * @param cards cards to look at when determining the number of sets.
     * @return the number of sets in cards
     */
     public static int numSets(List<Card> cards) {
        return getSetsInternal(cards, false).size() / 3;
     }


    /**
     * Each triplet of consecutive cards in the returned list is a set.
     *
     * @param cards sets of cards. Each triplet of consecutive cards in the returned list is a set.
     * @return the number of sets in cards (unless terminateEarly is true in which case we return 0 or 1)
     */
     public static List<Card> getSets(List<Card> cards) {
           return getSetsInternal(cards, false);
     }

    /**
     * Usually used to determine if a set game is over.
     *
     * @param cards sets of cards. Each triplet of consecutive cards in the returned list is a set.
     * @param terminateEarly if true we return one after finding the first set.
     * @return the number of sets in cards (unless terminateEarly is true in which case we return 0 or 1)
     */
     private static List<Card> getSetsInternal(List<Card> cards, boolean terminateEarly) {
        List<Card> sets = new LinkedList<>();
        if (cards == null || cards.size() < 3) {
            return sets;  // empty list
        }
        // check all combinations of cards adding up the number of sets found.
        // worst case: 20 cards without a set - we need to check 20 * 19 * 18 = ~8000
        // for each attribute, verify that the values are either all the same or all different.
        for (int i = 0; i < cards.size(); i++) {
            List<Card> candidate = new LinkedList<>();
            candidate.add(cards.get(i));
            for (int j = i+1; j < cards.size(); j++) {
                candidate.add(cards.get(j));
                for (int k = j+1; k < cards.size(); k++) {
                    candidate.add(cards.get(k));
                    if (isSet(candidate))  {
                        sets.addAll(candidate);
                        if (terminateEarly)
                            return sets;
                    }
                    candidate.remove(2);
                }
                candidate.remove(1);
            }
        }
        return sets;
    }

    public boolean equals(Object card) {
        Card c = (Card) card;
        return (c.color() == color() && c.number() == number() && c.texture() == texture() && c.shape() == shape());
    }

    /**
     * @return  int unique per type of card that can exist.
     */
    public int hashCode() {
        return color().ordinal() * 1000 + number().ordinal() * 100 + texture().ordinal() * 10 + shape().ordinal();
    }
}