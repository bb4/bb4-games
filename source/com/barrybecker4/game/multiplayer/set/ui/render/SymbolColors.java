// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.set.ui.render;

import com.barrybecker4.game.multiplayer.set.Card;

import java.awt.*;

/**
 * Colors for the different symbol patterns on the set cards.
 *
 * @author Barry Becker
 */
public final class SymbolColors {

    enum ColorType {
        SOLID, BORDER, HATCHED, HIGHLIGHT
    }

    private static final Color[][] SYMBOL_COLORS = {

        {   // FIRST
                new Color(210, 90, 80),   //  solid
                new Color(170, 5, 2),    //  border
                new Color(230, 42, 22),  //  hatched
                new Color(255, 22, 12)   //  highlight
        },
        {     // SECOND
                new Color(100, 210, 100),
                new Color(0, 140, 0),
                new Color(0, 200, 10),
                new Color(10, 252, 2)
        },
        {    // THIRD
                new Color(85, 95, 220),
                new Color(0, 0, 190),
                new Color(25, 25, 245),
                new Color(70, 60, 255)
        }
    };

    Color getColorForValue(Card.AttributeValue val, ColorType style) {
        return SYMBOL_COLORS[val.ordinal()][style.ordinal()];
    }

    Color getCardColor(Card card) {
        return getColorForValue(card.color(), ColorType.SOLID);
    }

    Color getBorderCardColor(Card card) {
        return getColorForValue(card.color(), card.isHighlighted() ? ColorType.HIGHLIGHT: ColorType.BORDER);
    }
}

