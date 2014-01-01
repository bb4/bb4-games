// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.model;

import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer;

import java.awt.*;

/**
 * Outcome of the two player game.
 * @author Barry Becker
 */
public enum Outcome {

    PLAYER1_WON {
        @Override
        public Color getColor() {
            return TwoPlayerPieceRenderer.DEFAULT_PLAYER1_COLOR;
        }
    },
    PLAYER2_WON {
        @Override
        public Color getColor() {
            return TwoPlayerPieceRenderer.DEFAULT_PLAYER2_COLOR;
        }
    },
    TIE {
        @Override
        public Color getColor() {
            return Color.GRAY.brighter();
        }
    };

    public abstract Color getColor();
}
