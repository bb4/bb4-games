// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.grid.cellrenderers;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Base segmented bar. Shows info about the two sides that played each other
 * for one of the trials. One segment for each of the players that started first.
 *
 * @author Barry Becker
 */
public abstract class SegmentedBar extends JPanel {

    protected static final Color BG_COLOR = new Color(220, 220, 221);
    protected static final Color BORDER_COLOR = new Color(20, 0, 0, 100);

    private static final int TEXT_INSET_X = 5;
    private static final int TEXT_INSET_Y = 10;

    /** constructor */
    public SegmentedBar() {
    }

    /** draw the two segment bar */
    @Override
    public void paint(Graphics g) {

        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        drawBackGround(g2);
        drawBar(g2);
    }

    private void drawBackGround(Graphics2D g2) {
        g2.setColor(BG_COLOR);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    protected abstract void drawBar(Graphics2D g2);

    /** draws the segmented bar pair with label horizontally. */
    protected void drawDualBar(double[] normValues,
                               String[] labels,
                               Color barColor,
                               Graphics2D g2) {
        int n1Width = 0;
        int n2Width = 0;
        if (normValues != null) {
            n1Width = (int)(normValues[0] * getWidth());
            n2Width = (int)(normValues[1] * getWidth());
        }
        int height = this.getHeight();

        g2.setPaint(createGradient(0, n1Width, barColor));
        g2.fillRect(0, 0, n1Width, height);

        g2.setPaint(createGradient(n1Width, n2Width, barColor));
        g2.fillRect(n1Width, 0, n2Width, height);

        // add border
        g2.setColor(BORDER_COLOR);
        g2.drawRect(0, 0, n1Width, height);
        g2.drawRect(n1Width, 0, n2Width, height);

        if (height > 18) {
            g2.drawString(labels[0], TEXT_INSET_X, TEXT_INSET_Y);
            g2.drawString(labels[1], n1Width + TEXT_INSET_X, height - TEXT_INSET_Y/2 + 1);
        }
    }

    protected GradientPaint createGradient(double offset, double width, Color color) {

        Point2D.Double origin = new Point2D.Double( offset, 0.0 );
        Point2D.Double end = new Point2D.Double( offset + width, 0.0 );

        Color startColor = color.brighter();

        return new GradientPaint(origin, startColor, end, color);
    }

}
