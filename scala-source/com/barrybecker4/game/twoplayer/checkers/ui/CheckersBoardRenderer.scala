/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers.ui

import com.barrybecker4.game.common.board.Board
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer
import com.barrybecker4.game.twoplayer.checkers.CheckersMove
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerBoardRenderer

import java.awt.{Color, Graphics, Graphics2D}

class CheckersBoardRenderer extends TwoPlayerBoardRenderer {
  pieceRenderer_ = CheckersPieceRenderer.getRenderer

  override def getPreferredCellSize: Int = 34

  override def drawBackground(
      g: Graphics,
      b: Board[?],
      startPos: Int,
      rightEdgePos: Int,
      bottomEdgePos: Int,
      panelWidth: Int,
      panelHeight: Int
  ): Unit = {
    super.drawBackground(g, b, startPos, rightEdgePos, bottomEdgePos, panelWidth, panelHeight)

    val nrows = b.getNumRows
    val ncols = b.getNumCols

    for (i <- 0 until nrows; j <- 0 until ncols) {
      g.setColor(if ((i + j) % 2 == 0) CheckersBoardRenderer.BLACK_SQUARE_COLOR 
                 else CheckersBoardRenderer.RED_SQUARE_COLOR)
      val ioff = getMargin + cellSize * i
      val joff = getMargin + cellSize * j
      g.fillRect(ioff, joff, cellSize, cellSize)
    }
  }

  override def drawGrid(
      g2: Graphics2D,
      startPos: Int,
      rightEdgePos: Int,
      bottomEdgePos: Int,
      start: Int,
      nrows1: Int,
      ncols1: Int,
      gridOffset: Int
  ): Unit = {}
}

object CheckersBoardRenderer {
  private var renderer_ : GameBoardRenderer = scala.compiletime.uninitialized
  private val BLACK_SQUARE_COLOR = new Color(2, 2, 2, 80)
  private val RED_SQUARE_COLOR = new Color(250, 0, 0, 80)

  def getRenderer: GameBoardRenderer = {
    if (renderer_ == null) renderer_ = new CheckersBoardRenderer()
    renderer_
  }
}
