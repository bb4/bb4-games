/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.ui

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.board.{BoardPosition, GamePiece}
import com.barrybecker4.game.common.ui.viewer.{GameBoardViewer, ViewerMouseListener}
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.gomoku.{GoMokuBoard, GoMokuController}

import java.awt.event.MouseEvent

/**
 *  Mouse handling for checkers game.
 *
 *  @author Barry Becker
 */
class GoMokuViewerMouseListener(viewer: GameBoardViewer[TwoPlayerMove, GoMokuBoard])
  extends ViewerMouseListener[TwoPlayerMove, GoMokuBoard](viewer) {

  override def mousePressed(e: MouseEvent): Unit = {
    val viewer = this.viewer.asInstanceOf[GoMokuBoardViewer]
    val controller = viewer.getController.asInstanceOf[GoMokuController[?]]

    if (controller.isProcessing || controller.isDone) {
      return
    }
    val loc = getRenderer.createLocation(e)

    val board = controller.getBoard.asInstanceOf[GoMokuBoard]

    val p = board.getPosition(loc)
    if ((p == null) || !p.isUnoccupied)
      return

    val m = TwoPlayerMove.createMove(loc.row, loc.col, 0,
      new GamePiece(controller.isPlayer1sTurn))

    viewer.continuePlay(m)
  }
}
