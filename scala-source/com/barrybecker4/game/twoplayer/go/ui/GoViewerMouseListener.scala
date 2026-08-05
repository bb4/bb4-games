/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.common.ui.viewer.{GameBoardViewer, ViewerMouseListener}
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer
import com.barrybecker4.game.twoplayer.go.GoController
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import com.barrybecker4.game.twoplayer.go.board.move.{GoMove, GoMoveGenerator}
import javax.swing.JOptionPane
import java.awt.event.MouseEvent

/**
 *  Mouse handling for Go game.
 *
 *  @author Barry Becker
 */
class GoViewerMouseListener(viewer: GameBoardViewer[GoMove, GoBoard]) 
  extends ViewerMouseListener[GoMove, GoBoard](viewer) {

  private var savedShowPiece_ : BoardPosition = scala.compiletime.uninitialized

  val controller = viewer.getController.asInstanceOf[GoController]
  if (!controller.getPlayers.allPlayersComputer) {
    getRenderer.setDraggedShowPiece(
      new GoBoardPosition(0, 0, null, new GoStone(controller.isPlayer1sTurn)))
    savedShowPiece_ = getRenderer.getDraggedShowPiece
  }

  /**
   *  mouseClicked requires both the mouse down and mouse up event to occur at the same location.
   */
  override def mousePressed(e: MouseEvent): Unit = {
    val controller = viewer.getController.asInstanceOf[GoController]
    if (controller.isProcessing) {
      return
    }
    val loc = getRenderer.createLocation(e)
    val board = controller.getBoard
    val player1sTurn = controller.isPlayer1sTurn

    GameContext.log(3, "BoardViewer: mousePressed: player1sTurn()=" + player1sTurn)

    val m = new GoMove(loc, 0, new GoStone(player1sTurn))

    val stone = board.getPosition(loc).asInstanceOf[GoBoardPosition]

    if (stone != null) {
      processStonePlacement(loc, m, stone)
    }
  }

  /**
   * Place the stone on the board.
   */
  private def processStonePlacement(loc: Location, m: GoMove, stone: GoBoardPosition): Unit = {
    val controller = viewer.getController.asInstanceOf[GoController]
    val board = controller.getBoard
    val player1sTurn = controller.isPlayer1sTurn

    if (stone.isOccupied) {
      JOptionPane.showMessageDialog(null, GameContext.getLabel("CANT_PLAY_ON_STONE"))
      GameContext.log(0, "BoardViewer: There is already a stone there: " + stone)
      return
    }
    if (GoMoveGenerator.isTakeBack(m.getToRow, m.getToCol, controller.getLastMove, board)) {
      JOptionPane.showMessageDialog(null, GameContext.getLabel("NO_TAKEBACKS"))
      return
    }
    assert(!stone.isVisited)

    if (m.isSuicidal(board)) {
      JOptionPane.showMessageDialog(null, GameContext.getLabel("SUICIDAL"))
      GameContext.log(1, "BoardViewer: That move is suicidal (and hence illegal): " + stone)
      return
    }

    if (!viewer.asInstanceOf[AbstractTwoPlayerBoardViewer[GoMove, GoBoard]].continuePlay(m)) {
      getRenderer.setDraggedShowPiece(null)
    } else if (controller.getPlayers.allPlayersHuman) {
      getRenderer.setDraggedShowPiece(
        new GoBoardPosition(loc.row, loc.col, null, new GoStone(!player1sTurn)))
    }
  }

  /**
   * if we are in wallPlacingMode, then we show the wall being dragged around.
   * When the player clicks the wall is irrevocably placed.
   */
  override def mouseMoved(e: MouseEvent): Unit = {
    val controller = viewer.getController.asInstanceOf[GoController]
    if (controller.isProcessing) {
      return
    }
    val loc = getRenderer.createLocation(e)

    if (getRenderer.getDraggedShowPiece != null) {
      getRenderer.getDraggedShowPiece.setLocation(loc)
    }
    viewer.refresh()
  }

  override def mouseEntered(e: MouseEvent): Unit = {
    getRenderer.setDraggedShowPiece(savedShowPiece_)
  }

  override def mouseExited(e: MouseEvent): Unit = {
    getRenderer.setDraggedShowPiece(null)
  }
}
