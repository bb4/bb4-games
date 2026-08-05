/* Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers.ui

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer
import com.barrybecker4.game.twoplayer.checkers.{CheckersBoard, CheckersMove, CheckersSearchable, MoveGenerator}
import com.barrybecker4.game.twoplayer.common.TwoPlayerController

import scala.jdk.CollectionConverters._

class CheckersViewerMouseListener(viewer: GameBoardViewer[CheckersMove, CheckersBoard])
    extends CheckersLikeViewerMouseListener[CheckersMove, CheckersBoard](viewer) {

  override def getPossibleMoveList(position: BoardPosition): java.util.List[CheckersMove] = {
    val controller = viewer.getController.asInstanceOf[TwoPlayerController[CheckersMove, CheckersBoard]]
    val lastMove = controller.getLastMove
    val generator = new MoveGenerator(
      controller.getSearchable().asInstanceOf[CheckersSearchable],
      controller.getComputerWeights().getDefaultWeights
    )

    val possibleMoveList = new MoveList[CheckersMove]()
    generator.addMoves(position, lastMove, possibleMoveList)
    possibleMoveList
  }

  override def customCheckFails(position: BoardPosition, destp: BoardPosition): Boolean = {
    (position == null) || (destp == null) || destp.isOccupied
  }
}
