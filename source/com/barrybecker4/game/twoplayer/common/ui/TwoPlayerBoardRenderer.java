/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui;

import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;

import java.awt.*;

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
public abstract class TwoPlayerBoardRenderer extends GameBoardRenderer {

    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    protected TwoPlayerBoardRenderer()  {}

    @Override
    protected void drawLastMoveMarker(Graphics2D g2, Player player, Board board) {

        TwoPlayerMove last = (TwoPlayerMove) board.getMoveList().getLastMove();
        // this draws a small indicator on the last move to show where it was played
        if ( last != null ) {
            g2.setColor( LAST_MOVE_INDICATOR_COLOR );
            g2.setStroke(LAST_MOVE_INDICATOR_STROKE);
            int cellSize = getCellSize();
            int xpos = getMargin() + (last.getToCol() - 1) * cellSize + 1;
            int ypos = getMargin() + (last.getToRow() - 1) * cellSize + 1;
            g2.drawOval( xpos, ypos, cellSize - 2, cellSize - 2 );
        }
    }
}

