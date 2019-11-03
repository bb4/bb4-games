/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.gametree;

import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.tree.IGameTreeViewable;
import com.barrybecker4.game.twoplayer.common.search.tree.NodeAttributes;
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode;

import java.util.Enumeration;

/**
 * Responsible for handling events related to modifying the nodes in the game tree.
 *
 * MOst operations lock on the tree instance because we want to be sure that we do not modify the
 * tree while we are making a copy of it for use in rendering it.
 *
 * @author Barry Becker
 */
public final class GameTreeViewable implements IGameTreeViewable {

    private final SearchTreeNode root_;

    /**
     * constructor - create the tree dialog.
     */
    public GameTreeViewable(TwoPlayerMove m) {
        root_ = new SearchTreeNode(m, new NodeAttributes());
    }

    /**
     * @return the root node so that it can be modified.
     */
    @Override
    public SearchTreeNode getRootNode() {
        return root_;
    }

    /**
     * @return the root node of a deeply copied tree (se we do not need to worry about it changing.
     */
    public SearchTreeNode getTreeCopy() {
        SearchTreeNode rootCopy;
        synchronized (root_) {
            rootCopy = getSubtreeCopy(root_);
        }
        return rootCopy;
    }

    /** @return a copy of the subtree rooted at root */
    private SearchTreeNode getSubtreeCopy(SearchTreeNode root) {
        SearchTreeNode rootCopy = (SearchTreeNode) root.clone();
        Enumeration enumeration = root.children();
        while (enumeration.hasMoreElements())  {
            SearchTreeNode child = (SearchTreeNode) enumeration.nextElement();
            rootCopy.add(getSubtreeCopy(child));   // recurse
        }
        return rootCopy;
    }

    /**
     * Add a child node at position i to the specified parent node.
     */
    @Override
    public void addNode(final SearchTreeNode parent, final SearchTreeNode child) {

        synchronized (root_) {
            parent.add(child);
        }
    }

    /**
     *  Show the specified list of pruned nodes under the specified parent.
     */
    @Override
    public void addPrunedNodes(final MoveList list, final SearchTreeNode parent,
                               final int i, final NodeAttributes attributes) {
        synchronized (root_) {
            // make a defensive copy of the list because we may modify it.
            final MoveList listCopy = new MoveList(list);
            parent.addPrunedChildNodes(listCopy, i, attributes);
        }
    }

    /**
     * Clear all nodes but the root.
     * @param p two player move to set the root to.
     */
    @Override
    public void resetTree(final TwoPlayerMove p) {

        synchronized (root_) {
                root_.removeAllChildren(); // clear it out
                p.setSelected(true);
                root_.setUserObject( p );
        }
    }
}

