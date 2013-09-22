/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.gametree;

import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 *  This class takes a root node and displays the portion of the tree visible in the text tree control.
 *  An actual graphics tree is shown to represent the game search tree.
 *
 *  @author Barry Becker
 */
final class GameTreeViewer extends JPanel implements MouseMotionListener
{
    private static final Color BACKGROUND_COLOR = Color.white;
    private static final int BACKGROUND_BAND_OPACITY = 100;
    private static final int MARGIN = 8;

    private static final BasicStroke THIN_STROKE = new BasicStroke(0.4f);
    private static final BasicStroke HIGHLIGHT_STROKE = new BasicStroke(1.4f);
    private static final BasicStroke BRUSH_HIGHLIGHT_STROKE = new BasicStroke(3.7f);
    private static final int PRUNE_SPACING = 12;

    /** circle around highlighted node   */
    private static final int HL_NODE_RADIUS = 3;
    private static final int HL_NODE_DIAMETER = 8;
    private static final long serialVersionUID = 0L;

    private Color backgroundColor_ = BACKGROUND_COLOR;
    private ColorMap colormap_;
    private TwoPlayerPieceRenderer pieceRenderer_;

    private SearchTreeNode root_;
    private int depth_ = 0;

    /** sum of the number of descendants of all nodes at the level */
    private int[] totalAtLevel_;

    private int width_;
    private int levelHeight_;

    /** most recently highlighted path. */
    private TreePath oldHighlightPath_;


    /**
     * Construct the viewer
     * @param root root of text tree to base tree graph on.
     */
    GameTreeViewer( SearchTreeNode root, ColorMap cmap, TwoPlayerPieceRenderer pieceRenderer) {
        colormap_ = cmap;
        pieceRenderer_ = pieceRenderer;
        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        setRoot(root);
    }

    public synchronized void setRoot( SearchTreeNode root) {
        root_ = root;
        // this is very expensive because it traverses the whole tree.
        // it might be simpler to just use the depth from the controller, but that would
        // not account for those times when we drill deeper using quiescent search.
        depth_ = root_.getDepth();

        totalAtLevel_ = new int[depth_+2];
        oldHighlightPath_ = null;

        if (root_.getUserObject() != null) {
            initializeTreeStats(root_, 0);
        }
    }

    /**
     * Draw the currently visible game tree.
     */
    public synchronized void refresh() {
        // this will paint the component immediately
        //paint( getGraphics() );
        repaint();
        oldHighlightPath_ = null;
    }

    /**
     * @param c  the new color of the tree.
     */
    @Override
    public synchronized void setBackground( Color c ) {
        backgroundColor_ = c;
        refresh();
    }

    private synchronized void initializeTreeStats( SearchTreeNode node, int depth) {
        if (node.isLeaf())  {
            if (node.isPruned()) {
                // give pruned nodes a little more space
                int pruneSpace =
                        node.getSpaceAllocation() + Math.max(1, 4 * PRUNE_SPACING - PRUNE_SPACING * depth);
                node.setSpaceAllocation(pruneSpace);
            }
            else {
                node.setSpaceAllocation(1);  // never 0;
            }
            totalAtLevel_[depth] += node.getSpaceAllocation();
            return;
        }

        Enumeration it = node.children();
        while (it.hasMoreElements()) {
            SearchTreeNode child = (SearchTreeNode)it.nextElement();
            initializeTreeStats( child, depth+1 );
            node.setSpaceAllocation(node.getSpaceAllocation() + child.getSpaceAllocation());
        }
        // count the node as a descendant
        node.setSpaceAllocation(node.getSpaceAllocation() + 1);
        totalAtLevel_[depth] += node.getSpaceAllocation();
    }


    // ---  these methods implement the MouseMotionListener interface   ---
    @Override
    public void mouseMoved( MouseEvent e ) {}
    @Override
    public void mouseDragged( MouseEvent e ) {}


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
            highlight( oldHighlightPath_, g2);
        }

        highlight( path, g2);
        g2.setPaintMode();
        oldHighlightPath_ = path;
    }

    private void highlight( TreePath path, Graphics2D g2) {
        Object[] pathNodes = path.getPath();
        SearchTreeNode lastNode = (SearchTreeNode)pathNodes[0];
        int diameter = HL_NODE_DIAMETER;
        Point lastLoc = lastNode.getPosition();
        g2.drawOval(lastLoc.x - HL_NODE_RADIUS, lastLoc.y - HL_NODE_RADIUS, diameter, diameter);
        for (int i=1; i<pathNodes.length; i++) {
            SearchTreeNode node = (SearchTreeNode)pathNodes[i];
            TwoPlayerMove m = (TwoPlayerMove)node.getUserObject();
            g2.setColor(colormap_.getColorForValue(m.getInheritedValue()));

            Point nodeLoc = node.getPosition();
            g2.drawLine(lastLoc.x, lastLoc.y, nodeLoc.x,nodeLoc.y);
            g2.setColor(colormap_.getColorForValue(m.getValue()));
            g2.drawOval(nodeLoc.x-HL_NODE_RADIUS, nodeLoc.y-HL_NODE_RADIUS, diameter, diameter);
            lastLoc = nodeLoc;
        }
    }

    /**
     * Draw the nodes and arcs in the game tree.
     * It can get quite huge.
     * @return the number of nodes that were drawn.
     */
    private long drawTree( SearchTreeNode root, Graphics2D g2) {
        int oldDepth = 0;
        int depth;
        int[] offsetAtLevel = new int[depth_+2];
        long numNodes = 0;

        drawNode(root, 0, 0, g2);
        List<SearchTreeNode> q = new LinkedList<SearchTreeNode>();
        q.add(root);

        while (q.size() > 0) {
            SearchTreeNode p = q.remove(0);
            numNodes++;
            depth = p.getLevel();
            // draw the arc and child root for each child c of p
            if (depth > oldDepth) {
                oldDepth = depth;
                offsetAtLevel[depth] = 0;
            }
            Enumeration enumeration = p.children();
            while (enumeration.hasMoreElements()) {
                SearchTreeNode c = (SearchTreeNode)enumeration.nextElement();
                drawArc(p, c, depth, offsetAtLevel[depth], offsetAtLevel[depth+1], g2);
                drawNode(c, depth+1, offsetAtLevel[depth+1], g2);
                offsetAtLevel[depth+1] += c.getSpaceAllocation();
                q.add(c);
            }
            offsetAtLevel[depth] += p.getSpaceAllocation();
        }
        return numNodes;
    }

    /**
     * Draw one of the game tree nodes
     */
    private synchronized void drawNode( SearchTreeNode node, int depth, int offset, Graphics2D g2) {
        TwoPlayerMove m = (TwoPlayerMove)node.getUserObject();
        g2.setColor( colormap_.getColorForValue(m.getValue()));
        int x = MARGIN + (int) (width_ * (offset + node.getSpaceAllocation() / 2.0) / totalAtLevel_[depth]);
        int y = MARGIN + depth*levelHeight_;
        node.setLocation(x, y);

        int width = 2;
        if (m.isSelected()) {
            width = 3;
            g2.fillRect(x, y, width, 3);
        }
        else {
            g2.fillRect(x, y, width, 3);
        }
        if (node.isPruned())  {
           // draw a marker to show that it has been pruned
           g2.setColor(Color.black);
           g2.fillRect(x, y+3, width, 1);
        }
    }

    /**
     * Draw a line/arc connecting a parent and child node.
     */
    private synchronized void drawArc( SearchTreeNode parent, SearchTreeNode child,
                                       int depth, int offset1, int offset2, Graphics2D g2) {
        TwoPlayerMove m = (TwoPlayerMove)child.getUserObject();
        boolean highlighted = m.isSelected() && ((TwoPlayerMove)parent.getUserObject()).isSelected();
        if (highlighted)
            g2.setStroke(HIGHLIGHT_STROKE);
        g2.setColor( colormap_.getColorForValue(m.getInheritedValue()));
        g2.drawLine(MARGIN + (int) (width_*(offset1 + parent.getSpaceAllocation() / 2.0) / totalAtLevel_[depth]),
                    MARGIN + depth*levelHeight_,
                    MARGIN + (int) (width_*(offset2 + child.getSpaceAllocation() / 2.0) / totalAtLevel_[depth+1]),
                    MARGIN + (depth+1)*levelHeight_);
        if (highlighted) {
            g2.setStroke(THIN_STROKE);
        }
    }

    /**
     * draw colored bands to give an indication of who is moving on each ply.
     * @param g2 graphics
     */
    private synchronized void drawBackground( Graphics2D g2 ) {
        g2.setColor( backgroundColor_ );
        g2.fillRect( 0, 0, getWidth(), getHeight() );

        Color c = pieceRenderer_.getPlayer1Color(); //colormap_.getMaxColor();
        Color p1Color = new Color(c.getRed(), c.getGreen(), c.getBlue(), BACKGROUND_BAND_OPACITY);
        c = pieceRenderer_.getPlayer2Color();  //colormap_.getMinColor();
        Color p2Color = new Color(c.getRed(), c.getGreen(), c.getBlue(), BACKGROUND_BAND_OPACITY);
        GradientPaint gp = new GradientPaint(0, (float)levelHeight_/4.0f, p2Color, 0, 5.0f*levelHeight_/4.0f, p1Color, true);
        g2.setPaint(gp);

        g2.fillRect( 0, 0, getWidth(), getHeight() );
    }

    /**
     * This renders the current tree to the canvas
     */
    @Override
    protected synchronized void paintComponent( Graphics g )  {
        Graphics2D g2 = (Graphics2D)g;

        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        super.paintComponents( g );
        drawBackground( g2 );

        if (root_==null || root_.getUserObject()==null) return;

        width_ = getWidth()-2*MARGIN;
        int height= getHeight()-2*MARGIN;
        if (depth_ == 0)
            return; // tree not ready to be painted.
        levelHeight_ = height /depth_;

        g2.setStroke(THIN_STROKE);
        long numNodes = drawTree(root_, g2);

        g2.setColor(Color.BLACK);
        g2.drawString("Nodes = " + FormatUtil.formatNumber(numNodes), MARGIN + width_ - 120 , MARGIN + 10);
    }
}