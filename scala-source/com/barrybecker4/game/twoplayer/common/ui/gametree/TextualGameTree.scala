/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.gametree

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode

import javax.swing.*
import javax.swing.event.TreeExpansionListener
import java.awt.*
import java.awt.event.MouseMotionListener

/**
 * Draw the entire game tree using a java tree control in a scrolled pane.
 * Contains 3 sub representations: a java text tree with nodes that can be expanded and collapsed,
 * game viewer, and the graphical GameTreeViewer at the bottom that renders a tree.
 *
 * @author Barry Becker
 */
final class TextualGameTree(root: SearchTreeNode, cellRenderer: GameTreeCellRenderer) extends JScrollPane:

  private val TREE_WIDTH = 420
  private val ROW_HEIGHT = 14

  private var tree: JTree = _
  private var cellRendererField: GameTreeCellRenderer = cellRenderer

  reset(root)

  def reset(root: SearchTreeNode): Unit =
    tree = createTree(root)
    this.setPreferredSize(Dimension(400, 600))
    this.setMinimumSize(Dimension(200, 120))
    this.setViewportView(tree)

  override def addMouseMotionListener(listener: MouseMotionListener): Unit =
    tree.addMouseMotionListener(listener)

  def addTreeExpansionListener(listener: TreeExpansionListener): Unit =
    tree.addTreeExpansionListener(listener)

  def expandRow(row: Int): Unit =
    tree.expandRow(row)
    tree.scrollRowToVisible(row)

  def getNumRows: Int =
    tree.getRowCount

  private def createTree(root: SearchTreeNode): JTree =
    val tree = JTree(root)
    try
      ToolTipManager.sharedInstance().registerComponent(tree)
      tree.setBackground(UIManager.getColor("Tree.textBackground"))
      tree.setCellRenderer(cellRendererField)
      tree.setShowsRootHandles(true)
      tree.putClientProperty("JTree.lineStyle", "Angled")
      tree.setRowHeight(ROW_HEIGHT)
    catch
      case e: ArrayIndexOutOfBoundsException =>
        GameContext.log(
          0,
          "Error: There was an ArayIndexOutOfBounds exception when creating a JTree from this root node: " + root
        )
        e.printStackTrace()
    tree
