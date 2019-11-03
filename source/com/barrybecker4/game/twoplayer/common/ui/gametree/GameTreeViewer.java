/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.gametree;

import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer;
import com.barrybecker4.ui.util.ColorMap;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.tree.TreePath;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 *  This class takes a root node and displays the portion of the tree visible in the text tree control.
 *  An actual graphics tree is shown to represent the game search tree.
 *
 *  @author Barry Becker
 */
final class GameTreeViewer extends JPanel {

    private static final int MARGIN = 8;
    private static final BasicStroke BRUSH_HIGHLIGHT_STROKE = new BasicStroke(3.7f);
    private static final long serialVersionUID = 0L;

    /** most recently highlighted path. */
    private TreePath oldHighlightPath_;

    private GameTreeRenderer gameTreeRenderer;


    /**
     * Construct the viewer
     * @param root root of text tree to base tree graph on.
     */
    GameTreeViewer(SearchTreeNode root, ColorMap cmap, TwoPlayerPieceRenderer pieceRenderer) {

        gameTreeRenderer = new GameTreeRenderer(root, cmap, pieceRenderer);
        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    }

    public synchronized void setRoot( SearchTreeNode root) {

        oldHighlightPath_ = null;
        gameTreeRenderer.setRoot(root);
    }

    /**
     * Draw the currently visible game tree.
     */
    public synchronized void refresh() {
        repaint();
        oldHighlightPath_ = null;
    }

    /**
     * perform a sequence of moves from somewhere in the game;
     * not necessarily the start. We do, however,
     * assume the moves are valid. It is for display purposes only.
     *
     * @param path path corresponding to a the list of moves to make
     */
    public synchronized void highlightPath( TreePath path ) {
        // unhighlight the old path and highlight the new one without redrawing the whole tree.
        // GameContext.log(2, "about to highlight "+path );
        Graphics2D g2 = (Graphics2D)getGraphics();
        g2.setXORMode(Color.WHITE);
        g2.setStroke(BRUSH_HIGHLIGHT_STROKE);

        // first unhighlight the old path
        if (oldHighlightPath_ != null)  {
            gameTreeRenderer.highlight(oldHighlightPath_, g2);
        }

        gameTreeRenderer.highlight( path, g2);
        g2.setPaintMode();
        oldHighlightPath_ = path;
    }

    /**
     * This renders the current tree to the canvas
     */
    @Override
    protected void paintComponent( Graphics g ) {

        super.paintComponents( g );
        Graphics2D g2 = (Graphics2D)g;

        gameTreeRenderer.drawBackground(g2, getWidth(), getHeight());
        int width = getWidth() - 2 * MARGIN;
        int height= getHeight()-2*MARGIN;
        gameTreeRenderer.paintTree(g2, width, height);
    }
}
