/* Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers.ui

import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.checkers.{CheckersPiece, CheckersPieceType}
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer

import java.awt.{Color, Graphics2D, Point}

class CheckersPieceRenderer private () extends TwoPlayerPieceRenderer {

  override def renderForShow(g2: Graphics2D, pos: Point, piece: GamePiece, pieceSize: Int): Unit = {
    if (piece.getType == CheckersPiece.REGULAR_PIECE) {
      super.renderForShow(g2, pos, piece, pieceSize)
    } else {
      g2.setColor(getPieceColor(piece))
      g2.fillRect(pos.x, pos.y, pieceSize, pieceSize)

      if (piece.getTransparency == 0) {
        g2.setColor(Color.black)
        g2.drawRect(pos.x, pos.y, pieceSize + 1, pieceSize + 1)
      }
    }
  }
}

object CheckersPieceRenderer {
  private var renderer_ : TwoPlayerPieceRenderer = scala.compiletime.uninitialized

  def getRenderer: TwoPlayerPieceRenderer = {
    if (renderer_ == null) renderer_ = new CheckersPieceRenderer()
    renderer_
  }
}
