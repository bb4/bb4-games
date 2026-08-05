/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.ui

import com.barrybecker4.game.common.board.{Board, BoardPosition, GamePiece}
import com.barrybecker4.game.common.ui.viewer.GamePieceRenderer
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBin
import com.barrybecker4.ui.util.GUIUtil
import java.awt.{BasicStroke, Color, Font, Graphics2D, Point, Stroke}
import java.awt.geom.Ellipse2D

/**
  * a singleton class that takes renders a mancala bin containing some number of stones.
  * @author Barry Becker
  */
class MancalaBinRenderer extends TwoPlayerPieceRenderer:

  /**
    * @return the game piece render color.
    */
  override def getPieceColor(piece: GamePiece): Color =
    MancalaBinRenderer.STONE_COLOR

  /**
    * @return color for annotation text (if any).
    */
  override def getTextColor(piece: GamePiece): Color =
    MancalaBinRenderer.STONE_COLOR

  /**
    * Draws the bin at this location with some representation for the stones in the bin.
    *
    * @param g2 graphics context
    * @param position the position of the piece to render
    */
  override def render(g2: Graphics2D, position: BoardPosition, cellSize: Int, margin: Int, b: Board[?]): Unit =
    val bin = position.getPiece.asInstanceOf[MancalaBin]
    // if there is no piece, then nothing to render
    if bin == null then
      return

    val pieceSize = getPieceSize(cellSize, bin)
    val pos = getPosition(position, cellSize, pieceSize, margin)
    renderForShow(g2, pos, bin, pieceSize)

  override def renderForShow(g2: Graphics2D, pos: Point, piece: GamePiece, pieceSize: Int): Unit =
    val bin = piece.asInstanceOf[MancalaBin]
    val yOffset = if bin.isHome then pieceSize >> 1 else 0
    val circle = new Ellipse2D.Float(pos.x, pos.y + yOffset, pieceSize + 1, pieceSize + 1)

    g2.setStroke(MancalaBinRenderer.BIN_STROKE)
    g2.setColor(if bin.isOwnedByPlayer1 then getPlayer1Color() else getPlayer2Color())
    g2.draw(circle)

    val offset = (0.22 * pieceSize).toInt
    g2.setColor(getTextColor(bin))

    val font = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, pieceSize / 2)
    g2.setFont(font)

    g2.drawString(bin.getNumStones.toString, pos.x + offset, pos.y + 3 * offset + yOffset)

object MancalaBinRenderer:
  private var renderer_ : GamePieceRenderer = _

  private val STONE_COLOR = new Color(20, 40, 80)
  private val BIN_STROKE: Stroke = new BasicStroke(2.0f)

  def getRenderer: GamePieceRenderer =
    if renderer_ == null then
      renderer_ = MancalaBinRenderer()
    renderer_
