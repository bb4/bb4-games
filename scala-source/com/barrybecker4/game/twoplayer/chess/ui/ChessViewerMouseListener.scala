/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess.ui

import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.game.twoplayer.checkers.ui.CheckersLikeViewerMouseListener
import com.barrybecker4.game.twoplayer.chess.{ChessBoard, ChessController, ChessMove, ChessPiece}

class ChessViewerMouseListener(viewer: GameBoardViewer[ChessMove, ChessBoard])
    extends CheckersLikeViewerMouseListener[ChessMove, ChessBoard](viewer) {

  override def getPossibleMoveList(position: BoardPosition): java.util.List[ChessMove] = {
    val board = viewer.getBoard.asInstanceOf[ChessBoard]
    val controller = viewer.getController.asInstanceOf[ChessController]

    val piece = position.getPiece.asInstanceOf[ChessPiece]
    val possibleMoveList = piece.findPossibleMoves(board, position.getRow, position.getCol, controller.getLastMove)
    controller.removeSelfCheckingMoves(possibleMoveList)
    possibleMoveList
  }

  override def customCheckFails(position: BoardPosition, destp: BoardPosition): Boolean = false
}
