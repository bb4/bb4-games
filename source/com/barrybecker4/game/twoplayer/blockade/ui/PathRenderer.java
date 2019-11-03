/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.ui;

import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove;
import com.barrybecker4.game.twoplayer.blockade.board.path.Path;
import com.barrybecker4.game.twoplayer.blockade.board.path.PathList;

import java.awt.*;
import java.util.Iterator;

/**
 * Render the paths on the blockade board.
 * Mostly for debugging purposes.
 * Usually the human players do not want to see these paths during a normal game.
 *
 * @author Barry Becker
 */
final class PathRenderer {

    private static final float PATH_WIDTH_RATIO = .16f;
    private static final float POINT_WIDTH_RATIO = .22f;
    private static final int ALPHA_CONST = 16;

    /** offset the players path a little so they are not right on top of each other. */
    private static final float PLAYER1_PATH_OFFSET = 0.5f;
    private static final float PLAYER2_PATH_OFFSET = 0.3f;

    /**
     * Private constructor.
     * Static util class. Do not instantiate.
     */
    private PathRenderer() {}


    /**
     * Draw all the shortest paths from the specified position pos going to all the opponent homes.
     * Draw the shorter paths darker or thicker (or something).
     * @param g2 graphics object
     * @param pos the starting position of the current pawn.
     * @param b game board
     */
    static void drawShortestPaths(Graphics2D g2, BlockadeBoardPosition pos, BlockadeBoard b, int cellSize)
    {
        BasicStroke pathStroke =
                new BasicStroke((float)cellSize * PATH_WIDTH_RATIO, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(pathStroke);
        if (pos == null || pos.isUnoccupied()) return;

        PathList paths = b.findShortestPaths(pos);
        boolean p1 = pos.getPiece().isOwnedByPlayer1();
        Color pathColor = p1? BlockadePieceRenderer.getRenderer().getPlayer1Color() :
                              BlockadePieceRenderer.getRenderer().getPlayer2Color();
        pathColor = pathColor.darker();

        for (final Path path : paths) {
            assert(path.getLength() >= 0);
            int alpha = Math.min(255, 5 + (19 * ALPHA_CONST) / Math.min(ALPHA_CONST, (path.getLength() + 1)));
            Color c = new Color(pathColor.getRed(), pathColor.getGreen(), pathColor.getBlue(), alpha);
            g2.setColor(c);
            drawPath(g2, path, cellSize);
        }
    }


    /**
     * Draws the specified path in the board viewer window.
     */
    private static void drawPath(Graphics2D g2, Path path, int cellSize)
    {
        Iterator it = path.iterator();
        int len = path.getLength() + 1;
        int x[] = new int[len];
        int y[] = new int[len];
        int ct = 0;
        BlockadeMove m = null;
        float offset = 0;
        while (it.hasNext()) {
            m = (BlockadeMove)it.next();
            offset = (m.isPlayer1() ?  PLAYER1_PATH_OFFSET :  PLAYER2_PATH_OFFSET) ;
            x[ct] = calcPosition(m.getFromCol(), offset, cellSize);
            y[ct] = calcPosition(m.getFromRow(), offset, cellSize);
            ct++;
        }
        if (m != null) {
            x[ct] = calcPosition(m.getToCol(), offset, cellSize);
            y[ct] = calcPosition(m.getToRow(), offset, cellSize);
            ct++;
            g2.drawPolyline(x, y , ct);
            int diameter = (int)(cellSize * POINT_WIDTH_RATIO);
            int radius = diameter / 2;
            for (int i=0; i < ct; i++) {
                g2.drawOval(x[i] - radius, y[i] - radius, diameter, diameter);
            }
        }
    }

    private static int calcPosition(int coord, float offset, int cellSize) {
        return  (int)(((float)coord - offset) * cellSize);
    }

}
