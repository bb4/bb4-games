// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.ui.dialog;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.multiplayer.poker.hand.Hand;
import com.barrybecker4.game.multiplayer.poker.ui.render.HandRenderer;

import javax.swing.*;
import java.awt.*;

/**
 * Shows the player the contents of their hand so they can bet on it.
 * @author Barry Becker
 */
final class PokerHandViewer extends JPanel {

    Hand hand_;
    HandRenderer handRenderer = new HandRenderer();

    public PokerHandViewer(Hand hand) {
        hand_ = new Hand(hand.getCards());
        hand_.setFaceUp(true);
        this.setPreferredSize(new Dimension(400, 120));
    }

    @Override
    protected void paintComponent(Graphics g) {
         handRenderer.render((Graphics2D) g, new ByteLocation(0, 2), hand_, 22);
    }
}

