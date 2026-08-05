/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.ui

import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeBoardPosition}
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.blockade.board.path.Path

import java.awt.{BasicStroke, Color, Graphics2D}
import scala.jdk.CollectionConverters.*

/**
 * Render the paths on the blockade board.
 * Mostly for debugging purposes.
 *
 * @author Barry Becker
 */
object PathRenderer:
  private val PATH_WIDTH_RATIO = 0.16f
  private val POINT_WIDTH_RATIO = 0.22f
  private val ALPHA_CONST = 16

  /** offset the players path a little so they are not right on top of each other. */
  private val PLAYER1_PATH_OFFSET = 0.5f
  private val PLAYER2_PATH_OFFSET = 0.3f

  /**
   * Draw all the shortest paths from the specified position pos going to all the opponent homes.
   */
  def drawShortestPaths(g2: Graphics2D, pos: BlockadeBoardPosition, b: BlockadeBoard, cellSize: Int): Unit =
    val pathStroke = new BasicStroke(
      cellSize.toFloat * PATH_WIDTH_RATIO, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
    g2.setStroke(pathStroke)
    if pos == null || pos.isUnoccupied then return

    val paths = b.findShortestPaths(pos)
    val p1 = pos.getPiece.isOwnedByPlayer1
    var pathColor =
      if p1 then BlockadePieceRenderer.getRenderer.getPlayer1Color()
      else BlockadePieceRenderer.getRenderer.getPlayer2Color()
    pathColor = pathColor.darker()

    for path <- paths.asScala do
      assert(path.getLength >= 0)
      val alpha = Math.min(255, 5 + (19 * ALPHA_CONST) / Math.min(ALPHA_CONST, path.getLength + 1))
      val c = new Color(pathColor.getRed, pathColor.getGreen, pathColor.getBlue, alpha)
      g2.setColor(c)
      drawPath(g2, path, cellSize)

  private def drawPath(g2: Graphics2D, path: Path, cellSize: Int): Unit =
    val it = path.iterator
    val len = path.getLength + 1
    val x = new Array[Int](len)
    val y = new Array[Int](len)
    var ct = 0
    var m: BlockadeMove = null
    var offset = 0f
    while it.hasNext do
      m = it.next()
      offset = if m.isPlayer1 then PLAYER1_PATH_OFFSET else PLAYER2_PATH_OFFSET
      x(ct) = calcPosition(m.getFromCol, offset, cellSize)
      y(ct) = calcPosition(m.getFromRow, offset, cellSize)
      ct += 1
    if m != null then
      x(ct) = calcPosition(m.getToCol, offset, cellSize)
      y(ct) = calcPosition(m.getToRow, offset, cellSize)
      ct += 1
      g2.drawPolyline(x, y, ct)
      val diameter = (cellSize * POINT_WIDTH_RATIO).toInt
      val radius = diameter / 2
      for i <- 0 until ct do
        g2.drawOval(x(i) - radius, y(i) - radius, diameter, diameter)

  private def calcPosition(coord: Int, offset: Float, cellSize: Int): Int =
    ((coord.toFloat - offset) * cellSize).toInt
