/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.renderers

import com.barrybecker4.game.common.board.{Board, BoardPosition, GamePiece}
import com.barrybecker4.game.common.ui.viewer.{GamePieceRenderer, GamePieceRenderer as GPR}
import com.barrybecker4.game.multiplayer.galactic.Planet
import com.barrybecker4.ui.gradient.RoundGradientPaint
import com.barrybecker4.ui.util.GUIUtil

import java.awt.{BasicStroke, Color, Font, Graphics2D}
import java.awt.geom.Ellipse2D

/**
 * A singleton class that takes a planet instance and renders it for the GalacticBoardViewer.
 *
 * @see com.barrybecker4.game.multiplayer.galactic.Galaxy
 * @author Barry Becker
 */
class PlanetRenderer private () extends GamePieceRenderer:

  override def getPieceSize(cellSize: Int, piece: GamePiece): Int =
    val planet = piece.asInstanceOf[Planet]
    val rad = planet.getRadius
    (cellSize * rad).toInt

  override def getPieceColor(piece: GamePiece): Color =
    piece.asInstanceOf[Planet].getColor

  override def render(g2: Graphics2D, position: BoardPosition, cellSize: Int, margin: Int, b: Board[?]): Unit =
    val planet = position.getPiece.asInstanceOf[Planet]
    if planet == null then return

    val pieceSize = getPieceSize(cellSize, planet)
    val pos = getPosition(position, cellSize, pieceSize, margin)
    val circle = Ellipse2D.Float(pos.x.toFloat, pos.y.toFloat, (pieceSize + 1).toFloat, (pieceSize + 1).toFloat)
    val hlOffset = (pieceSize / 2.3 + 0.5).toInt
    val c = getPieceColor(planet)

    val rgp = RoundGradientPaint(
      pos.x + hlOffset, pos.y + hlOffset, Color.WHITE, GPR.SPEC_HIGHLIGHT_RADIUS, c)

    g2.setPaint(rgp)
    g2.fill(circle)

    if planet.isUnderAttack then
      g2.setStroke(PlanetRenderer.ATTACK_STROKE)
      g2.setColor(PlanetRenderer.ATTACK_COLOR)
      g2.drawOval(pos.x, pos.y, pieceSize + 1, pieceSize + 1)

    if planet.isHighlighted then
      g2.setStroke(PlanetRenderer.HIGHLIGHT_STROKE)
      g2.setColor(PlanetRenderer.HIGHLIGHT_COLOR)
      g2.drawOval(pos.x, pos.y, pieceSize + 1, pieceSize + 1)

    val offset = if pieceSize < (0.6 * cellSize) then -1 else cellSize / 5
    if planet.getAnnotation != null then
      g2.setColor(Color.BLACK)
      g2.setFont(PlanetRenderer.PLANET_FONT)
      g2.drawString(planet.getAnnotation, pos.x + 2 * offset, pos.y + 3 * offset)

object PlanetRenderer:
  private var renderer_ : GamePieceRenderer = null

  private val ATTACK_COLOR = Color(255, 100, 0)
  private val ATTACK_STROKE = BasicStroke(3)
  private val HIGHLIGHT_COLOR = Color(245, 255, 0)
  private val HIGHLIGHT_STROKE = BasicStroke(2)
  private val PLANET_FONT = Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 11)

  def getRenderer: GamePieceRenderer =
    if renderer_ == null then renderer_ = PlanetRenderer()
    renderer_
