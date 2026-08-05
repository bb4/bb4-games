/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui.rendering

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.Board
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerBoardRenderer
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.group.{GroupAnalyzer, GroupAnalyzerMap}
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition
import com.barrybecker4.ui.util.GUIUtil
import javax.swing.ImageIcon
import java.awt.{Color, Font, Graphics, Graphics2D}
import java.awt.geom.Rectangle2D
import scala.jdk.CollectionConverters._

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * @author Barry Becker
 */
class GoBoardRenderer private() extends TwoPlayerBoardRenderer {
  pieceRenderer_ = GoStoneRenderer.getRenderer

  override def getPreferredCellSize: Int = 16

  override def getMargin: Int = GoBoardRenderer.BOARD_MARGIN

  override def offsetGrid(): Boolean = true

  private def int2char(i: Int): String = {
    val c = ('a' + i).toChar
    c.toString
  }

  override def drawGrid(g2: Graphics2D, startPos: Int, rightEdgePos: Int, bottomEdgePos: Int, start: Int,
                        nrows1: Int, ncols1: Int, gridOffset: Int): Unit = {
    super.drawGrid(g2, startPos, rightEdgePos, bottomEdgePos, start, nrows1, ncols1, gridOffset)

    if (GameContext.getDebugMode == 0) return

    g2.setColor(GoBoardRenderer.TICK_LABEL_COLOR)
    g2.setFont(GoBoardRenderer.TICK_LABEL_FONT)
    val metrics = GoBoardRenderer.TICK_LABEL_FONT.getMaxCharBounds(g2.getFontRenderContext)
    val textHt = Math.ceil(metrics.getHeight).toInt
    val appxHt1 = (textHt / 2.8).toInt

    for (i <- start to nrows1) {
      val ypos = getMargin + appxHt1 + i * cellSize + gridOffset
      g2.drawString(int2char(i), 1, ypos)
    }
    for (i <- start to ncols1) {
      val xpos = getMargin - 1 + i * cellSize + gridOffset
      g2.drawString(int2char(i), xpos, 3 + getMargin - appxHt1)
    }
  }

  override def drawBackground(g: Graphics, b: Board[?], startPos: Int, rightEdgePos: Int, bottomEdgePos: Int,
                               panelWidth: Int, panelHeight: Int): Unit = {
    super.drawBackground(g, b, startPos, rightEdgePos, bottomEdgePos, panelWidth, panelHeight)
    val t = (cellSize / 2.0f).toInt
    g.drawImage(GoBoardRenderer.woodGrainImage_.getImage, startPos - t, startPos - t,
      rightEdgePos - startPos + 2 * t, bottomEdgePos - startPos + 2 * t, null)
  }

  override def drawMarkers(board: Board[?], players: PlayerList, g2: Graphics2D): Unit = {
    val b = board.asInstanceOf[GoBoard]
    val starpoints = b.getHandicapPositions
    val it = starpoints.iterator
    g2.setColor(Color.black)
    val rad = cellSize.toDouble / 21.0 + 0.46
    while (it.hasNext) {
      val p = it.next.asInstanceOf[GoBoardPosition]
      g2.fillOval(getMargin + (cellSize * (p.getCol - 0.505) - rad).toInt,
        getMargin + (cellSize * (p.getRow - 0.505) - rad).toInt,
        (2.0 * rad + 1.7).toInt, (2.0 * rad + 1.7).toInt)
    }

    if (GameContext.getDebugMode > 0) {
      val groupRenderer = new GoGroupRenderer(b, GoBoardRenderer.COLORMAP, cellSize.toFloat, getMargin, g2)
      val map = new GroupAnalyzerMap()
      for (group <- b.getGroups.asScala) {
        val analyzer = new GroupAnalyzer(group, map)
        groupRenderer.drawGroupDecoration(analyzer)
      }
    }

    super.drawMarkers(board, players, g2)
  }
}

object GoBoardRenderer {
  private var renderer_ : GameBoardRenderer = _

  val COLORMAP: ColorMap = new GoColorMap()
  private val TICK_LABEL_COLOR = new Color(10, 10, 10)
  private val BOARD_MARGIN = 12
  private val TICK_LABEL_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 11)
  private val woodGrainImage_ = GUIUtil.getIcon(GameContext.GAME_RESOURCE_ROOT + "twoplayer/go/ui/images/goBoard1.gif")

  def getRenderer: GameBoardRenderer = {
    if (renderer_ == null) renderer_ = new GoBoardRenderer()
    renderer_
  }
}
