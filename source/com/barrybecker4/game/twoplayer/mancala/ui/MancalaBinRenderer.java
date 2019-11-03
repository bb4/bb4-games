/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.ui;

import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.common.ui.viewer.GamePieceRenderer;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer;
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBin;
import com.barrybecker4.ui.util.GUIUtil;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

/**
 * a singleton class that takes renders a mancala bin containing some number of stones.
 * @author Barry Becker
 */
public class MancalaBinRenderer extends TwoPlayerPieceRenderer {

    /** there must be one of these for each derived class. */
    private static GamePieceRenderer renderer_ = null;

    private static final Color STONE_COLOR = new Color(20, 40, 80);
    private static final Stroke BIN_STROKE = new BasicStroke(2.0f);


    /**
     * private constructor because this class is a singleton.
     * Use getPieceRenderer instead
     */
    protected MancalaBinRenderer() {}


    public static GamePieceRenderer getRenderer() {
        if (renderer_ == null)
            renderer_ = new MancalaBinRenderer();
        return renderer_;
    }

    /**
     * @return the game piece render color.
     */
    @Override
    protected Color getPieceColor(GamePiece piece) {
        return STONE_COLOR;
    }

    /**
     * @return color for annotation text (if any).
     */
    @Override
    protected Color getTextColor(GamePiece piece)  {
        return STONE_COLOR;
    }


   /**
    * Draws the bin at this location with some representation for the stones in the bin.
    *
    * @param g2 graphics context
    * @param position the position of the piece to render
    */
   public void render(Graphics2D g2, BoardPosition position, int cellSize, int margin, Board b) {

       MancalaBin bin = (MancalaBin) position.getPiece();
       // if there is no piece, then nothing to render
       if (bin == null) {
           return;
       }

       int pieceSize = getPieceSize(cellSize, bin);
       Point pos = getPosition(position, cellSize, pieceSize, margin);
       renderForShow(g2, pos, bin, pieceSize);
   }

    public void renderForShow(Graphics2D g2, Point pos, GamePiece piece, int pieceSize) {

        MancalaBin bin = (MancalaBin) piece;
        int yOffset = bin.isHome() ? pieceSize >> 1 : 0;
        Ellipse2D circle = new Ellipse2D.Float( pos.x, pos.y + yOffset, pieceSize + 1, pieceSize + 1 );

        g2.setStroke(BIN_STROKE);
        g2.setColor(bin.isOwnedByPlayer1() ? getPlayer1Color() : getPlayer2Color());
        g2.draw(circle);

       int offset = (int) (0.22 * pieceSize);
       g2.setColor(getTextColor(bin));

       Font font = new Font(GUIUtil.DEFAULT_FONT_FAMILY(), Font.PLAIN, pieceSize/2);
       g2.setFont(font);

       g2.drawString(Integer.toString(bin.getNumStones()), pos.x + offset, pos.y + 3 * offset + yOffset);
    }
}

