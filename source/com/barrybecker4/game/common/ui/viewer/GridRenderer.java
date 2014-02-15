/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.viewer;

import com.barrybecker4.ui.themes.BarryTheme;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

/**
 * Renders a grid
 *
 * @author Barry Becker
 */
public abstract class GridRenderer {

    private Dimension dimensions;

    /** the size of a game board cell where the pieces go */
    private int cellSize;

    private int margin;

    private Color gridColor = BarryTheme.UI_COLOR_SECONDARY1;

    /**
     * Constructor
     */
    protected GridRenderer(int numRows, int numCols, int cellSize, int margin, Color gridColor)  {
        this.cellSize = cellSize;
        this.margin = margin;
        this.gridColor = gridColor;
    }


    /**
     * Draw the gridlines over the background.
     * Draw the hatches which delineate the cells on the board.
     */
    protected void drawGrid(Graphics2D g2, int startPos, int rightEdgePos, int bottomEdgePos, int start,
                            int nrows1, int ncols1, int gridOffset) {

        g2.setColor( gridColor );
        int xpos, ypos;
        int i;

        for ( i = start; i <= nrows1; i++ )  //   -----
        {
            ypos = margin + i * cellSize + gridOffset;
            g2.drawLine( startPos, ypos, rightEdgePos, ypos );
        }
        for ( i = start; i <= ncols1; i++ )  //   ||||
        {
            xpos = margin + i * cellSize + gridOffset;
            g2.drawLine( xpos, startPos, xpos, bottomEdgePos );
        }
    }
}

