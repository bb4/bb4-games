package com.barrybecker4.game.twoplayer.hex.ui

import com.barrybecker4.common.geometry.{ByteLocation, IntLocation, Location}
import com.barrybecker4.game.common.board.{Board, BoardPosition}
import com.barrybecker4.game.common.player.{Player, PlayerList}
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.ui.{TwoPlayerBoardRenderer, TwoPlayerPieceRenderer}

import java.awt.{BasicStroke, Color, Graphics2D, Point, RenderingHints, Stroke}
import java.awt.event.MouseEvent

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 */
class HexBoardRenderer extends TwoPlayerBoardRenderer:

  pieceRenderer_ = HexPieceRenderer.getRenderer

  override def getPreferredCellSize: Int = 16

  override def getMargin: Int = HexBoardRenderer.BOARD_MARGIN

  override def getBoardAspectRatio: Double = 1.4

  /**
   * whether to draw the pieces on cell centers or vertices (the way go requires).
   */
  override def offsetGrid(): Boolean = true

  /**
   * Constructs a new Location given a MouseEvent
   *
   * @param e event containing the coordinates.
   * @return new location based on mouse position.
   */
  override def createLocation(e: MouseEvent): Location =
    val size = Math.max(1, getCellSize)
    val row = ((e.getY - getMargin) / (HexUtil.ROOT3D2 * size) + 1).toInt
    val col = (e.getX - getMargin - ((row - 2) * cellSize) / 2) / size + 1
    ByteLocation(row.toByte, col.toByte)

  override def getPosition(coords: Location): IntLocation =
    val cellSizeD2 = cellSize / 2.0
    val y = getMargin + (coords.row * (cellSize - 0.5) * HexUtil.ROOT3D2).toInt
    val x = (getMargin + (coords.col - 1) * cellSize + (coords.row - 1) * cellSizeD2).toInt
    IntLocation(y, x)

  /**
   * draw the hex game grid
   */
  override def drawGrid(g2: Graphics2D, startPos: Int, rightEdgePos: Int, 
                                 bottomEdgePos: Int, start: Int, nrows1: Int, 
                                 ncols1: Int, gridOffset: Int): Unit =
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

    val cellSizeD2 = cellSize / 2

    for i <- start to nrows1 do
      for j <- start to ncols1 do
        g2.setColor(getGridColor)
        val xpos = getMargin + j * cellSize + gridOffset + (i - 1) * cellSizeD2
        val ypos = getMargin + (i * cellSize * HexUtil.ROOT3D2).toInt + gridOffset

        val point = Point(xpos, ypos)
        val rad = cellSizeD2 / HexUtil.ROOT3D2
        HexagonRenderer.drawHexagon(g2, point, rad, HexBoardRenderer.TILE_BORDER_COLOR, 
                                   HexBoardRenderer.TILE_STROKE)

        if i == start then
          drawEdge(g2, getPieceRenderer.getPlayer1Color(), point, rad, 210, 270, 330)
        if i == nrows1 then
          drawEdge(g2, getPieceRenderer.getPlayer1Color(), point, rad, 30, 90, 150)
        if j == start then
          drawEdge(g2, getPieceRenderer.getPlayer2Color(), point, rad, 90, 150, 210)
        if j == ncols1 then
          drawEdge(g2, getPieceRenderer.getPlayer2Color(), point, rad, 270, 330, 390)

  private def drawEdge(g2: Graphics2D, color: Color, point: Point, rad: Double, 
                      a1: Int, a2: Int, a3: Int): Unit =
    g2.setColor(color)
    val ang1 = HexUtil.rad(a1)
    val x1 = (point.getX + rad * Math.cos(ang1)).toInt
    val y1 = (point.getY + rad * Math.sin(ang1)).toInt
    val ang2 = HexUtil.rad(a2)
    val x2 = (point.getX + rad * Math.cos(ang2)).toInt
    val y2 = (point.getY + rad * Math.sin(ang2)).toInt
    val ang3 = HexUtil.rad(a3)
    val x3 = (point.getX + rad * Math.cos(ang3)).toInt
    val y3 = (point.getY + rad * Math.sin(ang3)).toInt
    g2.drawLine(x1, y1, x2, y2)
    g2.drawLine(x2, y2, x3, y3)

  override def getPieceRenderer: TwoPlayerPieceRenderer =
    pieceRenderer_.asInstanceOf[TwoPlayerPieceRenderer]

  /**
   * draw the background.
   */
  override def drawBackground(g: java.awt.Graphics, b: Board[?], startPos: Int, 
                                       rightEdgePos: Int, bottomEdgePos: Int, 
                                       panelWidth: Int, panelHeight: Int): Unit =
    super.drawBackground(g, b, startPos, rightEdgePos, bottomEdgePos, panelWidth, panelHeight)

  /**
   * first draw borders for the groups in the appropriate color, then draw the pieces for both players.
   */
  override def drawMarkers(board: Board[?], players: PlayerList, g2: Graphics2D): Unit =
    super.drawMarkers(board, players, g2)

  override def drawLastMoveMarker(g2: Graphics2D, player: Player, board: Board[?]): Unit =
    val last = board.getMoveList.getLastMove.asInstanceOf[TwoPlayerMove]
    if last != null then
      val cellSize = getCellSize
      val pos = getPosition(last.getToLocation)
      val rad = cellSize / 2.0
      HexagonRenderer.drawHexagon(g2,
        Point(pos.getX, (pos.getY - 0.65 * rad).toInt), rad,
        HexBoardRenderer.LAST_MOVE_INDICATOR_COLOR, 
        HexBoardRenderer.LAST_MOVE_INDICATOR_STROKE)

object HexBoardRenderer:
  private var renderer_ : GameBoardRenderer = null
  private val BOARD_MARGIN = 30
  private val TILE_BORDER_COLOR = Color(30, 30, 90, 50)
  private val TILE_STROKE: Stroke = BasicStroke(2)
  private val LAST_MOVE_INDICATOR_COLOR = Color(0, 255, 255, 80)
  private val LAST_MOVE_INDICATOR_STROKE: Stroke = BasicStroke(3.0f)

  /**
   * @return singleton instance
   */
  def getRenderer: GameBoardRenderer =
    if renderer_ == null then
      renderer_ = HexBoardRenderer()
    renderer_
