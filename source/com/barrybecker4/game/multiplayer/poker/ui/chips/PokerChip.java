/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.chips;

import java.awt.*;

/**
 * @author Barry Becker
 *
 */
public enum PokerChip {

    ONE("One Dollar Chip", 1, new Color(255, 200, 0)),
    FIVE("Five Dollar Chip", 5, new Color(200, 0, 0)),
    TEN("Ten Dollar Chip", 10, new Color(0, 80, 255)),
    TWENTY_FIVE("25 Dollar Chip", 25, new Color(0, 200, 10)),
    FIVE_HUNDRED("500 Dollar Chip", 500, new Color(220, 0, 200));


    private final String label_;

    /** occurs one in this many hands */
    private final int value_;

    private final Color color_;


    PokerChip(String label, int value, Color color) {
        label_ = label;
        value_ = value;
        color_ = color;
    }

    public String getLabel() {
        return label_;
    }

    int getValue() {
        return value_;
    }

    public Color getColor() {
        return color_;
    }

}
