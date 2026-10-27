/* Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.ui

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.common.ui.viewer.{GameBoardViewer, ViewerMouseListener}
import com.barrybecker4.game.twoplayer.mancala.MancalaController
import com.barrybecker4.game.twoplayer.mancala.board.{MancalaBin, MancalaBoard}
import com.barrybecker4.game.twoplayer.mancala.move.MancalaMove
import java.awt.event.MouseEvent

/**
  * Mouse handling for checkers game.
  *
  * @author Barry Becker
  */
class MancalaViewerMouseListener(viewer: GameBoardViewer[MancalaMove, MancalaBoard])
  extends ViewerMouseListener[MancalaMove, MancalaBoard](viewer):

  /**
    * When the mouse is clicked on a bin, the stones in that bin should be
    * picked up and seeded counter-clockwise into other bins (skipping the opponent home bin).
    * @param e mouse event
    */
  override def mousePressed(e: MouseEvent): Unit =
    val mancalaViewer = this.viewer.asInstanceOf[MancalaBoardViewer]
    val controller = mancalaViewer.getController.asInstanceOf[MancalaController]

    if controller.isProcessing || controller.isDone then
      return

    val loc = getRenderer.createLocation(e)
    val board = controller.getBoard

    // If the player click on a home base, an opponent bin, or some invalid location, just return
    val p = board.getPosition(loc)
    if p == null || p.isUnoccupied then
      return

    val bin = p.getPiece.asInstanceOf[MancalaBin]
    if bin.isHome
        || bin.getNumStones == 0
        || bin.isOwnedByPlayer1 != controller.isPlayer1sTurn then
      return

    val m = MancalaMove(controller.isPlayer1sTurn, loc, bin.getNumStones, 0)
    mancalaViewer.refresh()
    mancalaViewer.continuePlay(m)
