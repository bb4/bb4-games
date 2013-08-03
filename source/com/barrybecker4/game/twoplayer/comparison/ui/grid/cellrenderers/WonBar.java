// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.grid.cellrenderers;

import com.barrybecker4.game.twoplayer.comparison.model.Outcome;

import java.awt.*;

/**
 * The bar that shows which side won for each player who started fist.
 * Gray if tie.
 *
 * @author Barry Becker
 */
@SuppressWarnings("HardCodedStringLiteral")
public class WonBar extends SegmentedBar {

    private static final int TEXT_INSET = 12;
    private static final int IMAGE_INSET = 20;

    private Outcome[] outcomes;
    private Image[] finalImages;

    public void setOutcomes(Outcome[] outcomes, Image[] finalImages) {
        this.outcomes = outcomes;
        this.finalImages = finalImages;
    }

    @Override
    protected void drawBar(Graphics2D g2) {

        Color side1Wincolor = BG_COLOR;
        Color side2Wincolor = BG_COLOR;

        if (outcomes != null)   {
            side1Wincolor = outcomes[0].getColor();
            side2Wincolor = outcomes[1].getColor();
        }
        int height = this.getHeight();
        int width = getWidth()/2;

        int imageSize = width - 2 * IMAGE_INSET;
        g2.setColor(side1Wincolor);
        g2.fillRect(0, 0, width, height);


        g2.setColor(side2Wincolor);
        g2.fillRect(width, 0, width, height);

        if (finalImages[0] != null)
            g2.drawImage(finalImages[0], IMAGE_INSET, IMAGE_INSET, imageSize, imageSize, null);
        if (finalImages[1] != null)
            g2.drawImage(finalImages[1], IMAGE_INSET + width, IMAGE_INSET, imageSize, imageSize, null);

        drawLabels(g2);
    }

    private void drawLabels(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        int width = getWidth()/2;
        String lab1 = (width > 70) ?  "player1 first" : "p1 1st";
        String lab2 = (width > 70) ?  "player2 first" : "p2 1st";
        g2.drawString(lab1, TEXT_INSET, TEXT_INSET);
        g2.drawString(lab2, width + TEXT_INSET, TEXT_INSET);
    }
}
