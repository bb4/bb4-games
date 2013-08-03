/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers.ui;

import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.twoplayer.checkers.CheckersPiece;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer;

import java.awt.*;

/**
 *  a singleton class that takes a checkers piece and renders it for the CheckersBoardViewer.
 * @see CheckersBoardViewer
 * @author Barry Becker
 */
public class CheckersPieceRenderer extends TwoPlayerPieceRenderer {

    private static TwoPlayerPieceRenderer renderer_ = null;

    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    private CheckersPieceRenderer()
    {}

    public static TwoPlayerPieceRenderer getRenderer() {

        if (renderer_ == null)
            renderer_ = new CheckersPieceRenderer();
        return renderer_;
    }

    /**
     * this draws the actual piece.
     */
    @Override
    public void render( Graphics2D g2, BoardPosition position, int cellSize, int margin, Board b) {

        CheckersPiece piece = (CheckersPiece)position.getPiece();
        if (piece == null)
            return; // nothing to render

        int pieceSize = getPieceSize(cellSize, piece);

        if ( piece.getType() == CheckersPiece.REGULAR_PIECE ) {
            super.render( g2, position, cellSize, margin, b);
        }
        else {  //draw a KING
            g2.setColor( getPieceColor(piece) );
            Point pos = getPosition(position, cellSize, pieceSize, margin);

            g2.fillRect( pos.x, pos.y, pieceSize, pieceSize );

            if ( piece.getTransparency() == 0 ) {
                 // black outline
                 g2.setColor( Color.black );
                 g2.drawRect( pos.x , pos.y, pieceSize + 1, pieceSize + 1 );
            }
        }
    }
}
