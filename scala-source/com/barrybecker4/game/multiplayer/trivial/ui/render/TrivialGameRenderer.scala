/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.ui.render

import com.barrybecker4.game.common.board.Board
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer
import com.barrybecker4.game.multiplayer.common.{MultiGamePlayer, MultiPlayerMarker}
import com.barrybecker4.game.multiplayer.common.ui.MultiGameBoardRenderer

import java.awt.{Graphics, Graphics2D}

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
class TrivialGameRenderer private () extends MultiGameBoardRenderer:

  pieceRenderer_ = TrivialPlayerRenderer.getRenderer

  /**
   * draw a grid of some sort if there is one.
   * none by default.
   */
  override def drawGrid(g2: Graphics2D, startPos: Int, rightEdgePos: Int, 
                                   bottomEdgePos: Int, start: Int, nrows1: Int, 
                                   ncols1: Int, gridOffset: Int): Unit = ()

  override def drawBackground(g: Graphics, board: Board[?], startPos: Int, 
                                        rightEdgePos: Int, bottomEdgePos: Int,
                                        panelWidth: Int, panelHeight: Int): Unit =
    super.drawBackground(g, board, startPos, rightEdgePos, bottomEdgePos, panelWidth, panelHeight)
    drawTable(g, board)

  /**
   * Draw some indication of where the last move was made.
   * The default is to show nothing.  Draw a background circle for the player whose turn it is
   */
  override def drawLastMoveMarker(g2: Graphics2D, currentPlayer: Player, board: Board[?]): Unit =
    val m = currentPlayer.getActualPlayer.asInstanceOf[MultiGamePlayer].getPiece
    assert(m != null)
    assert(g2 != null)
    assert(m.getLocation != null)

    g2.setColor(TrivialPlayerRenderer.HIGHLIGHT_COLOR)
    g2.fillOval(cellSize * (m.getLocation.col - 2),
                cellSize * (m.getLocation.row - 2),
                10 * cellSize, 10 * cellSize)

object TrivialGameRenderer:
  private var renderer: GameBoardRenderer = scala.compiletime.uninitialized

  def getRenderer: GameBoardRenderer =
    if renderer == null then
      renderer = new TrivialGameRenderer()
    renderer
