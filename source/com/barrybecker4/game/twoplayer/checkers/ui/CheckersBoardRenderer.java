/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers.ui;

import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerBoardRenderer;

import java.awt.*;

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
public class CheckersBoardRenderer extends TwoPlayerBoardRenderer {

    private  static GameBoardRenderer renderer_;

    // colors of the squares on the chess board.
    // make them transparent so the background color shows through.
    private static final Color BLACK_SQUARE_COLOR = new Color(2, 2, 2, 80);
    private static final Color RED_SQUARE_COLOR = new Color(250, 0, 0, 80);

    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    protected CheckersBoardRenderer() {
        pieceRenderer_ = CheckersPieceRenderer.getRenderer();
    }

    public static GameBoardRenderer getRenderer() {
        if (renderer_ == null)
            renderer_ = new CheckersBoardRenderer();
        return renderer_;
    }

    @Override
    protected int getPreferredCellSize() {
        return 34;
    }

    @Override
    protected void drawBackground(Graphics g, Board b, int startPos, int rightEdgePos, int bottomEdgePos,
                                  int panelWidth, int panelHeight)  {
        super.drawBackground(g, b, startPos, rightEdgePos, bottomEdgePos, panelWidth, panelHeight);

        int nrows = b.getNumRows();
        int ncols = b.getNumCols();

        for (int i=0; i<nrows; i++) {
            for (int j=0; j<ncols; j++)  {
                g.setColor(((i+j)%2 == 0)? BLACK_SQUARE_COLOR : RED_SQUARE_COLOR);
                int ioff = getMargin() + cellSize * i;
                int joff = getMargin() + cellSize * j;
                g.fillRect( ioff, joff, cellSize, cellSize);
            }
        }
    }

    /**
     * draw a grid of some sort if there is one.
     * none by default for poker.
     */
    @Override
    protected void drawGrid(Graphics2D g2, int startPos, int rightEdgePos, int bottomEdgePos, int start,
                            int nrows1, int ncols1, int gridOffset) {}

}

