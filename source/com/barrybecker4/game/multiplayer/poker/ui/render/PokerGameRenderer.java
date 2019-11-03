/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.render;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.multiplayer.common.ui.MultiGameBoardRenderer;
import com.barrybecker4.game.multiplayer.poker.model.PokerTable;
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer;

import java.awt.*;

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
public class PokerGameRenderer extends MultiGameBoardRenderer {

    private  static GameBoardRenderer renderer_;


    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    private PokerGameRenderer() {
        pieceRenderer_ = PokerPlayerRenderer.getRenderer();
    }

    public static GameBoardRenderer getRenderer() {
        if (renderer_ == null)
            renderer_ = new PokerGameRenderer();
        return renderer_;
    }

    /**
     * draw a grid of some sort if there is one.
     * none by default for poker.
     */
    @Override
    protected void drawGrid(Graphics2D g2, int startPos, int rightEdgePos, int bottomEdgePos, int start,
                            int nrows1, int ncols1, int gridOffset) {}

    @Override
    protected void drawBackground( Graphics g, Board board, int startPos,
                                   int rightEdgePos, int bottomEdgePos,
                                   int panelWidth, int panelHeight ) {
        super.drawBackground(g, board, startPos, rightEdgePos, bottomEdgePos, panelWidth, panelHeight);
        drawTable(g, board);
    }

    /**
     * Draw the pieces and possibly other game markers for both players.
     * The pot will be drawn in the middle of the table.
     */
    @Override
    protected void drawMarkers(Board board, PlayerList players, Graphics2D g2  ) {

        Location loc = new ByteLocation(board.getNumRows() >> 1, (board.getNumCols() >> 1) - 3);
        int pot = ((PokerTable)board).getPotValue();
        new ChipRenderer().render(g2, loc, pot, this.getCellSize());

        // now draw the players and their stuff (face, name, chips, cards, etc)
        super.drawMarkers(board, players, g2);
    }

    /**
     * Draw some indication of where the last move was made.
     * The default is to show nothing.
     */
    @Override
    protected void drawLastMoveMarker(Graphics2D g2, Player currentPlayer, Board board) {
         // draw a background circle for the player whose turn it is
        PokerPlayer player = (PokerPlayer) currentPlayer.getActualPlayer();
        PokerPlayerMarker m = player.getPiece();
        g2.setColor(PokerPlayerRenderer.HIGHLIGHT_COLOR);
        g2.fillOval(cellSize *(m.getLocation().col()-2),
                    cellSize *(m.getLocation().row()-2),
                    10* cellSize, 10* cellSize);
    }

}

