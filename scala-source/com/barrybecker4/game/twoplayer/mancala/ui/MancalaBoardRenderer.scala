/* Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.ui

import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.game.common.board.{Board, BoardPosition}
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerBoardRenderer
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBin
import java.awt.Graphics2D

/**
  * Singleton class that takes a game board and renders it for the GameBoardViewer.
  * Having the board renderer separate from the viewer helps to separate out the rendering logic
  * from other features of the GameBoardViewer.
  *
  * @author Barry Becker
  */
class MancalaBoardRenderer extends TwoPlayerBoardRenderer:

  pieceRenderer_ = MancalaBinRenderer.getRenderer

  /**
    * no grid by default for mancala.
    */
  override def drawGrid(
      g2: Graphics2D,
      startPos: Int,
      rightEdgePos: Int,
      bottomEdgePos: Int,
      start: Int,
      nrows1: Int,
      ncols1: Int,
      gridOffset: Int
  ): Unit = ()

  override def drawLastMoveMarker(g2: Graphics2D, player: Player, board: Board[?]): Unit =
    val last = board.getMoveList.getLastMove.asInstanceOf[TwoPlayerMove]

    // this draws a small indicator on the last move to show where it was played
    if last != null then
      g2.setColor(GameBoardRenderer.LAST_MOVE_INDICATOR_COLOR)
      g2.setStroke(GameBoardRenderer.LAST_MOVE_INDICATOR_STROKE)
      val cellSize = getCellSize
      val pos = getPosition(last.getToLocation)
      val bin = board.getPosition(last.getToLocation).getPiece.asInstanceOf[MancalaBin]

      val yOffset = if bin.isHome then cellSize >> 1 else 0

      val x = pos.getX
      val y = pos.getY + yOffset
      g2.drawOval(x, y, cellSize - 2, cellSize - 2)

object MancalaBoardRenderer:
  private var renderer_ : GameBoardRenderer = _

  def getRenderer: GameBoardRenderer =
    if renderer_ == null then
      renderer_ = MancalaBoardRenderer()
    renderer_
