/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.ui.render

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.board.{Board, BoardPosition, GamePiece}
import com.barrybecker4.game.common.ui.viewer.{GameBoardRenderer, GamePieceRenderer}
import com.barrybecker4.game.multiplayer.common.MultiPlayerMarker
import com.barrybecker4.game.multiplayer.trivial.player.TrivialPlayer
import com.barrybecker4.ui.gradient.RoundGradientPaint

import java.awt.{Color, Font, Graphics2D, Point}
import java.awt.geom.Ellipse2D

/**
 * A singleton class that takes a trivial player and renders it for the TrivialGameViewer.
 *
 * @author Barry Becker
 */
class TrivialPlayerRenderer private () extends GamePieceRenderer:

  override def getPieceSize(cellSize: Int, piece: GamePiece): Int =
    (0.85f * cellSize * 2).toInt

  override def getPieceColor(piece: GamePiece): Color =
    val marker = piece.asInstanceOf[MultiPlayerMarker]
    marker.getColor

  /**
   * this draws the actual card at this location (if there is one).
   *
   * @param g2 graphics context
   * @param position the position of the piece to render
   */
  override def render(g2: Graphics2D, position: BoardPosition, cellSize: Int, margin: Int, b: Board[?]): Unit =
    val playerMarker = position.getPiece.asInstanceOf[MultiPlayerMarker]
    if playerMarker == null then
      return

    val pieceSize = getPieceSize(cellSize, playerMarker)
    val pos = getPosition(position, cellSize, pieceSize, margin)
    val circle = new Ellipse2D.Float(pos.x.toFloat, pos.y.toFloat, (pieceSize + 1).toFloat, (pieceSize + 1).toFloat)
    val hlOffset = (pieceSize / 2.3 + 0.5).toInt
    val c = getPieceColor(playerMarker)

    val rgp = new RoundGradientPaint(
      pos.x + hlOffset, pos.y + hlOffset, Color.white, GamePieceRenderer.SPEC_HIGHLIGHT_RADIUS, c)

    g2.setPaint(rgp)
    g2.fill(circle)

    if playerMarker.isHighlighted then
      g2.setColor(TrivialPlayerRenderer.HIGHLIGHT_COLOR)
      g2.fillOval(pos.x, pos.y, 3 * pieceSize, 3 * pieceSize)

    drawOptionalAnnotation(g2, cellSize, playerMarker, pieceSize, pos)

    val p = playerMarker.getOwner.asInstanceOf[TrivialPlayer]
    if p.isRevealed then
      renderValue(g2, position.getLocation, p.getValue, cellSize)

  private def drawOptionalAnnotation(g2: Graphics2D, cellSize: Int,
                                      playerMarker: MultiPlayerMarker, pieceSize: Int, pos: Point): Unit =
    val font = getAnnotationFont(cellSize.toFloat)

    val offset = if pieceSize < (0.6 * cellSize) then -1 else cellSize / 5
    if playerMarker.getAnnotation != null then
      g2.setColor(Color.black)
      g2.setFont(font)
      g2.drawString(playerMarker.getAnnotation, pos.x - cellSize, pos.y - 3 * offset)

  private def getAnnotationFont(cellSize: Float): Font =
    val fontSize = cellSize / GameBoardRenderer.MINIMUM_CELL_SIZE.toFloat * 8
    GamePieceRenderer.BASE_FONT.deriveFont(Font.BOLD, fontSize)

  /**
   * Draw the trivial hand (the cards are all face up or all face down)
   */
  private def renderValue(g2: Graphics2D, location: Location, value: Int, cellSize: Int): Unit =
    val x = (location.col - 1) * cellSize
    val y = ((location.row + 1.6) * cellSize).toInt
    val cardArc = (cellSize * TrivialPlayerRenderer.CARD_ARC).toInt

    g2.setColor(TrivialPlayerRenderer.CARD_BG_COLOR)

    val w = (cellSize * (TrivialPlayerRenderer.CARD_WIDTH + TrivialPlayerRenderer.CARD_ARC)).toInt
    val h = (cellSize * TrivialPlayerRenderer.CARD_HEIGHT).toInt
    g2.fillRoundRect(x, y, w, h, cardArc, cardArc)
    g2.setColor(TrivialPlayerRenderer.BLACK_COLOR)
    g2.drawRoundRect(x, y, w, h, cardArc, cardArc)

    g2.setColor(TrivialPlayerRenderer.BLACK_COLOR)
    val font = GamePieceRenderer.BASE_FONT.deriveFont(Font.BOLD, cellSize.toFloat / GameBoardRenderer.MINIMUM_CELL_SIZE.toFloat * 9)
    g2.setFont(font)
    g2.drawString(value.toString, x + cardArc, y + 4 * cardArc)

object TrivialPlayerRenderer:
  private var renderer: GamePieceRenderer = scala.compiletime.uninitialized

  val HIGHLIGHT_COLOR = new Color(245, 255, 0, 50)

  private val CARD_WIDTH = 2.0f
  private val CARD_HEIGHT = 3.3f
  private val CARD_ARC = 0.28f
  private val CARD_BG_COLOR = Color.white
  private val BLACK_COLOR = Color.black

  def getRenderer: GamePieceRenderer =
    if renderer == null then
      renderer = new TrivialPlayerRenderer()
    renderer
