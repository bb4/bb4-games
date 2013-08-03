// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.ui.infopanel;

import com.barrybecker4.game.multiplayer.poker.ui.chips.PokerChip;
import com.barrybecker4.ui.legend.DiscreteColorLegend;

import javax.swing.*;
import java.awt.*;

/**
 * Show legend for the different chip amounts.
 *
 * @author Barry Becker
 */
class ChipLegendPanel extends JPanel {

    /**
     * Constructor
     */
    ChipLegendPanel() {
        initUI();
    }

    /**
     * This panel shows a discrete color legend for the poker chip values
     */
    void initUI() {
        PokerChip[] chipTypes = PokerChip.values();
        int n = chipTypes.length;
        Color[] colors = new Color[n];
        String[] values = new String[n];
        for (int i = n; i > 0; i--) {
            colors[n-i] = chipTypes[i-1].getColor();
            values[n-i] = chipTypes[i-1].getLabel();
        }
        JPanel legend = new DiscreteColorLegend(null, colors, values);
        legend.setPreferredSize(new Dimension(500, 100));
        add(legend);
    }

}