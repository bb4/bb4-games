/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui.render;

import com.barrybecker4.game.multiplayer.set.Card;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import static com.barrybecker4.game.multiplayer.set.ui.render.SymbolColors.ColorType.HATCHED;

/**
 * Takes a card and renders it to the Viewer.
 * We use a separate card rendering class to avoid having ui in the card class itself.
 * This allows us to more cleanly separate the view from the model.
 *
 * @author Barry Becker
 */
public final class CardRenderer {

    public static final int LEFT_MARGIN = 5;
    public static final int TOP_MARGIN = 5;

    public static final float CARD_HEIGHT_RAT = 1.5f;

    private static final Color BACKGROUND_COLOR = new Color(250, 250, 255);

    private static final Color BORDER_COLOR = new Color(45, 45, 55);
    private static final Color HIGHLIGHTED_BORDER_COLOR = new Color(205, 205, 0);
    private static final Color SELECTED_BORDER_COLOR = new Color(255, 255, 0);

    private static final float MARGIN_RAT = 0.02f;
    private static final Stroke SHAPE_BORDER_STROKE = new BasicStroke(4.0f);

    private static final float SHAPE_SIZE_FRAC = 0.82f;
    private static final float THIRD_SHAPE_FRAC = 0.9f; // slightly different for the diamond

    private static final float SHAPE_WIDTH_FRAC = 0.7f;
    private static final float SHAPE_HEIGHT_FRAC = 0.25f;

    private enum ColorType {
        SOLID, BORDER, HATCHED, HIGHLIGHT
    }

    private static SymbolColors symbolColors = new SymbolColors();

    /** rounded edge   */
    private static final float ARC_RATIO = 0.12f;

    /**
     * private constructor because this class is a singleton.
     * Use getPieceRenderer instead
     */
    private CardRenderer() {}

    private static Paint getCardTexture(Card card) {
        switch (card.texture()) {
            case FIRST : return BACKGROUND_COLOR;
            case SECOND : return symbolColors.getCardColor(card);
            case THIRD :
                return new GradientPaint(75, 75, BACKGROUND_COLOR, 80, 75,
                             symbolColors.getColorForValue(card.color(), HATCHED), true);
        }
        return  null;
    }


    private static Shape getShape(Card card, int width, int height) {
        Shape shape = null;
        int topMargin = (int) ((1.0 - SHAPE_SIZE_FRAC) * height);
        int leftMargin = (int) ((1.0 - SHAPE_SIZE_FRAC) * width);
        float w = width * SHAPE_SIZE_FRAC;
        float h = height * SHAPE_SIZE_FRAC;
        switch (card.shape()) {
            case FIRST : shape = new Ellipse2D.Float( leftMargin, topMargin, w, h );  break;
            case SECOND : shape = new Rectangle2D.Float( leftMargin, topMargin, w, h ); break;
            case THIRD :
                float hh = (THIRD_SHAPE_FRAC * height);
                float ww = (THIRD_SHAPE_FRAC * width);
                float leftStart = (int) ((1.0 - THIRD_SHAPE_FRAC) * width);
                GeneralPath path = new GeneralPath();
                int hd2 = (int) hh >> 1;
                int wd2 = (int) ww >> 1;
                path.moveTo(leftStart, hd2);
                path.lineTo(leftStart + wd2, 0);
                path.lineTo(leftStart + ww, hd2);
                path.lineTo(leftStart + wd2, hh  );
                path.lineTo(leftStart, hd2);
                shape = path;
                break;
        }
        return shape;
    }

    private static int getNumber(Card card) {
        switch (card.number()) {
            case FIRST : return 1;
            case SECOND : return 2;
            case THIRD : return 3;
        }
        return 0;
    }

   /**
     * this draws the actual piece at this location (if there is one).
     * Uses the RoundGradientFill from Knudsen to put a specular highlight on the stone.
     *
     * @param g2 graphics context
     * @param position the position of the piece to render
     */
    public static void render(Graphics2D g2, Card card, Point2D position, int width, int height, boolean highlight) {
       int x = (int)position.getX();
       int y = (int)position.getY();

       int cardArc = (int) (ARC_RATIO * width);
       int margin = (int) (MARGIN_RAT * width);

       g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
       g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
       g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
       g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

       if (card.isSelected()) {
           g2.setColor(SELECTED_BORDER_COLOR);
       }
       else if (card.isHighlighted())  {
           g2.setColor(HIGHLIGHTED_BORDER_COLOR);
       } else {
           g2.setColor(BORDER_COLOR);
       }
       g2.fillRoundRect(x + margin, y + margin, width - margin, height - margin, cardArc, cardArc);
       g2.setColor(BACKGROUND_COLOR);
       g2.fillRoundRect(x + 2*margin, y + 2*margin, width - 3 * margin, height - 3 * margin, cardArc, cardArc);

       g2.setColor(symbolColors.getCardColor(card));

       Shape shape = getShape(card, (int) (width * SHAPE_WIDTH_FRAC), (int) (height * SHAPE_HEIGHT_FRAC));
       int num = getNumber(card);
       int startXoffset = (int)((0.97 - SHAPE_WIDTH_FRAC)/ 2.0 * width);
       int startYoffset = (int)((3.0 - num) * height * 0.1) +  (int) ((0.99-3.0*SHAPE_HEIGHT_FRAC)/2.0 * height);
       int offset = (int)((height - 2 * margin) / 3.8);

       g2.setStroke(SHAPE_BORDER_STROKE);

       g2.translate(x + startXoffset, y);
       for (int i = 0; i < num; i++) {

           g2.translate(0, startYoffset + i*offset);
           g2.setPaint(getCardTexture(card));
           g2.fill(shape);
           g2.setPaint(symbolColors.getBorderCardColor(card));
           g2.draw(shape);
           g2.translate(0, -startYoffset - i*offset);
       }
       g2.translate(-x - startXoffset, -y);
    }
}

