package com.barrybecker4.game.twoplayer.hex.ui

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.board.{BoardPosition, GamePiece}
import com.barrybecker4.game.common.ui.viewer.ViewerMouseListener
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.hex.{HexBoard, HexController}

import java.awt.event.MouseEvent

/**
 *  Mouse handling for checkers game.
 */
class HexViewerMouseListener(hexViewer: HexBoardViewer) 
    extends ViewerMouseListener[TwoPlayerMove, HexBoard](hexViewer):

  override def mousePressed(e: MouseEvent): Unit =
    val viewer = hexViewer
    val controller = viewer.get2PlayerController().asInstanceOf[HexController]

    if controller.isProcessing || controller.isDone then
      return
    
    val loc = getRenderer.createLocation(e)
    val board = controller.getBoard

    val p = board.getPosition(loc)
    if p == null || !p.isUnoccupied then
      return

    val m = TwoPlayerMove.createMove(
      loc.row, loc.col, 0,
      GamePiece(controller.isPlayer1sTurn))

    viewer.continuePlay(m)
