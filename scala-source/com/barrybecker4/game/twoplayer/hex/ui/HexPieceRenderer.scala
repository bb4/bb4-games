package com.barrybecker4.game.twoplayer.hex.ui

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.{Board, BoardPosition, GamePiece}
import com.barrybecker4.game.common.ui.viewer.GamePieceRenderer
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer

import java.awt.{BasicStroke, Color, Graphics2D, Point, Stroke}

/**
 * a singleton class that takes a game piece and renders it for the TwoPlayerBoardViewer.
 * We use a separate piece rendering class to avoid having ui in the piece class itself.
 * This allows us to more cleanly separate the client pieces from the server. *
 */
class HexPieceRenderer extends TwoPlayerPieceRenderer:

  override def getPosition(position: BoardPosition, cellSize: Int, pieceSize: Int, margin: Int): Point =
    val row = position.getRow - 1
    val col = position.getCol - 1

    position_.x = margin + (col * cellSize + (row * cellSize) / 2.0).toInt
    position_.y = margin + ((row + 0.5) * cellSize * HexUtil.ROOT3D2 + cellSize / 12).toInt
    position_

  /**
   * this draws the actual piece at this location (if there is one).
   *
   * @param g2 graphics context
   * @param position the position of the piece to render
   */
  override def render(g2: Graphics2D, position: BoardPosition, cellSize: Int, margin: Int, b: Board[?]): Unit =
    val piece = position.getPiece
    if piece == null then return

    val pieceSize = getPieceSize(cellSize, piece)
    val pos = getPosition(position, cellSize, pieceSize, margin)
    val rad = cellSize / 2

    HexagonRenderer.fillHexagon(g2, pos, rad, getPieceColor(piece))

    if piece.getTransparency == 0 && GameContext.getDebugMode == 0 then
      g2.setColor(Color.black)
      HexagonRenderer.drawHexagon(g2, pos, rad, Color.black, HexPieceRenderer.OUTLINE_STROKE)

    if piece.getAnnotation != null then
      val offset = (cellSize - pieceSize) >> 1
      g2.setColor(getTextColor(piece))
      g2.setFont(GamePieceRenderer.BASE_FONT)
      g2.drawString(piece.getAnnotation, pos.x + 2 * offset, pos.y + 3 * offset)

object HexPieceRenderer:
  private var renderer_ : HexPieceRenderer = null
  private val OUTLINE_STROKE: Stroke = BasicStroke(1.0f)

  def getRenderer: GamePieceRenderer =
    if renderer_ == null then
      renderer_ = HexPieceRenderer()
    renderer_
