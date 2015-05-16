/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.ui;

import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;

import java.awt.*;

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
public abstract class MultiGameBoardRenderer extends GameBoardRenderer {

    private static final Color DEFAULT_GRID_COLOR = Color.GRAY;
    private static final Color DEFAULT_TABLE_COLOR = new Color(200, 180, 170);


    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    protected MultiGameBoardRenderer() {
        setGridColor(DEFAULT_GRID_COLOR);
    }

    @Override
    protected int getPreferredCellSize() {
        return 8;
    }

    Color getDefaultTableColor()  {
        return DEFAULT_TABLE_COLOR;
    }

    /**
     * whether or not to draw the pieces on cell centers or vertices (like go or gomoku, but not like checkers).
     */
    @Override
    protected boolean offsetGrid()  {
        return true;
    }

    /**
     * Draw the background and a depiction of a circular game table.
     * Used only by games with a table (like poker)
     */
    protected void drawTable(Graphics g, Board board) {
        g.setColor( backgroundColor_ );
        int width = board.getNumCols() * getCellSize();
        int height = board.getNumRows() * getCellSize();
        g.setColor(getDefaultTableColor());
        g.fillOval((int)(0.05*width), (int)(0.05*height), (int)(0.9*width), (int)(0.9*height));
    }
}

