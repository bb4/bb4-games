/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui

import com.barrybecker4.common.geometry.{IntLocation, Location}
import com.barrybecker4.game.common.board.Board
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove

import java.awt.Graphics2D

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
abstract class TwoPlayerBoardRenderer extends GameBoardRenderer:

  protected def getPosition(coords: Location): IntLocation =
    new IntLocation(
      getMargin + (coords.row - 1) * cellSize + 1,
      getMargin + (coords.col - 1) * cellSize + 1
    )

  override def drawLastMoveMarker(g2: Graphics2D, player: Player, board: Board[?]): Unit =
    val last = board.getMoveList.getLastMove.asInstanceOf[TwoPlayerMove]

    // this draws a small indicator on the last move to show where it was played
    if last != null then
      g2.setColor(GameBoardRenderer.LAST_MOVE_INDICATOR_COLOR)
      g2.setStroke(GameBoardRenderer.LAST_MOVE_INDICATOR_STROKE)
      val cellSize = getCellSize
      val pos = getPosition(last.getToLocation)
      g2.drawOval(pos.getX, pos.getY, cellSize - 2, cellSize - 2)
