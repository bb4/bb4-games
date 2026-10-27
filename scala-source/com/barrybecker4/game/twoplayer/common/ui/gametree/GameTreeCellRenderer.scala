/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.gametree

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer

import javax.swing.*
import javax.swing.tree.DefaultTreeCellRenderer
import java.awt.*

/**
 *  this class defines how to draw entrees in the game tree ui.
 *
 *  @author Barry Becker
 */
class GameTreeCellRenderer extends DefaultTreeCellRenderer:

  private var p1Color: Color = Color.green
  private var colormap: ColorMap = null

  private val SELECTED_COLOR = Color(200, 20, 0)

  private val pieceLabel = JLabel()
  private val cellPanel = JPanel()

  commonInit()

  /**
   * Constructor with piece renderer
   */
  def this(pieceRenderer: TwoPlayerPieceRenderer) =
    this()
    setColorMap(createColormap(pieceRenderer))

  /**
   * initialize the colormap used to color the game tree rows, nodes, and arcs.
   * We will use this colormap for both the text tree and the graphical
   * tree viewers so they have consistent coloring.
   * @return the colormap
   */
  private def createColormap(renderer: TwoPlayerPieceRenderer): ColorMap =
    val values = Array[Double](
      -SearchStrategy.WINNING_VALUE,
      -SearchStrategy.WINNING_VALUE / 20.0,
      0.0,
      SearchStrategy.WINNING_VALUE / 20.0,
      SearchStrategy.WINNING_VALUE
    )
    val colors = Array[Color](
      renderer.getPlayer2Color().darker,
      renderer.getPlayer2Color(),
      Color(160, 160, 160),
      renderer.getPlayer1Color(),
      renderer.getPlayer1Color().darker
    )
    ColorMap(values, colors)

  private def commonInit(): Unit =
    cellPanel.setBackground(UIManager.getColor("Tree.textBackground"))
    setOpaque(false)
    pieceLabel.setOpaque(true)
    pieceLabel.setText(" ")
    pieceLabel.setBackground(p1Color)

    cellPanel.setLayout(FlowLayout())
    cellPanel.add(pieceLabel)
    cellPanel.add(this)
    cellPanel.setMinimumSize(Dimension(300, 30))
    cellPanel.setVisible(true)

  def getColorMap: ColorMap = colormap

  def setColorMap(cmap: ColorMap): Unit =
    colormap = cmap

  override def getTreeCellRendererComponent(
      tree: JTree,
      value: Object,
      sel: Boolean,
      expanded: Boolean,
      leaf: Boolean,
      row: Int,
      hasFocus1: Boolean
  ): Component =
    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus1)

    val bg = getBGColor(value)
    val fg = getFGColor(value)

    setForeground(fg)
    setBackgroundSelectionColor(Color.orange)

    this

  private def getFGColor(value: Object): Color =
    val node = value.asInstanceOf[SearchTreeNode]
    val m = node.getUserObject.asInstanceOf[TwoPlayerMove]
    if m == null then
      return Color.gray

    if m.isSelected then
      return SELECTED_COLOR

    Color.black

  def getBGColor(value: Object): Color =
    val node = value.asInstanceOf[SearchTreeNode]
    val numChildren = node.getChildCount
    setText(getText + " kids=" + numChildren)
    val m = node.getUserObject.asInstanceOf[TwoPlayerMove]
    if m == null then return Color.gray // passing move?

    val c =
      if colormap != null then
        colormap.getColorForValue(m.getInheritedValue)
      else
        var value = (2.0 * Math.sqrt(Math.abs(m.getInheritedValue))).toInt
        if m.getInheritedValue < 0 then
          value = -value
        val v1 = 255 - Math.min(Math.max(value, 0), 255)
        val v2 = 255 - Math.min(Math.max(-value, 0), 255)
        val v3 = 255
        Color(v1, v2, v3)
    c
