/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui.gametree

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode
import com.barrybecker4.game.twoplayer.common.ui.gametree.GameTreeCellRenderer
import com.barrybecker4.game.twoplayer.go.ui.rendering.GoStoneRenderer
import javax.swing.JTree
import java.awt.{Color, Component, Dimension, Font, Graphics, Graphics2D}

/**
 *  This class defines how to draw entrees in the textual game tree ui.
 *
 *  @author Barry Becker
 */
final class GoTreeCellRenderer extends GameTreeCellRenderer {

  private var node_ : SearchTreeNode = _

  setColorMap(GoTreeCellRenderer.COLORMAP)
  this.setPreferredSize(new Dimension(1000, 16))

  override def getTreeCellRendererComponent(
    tree: JTree, value: Object,
    sel: Boolean, expanded: Boolean,
    leaf: Boolean, row: Int, hasFocus1: Boolean): Component = {

    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus1)

    node_ = value.asInstanceOf[SearchTreeNode]

    val bg = getBGColor(value)

    this.setBackgroundNonSelectionColor(bg)
    this.setBackgroundSelectionColor(Color.orange)

    this
  }

  override def getBGColor(value: Object): Color = GoTreeCellRenderer.ROW_BG_COLOR

  /**
   * Paints the value.  The background is filled based on what was selected.
   */
  override def paint(g: Graphics): Unit = {
    drawCellBackground(g)

    val g2 = g.asInstanceOf[Graphics2D]
    val move = node_.getUserObject.asInstanceOf[TwoPlayerMove]
    if (move == null) {
      return
    }

    drawStoneIcon(move.isPlayer1, g2)
    drawMoveText(move, g2)
  }

  /**
   * Draw a nice icon to show who's move it corresponds to
   */
  private def drawStoneIcon(isPlayer1: Boolean, g2: Graphics2D): Unit = {
    if (isPlayer1) {
      g2.drawImage(GoStoneRenderer.BLACK_STONE_IMG.getImage, 1, 0, GoTreeCellRenderer.STONE_IMG_SIZE, 
        GoTreeCellRenderer.STONE_IMG_SIZE, null)
    } else {
      g2.drawImage(GoStoneRenderer.WHITE_STONE_IMG.getImage, 1, 0, GoTreeCellRenderer.STONE_IMG_SIZE, 
        GoTreeCellRenderer.STONE_IMG_SIZE, null)
    }
  }

  /**
   * Draw the inherited value, the base value, and the move(with its attributes) to the right of the icon.
   */
  private def drawMoveText(move: TwoPlayerMove, g2: Graphics2D): Unit = {
    val inheritedValue = move.getInheritedValue
    val value = move.getValue

    g2.setFont(GoTreeCellRenderer.FONT)

    var end = GoTreeCellRenderer.TEXT_MARGIN + GoTreeCellRenderer.STONE_IMG_SIZE
    end = drawStringValue("inhrtd=", inheritedValue, end, g2)
    end = drawStringValue("val=", value, end, g2)

    val bldr = new StringBuilder()
    if (node_.isPruned) {
      bldr.append(" *PRUNED")
    } else {
      val numKids = if (node_.getChildMoves == null) 0 else node_.getChildMoves.length
      bldr.append(" kids=").append(numKids)
      bldr.append(node_.toString)
    }

    g2.drawString(bldr.toString, end, 8)
  }

  private def drawStringValue(prefix: String, value: Int, offset: Int, g2: Graphics2D): Int = {
    val c = getColorMap.getColorForValue(value)
    val text = prefix + FormatUtil.formatNumber(value)
    g2.setColor(c)
    val width = g2.getFontMetrics.getStringBounds(text, g2).getWidth.toInt
    g2.fillRect(offset, 1, width + 2 * GoTreeCellRenderer.TEXT_MARGIN, 10)
    g2.setColor(this.getForeground)
    g2.drawString(text, offset + GoTreeCellRenderer.TEXT_MARGIN, 9)
    offset + width + 3 * GoTreeCellRenderer.TEXT_MARGIN
  }

  private def drawCellBackground(g: Graphics): Unit = {
    var bColor: Color = null

    if (selected) {
      bColor = getBackgroundSelectionColor
    } else {
      bColor = getBackgroundNonSelectionColor
      if (bColor == null) bColor = getBackground
    }

    if (bColor != null) {
      val imageOffset = 0
      g.setColor(bColor)
      if (getComponentOrientation.isLeftToRight) {
        g.fillRect(imageOffset, 0, getWidth - imageOffset, getHeight)
      } else {
        g.fillRect(0, 0, getWidth - imageOffset, getHeight)
      }
    }
  }
}

object GoTreeCellRenderer {
  private val ROW_BG_COLOR = new Color(220, 210, 240)

  private val STONE_IMG_SIZE = 11
  private val TEXT_MARGIN = 4
  private val FONT = new Font("Sans Serif", Font.PLAIN, 10)

  private val COLORMAP = new GoTreeColorMap()
}
