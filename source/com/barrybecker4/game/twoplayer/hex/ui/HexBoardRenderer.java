/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.hex.ui;

import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerBoardRenderer;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
class HexBoardRenderer extends TwoPlayerBoardRenderer {

    private static GameBoardRenderer renderer_;

    private static final int BOARD_MARGIN = 25;
    private static final Color TILE_BORDER_COLOR = new Color(20, 20, 50);
    private static final Stroke TILE_STROKE = new BasicStroke(1);

    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    private HexBoardRenderer() {
        pieceRenderer_ = TwoPlayerPieceRenderer.getRenderer();
    }

    /**
     * @return singleton instance
     */
    public static GameBoardRenderer getRenderer() {
        if (renderer_ == null)
            renderer_ = new HexBoardRenderer();
        return renderer_;
    }

    @Override
    protected int getPreferredCellSize() {
        return 16;
    }

    @Override
    protected int getMargin()  {
        return BOARD_MARGIN;
    }

    /**
     * whether to draw the pieces on cell centers or vertices (the way go requires).
     */
    @Override
    protected boolean offsetGrid() {
        return true;
    }

    /**
     * draw the hex game grid
     */
    @Override
    protected void drawGrid(Graphics2D g2, int startPos, int rightEdgePos, int bottomEdgePos, int start,
                            int nrows1, int ncols1, int gridOffset) {

        g2.setColor( getGridColor() );
        int xpos, ypos;
        int cellSizeD2 = cellSize/2;

        for (int i = start; i <= nrows1; i++ ) {
            for (int j = start; j <= ncols1; j++ ) {
                xpos = getMargin() + j * cellSize + gridOffset + (i-1) * cellSizeD2;
                ypos = getMargin() + i * cellSize + gridOffset;
                //g2.drawLine( startPos, ypos, rightEdgePos, ypos );
                Point point = new Point(xpos, ypos);
                drawHexagon(g2, point, cellSizeD2);
            }
        }
    }

    private void drawHexagon(Graphics2D g2, Point point, double radius) {

        int numPoints = 7;
        int[] xpoints = new int[numPoints];
        int[] ypoints = new int[numPoints];

        for (int i = 0; i <= 6; i++) {
            double angStart = HexUtil.rad(30 + 60 * i);
            xpoints[i] = (int)(point.getX() + radius * Math.cos(angStart));
            ypoints[i] = (int)(point.getY() + radius * Math.sin(angStart));
        }

        Polygon poly = new Polygon(xpoints, ypoints, numPoints);
        //g2.setColor(TILE_BG_COLOR);
        //g2.fillPolygon(poly);
        g2.setColor(TILE_BORDER_COLOR);
        g2.setStroke(TILE_STROKE);
        g2.drawPolygon(poly);
    }

    /**
     * draw the background.
     */
    @Override
    protected void drawBackground( Graphics g, Board b, int startPos, int rightEdgePos, int bottomEdgePos,
                                   int panelWidth, int panelHeight) {
        super.drawBackground( g, b,  startPos, rightEdgePos, bottomEdgePos, panelWidth, panelHeight);
        int t = (int)(cellSize /2.0f);
    }


    /**
     * first draw borders for the groups in the appropriate color, then draw the pieces for both players.
     */
    @Override
    protected void drawMarkers(Board board, PlayerList players, Graphics2D g2 ) {

        super.drawMarkers(board, players, g2);
    }
}

