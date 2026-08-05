/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.ui

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.{GameContext, GameController}
import com.barrybecker4.game.common.board.{BoardPosition, IRectangularBoard}
import com.barrybecker4.game.common.ui.viewer.{GameBoardViewer, GameBoardRenderer, ViewerMouseListener}
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer
import com.barrybecker4.game.twoplayer.gomoku.{GoMokuBoard, GoMokuController}

import java.awt.event.MouseEvent

/**
 *  Takes a GoMokuController as input and displays the
 *  current state of the GoMoku Game. The GoMokuController contains a GoMokuBoard
 *  which describes this state.
 *
 *  @author Barry Becker
 */
class GoMokuBoardViewer extends AbstractTwoPlayerBoardViewer[TwoPlayerMove, GoMokuBoard] {

  override def createController(): GameController[TwoPlayerMove, GoMokuBoard] = {
    new GoMokuController[GoMokuBoard]().asInstanceOf[GameController[TwoPlayerMove, GoMokuBoard]]
  }

  override def getBoardRenderer: GameBoardRenderer = {
    GoMokuBoardRenderer.getRenderer
  }

  override def createViewerMouseListener(): ViewerMouseListener[TwoPlayerMove, GoMokuBoard] = {
    new GoMokuViewerMouseListener(this)
  }

  /**
   * @return the tooltip for the panel given a mouse event.
   */
  override def getToolTipText(e: MouseEvent): String = {
    if (get2PlayerController().isProcessing || GameContext.getDebugMode < 1)
      return ""

    val loc = getBoardRenderer.createLocation(e)
    val sb = new StringBuilder("<html><font=-3>")

    val space = controller.getBoard.asInstanceOf[IRectangularBoard[_]].getPosition(loc)
    if (space != null && GameContext.getDebugMode > 0) {
      sb.append(space.getLocation)
    }
    else {
      sb.append(loc)
    }
    sb.append("</font></html>")
    sb.toString
  }
}
