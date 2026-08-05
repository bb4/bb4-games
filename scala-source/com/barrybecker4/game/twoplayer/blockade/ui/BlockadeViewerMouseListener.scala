/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.ui

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.{Board, BoardPosition, GamePiece}
import com.barrybecker4.game.common.ui.viewer.{GameBoardRenderer, GameBoardViewer, ViewerMouseListener}
import com.barrybecker4.game.twoplayer.blockade.BlockadeController
import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeBoardPosition}
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall

import javax.swing.JOptionPane
import java.awt.event.MouseEvent
import scala.jdk.CollectionConverters._

/**
 *  Mouse handling for blockade game.
 *
 *  @author Barry Becker
 */
class BlockadeViewerMouseListener(viewer: GameBoardViewer[BlockadeMove, BlockadeBoard])
  extends ViewerMouseListener[BlockadeMove, BlockadeBoard](viewer) {

  private var currentMove: BlockadeMove = null

  /** becomes true if the player has placed his pawn on an opponent base. */
  private var hasWon = false

  /** this becomes true when the player needs to place a wall instead of a piece during his turn.  */
  private var wallPlacingMode = false

  private val wallPlacer = new WallPlacer(viewer.getBoard.asInstanceOf[BlockadeBoard])

  reset()

  override def reset(): Unit = {
    currentMove = null
    hasWon = false
    wallPlacingMode = false
  }

  override def mousePressed(e: MouseEvent): Unit = {
    val bViewer = viewer.asInstanceOf[BlockadeBoardViewer]
    if (bViewer.get2PlayerController().isProcessing || wallPlacingMode)
      return

    val board = viewer.getBoard.asInstanceOf[BlockadeBoard]
    val loc = getRenderer.createLocation(e)
    val position = board.getPosition(loc)

    if (position == null || position.isUnoccupied) {
      return
    }
    val piece = position.getPiece
    if (bViewer.get2PlayerController().isPlayer1sTurn != piece.isOwnedByPlayer1)
      return // wrong players piece

    getRenderer.setDraggedPiece(position)
  }

  override def mouseReleased(event: MouseEvent): Unit = {
    val bViewer = viewer.asInstanceOf[BlockadeBoardViewer]
    val loc = getRenderer.createLocation(event)

    if (!wallPlacingMode) {
      val placed = placePiece(loc)
      if (!placed) {
        getRenderer.setDraggedPiece(null)
      }
      if (hasWon) {
        bViewer.continuePlay(currentMove)
      }
      return
    }

    val wallPlaced = placeWall(loc, currentMove)
    if (!wallPlaced) {
      return
    }

    bViewer.continuePlay(currentMove)
  }

  override def mouseDragged(e: MouseEvent): Unit = {
    val renderer = getRenderer
    val loc = renderer.createLocation(e)

    if (renderer.getDraggedShowPiece != null) {
      renderer.getDraggedShowPiece.setLocation(loc)
    }
    viewer.repaint()
  }

  override def mouseMoved(e: MouseEvent): Unit = {
    val bViewer = viewer.asInstanceOf[BlockadeBoardViewer]
    if (wallPlacingMode) {
      val board = viewer.getBoard.asInstanceOf[BlockadeBoard]
      val loc = getRenderer.createLocation(e)
      if (board.getPosition(loc) == null) {
        return // out of bounds
      }

      val positions =
        wallPlacer.getCellLocations(e.getX, e.getY, loc, getRenderer.getCellSize)

      getRenderer.asInstanceOf[BlockadeBoardRenderer].setDraggedWall(
        new BlockadeWall(positions(0), positions(1)))

      bViewer.repaint()
    }
  }

  private def placePiece(loc: Location): Boolean = {
    if (getRenderer.getDraggedPiece == null) {
      return false // nothing being dragged
    }

    val controller = viewer.getController.asInstanceOf[BlockadeController]
    val board = controller.getBoard
    val position =
      board.getPosition(getRenderer.getDraggedPiece.getLocation)

    getRenderer.setDraggedPiece(null)

    val m = checkAndGetValidMove(position, loc)
    if (m == null) {
      return false
    }

    currentMove = m
    GameContext.log(1, "legal human move :" + m.toString)
    position.getPiece.setTransparency(0.toShort)
    val isPlayer1 = position.getPiece.isOwnedByPlayer1
    val newPosition =
      board.getPosition(currentMove.getToRow, currentMove.getToCol)
    newPosition.setPiece(position.getPiece)
    position.setPiece(null)
    viewer.refresh()

    if (newPosition.isHomeBase(!isPlayer1)) {
      hasWon = true
      assert(isPlayer1 == (controller.getCurrentPlayer == controller.getPlayers.getFirstPlayer))
      controller.getCurrentPlayer.setWon(true)
    } else {
      wallPlacingMode = true
    }
    true
  }

  private def checkAndGetValidMove(origPosition: BlockadeBoardPosition, placedLocation: Location): BlockadeMove = {
    val controller = viewer.getController.asInstanceOf[BlockadeController]
    val board = controller.getBoard
    val possibleMoveList = controller.getPossibleMoveList(origPosition)

    val destpos = board.getPosition(placedLocation)
    if (customCheckFails(destpos)) {
      JOptionPane.showMessageDialog(viewer, GameContext.getLabel("ILLEGAL_MOVE"))
      return null
    }

    possibleMoveList.asScala.find { m =>
      m.getToRow == destpos.getRow && m.getToCol == destpos.getCol
    }.orNull
  }

  private def customCheckFails(destpos: BlockadeBoardPosition): Boolean =
    destpos == null || (destpos.isOccupied && !destpos.isHomeBase)

  private def placeWall(loc: Location, m: BlockadeMove): Boolean = {
    val bbRenderer = getRenderer.asInstanceOf[BlockadeBoardRenderer]
    val draggedWall = bbRenderer.getDraggedWall
    if (draggedWall == null)
      return false

    val board = viewer.getBoard.asInstanceOf[BlockadeBoard]
    val sError = board.checkLegalWallPlacement(draggedWall, loc)

    if (sError != null) {
      JOptionPane.showMessageDialog(viewer, sError)
      bbRenderer.setDraggedWall(null)
      false
    } else {
      wallPlacingMode = false
      m.setWall(draggedWall)
      bbRenderer.setDraggedWall(null)
      true
    }
  }
}
