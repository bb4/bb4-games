/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.renderers;

import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.common.ui.viewer.GamePieceRenderer;
import com.barrybecker4.game.multiplayer.galactic.Galaxy;
import com.barrybecker4.game.multiplayer.galactic.Planet;
import com.barrybecker4.ui.gradient.RoundGradientPaint;
import com.barrybecker4.ui.util.GUIUtil;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * A singleton class that takes a planet instance and renders it for the GalacticBoardViewer.
 * @see Galaxy
 * @author Barry Becker
 */
public class PlanetRenderer extends GamePieceRenderer {

    private static GamePieceRenderer renderer_ = null;

    private static final Color ATTACK_COLOR = new Color(255, 100, 0);
    private static final BasicStroke ATTACK_STROKE = new BasicStroke(3);

    private static final Color HIGHLIGHT_COLOR = new Color(245, 255, 0);
    private static final BasicStroke HIGHLIGHT_STROKE = new BasicStroke(2);

    private static final Font PLANET_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY(), Font.PLAIN, 11 );

    /**
     * private constructor because this class is a singleton.
     * Use getPieceRenderer instead
     */
    private PlanetRenderer() {}

    public static GamePieceRenderer getRenderer() {
        if (renderer_ == null)
            renderer_ = new PlanetRenderer();
        return renderer_;
    }

    @Override
    protected int getPieceSize(int cellSize, GamePiece piece) {
        Planet planet = (Planet)piece;

        double rad = planet.getRadius();
        return (int) (cellSize * rad);
    }

    @Override
    protected Color getPieceColor(GamePiece piece) {
        Planet planet = (Planet)piece;
        return planet.getColor();
    }

    /**
     * this draws the actual piece at this location (if there is one).
     * Uses the RoundGradientFill from Knudsen to put a specular highlight on the planet.
     *
     * @param g2 graphics context
     * @param position the position of the piece to render
     */
    @Override
    public void render( Graphics2D g2, BoardPosition position, int cellSize, int margin, Board b) {
        Planet planet = (Planet)position.getPiece();
        if (planet == null) {
            return; // nothing to render
        }

        int pieceSize = getPieceSize(cellSize, planet);
        Point pos = getPosition(position, cellSize, pieceSize, margin);
        Ellipse2D circle = new Ellipse2D.Float( pos.x, pos.y, pieceSize + 1, pieceSize + 1 );
        int hlOffset = (int) (pieceSize / 2.3 + 0.5);  //spec highlight offset
        Color c = getPieceColor(planet);

        RoundGradientPaint rgp = new RoundGradientPaint(
                pos.x + hlOffset, pos.y + hlOffset, Color.white, SPEC_HIGHLIGHT_RADIUS, c );

        g2.setPaint( rgp );
        g2.fill( circle );

        if ( planet.isUnderAttack() ) {
            g2.setStroke(ATTACK_STROKE);
            g2.setColor( ATTACK_COLOR );
            g2.drawOval( pos.x, pos.y, pieceSize + 1, pieceSize + 1 );
        }

        if ( planet.isHighlighted() ) {
                g2.setStroke(HIGHLIGHT_STROKE);
                g2.setColor( HIGHLIGHT_COLOR );
                g2.drawOval( pos.x, pos.y, pieceSize + 1, pieceSize + 1 );
            }


        int offset = (pieceSize<(0.6*cellSize))? -1 : cellSize/5;
        if ( planet.getAnnotation() != null ) {
                g2.setColor( Color.black );
                g2.setFont( PLANET_FONT );
                g2.drawString( planet.getAnnotation(), pos.x + 2*offset, pos.y + 3*offset);
        }
    }
}
