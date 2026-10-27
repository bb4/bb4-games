/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.ui

import com.barrybecker4.game.common.board.{Board, BoardPosition}
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer

import java.awt.{Color, Graphics2D}
import scala.compiletime.uninitialized

/**
 * A singleton class that takes a checkers piece and renders it for the BlockadeBoardViewer.
 *
 * @author Barry Becker
 */
class BlockadePieceRenderer private () extends TwoPlayerPieceRenderer:

  /**
   * this draws the actual piece.
   */
  override def render(g2: Graphics2D, position: BoardPosition, cellSize: Int, margin: Int, b: Board[?]): Unit =
    val piece = position.getPiece
    if piece != null then
      super.render(g2, position, cellSize, margin, b)
    val bpos = position.asInstanceOf[BlockadeBoardPosition]
    BlockadePieceRenderer.renderWallAtPosition(g2, bpos, cellSize, margin)

object BlockadePieceRenderer:
  private val EAST_WALL_COLOR = new Color(160, 110, 120)
  private val SOUTH_WALL_COLOR = new Color(110, 160, 120)
  private val WALL_WIDTH_FRAC = 0.15

  private var renderer_ : TwoPlayerPieceRenderer = uninitialized

  def getRenderer: TwoPlayerPieceRenderer =
    if renderer_ == null then
      renderer_ = new BlockadePieceRenderer()
    renderer_

  /**
   * @return true if at least one wall was rendered.
   */
  def renderWallAtPosition(g2: Graphics2D, bpos: BlockadeBoardPosition, cellSize: Int, margin: Int): Boolean =
    val xpos = margin + cellSize * bpos.getCol
    val ypos = margin + cellSize * bpos.getRow

    val wallWidthD2 = (WALL_WIDTH_FRAC * cellSize).toInt
    val wallThickness = (2.1 * wallWidthD2).toInt

    var drewWall = false
    if bpos.getEastWall != null then
      g2.setColor(EAST_WALL_COLOR)
      g2.fill3DRect(xpos - wallWidthD2, ypos - cellSize, wallThickness, cellSize, true)
      drewWall = true
    if bpos.getSouthWall != null then
      g2.setColor(SOUTH_WALL_COLOR)
      g2.fill3DRect(xpos - cellSize, ypos - wallWidthD2, cellSize, wallThickness, true)
      drewWall = true
    drewWall
