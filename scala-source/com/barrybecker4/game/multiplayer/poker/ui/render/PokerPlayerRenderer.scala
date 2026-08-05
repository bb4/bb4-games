/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.render

import com.barrybecker4.game.common.board.{Board, BoardPosition, GamePiece}
import com.barrybecker4.game.common.ui.viewer.GamePieceRenderer
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerBoardRenderer
import com.barrybecker4.ui.gradient.RoundGradientPaint
import com.barrybecker4.ui.util.GUIUtil
import java.awt.{Color, Font, Graphics2D, Point}
import java.awt.geom.Ellipse2D

class PokerPlayerRenderer extends GamePieceRenderer {
  import PokerPlayerRenderer.*

  private val handRenderer = HandRenderer()
  private val chipRenderer = ChipRenderer()

  override def getPieceSize(cellSize: Int, piece: GamePiece): Int = 
    (0.85f * cellSize * 2).toInt

  override def getPieceColor(piece: GamePiece): Color = {
    val marker = piece.asInstanceOf[PokerPlayerMarker]
    marker.getColor
  }

  override def render(g2: Graphics2D, position: BoardPosition, cellSize: Int, margin: Int, b: Board[_]): Unit = {
    val playerMarker = position.getPiece.asInstanceOf[PokerPlayerMarker]
    if (playerMarker == null) return

    val pieceSize = getPieceSize(cellSize, playerMarker)
    val pos = getPosition(position, cellSize, pieceSize, margin)
    drawMarker(g2, playerMarker, pieceSize, pos)

    drawLabel(g2, cellSize, playerMarker, pieceSize, pos)

    val player = playerMarker.getOwner.asInstanceOf[PokerPlayer]
    drawHand(g2, position, cellSize, player, pieceSize, pos)

    chipRenderer.render(g2, position.getLocation, player.getCash, cellSize)
  }

  private def drawHand(g2: Graphics2D, position: BoardPosition, cellSize: Int, player: PokerPlayer,
                       pieceSize: Int, pos: Point): Unit = {
    if (!player.hasFolded) {
      handRenderer.render(g2, position.getLocation, player.getHand, cellSize)
    } else {
      g2.setColor(FOLDED_COLOR)
      g2.fillRect(pos.x - cellSize, pos.y - cellSize, 6 * pieceSize, 6 * pieceSize)
    }
  }

  private def drawLabel(g2: Graphics2D, cellSize: Int, playerMarker: PokerPlayerMarker, 
                       pieceSize: Int, pos: Point): Unit = {
    val font = BASE_FONT.deriveFont(Font.BOLD, cellSize.toFloat / 24 * 8)
    val offset = if (pieceSize < (0.6 * cellSize)) -1 else cellSize / 5
    if (playerMarker.getAnnotation != null) {
      g2.setColor(Color.black)
      g2.setFont(font)
      g2.drawString(playerMarker.getAnnotation, pos.x - 2 * cellSize - 2, pos.y - 3 * offset)
    }
  }

  private def drawMarker(g2: Graphics2D, playerMarker: PokerPlayerMarker, pieceSize: Int, pos: Point): Unit = {
    val circle = Ellipse2D.Float(pos.x, pos.y, pieceSize + 1, pieceSize + 1)
    val hlOffset = (pieceSize / 2.3 + 0.5).toInt
    val c = getPieceColor(playerMarker)

    // TODO: Fix RoundGradientPaint - Scala can't match constructor signature  
    // val rgp = new RoundGradientPaint(pos.x + hlOffset, pos.y + hlOffset, Color.white, SPEC_HIGHLIGHT_RADIUS, c)
    // g2.setPaint(rgp)
    g2.setPaint(c)
    g2.fill(circle)
    if (playerMarker.isHighlighted) {
      g2.setColor(HIGHLIGHT_COLOR)
      g2.fillOval(pos.x, pos.y, 3 * pieceSize, 3 * pieceSize)
    }
  }
}

object PokerPlayerRenderer {
  val FONT_SIZE = 6
  val POKER_FONT = Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, FONT_SIZE)
  val BASE_FONT = Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 12)
  val SPEC_HIGHLIGHT_RADIUS = 20

  private var renderer_ : GamePieceRenderer = null

  val HIGHLIGHT_COLOR = Color(245, 255, 0, 50)
  private val FOLDED_COLOR = Color(50, 50, 55, 30)

  def getRenderer: GamePieceRenderer = {
    if (renderer_ == null)
      renderer_ = PokerPlayerRenderer()
    renderer_
  }
}
