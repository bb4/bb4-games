/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui;

import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.common.ui.viewer.GamePieceRenderer;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * a singleton class that takes a game piece and renders it for the TwoPlayerBoardViewer.
 * We use a separate piece rendering class to avoid having ui in the piece class itself.
 * This allows us to more cleanly separate the client pieces from the server. *
 * @author Barry Becker
 */
public class TwoPlayerPieceRenderer extends GamePieceRenderer {

    /** there must be one of these for each derived class too. */
    private static GamePieceRenderer renderer_ = null;

    // We should move the color options to the PlayerOptions class so they can be customized.
    public static final Color DEFAULT_PLAYER1_COLOR = new Color( 180, 60, 100);
    public static final Color DEFAULT_PLAYER2_COLOR = new Color( 10, 120, 255);

    private static final Color PLAYER1_TEXT_COLOR = new Color( 255, 250, 255 );
    private static final Color PLAYER2_TEXT_COLOR = new Color( 0, 50, 30 );


    private static final Color URGENT_COLOR = new Color(245, 10, 0);
    private static final DecimalFormat format_ = new DecimalFormat("###,###.#");
    private static final double NEXT_MOVE_SIZE_FRAC = 0.2;

    /**
     * private constructor because this class is a singleton.
     * Use getPieceRenderer instead
     */
    protected TwoPlayerPieceRenderer() {}


    public static GamePieceRenderer getRenderer() {
        if (renderer_ == null)
            renderer_ = new TwoPlayerPieceRenderer();
        return renderer_;
    }

    /**
     *  @return color the player1 pieces should be. Ignored if using icons to represent the pieces.
     */
    public Color getPlayer1Color() {
        return DEFAULT_PLAYER1_COLOR;
    }

    /**
     *  @return color the player2 pieces should be. Ignored if using icons to represent the pieces.
     */
    public Color getPlayer2Color() {
        return DEFAULT_PLAYER2_COLOR;
    }

    /**
     * @return the game piece render color.
     */
    @Override
    protected Color getPieceColor(GamePiece piece) {
        Color playerColor = piece.isOwnedByPlayer1() ? getPlayer1Color() : getPlayer2Color();
         return  new Color( playerColor.getRed(), playerColor.getGreen(), playerColor.getBlue(),
                           255 - piece.getTransparency() );
    }

    /**
     * @return color for annotation text (if any).
     */
    @Override
    protected Color getTextColor(GamePiece piece)  {
        Color textColor = PLAYER2_TEXT_COLOR;
        if ( piece.isOwnedByPlayer1() ) {
            textColor = PLAYER1_TEXT_COLOR;
        }
        return textColor;
    }

    /**
     * show the next moves in a special way.
     */
    public void renderNextMove( Graphics2D g2, TwoPlayerMove move, int cellSize,
                                int margin, Board b) {

        if (move.isPassOrResignation()) return;

        assert (move.getPiece() != null) : "piece for next move is null: " + move;

        g2.setColor(getPieceColor(move.getPiece()));

        BoardPosition position = b.getPosition(move.getToRow(), move.getToCol());
        int pieceSize = (int)(NEXT_MOVE_SIZE_FRAC * getPieceSize(cellSize, move.getPiece()));
        Point pos = getPosition(position, cellSize, pieceSize, margin);
        g2.setFont(BASE_FONT);
        g2.fillOval( pos.x, pos.y, pieceSize, pieceSize );
        g2.setColor(move.isUrgent() ? URGENT_COLOR : Color.DARK_GRAY);
        g2.drawString(format_.format(move.getValue()), pos.x - 3 , pos.y + 2);
    }
}

