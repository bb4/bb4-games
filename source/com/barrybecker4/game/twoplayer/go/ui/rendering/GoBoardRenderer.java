/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui.rendering;

import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerBoardRenderer;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzer;
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap;
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup;
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
public class GoBoardRenderer extends TwoPlayerBoardRenderer {
    private static GameBoardRenderer renderer_;

    public static final ColorMap COLORMAP = new GoColorMap();

    private static final Color TICK_LABEL_COLOR = new Color(10, 10, 10);
    private static final int BOARD_MARGIN = 12;
    private static final Font TICK_LABEL_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY(), Font.PLAIN, 11);

    /** the image for the wooden board. */
    private static final ImageIcon woodGrainImage_ =
            GUIUtil.getIcon(GameContext.GAME_RESOURCE_ROOT + "twoplayer/go/ui/images/goBoard1.gif");

    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    private GoBoardRenderer() {
        pieceRenderer_ = GoStoneRenderer.getRenderer();
    }

    /**
     * @return singleton instance
     */
    public static GameBoardRenderer getRenderer() {
        if (renderer_ == null)
            renderer_ = new GoBoardRenderer();
        return renderer_;
    }

    @Override
    protected int getPreferredCellSize() {
        return 16;
    }

    @Override
    protected int getMargin()  {
        return BOARD_MARGIN;
    }

    /**
     * whether to draw the pieces on cell centers or vertices (the way go requires).
     */
    @Override
    protected boolean offsetGrid() {
        return true;
    }

    /**
     * @param i number
     * @return i converted to a character string.
     */
    private String int2char(int i) {
        char c = (char)('a' + i);
        return Character.toString(c);
    }

    /**
     * draw a grid of some sort if there is one.
     * none by default for poker.
     */
    @Override
    protected void drawGrid(Graphics2D g2, int startPos, int rightEdgePos, int bottomEdgePos, int start,
                            int nrows1, int ncols1, int gridOffset) {

        super.drawGrid(g2, startPos, rightEdgePos, bottomEdgePos, start, nrows1, ncols1, gridOffset);

        // only draw the row and column numbering if in debug mode
        if (GameContext.getDebugMode() == 0)
            return;

        g2.setColor( TICK_LABEL_COLOR );
        g2.setFont(TICK_LABEL_FONT);
        Rectangle2D metrics = TICK_LABEL_FONT.getMaxCharBounds(g2.getFontRenderContext());
        int textHt = (int)Math.ceil(metrics.getHeight());
        int appxHt1 = (int)(textHt/2.8);

        for (int i = start; i <= nrows1; i++ ) {  //   |
            int ypos = getMargin() + appxHt1 + i * cellSize + gridOffset;
            g2.drawString(int2char(i), 1, ypos);
        }
        for (int i = start; i <= ncols1; i++ ) {  //   --
            int xpos = getMargin() - 1 + i * cellSize + gridOffset;
            g2.drawString(int2char(i), xpos, (3 +  getMargin() - appxHt1));
        }
    }


    /**
     * draw the wood grain background.
     */
    @Override
    protected void drawBackground( Graphics g, Board b, int startPos, int rightEdgePos, int bottomEdgePos,
                                   int panelWidth, int panelHeight) {
        super.drawBackground( g, b,  startPos, rightEdgePos, bottomEdgePos, panelWidth, panelHeight);
        int t = (int)(cellSize /2.0f);
        g.drawImage(woodGrainImage_.getImage(), (startPos-t), (startPos-t),
                                                (rightEdgePos-startPos+2 * t), (bottomEdgePos-startPos+2 * t), null);
    }


    /**
     * first draw borders for the groups in the appropriate color, then draw the pieces for both players.
     */
    @Override
    protected void drawMarkers(Board board, PlayerList players, Graphics2D g2 ) {

        GoBoard b = (GoBoard) board;
        // draw the star point markers
        List starpoints = b.getHandicapPositions();
        Iterator it = starpoints.iterator();
        g2.setColor(Color.black);
        double rad = (float) cellSize / 21.0 + 0.46;
        while (it.hasNext()) {
            GoBoardPosition p = (GoBoardPosition)it.next();
            g2.fillOval(getMargin() + (int)(cellSize *(p.getCol()-0.505)-rad),
                        getMargin() +(int)(cellSize *(p.getRow()-0.505)-rad),
                        (int)(2.0 * rad + 1.7), (int)(2.0 * rad + 1.7));
        }

        // draw the group borders
        if ( GameContext.getDebugMode() > 0 ) {
            GoGroupRenderer groupRenderer = new GoGroupRenderer(b, COLORMAP, (float) cellSize, getMargin(), g2);

            GroupAnalyzerMap map = new GroupAnalyzerMap();
            for (IGoGroup group : b.getGroups()) {

                GroupAnalyzer analyzer = new GroupAnalyzer(group, map);
                groupRenderer.drawGroupDecoration(analyzer);
            }
        }

        super.drawMarkers(board, players, g2);
        //drawNextMoveMarkers(board, g2);
    }

}

