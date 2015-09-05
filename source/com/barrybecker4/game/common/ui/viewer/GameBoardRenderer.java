/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.viewer;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.IBoard;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.ui.themes.BarryTheme;
import com.barrybecker4.ui.util.GUIUtil;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
public abstract class GameBoardRenderer {

    /** the size of a game board cell where the pieces go */
    protected int cellSize;

    /** to move pieces you drag them (if the move is valid) */
    private BoardPosition draggedPiece_ = null;

    /** this copy of the dragged piece is only for show   */
    private BoardPosition draggedShowPiece_ = null;

    /**
     * singleton class for rendering the game pieces
     * we use a separate piece rendering class to avoid having ui in the piece class itself.
     * This allows us to more cleanly separate the client pieces from the server.
     * this must be initialized in the derived classes constructor.
     */
    protected GamePieceRenderer pieceRenderer_ = null;

    // defaults for the grid and board colors.
    // The may be changed using the options panel in the ui.
    private static final Color BACKGROUND_COLOR = BarryTheme.UI_COLOR_SECONDARY3;
    private static final Color GRID_COLOR = BarryTheme.UI_COLOR_SECONDARY1;
    private static final int BOARD_MARGIN = 5;
    private static final int PREFERRED_CELL_SIZE = 16;

    protected Color backgroundColor_ = BACKGROUND_COLOR;
    private Color gridColor_ = GRID_COLOR;

    private static final Font VIEWER_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 8 );
    protected static final Color LAST_MOVE_INDICATOR_COLOR = new Color( 255, 120, 0 );
    protected static final Stroke LAST_MOVE_INDICATOR_STROKE = new BasicStroke(3);

    /** don't allow the cells of the game board to get smaller than this */
    public static final int MINIMUM_CELL_SIZE = 8;
    private static final short DRAG_TRANSPARENCY = 170;

    /**
     * private constructor because this class is a singleton.
     * Use getBoardRenderer instead.
     */
    protected GameBoardRenderer()  {}


    public GamePieceRenderer getPieceRenderer() {
        return pieceRenderer_;
    }

    public Dimension getSize(int nrows, int ncols) {
        return new Dimension( 2*getMargin() + ncols * getCellSize(),
                                2*getMargin() + nrows * getCellSize() );
    }

    public Dimension getPreferredSize(int nrows, int ncols) {

        return new Dimension(
                2*getMargin() + ncols * getPreferredCellSize(),
                2*getMargin() + nrows * getPreferredCellSize());
    }

    /**
     * @param c the new color of the board.
     */
    public void setBackground( Color c ) {
        backgroundColor_ = c;
    }

    /**
     * @return the board color
     */
    public Color getBackground() {
        return backgroundColor_;
    }

    /**
     * @param position The position containing the piec that is to be dragged
     */
    public void setDraggedPiece(BoardPosition position)  {
        draggedPiece_ = position;
        if (position != null)  {
            draggedShowPiece_ = position.copy();
            draggedShowPiece_.getPiece().setTransparency( (short) 160 );
        }
        else {
            draggedShowPiece_ = null;
        }
    }

    public BoardPosition getDraggedPiece() {
        return draggedPiece_;
    }

    /**
     * @param position position of fake overlay piece for show while dragging
     */
    public void setDraggedShowPiece(BoardPosition position) {
        draggedShowPiece_ = position;
    }

    public BoardPosition getDraggedShowPiece() {
        return draggedShowPiece_;
    }

    /**
     * Constructs a new Location given a MouseEvent
     *
     * @param e event containing the coordinates.
     * @return new location based on mouse position.
     */
    public Location createLocation( MouseEvent e) {
        int size = Math.max(1, getCellSize());
        int row = (e.getY()- getMargin())/ size + 1;
        int col = (e.getX()- getMargin())/ size + 1;
        return new ByteLocation(row, col);
    }

    /**
     * @return  default cell size (override for specific games).
     */
    protected int getPreferredCellSize()  {
        return PREFERRED_CELL_SIZE;
    }

    /**
     * @return the space to the left and at the top of the board.
     */
    protected int getMargin() {
        return BOARD_MARGIN;
    }
    /**
     * @return whether or not to draw the pieces on cell centers or vertices (like go or gomoku, but not like checkers).
     */
    protected boolean offsetGrid() {
        return false;
    }

    /**
     * @param c  the new color of the board's grid.
     */
    public void setGridColor( Color c ) {
        gridColor_ = c;
        GameContext.log(3, "setting gridColor to " + c);
    }

    /**
     * @return c  the new color of the board's grid.
     */
    public Color getGridColor() {
        return gridColor_;
    }

    /**
     * @return  the size of a board position cell (must be square).
     */
    public final int getCellSize() {
        return cellSize;
    }

    /**
     * Clear the background so old pieces are erased.
     */
    protected void drawBackground( Graphics g, Board b, int startPos, int rightEdgePos, int bottomEdgePos,
                                   int panelWidth, int panelHeight) {
        g.setColor( backgroundColor_ );
        g.fillRect( 0, 0, panelWidth, panelHeight);
    }

    /**
     * Compute the cell size based on the the dimensions of the viewer
     * The viewer window may be resized causing the cell size to change dynamically
     * @return the current cell size given the board and panel dimensions.
     */
    protected int calcCellSize( Board board, int panelWidth, int panelHeight )  {
        int size;
        int nrows = board.getNumRows();
        int ncols = board.getNumCols();

        double aspect = getBoardAspectRatio();
        float panelAspect = (float) panelWidth / (float) panelHeight;
        float boardAspect = (float) aspect *  ncols /  nrows;

        //GameContext.log(0, "compare "+boardAspect+"("+ncols+","+nrows+") to "
        //    + panelAspect + "("+panelWidth+","+panelHeight+") to ");
        if ( boardAspect < panelAspect )
            size = ((panelHeight - 2 * getMargin() + 1) / nrows);
        else
            size = (int)((panelWidth - 2 * getMargin() + 1) / (aspect * ncols));

        return Math.max( size, MINIMUM_CELL_SIZE );
    }

    /**
     * Usually this is one, but some boards (like hex) have unusual shapes.
     * @return the ideal width / height ratio
     */
    protected double getBoardAspectRatio() {
        return 1.0;
    }

    /**
     * Draw the gridlines over the background.
     */
    protected void drawGrid(Graphics2D g2, int startPos, int rightEdgePos, int bottomEdgePos, int start,
                            int nrows1, int ncols1, int gridOffset) {

        // draw the hatches which delineate the cells
        g2.setColor( getGridColor() );
        int xpos, ypos;
        int i;

        for ( i = start; i <= nrows1; i++ )  //   -----
        {
            ypos = getMargin() + i * cellSize + gridOffset;
            g2.drawLine( startPos, ypos, rightEdgePos, ypos );
        }
        for ( i = start; i <= ncols1; i++ )  //   ||||
        {
            xpos = getMargin() + i * cellSize + gridOffset;
            g2.drawLine( xpos, startPos, xpos, bottomEdgePos );
        }
    }

    /**
     * Draw some indication of where the last move was made.
     * The default is to show nothing.
     */
    protected void drawLastMoveMarker(Graphics2D g2, Player currentPlayer, Board board) {}

    /**
     * Draw the pieces and possibly other game markers for both players.
     */
    protected void drawMarkers(Board board, PlayerList players, Graphics2D g2 ) {
        int nrows = board.getNumRows();
        int ncols = board.getNumCols();
        for ( int i = 1; i <= nrows; i++ ) {
            for ( int j = 1; j <= ncols; j++ ) {
                pieceRenderer_.render(g2, board.getPosition( i, j ), cellSize, getMargin(), board);
            }
        }
    }

    /**
     * This renders the current state of the Board to the screen.
     */
    public void render(Graphics g, Player currentPlayer, PlayerList players,
                       IBoard board, int panelWidth, int panelHeight ) {

        Board b = (Board)board;
        cellSize = calcCellSize( b, panelWidth, panelHeight );

        Graphics2D g2 = (Graphics2D)g;

        int gridOffset = 0;
        int start = 0;
        int nrows = b.getNumRows();
        int ncols = b.getNumCols();
        int nrows1 = nrows;
        int ncols1 = ncols;
        // if the grid is offset, it means the pieces will be shown at the vertices.
        if ( offsetGrid() ) {
            gridOffset = cellSize >> 1;
            nrows1 = nrows - 1;
            ncols1 = ncols - 1;
        }

        int startPos = getMargin() + gridOffset;
        int rightEdgePos = getMargin() + cellSize * ncols1 + gridOffset;
        int bottomEdgePos = getMargin() + cellSize * nrows1 + gridOffset;

        drawBackground( g2, b, startPos, rightEdgePos, bottomEdgePos, panelWidth, panelHeight );

        drawGrid(g2, startPos, rightEdgePos, bottomEdgePos, start, nrows1, ncols1, gridOffset);

        g2.setFont( VIEWER_FONT );

        // now draw both player markers
        drawMarkers(b, players, g2);

        // if there is a piece being dragged, draw it
        if ( draggedShowPiece_ != null ) {
            draggedShowPiece_.getPiece().setTransparency( DRAG_TRANSPARENCY );
            pieceRenderer_.render(g2, draggedShowPiece_, cellSize, getMargin(), b);
        }

        drawLastMoveMarker(g2, currentPlayer, b);
    }
}

