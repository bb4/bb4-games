/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.renderers;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.multiplayer.common.ui.MultiGameBoardRenderer;
import com.barrybecker4.game.multiplayer.galactic.Order;
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer;

import java.awt.*;
import java.awt.geom.Point2D;


/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
public class GalaxyRenderer extends MultiGameBoardRenderer {

    private  static GameBoardRenderer renderer_;

    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    private GalaxyRenderer() {
        pieceRenderer_ = PlanetRenderer.getRenderer();
    }

    public static GameBoardRenderer getRenderer() {
        if (renderer_ == null)
            renderer_ = new GalaxyRenderer();
        return renderer_;
    }


    @Override
    protected int getPreferredCellSize() {
        return 16;
    }

    /**
     * Draw the pieces and possibly other game markers for both players.
     */
    @Override
    protected void drawMarkers(Board board, PlayerList players, Graphics2D g2 ) {

        // before we draw the planets, draw the fleets and their paths
        for (final Player player : players) {
            for (Order order : ((GalacticPlayer) player).getOrders()) {

                int margin = getMargin();

                Location begin = order.getOrigin().getLocation();
                Point2D end = order.getCurrentLocation();

                g2.setColor(order.getOwner().getColor());
                int beginX = (int) (margin + cellSize * (begin.col() - 0.5));
                int beginY = (int) (margin + cellSize * begin.row() - 0.5);
                int endX = (int) (margin + cellSize * (end.getX() - 0.5));
                int endY = (int) (margin + cellSize * (end.getY() - 0.5));

                g2.drawLine(beginX, beginY, endX, endY);

                // the glyph at the end of the line representing the fleet
                int rad = (int) Math.round(Math.sqrt(order.getFleetSize()));
                g2.drawOval((int) (endX - rad / 2.0), (int) (endY - rad / 2.0), rad, rad);
            }
        }

        // now draw the planets on top
        super.drawMarkers(board, players, g2);
    }

}

