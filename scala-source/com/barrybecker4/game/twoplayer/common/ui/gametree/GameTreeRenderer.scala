/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.gametree

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer
import com.barrybecker4.ui.util.ColorMap

import javax.swing.tree.TreePath
import java.awt.BasicStroke
import java.awt.Color
import java.awt.GradientPaint
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Point
import java.awt.RenderingHints
import java.util.Enumeration
import java.util.LinkedList
import java.util.List

/**
 *  This class takes a root node and displays the portion of the tree visible in the text tree control.
 *  An actual graphics tree is shown to represent the game search tree.
 *
 *  @author Barry Becker
 */
final class GameTreeRenderer(root: SearchTreeNode, cmap: ColorMap, pieceRenderer: TwoPlayerPieceRenderer):

  private val BACKGROUND_COLOR = Color.white
  private val BACKGROUND_BAND_OPACITY = 100
  private val MARGIN = 8
  private val PRUNE_SPACING = 12

  private val THIN_STROKE = BasicStroke(0.4f)
  private val HIGHLIGHT_STROKE = BasicStroke(1.4f)

  /** circle around highlighted node   */
  private val HL_NODE_RADIUS = 3
  private val HL_NODE_DIAMETER = 8

  private var backgroundColor: Color = BACKGROUND_COLOR
  private var colormap: ColorMap = cmap
  private var pieceRendererField: TwoPlayerPieceRenderer = pieceRenderer

  private var rootNode: SearchTreeNode = _
  private var depth: Int = 0

  /** sum of the number of descendants of all nodes at the level */
  private var totalAtLevel: Array[Int] = _

  private var width: Int = _
  private var levelHeight: Int = _

  setRoot(root)

  def setRoot(root: SearchTreeNode): Unit =
    rootNode = root
    // this is very expensive because it traverses the whole tree.
    // it might be simpler to just use the depth from the controller, but that would
    // not account for those times when we drill deeper using quiescent search.
    depth = rootNode.getDepth

    totalAtLevel = new Array[Int](depth + 2)

    if rootNode.getUserObject != null then
      initializeTreeStats(rootNode, 0)

  /**
   * This renders the current tree to the canvas
   */
  def paintTree(g: Graphics, widthParam: Int, height: Int): Unit =
    val g2 = g.asInstanceOf[Graphics2D]

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

    width = widthParam
    drawBackground(g2, width, height)

    if rootNode == null || rootNode.getUserObject == null then return

    if depth == 0 then
      // tree not ready to be painted.
      return
    levelHeight = height / depth

    g2.setStroke(THIN_STROKE)
    val numNodes = drawTree(rootNode, g2)

    g2.setColor(Color.BLACK)
    g2.drawString("Nodes = " + FormatUtil.formatNumber(numNodes), MARGIN + width - 120, MARGIN + 10)

  /**
   * @param c  the new color of the tree.
   */
  def setBackground(c: Color): Unit =
    backgroundColor = c

  /**
   * Draw the nodes and arcs in the game tree.
   * It can get quite huge.
   * @return the number of nodes that were drawn.
   */
  private def drawTree(root: SearchTreeNode, g2: Graphics2D): Long =
    var oldDepth = 0
    var depthVar = 0
    val offsetAtLevel = new Array[Int](depth + 2)
    var numNodes = 0L

    drawNode(root, 0, 0, g2)
    val q: List[SearchTreeNode] = new LinkedList[SearchTreeNode]()
    q.add(root)

    while q.size > 0 do
      val p = q.remove(0)
      numNodes += 1
      depthVar = p.getLevel
      // draw the arc and child root for each child c of p
      if depthVar > oldDepth then
        oldDepth = depthVar
        offsetAtLevel(depthVar) = 0
      val enumeration = p.children()
      while enumeration.hasMoreElements do
        val c = enumeration.nextElement().asInstanceOf[SearchTreeNode]
        drawArc(p, c, depthVar, offsetAtLevel(depthVar), offsetAtLevel(depthVar + 1), g2)
        drawNode(c, depthVar + 1, offsetAtLevel(depthVar + 1), g2)
        offsetAtLevel(depthVar + 1) += c.getSpaceAllocation
        q.add(c)
      offsetAtLevel(depthVar) += p.getSpaceAllocation
    numNodes

  private def initializeTreeStats(node: SearchTreeNode, depthParam: Int): Unit =
    if node.isLeaf then
      if node.isPruned then
        // give pruned nodes a little more space
        val pruneSpace =
          node.getSpaceAllocation + Math.max(1, 4 * PRUNE_SPACING - PRUNE_SPACING * depthParam)
        node.setSpaceAllocation(pruneSpace)
      else
        node.setSpaceAllocation(1) // never 0
      totalAtLevel(depthParam) += node.getSpaceAllocation
      return

    val it = node.children()
    while it.hasMoreElements do
      val child = it.nextElement().asInstanceOf[SearchTreeNode]
      initializeTreeStats(child, depthParam + 1)
      node.setSpaceAllocation(node.getSpaceAllocation + child.getSpaceAllocation)
    // count the node as a descendant
    node.setSpaceAllocation(node.getSpaceAllocation + 1)
    totalAtLevel(depthParam) += node.getSpaceAllocation

  /** highlight a path */
  def highlight(path: TreePath, g2: Graphics2D): Unit =
    val pathNodes = path.getPath
    val lastNode = pathNodes(0).asInstanceOf[SearchTreeNode]
    val diameter = HL_NODE_DIAMETER
    var lastLoc = lastNode.getPosition
    g2.drawOval(lastLoc.x - HL_NODE_RADIUS, lastLoc.y - HL_NODE_RADIUS, diameter, diameter)
    for i <- 1 until pathNodes.length do
      val node = pathNodes(i).asInstanceOf[SearchTreeNode]
      val m = node.getUserObject.asInstanceOf[TwoPlayerMove]
      g2.setColor(colormap.getColorForValue(m.getInheritedValue))

      val nodeLoc = node.getPosition
      g2.drawLine(lastLoc.x, lastLoc.y, nodeLoc.x, nodeLoc.y)
      g2.setColor(colormap.getColorForValue(m.getValue))
      g2.drawOval(nodeLoc.x - HL_NODE_RADIUS, nodeLoc.y - HL_NODE_RADIUS, diameter, diameter)
      lastLoc = nodeLoc

  /**
   * Draw one of the game tree nodes
   */
  private def drawNode(node: SearchTreeNode, depthParam: Int, offset: Int, g2: Graphics2D): Unit =
    val m = node.getUserObject.asInstanceOf[TwoPlayerMove]
    g2.setColor(colormap.getColorForValue(m.getValue))
    val x = MARGIN + (width * (offset + node.getSpaceAllocation / 2.0) / totalAtLevel(depthParam)).toInt
    val y = MARGIN + depthParam * levelHeight
    node.setLocation(x, y)

    val nodeWidth = if m.isSelected then 3 else 2
    g2.fillRect(x, y, nodeWidth, 3)

    g2.setColor(Color.black)
    if depthParam < 2 then
      g2.drawString(m.getValue + "  " + m.getInheritedValue, x - 15, y + 5)

    if node.isPruned then
      // draw a marker to show that it has been pruned
      g2.fillRect(x, y + 3, nodeWidth, 1)

  /**
   * Draw a line/arc connecting a parent and child node.
   */
  private def drawArc(
      parent: SearchTreeNode,
      child: SearchTreeNode,
      depthParam: Int,
      offset1: Int,
      offset2: Int,
      g2: Graphics2D
  ): Unit =
    val m = child.getUserObject.asInstanceOf[TwoPlayerMove]
    val highlighted = m.isSelected && parent.getUserObject.asInstanceOf[TwoPlayerMove].isSelected
    if highlighted then
      g2.setStroke(HIGHLIGHT_STROKE)
    g2.setColor(colormap.getColorForValue(m.getInheritedValue))
    g2.drawLine(
      MARGIN + (width * (offset1 + parent.getSpaceAllocation / 2.0) / totalAtLevel(depthParam)).toInt,
      MARGIN + depthParam * levelHeight,
      MARGIN + (width * (offset2 + child.getSpaceAllocation / 2.0) / totalAtLevel(depthParam + 1)).toInt,
      MARGIN + (depthParam + 1) * levelHeight
    )
    if highlighted then
      g2.setStroke(THIN_STROKE)

  /**
   * draw colored bands to give an indication of who is moving on each ply.
   * @param g2 graphics
   */
  def drawBackground(g2: Graphics2D, widthParam: Int, height: Int): Unit =
    g2.setColor(backgroundColor)
    g2.fillRect(0, 0, widthParam, height)

    val c = pieceRendererField.getPlayer1Color()
    val p1Color = Color(c.getRed, c.getGreen, c.getBlue, BACKGROUND_BAND_OPACITY)
    val c2 = pieceRendererField.getPlayer2Color()
    val p2Color = Color(c2.getRed, c2.getGreen, c2.getBlue, BACKGROUND_BAND_OPACITY)
    val cycle = true
    val gp =
      GradientPaint(0, levelHeight / 4.0f, p2Color, 0, 5.0f * levelHeight / 4.0f, p1Color, cycle)
    g2.setPaint(gp)

    g2.fillRect(0, 0, widthParam, height)
