/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.hex.ui;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.IntLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerBoardRenderer;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
class HexBoardRenderer extends TwoPlayerBoardRenderer {

    private static GameBoardRenderer renderer_;

    private static final int BOARD_MARGIN = 30;
    private static final Color TILE_BORDER_COLOR = new Color(30, 30, 90,50);
    private static final Stroke TILE_STROKE = new BasicStroke(2);

    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    private HexBoardRenderer() {
        pieceRenderer_ = HexPieceRenderer.getRenderer();
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

    @Override
    protected double getBoardAspectRatio() {
        return 1.4;
    }

    /**
     * whether to draw the pieces on cell centers or vertices (the way go requires).
     */
    @Override
    protected boolean offsetGrid() {
        return true;
    }


    /**
     * Constructs a new Location given a MouseEvent
     *
     * @param e event containing the coordinates.
     * @return new location based on mouse position.
     */
    public Location createLocation( MouseEvent e) {
        int size = Math.max(1, getCellSize());
        int row = (int)(((e.getY() - getMargin()) / (HexUtil.ROOT3D2 * size) + 1));
        int col = (e.getX() - getMargin() - ((row - 2) * cellSize)/2) / size + 1;
        return new ByteLocation(row, col);
    }


    @Override
    protected IntLocation getPosition(Location coords) {
        double cellSizeD2 = cellSize / 2.0;
        int y = getMargin() + (int)(coords.getRow() * (cellSize - 0.5) * HexUtil.ROOT3D2); // + 2;
        int x = (int)(getMargin() + (coords.getCol() - 1) * cellSize + (coords.getRow() - 1) * cellSizeD2);
        return new IntLocation(y, x);
    }

    /**
     * draw the hex game grid
     */
    @Override
    protected void drawGrid(Graphics2D g2, int startPos, int rightEdgePos, int bottomEdgePos, int start,
                            int nrows1, int ncols1, int gridOffset) {

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int xpos, ypos;
        int cellSizeD2 = cellSize/2;

        for (int i = start; i <= nrows1; i++ ) {
            for (int j = start; j <= ncols1; j++ ) {
                g2.setColor( getGridColor() );
                xpos = getMargin() + j * cellSize + gridOffset + (i - 1) * cellSizeD2;
                ypos = getMargin() + (int)(i * cellSize * HexUtil.ROOT3D2) + gridOffset;

                Point point = new Point(xpos, ypos);
                double rad = cellSizeD2 / HexUtil.ROOT3D2;
                HexagonRenderer.drawHexagon(g2, point, rad, TILE_BORDER_COLOR, TILE_STROKE);

                if (i == start ) {
                    drawEdge(g2, getPieceRenderer().getPlayer1Color(), point, rad, 210, 270, 330);
                }
                if (i == nrows1) {
                    drawEdge(g2, getPieceRenderer().getPlayer1Color(), point, rad, 30, 90, 150);
                }
                if (j == start) {
                    drawEdge(g2, getPieceRenderer().getPlayer2Color(), point, rad, 90, 150, 210);
                }
                if (j == ncols1) {
                    drawEdge(g2, getPieceRenderer().getPlayer2Color(), point, rad, 270, 330, 390);
                }
            }
        }
    }

    private void drawEdge(Graphics2D g2, Color color, Point point, double rad, int a1, int a2, int a3) {
        g2.setColor(color);
        double ang1 = HexUtil.rad(a1);
        int x1 = (int)(point.getX() + rad * Math.cos(ang1));
        int y1 = (int)(point.getY() + rad * Math.sin(ang1));
        double ang2 = HexUtil.rad(a2);
        int x2 = (int)(point.getX() + rad * Math.cos(ang2));
        int y2 = (int)(point.getY() + rad * Math.sin(ang2));
        double ang3 = HexUtil.rad(a3);
        int x3 = (int)(point.getX() + rad * Math.cos(ang3));
        int y3 = (int)(point.getY() + rad * Math.sin(ang3));
        g2.drawLine(x1, y1, x2, y2);
        g2.drawLine(x2, y2, x3, y3);
    }

    public TwoPlayerPieceRenderer getPieceRenderer() {
        return (TwoPlayerPieceRenderer) pieceRenderer_;
    }

    /**
     * draw the background.
     */
    @Override
    protected void drawBackground( Graphics g, Board b, int startPos, int rightEdgePos, int bottomEdgePos,
                                   int panelWidth, int panelHeight) {
        super.drawBackground( g, b,  startPos, rightEdgePos, bottomEdgePos, panelWidth, panelHeight);
    }


    /**
     * first draw borders for the groups in the appropriate color, then draw the pieces for both players.
     */
    @Override
    protected void drawMarkers(Board board, PlayerList players, Graphics2D g2 ) {

        super.drawMarkers(board, players, g2);
    }

    @Override
    protected void drawLastMoveMarker(Graphics2D g2, Player player, Board board) {

        TwoPlayerMove last = (TwoPlayerMove) board.getMoveList().getLastMove();
        // this draws a small indicator on the last move to show where it was played
        if ( last != null ) {
            int cellSize = getCellSize();
            IntLocation pos = getPosition(last.getToLocation());
            System.out.println("last to location = " + last.getToLocation() + " pos = " + pos);
            double rad = cellSize / 2.0;
            HexagonRenderer.drawHexagon(g2,
                    new Point(pos.getX(), (int)(pos.getY() - 0.65 * rad)), rad,
                    LAST_MOVE_INDICATOR_COLOR, LAST_MOVE_INDICATOR_STROKE);
        }
    }
}

