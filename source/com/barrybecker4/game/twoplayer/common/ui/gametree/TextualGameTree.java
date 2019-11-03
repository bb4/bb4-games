/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.gametree;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode;

import javax.swing.*;
import javax.swing.event.TreeExpansionListener;
import java.awt.*;
import java.awt.event.MouseMotionListener;

/**
 * Draw the entire game tree using a java tree control in a scrolled pane.
 * Contains 3 sub representations: a java text tree with nodes that can be expanded and collapsed,
 * game viewer, and the graphical GameTreeViewer at the bottom that renders a tree.
 *
 * @author Barry Becker
 */
public final class TextualGameTree extends JScrollPane {

    private static final int TREE_WIDTH = 420;
    private static final int ROW_HEIGHT = 14;

    private JTree tree_;
    private GameTreeCellRenderer cellRenderer_;

    /**
     * Constructor - create the textual game tree.
     */
    public TextualGameTree(SearchTreeNode root, GameTreeCellRenderer cellRenderer) {

        cellRenderer_ = cellRenderer;
        reset(root);
    }

    public void reset(SearchTreeNode root) {
        tree_ = createTree(root);
        this.setPreferredSize( new Dimension( 400, 600 ) );
        this.setMinimumSize(new Dimension(200, 120));
        this.setViewportView(tree_);
    }

    @Override
    public void addMouseMotionListener(MouseMotionListener listener) {
       tree_.addMouseMotionListener(listener);
    }

    public void addTreeExpansionListener(TreeExpansionListener listener) {
       tree_.addTreeExpansionListener(listener);
    }

    public void expandRow(int row) {
        tree_.expandRow(row);
        tree_.scrollRowToVisible(row);
    }

    public int getNumRows() {
        return tree_.getRowCount();
    }


    private JTree createTree(SearchTreeNode root) {
        JTree tree = new JTree(root);
        try {
            ToolTipManager.sharedInstance().registerComponent(tree);
            tree.setBackground(UIManager.getColor( "Tree.textBackground" ));
            tree.setCellRenderer( cellRenderer_ );
            //tree.setPreferredSize( new Dimension( TREE_WIDTH, 900 ) );
            tree.setShowsRootHandles( true );
            tree.putClientProperty( "JTree.lineStyle", "Angled" );
            tree.setRowHeight( ROW_HEIGHT );
        }
        catch (ArrayIndexOutOfBoundsException e) {
            GameContext.log(0,
                    "Error: There was an ArayIndexOutOfBounds exception when creating a JTree from this root node: " + root);
            e.printStackTrace();
        }
        return tree;
    }
}
