/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess.ui

import com.barrybecker4.game.common.board.Board
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer
import com.barrybecker4.game.twoplayer.checkers.ui.CheckersBoardRenderer
import com.barrybecker4.game.twoplayer.chess.ChessMove

import java.awt.Graphics2D

class ChessBoardRenderer extends CheckersBoardRenderer {
  pieceRenderer_ = ChessPieceRenderer.getRenderer
  
  override def drawLastMoveMarker(g2: Graphics2D, player: Player, board: Board[?]): Unit = {}
}

object ChessBoardRenderer {
  private var renderer_ : GameBoardRenderer = _

  def getRenderer: GameBoardRenderer = {
    if (renderer_ == null) renderer_ = new ChessBoardRenderer()
    renderer_
  }
}
