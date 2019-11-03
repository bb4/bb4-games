/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui.rendering;

import com.barrybecker4.ui.util.ImageUtil;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

/**
 * Singleton class that takes a go piece and renders it for the GoBoardViewer.
 * @author Barry Becker
 */
public final class GoStoneRenderer extends TwoPlayerPieceRenderer
{
    private static TwoPlayerPieceRenderer renderer_ = null;

    // if rendering the stones we use these colors
    // the stone colors ( a specular highlight is added to the stones when rendering )
    private static final Color PLAYER1_STONE_COLOR = new Color( 90, 90, 90 );
    private static final Color PLAYER2_STONE_COLOR = new Color( 230, 230, 230 );  // off-white

    private static final Color ATARI_COLOR = new Color( 255, 210, 90, 255 );  // bright red
    private static final int ATARI_MARKER_RADIUS = 6;

    // instead of rendering we can just show image icons which look even better.
    // gets the images from resources or the filesystem depending if we are running as an applet or
    // application respectively.
    private static final String DIR = GameContext.GAME_RESOURCE_ROOT + "twoplayer/go/ui/images/";  // NON-NLS
    public static final ImageIcon BLACK_STONE_IMG = GUIUtil.getIcon(DIR + "goStoneBlack.png");   // NON-NLS
    public static final ImageIcon WHITE_STONE_IMG = GUIUtil.getIcon(DIR + "goStoneWhite.png");     // NON-NLS
    private static final ImageIcon BLACK_STONE_DEAD_IMG = GUIUtil.getIcon(DIR + "goStoneBlackDead.png");  // NON-NLS
    private static final ImageIcon WHITE_STONE_DEAD_IMG = GUIUtil.getIcon(DIR + "goStoneWhiteDead.png");  // NON-NLS

    private static final float[] scaleFactors_ = {1.0f, 1.0f, 1.0f, 1.0f};
    private static final float[] OFFSETS = {0.0f, 0.0f, 0.0f, 0.0f};
    private static final Font ANNOTATION_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY(), Font.BOLD, 16 );

    /**
     * protected constructor because this class is a singleton.
     * Use getPieceRenderer instead
     */
    private GoStoneRenderer() {}

    public static TwoPlayerPieceRenderer getRenderer() {
        if (renderer_ == null)
            renderer_ = new GoStoneRenderer();
        return renderer_;
    }

    /**
     * @return the color the pieces for player1.   (black)
     */
    @Override
    public Color getPlayer1Color() {
        return PLAYER1_STONE_COLOR;
    }

     /**
     * @return the color the pieces for player2.   (white)
     */
    @Override
    public Color getPlayer2Color() {
        return PLAYER2_STONE_COLOR;
    }

    /**
     * @return  the image to show for the graphical representation of the go stone
     */
    private static Image getImage(GoStone stone) {
        if (stone.isDead())
            return (stone.isOwnedByPlayer1() ? BLACK_STONE_DEAD_IMG.getImage(): WHITE_STONE_DEAD_IMG.getImage());
        else
            return (stone.isOwnedByPlayer1() ? BLACK_STONE_IMG.getImage(): WHITE_STONE_IMG.getImage());
    }

    /**
     * This draws the actual piece.
     * Draws the go stone as an image.
     * Apply a RescaleOp filter to adjust the transparency if need be.
     *
     * @param g2 graphics context
     * @param position of the piece to render
     */
    @Override
    public void render( Graphics2D g2, BoardPosition position, int cellSize, int margin, Board board) {
        GoBoardPosition stonePos = (GoBoardPosition)position;
        if (GameContext.getDebugMode() > 0)  {
            drawTerritoryShading(g2, position, cellSize, margin);
        }

        GoStone stone = (GoStone)position.getPiece();
        if (stone != null) {
            boolean inAtari = stonePos.isInAtari((GoBoard)board);
            drawStone(g2, position, cellSize, margin, inAtari, stone);
        }
    }

    /**
     * As a debugging aid draw the background as a function of the territorial score (-1 : 1)
     */
    private void drawTerritoryShading(Graphics2D g2, BoardPosition position, int cellSize, int margin) {
        double score = ((GoBoardPosition)position).getScoreContribution();
        Color pc = (score > 0? PLAYER1_STONE_COLOR : PLAYER2_STONE_COLOR);
        int op = (int)((100 * Math.abs(score)));
        if (op > 255) {
            GameContext.log(1, "error: score too big =" + score + "\n");
        }
        Color c = new Color(pc.getRed(), pc.getGreen(), pc.getBlue(),
                     Math.min(255, op));
        g2.setColor(c);
        g2.fillRect(margin + cellSize*(position.getCol()-1),
                    margin + cellSize*(position.getRow()-1),
                    cellSize, cellSize );
    }

    /**
     * Draw the stone and its decoration.
     */
    private void drawStone(Graphics2D g2, BoardPosition position, int cellSize, int margin, boolean inAtari,
                       GoStone stone) {
        int pieceSize = getPieceSize(cellSize, stone);
        Point pos = getPosition(position, cellSize, pieceSize, margin);
        float transp = stone.getTransparency();
        Image img = getImage(stone);
        if (transp > 0) {
            scaleFactors_[3] = (255 - transp)/255;
            RescaleOp transparencyOp = new RescaleOp(scaleFactors_, OFFSETS, null);
            BufferedImage bufImg = ImageUtil.makeBufferedImage(getImage(stone));
            img = transparencyOp.filter(bufImg, null);
        }
        g2.drawImage(img, pos.x, pos.y, pieceSize, pieceSize , null);

        if (GameContext.getDebugMode() > 0 && inAtari) {
            g2.setColor(ATARI_COLOR);
            g2.fillOval(pos.x, pos.y, ATARI_MARKER_RADIUS, ATARI_MARKER_RADIUS);
        }
        if ( stone.getAnnotation() != null ) {
            drawAnnotations(g2, cellSize, stone, pieceSize, pos);
        }
    }

    /**
     * Textual notes on the board position to aid debugging.
     */
    private void drawAnnotations(Graphics2D g2, int cellSize, GoStone stone, int pieceSize, Point pos) {
        int offset = (cellSize - pieceSize) >> 1;
        g2.setFont( ANNOTATION_FONT );
        int xPos =  pos.x + 2 * offset;
        int yPos =  pos.y + 4 * offset;
        g2.setColor( stone.isOwnedByPlayer1()? Color.WHITE: Color.BLACK);
        g2.drawString( stone.getAnnotation(), xPos, yPos);
        g2.setColor( stone.isOwnedByPlayer1()? Color.BLACK : Color.WHITE);
        g2.drawString( stone.getAnnotation(), xPos + 1, yPos  + 1);
    }

}
