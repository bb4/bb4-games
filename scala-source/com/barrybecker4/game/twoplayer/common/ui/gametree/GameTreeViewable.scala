/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.gametree

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.search.tree.IGameTreeViewable
import com.barrybecker4.game.twoplayer.common.search.tree.NodeAttributes
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode

import java.util.Enumeration

/**
 * Responsible for handling events related to modifying the nodes in the game tree.
 *
 * Most operations lock on the tree instance because we want to be sure that we do not modify the
 * tree while we are making a copy of it for use in rendering it.
 *
 * @author Barry Becker
 */
final class GameTreeViewable(m: TwoPlayerMove) extends IGameTreeViewable:

  private val root: SearchTreeNode = SearchTreeNode(m, NodeAttributes())

  /**
   * @return the root node so that it can be modified.
   */
  override def getRootNode: SearchTreeNode = root

  /**
   * @return the root node of a deeply copied tree (so we do not need to worry about it changing.
   */
  def getTreeCopy: SearchTreeNode =
    var rootCopy: SearchTreeNode = null
    root.synchronized {
      rootCopy = getSubtreeCopy(root)
    }
    rootCopy

  /** @return a copy of the subtree rooted at root */
  private def getSubtreeCopy(rootNode: SearchTreeNode): SearchTreeNode =
    val rootCopy = rootNode.clone().asInstanceOf[SearchTreeNode]
    val enumeration = rootNode.children()
    while enumeration.hasMoreElements do
      val child = enumeration.nextElement().asInstanceOf[SearchTreeNode]
      rootCopy.add(getSubtreeCopy(child)) // recurse
    rootCopy

  /**
   * Add a child node at position i to the specified parent node.
   */
  override def addNode(parent: SearchTreeNode, child: SearchTreeNode): Unit =
    root.synchronized {
      parent.add(child)
    }

  /**
   *  Show the specified list of pruned nodes under the specified parent.
   */
  def addPrunedNodes(
      list: MoveList[?],
      parent: SearchTreeNode,
      i: Int,
      attributes: NodeAttributes
  ): Unit =
    root.synchronized {
      // make a defensive copy of the list because we may modify it.
      val listCopy = MoveList(list)
      parent.addPrunedChildNodes(listCopy, i, attributes)
    }

  /**
   * Clear all nodes but the root.
   * @param p two player move to set the root to.
   */
  override def resetTree(p: TwoPlayerMove): Unit =
    root.synchronized {
      root.removeAllChildren() // clear it out
      p.setSelected(true)
      root.setUserObject(p)
    }
