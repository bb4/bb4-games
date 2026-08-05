/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.gametree

import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer
import com.barrybecker4.ui.util.ColorMap

import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.border.BevelBorder
import javax.swing.tree.TreePath
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D

/**
 *  This class takes a root node and displays the portion of the tree visible in the text tree control.
 *  An actual graphics tree is shown to represent the game search tree.
 *
 *  @author Barry Becker
 */
final class GameTreeViewer(root: SearchTreeNode, cmap: ColorMap, pieceRenderer: TwoPlayerPieceRenderer) extends JPanel:

  private val MARGIN = 8
  private val BRUSH_HIGHLIGHT_STROKE = BasicStroke(3.7f)
  private val serialVersionUID = 0L

  /** most recently highlighted path. */
  private var oldHighlightPath: TreePath = _

  private var gameTreeRenderer: GameTreeRenderer = GameTreeRenderer(root, cmap, pieceRenderer)

  setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED))

  def setRoot(root: SearchTreeNode): Unit =
    oldHighlightPath = null
    gameTreeRenderer.setRoot(root)

  /**
   * Draw the currently visible game tree.
   */
  def refresh(): Unit =
    repaint()
    oldHighlightPath = null

  /**
   * perform a sequence of moves from somewhere in the game
   * not necessarily the start. We do, however,
   * assume the moves are valid. It is for display purposes only.
   *
   * @param path path corresponding to a the list of moves to make
   */
  def highlightPath(path: TreePath): Unit =
    // unhighlight the old path and highlight the new one without redrawing the whole tree.
    val g2 = getGraphics.asInstanceOf[Graphics2D]
    g2.setXORMode(Color.WHITE)
    g2.setStroke(BRUSH_HIGHLIGHT_STROKE)

    // first unhighlight the old path
    if oldHighlightPath != null then
      gameTreeRenderer.highlight(oldHighlightPath, g2)

    gameTreeRenderer.highlight(path, g2)
    g2.setPaintMode()
    oldHighlightPath = path

  /**
   * This renders the current tree to the canvas
   */
  override def paintComponent(g: Graphics): Unit =
    super.paintComponents(g)
    val g2 = g.asInstanceOf[Graphics2D]

    gameTreeRenderer.drawBackground(g2, getWidth, getHeight)
    val width = getWidth - 2 * MARGIN
    val height = getHeight - 2 * MARGIN
    gameTreeRenderer.paintTree(g2, width, height)
