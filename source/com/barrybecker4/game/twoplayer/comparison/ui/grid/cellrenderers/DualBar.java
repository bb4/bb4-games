// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.grid.cellrenderers;

import java.awt.*;

/**
 * A two part bar showing a result for when each side starts the game.
 *
 * @author Barry Becker
 */
public class DualBar extends SegmentedBar {

    private Color color;
    private double[] segments;
    private String[] labels;

    /**
     * Constructor
     */
    public DualBar(Color barColor) {
        color = barColor;
    }

    public void setBarSegments(double[] barSegments, String[] barLabels) {
        segments = barSegments;
        labels = barLabels;
    }

    @Override
    protected void drawBar(Graphics2D g2) {

        drawDualBar(segments, labels, color, g2);
    }


}
