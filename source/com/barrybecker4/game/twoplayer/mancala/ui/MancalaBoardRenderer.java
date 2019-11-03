/* Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.ui;

import com.barrybecker4.common.geometry.IntLocation;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerBoardRenderer;
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBin;

import java.awt.Graphics2D;

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
public class MancalaBoardRenderer extends TwoPlayerBoardRenderer {

    private static GameBoardRenderer renderer_;


    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    protected MancalaBoardRenderer(){
        pieceRenderer_ = MancalaBinRenderer.getRenderer();
    }

    public static GameBoardRenderer getRenderer() {
        if (renderer_ == null)
            renderer_ = new MancalaBoardRenderer();
        return renderer_;
    }

     /**
      * no grid by default for mancala.
      */
    @Override
    protected void drawGrid(Graphics2D g2, int startPos, int rightEdgePos, int bottomEdgePos, int start,
                            int nrows1, int ncols1, int gridOffset) {}


    @Override
    protected void drawLastMoveMarker(Graphics2D g2, Player player, Board board) {

        TwoPlayerMove last = (TwoPlayerMove) board.getMoveList().getLastMove();

        // this draws a small indicator on the last move to show where it was played
        if ( last != null ) {
            g2.setColor(LAST_MOVE_INDICATOR_COLOR );
            g2.setStroke(LAST_MOVE_INDICATOR_STROKE);
            int cellSize = getCellSize();
            IntLocation pos = getPosition(last.getToLocation());
            MancalaBin bin = (MancalaBin) board.getPosition(last.getToLocation()).getPiece();

            int yOffset = bin.isHome() ? cellSize >> 1 : 0;

            int x = pos.getX();
            int y = pos.getY() + yOffset;
                    g2.drawOval(x, y, cellSize - 2, cellSize - 2 );
        }
    }
}

