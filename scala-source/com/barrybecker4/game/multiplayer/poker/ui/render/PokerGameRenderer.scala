/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.render

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.common.board.Board
import com.barrybecker4.game.common.player.{Player, PlayerList}
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer
import com.barrybecker4.game.multiplayer.common.ui.MultiGameBoardRenderer
import com.barrybecker4.game.multiplayer.poker.model.PokerTable
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer
import java.awt.{Graphics, Graphics2D}

class PokerGameRenderer extends MultiGameBoardRenderer {
  pieceRenderer_ = PokerPlayerRenderer.getRenderer

  override def drawGrid(g2: Graphics2D, startPos: Int, rightEdgePos: Int, bottomEdgePos: Int, 
                       start: Int, nrows1: Int, ncols1: Int, gridOffset: Int): Unit = {}

  override def drawBackground(g: Graphics, board: Board[?], startPos: Int, rightEdgePos: Int, 
                             bottomEdgePos: Int, panelWidth: Int, panelHeight: Int): Unit = {
    super.drawBackground(g, board, startPos, rightEdgePos, bottomEdgePos, panelWidth, panelHeight)
    drawTable(g, board)
  }

  override def drawMarkers(board: Board[?], players: PlayerList, g2: Graphics2D): Unit = {
    val loc = ByteLocation((board.getNumRows >> 1).toByte, ((board.getNumCols >> 1) - 3).toByte)
    val pot = board.asInstanceOf[PokerTable].getPotValue
    ChipRenderer().render(g2, loc, pot, this.getCellSize)

    super.drawMarkers(board, players, g2)
  }

  override def drawLastMoveMarker(g2: Graphics2D, currentPlayer: Player, board: Board[?]): Unit = {
    val player = currentPlayer.getActualPlayer.asInstanceOf[PokerPlayer]
    val m = player.getPiece
    g2.setColor(PokerPlayerRenderer.HIGHLIGHT_COLOR)
    g2.fillOval(cellSize * (m.getLocation.col - 2),
                cellSize * (m.getLocation.row - 2),
                10 * cellSize, 10 * cellSize)
  }
}

object PokerGameRenderer {
  private var renderer_ : GameBoardRenderer = null

  def getRenderer: GameBoardRenderer = {
    if (renderer_ == null)
      renderer_ = PokerGameRenderer()
    renderer_
  }
}
