/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess.ui

import com.barrybecker4.game.common.GameController
import com.barrybecker4.game.common.ui.viewer.{GameBoardRenderer, ViewerMouseListener}
import com.barrybecker4.game.twoplayer.chess.{ChessBoard, ChessController, ChessMove}
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer

/**
 * This class takes a ChessController as input and displays the
 * Current state of the Chess Game. The ChessController contains a ChessBoard
 * which describes this state.
 */
class ChessBoardViewer extends AbstractTwoPlayerBoardViewer[ChessMove, ChessBoard] {

  override def createController(): GameController[ChessMove, ChessBoard] =
    new ChessController()

  override def getBoardRenderer: GameBoardRenderer =
    ChessBoardRenderer.getRenderer

  override def createViewerMouseListener(): ViewerMouseListener[ChessMove, ChessBoard] =
    new ChessViewerMouseListener(this)

  override def showLastMove(onDone: () => Unit): Unit =
    get2PlayerController().getLastMove match
      case Some(move: ChessMove) =>
        animateComputerMove(move.fromLocation, move.toLocation, move.captureList, move.getPiece, onDone)
      case _ =>
        refresh()
        onDone()
}
