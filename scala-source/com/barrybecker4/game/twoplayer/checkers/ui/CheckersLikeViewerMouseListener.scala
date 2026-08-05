/* Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers.ui

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.{GameContext, GameController}
import com.barrybecker4.game.common.board.{Board, BoardPosition, GamePiece}
import com.barrybecker4.game.common.ui.viewer.{GameBoardViewer, ViewerMouseListener}
import com.barrybecker4.game.twoplayer.common.{TwoPlayerBoard, TwoPlayerController, TwoPlayerMove}
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer

import javax.swing.JOptionPane
import java.awt.event.MouseEvent
import scala.jdk.CollectionConverters._

abstract class CheckersLikeViewerMouseListener[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](
    viewer: GameBoardViewer[M, B]
) extends ViewerMouseListener[M, B](viewer) {

  override def mousePressed(e: MouseEvent): Unit = {
    val controller = viewer.getController.asInstanceOf[TwoPlayerController[M, B]]

    if (controller.isProcessing) return
    
    val loc = getRenderer.createLocation(e)
    val board = controller.getBoard
    val position = board.getPosition(loc)
    
    if ((position == null) || position.isUnoccupied) return
    
    val piece = position.getPiece
    if (controller.isPlayer1sTurn != piece.isOwnedByPlayer1) return

    getRenderer.setDraggedPiece(position)
  }

  override def mouseReleased(e: MouseEvent): Unit = {
    val twoPlayerViewer = viewer.asInstanceOf[AbstractTwoPlayerBoardViewer[M, B]]
    val controller = twoPlayerViewer.getController

    val loc = getRenderer.createLocation(e)

    if (getRenderer.getDraggedPiece == null) return

    val board = controller.getBoard
    val position = board.getPosition(getRenderer.getDraggedPiece.getLocation)

    getRenderer.setDraggedPiece(null)

    val destp = board.getPosition(loc)
    if (customCheckFails(position, destp)) {
      invalidMove()
      return
    }

    val possibleMoveList = getPossibleMoveList(position).asScala
    val foundMove = possibleMoveList.find(m => 
      m.getToRow == destp.getRow && m.getToCol == destp.getCol
    )

    if (foundMove.isEmpty) {
      invalidMove()
      return
    }

    if (!twoPlayerViewer.continuePlay(foundMove.get)) {
      twoPlayerViewer.showWinnerDialog()
    }
  }

  def customCheckFails(position: BoardPosition, destp: BoardPosition): Boolean

  override def mouseDragged(e: MouseEvent): Unit = {
    val loc = getRenderer.createLocation(e)
    if (getRenderer.getDraggedShowPiece != null) {
      getRenderer.getDraggedShowPiece.setLocation(loc)
    }
    viewer.refresh()
  }

  def getPossibleMoveList(position: BoardPosition): java.util.List[M]

  private def invalidMove(): Unit = {
    JOptionPane.showMessageDialog(viewer, GameContext.getLabel("ILLEGAL_MOVE"))
    viewer.refresh()
  }
}
