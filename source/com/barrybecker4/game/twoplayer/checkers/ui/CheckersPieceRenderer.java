/* Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers.ui;

import com.barrybecker4.game.common.board.GamePiece;
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
    private CheckersPieceRenderer() {}

    public static TwoPlayerPieceRenderer getRenderer() {

        if (renderer_ == null)
            renderer_ = new CheckersPieceRenderer();
        return renderer_;
    }


    public void renderForShow(Graphics2D g2, Point pos, GamePiece piece, int pieceSize) {

        if ( piece.getType() == CheckersPiece.REGULAR_PIECE ) {
            super.renderForShow( g2, pos, piece, pieceSize);
        }
        else {  //draw a KING
            g2.setColor( getPieceColor(piece) );
            g2.fillRect( pos.x, pos.y, pieceSize, pieceSize );

            if ( piece.getTransparency() == 0 ) {
                 // black outline
                 g2.setColor( Color.black );
                 g2.drawRect( pos.x , pos.y, pieceSize + 1, pieceSize + 1 );
            }
        }
    }
}
