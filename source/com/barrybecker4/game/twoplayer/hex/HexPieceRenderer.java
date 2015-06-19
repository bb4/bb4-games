/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.hex;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.common.ui.viewer.GamePieceRenderer;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer;
import com.barrybecker4.game.twoplayer.hex.ui.HexUtil;
import com.barrybecker4.game.twoplayer.hex.ui.HexagonRenderer;

import java.awt.*;

/**
 * a singleton class that takes a game piece and renders it for the TwoPlayerBoardViewer.
 * We use a separate piece rendering class to avoid having ui in the piece class itself.
 * This allows us to more cleanly separate the client pieces from the server. *
 * @author Barry Becker
 */
public class HexPieceRenderer extends TwoPlayerPieceRenderer {

    /** there must be one of these for each derived class too. */
    private static HexPieceRenderer renderer_ = null;

    private static final Stroke OUTLINE_STROKE = new BasicStroke(1.0f);

    /**
     * private constructor because this class is a singleton.
     * Use getPieceRenderer instead
     */
    protected HexPieceRenderer() {}


    public static GamePieceRenderer getRenderer() {
        if (renderer_ == null)
            renderer_ = new HexPieceRenderer();
        return renderer_;
    }


    protected Point getPosition(BoardPosition position, int cellSize, int pieceSize, int margin) {
        int row = position.getRow() - 1;
        int col = position.getCol() - 1;
        int offset = 0;//(cellSize - pieceSize) >> 1;

        position_.x = margin + col * cellSize + offset + row * cellSize/2;
        position_.y = margin + (int)((row + 0.5) * cellSize * HexUtil.ROOT3D2) + offset + 1;
        return position_;
    }


    /**
     * this draws the actual piece at this location (if there is one).
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
        int rad = cellSize/2;

        HexagonRenderer.fillHexagon(g2, pos, rad, getPieceColor(piece));

        // only draw the outline if we are not in a debug mode.
        // when in debug mode we want to emphasize other annotations instead of the piece
        if ( piece.getTransparency() == 0 && (GameContext.getDebugMode() == 0) ) {
            g2.setColor( Color.black );
            HexagonRenderer.drawHexagon(g2, pos, rad, Color.black, OUTLINE_STROKE);
        }

        if ( piece.getAnnotation() != null ) {
            int offset = (cellSize - pieceSize) >> 1;
            g2.setColor( getTextColor(piece) );
            g2.setFont( BASE_FONT );
            g2.drawString( piece.getAnnotation(), pos.x + 2 * offset, pos.y + 3 * offset);
        }
    }
}

