/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui.render;

import com.barrybecker4.game.common.board.IBoard;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.multiplayer.common.ui.MultiGameBoardRenderer;
import com.barrybecker4.game.multiplayer.set.Card;
import com.barrybecker4.game.multiplayer.set.SetBoard;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
public class SetGameRenderer extends MultiGameBoardRenderer {

    private  static GameBoardRenderer renderer_;

    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead.
     * Note: there is no piece renderer for set.
     */
    private SetGameRenderer() {}

    public static GameBoardRenderer getRenderer() {
        if (renderer_ == null)
            renderer_ = new SetGameRenderer();
        return renderer_;
    }

    private int getCanvasWidth(int panelWidth) {
        return panelWidth - 2 * CardRenderer.LEFT_MARGIN;
    }

    /**
     *  The number of card columns is computed based on the panel width/height ratio.
     * @return the number of columns of cards to show
     */
    private int getNumColumns(int panelWidth, int panelHeight, int numCards) {
        float rat = (float) getCanvasWidth(panelWidth) / (panelHeight - 2 * CardRenderer.TOP_MARGIN);

        int numColumns = 20;
        if (rat < 0.05) {
            numColumns = 1;
        } else if (rat < 0.15) {
            numColumns = 2;
        } else if (rat < 0.3) {
            numColumns = 3;
        } else if (rat < 0.6) {
            numColumns = 4;
        } else if (rat < 0.9) {
            numColumns = 5;
        } else if (rat < 1.2) {
            numColumns = 6;
        } else if (rat < 2.0) {
            numColumns = 7;
        } else if (rat < 3.4) {
            numColumns = 10;
        }
        // if there are a lot of cards showing, double the number of columns
        numColumns *= (1 + numCards / 41);
        return numColumns;
    }

    private Dimension calcCardDimension(int numCols, int panelWidth) {
        int cardWidth = getCanvasWidth(panelWidth) / numCols;
        return new Dimension(cardWidth, (int) (cardWidth * CardRenderer.CARD_HEIGHT_RAT));
    }

    /**
     * @return  the card that the mouse is currently over (at x, y coordinates)
     */
    public Card findCardOver(IBoard board, int x, int y, int panelWidth, int panelHeight) {
        SetBoard b = (SetBoard)board;

        int numCards = b.getNumCardsShowing();
        int numCols = getNumColumns(panelWidth, panelHeight, numCards);

        Dimension cardDim = calcCardDimension(numCols, panelWidth);
        int cardWidth = (int) cardDim.getWidth();
        int cardHeight = (int) cardDim.getHeight();

        int selectedIndex = -1;
        for (int i = 0; i<numCards; i++ ) {
            int row = i / numCols;
            int col = i % numCols;
            int colPos = col * cardWidth + CardRenderer.LEFT_MARGIN;
            int rowPos = row * cardHeight + CardRenderer.TOP_MARGIN;
            if (   x > colPos && x <= colPos + cardDim.getWidth()
                && y > rowPos && y <= rowPos + cardDim.getHeight()) {
                selectedIndex = i;
                break;
            }
        }
        if (selectedIndex == -1) {
            return null;
        }
        return b.getDeck().get(selectedIndex);
    }

    /**
     * This renders the current state of the Board to the screen.
     * Erase what's there and redraw.
     */
    @Override
    public void render( Graphics g, Player currentPlayer, PlayerList players,
                        IBoard board, int panelWidth, int panelHeight ) {

        SetBoard b = (SetBoard) board;
        int numCards = b.getNumCardsShowing();

        g.clearRect( 0, 0, panelWidth, panelHeight );
        g.setColor( getBackground() );
        g.fillRect( 0, 0, panelWidth, panelHeight );

        int numCols = getNumColumns(panelWidth, panelHeight, numCards);

        Dimension cardDim = calcCardDimension(numCols, panelWidth);
        int cardWidth = (int) cardDim.getWidth();
        int cardHeight = (int) cardDim.getHeight();

        for (int i = 0; i < numCards; i++ ) {
            int row = i / numCols;
            int col = i % numCols;
            int colPos = col * cardWidth + CardRenderer.LEFT_MARGIN;
            int rowPos = row * cardHeight + CardRenderer.TOP_MARGIN;
            CardRenderer.render((Graphics2D) g, b.getDeck().get(i),
                                new Point2D.Float(colPos, rowPos), cardWidth, cardHeight, false);
        }
    }
}

