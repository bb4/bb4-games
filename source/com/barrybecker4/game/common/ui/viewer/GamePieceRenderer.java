/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.viewer;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.ui.gradient.RoundGradientPaint;
import com.barrybecker4.ui.util.GUIUtil;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * Abstract singleton class that takes a game piece and renders it for the GameBoardViewer.
 * We use a separate piece rendering class to avoid having ui in the piece class itself.
 * This allows us to more cleanly separate the client and server code.
 *
 * @see GameBoardViewer
 * @author Barry Becker
 */
public abstract class GamePieceRenderer {

    protected static final Font BASE_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 12 );

    protected static final Point2D.Double SPEC_HIGHLIGHT_RADIUS = new Point2D.Double(0, 7);

    /** use static to avoid creating a lot of new objects. */
    protected final Point position_ = new Point(0, 0);


    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    protected GamePieceRenderer()
    {}

    /**
     * @return the game piece render color.
     */
    protected abstract Color getPieceColor(GamePiece piece);


    /**
     * @return  the diameter of the piece on the board.
     */
    protected int getPieceSize(int cellSize, GamePiece piece) {
        int pieceSize = (int) (0.85f * cellSize);
        // make the piece a little smaller in debug mode
        if ( GameContext.getDebugMode() > 0 )
            pieceSize = (int) (0.75f * cellSize);
        return pieceSize;
    }


    protected Point getPosition(BoardPosition position, int cellSize, int pieceSize, int margin) {
        int offset = (cellSize - pieceSize) >> 1;
        position_.x = margin + cellSize*(position.getCol()-1) + offset;
        position_.y = margin + cellSize*(position.getRow()-1) + offset;
        return position_;
    }

    protected Color getTextColor(GamePiece piece) {
        return Color.black;
    }

   /**
    * this draws the actual piece at this location (if there is one).
    * Uses the RoundGradientFill from Knudsen to put a specular highlight on the stone.
    *
    * @param g2 graphics context
    * @param position the position of the piece to render
    */
   public void render(Graphics2D g2, BoardPosition position, int cellSize, int margin, Board b) {
       GamePiece piece = position.getPiece();
       // if there is no piece, then nothing to render
       if (piece == null)
           return;

       int pieceSize = getPieceSize(cellSize, piece);
       Point pos = getPosition(position, cellSize, pieceSize, margin);
       Ellipse2D circle = new Ellipse2D.Float( pos.x, pos.y, pieceSize + 1, pieceSize + 1 );

       //spec highlight offset
       int hlOffset = (int) (pieceSize / 2.3 + 0.5);
       RoundGradientPaint rgp = new RoundGradientPaint(
                pos.x + hlOffset, pos.y + hlOffset, Color.white, SPEC_HIGHLIGHT_RADIUS, getPieceColor(piece) );

       g2.setPaint( rgp );
       g2.fill( circle );

       // only draw the outline if we are not in a debug mode.
       // when in debug mode we want to emphasize other annotations instead of the piece
       if ( piece.getTransparency() == 0 && (GameContext.getDebugMode() == 0) ) {
           g2.setColor( Color.black );
           g2.drawOval( pos.x, pos.y, pieceSize, pieceSize );
       }

       if ( piece.getAnnotation() != null ) {
           int offset = (cellSize - pieceSize) >> 1;
           g2.setColor( getTextColor(piece) );
           g2.setFont( BASE_FONT );
           g2.drawString( piece.getAnnotation(), pos.x + 2*offset, pos.y + 3*offset);
       }
   }

}

