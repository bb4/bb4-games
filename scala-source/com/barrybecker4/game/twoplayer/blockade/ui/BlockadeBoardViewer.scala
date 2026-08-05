/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.ui

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.{GameContext, GameController}
import com.barrybecker4.game.common.ui.viewer.{GameBoardRenderer, ViewerMouseListener}
import com.barrybecker4.game.twoplayer.blockade.BlockadeController
import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeBoardPosition}
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer

import java.awt.event.MouseEvent

/**
 * This class takes a BlockadeController as input and displays the
 * Current state of the Blockade Game. The BlockadeController contains a BlockadeBoard
 * which describes this state.
 *
 * @author Barry Becker
 */
class BlockadeBoardViewer extends AbstractTwoPlayerBoardViewer[BlockadeMove, BlockadeBoard] {

  override def createController(): GameController[BlockadeMove, BlockadeBoard] = 
    new BlockadeController()

  override def getBoardRenderer: GameBoardRenderer = 
    BlockadeBoardRenderer.getRenderer

  override def createViewerMouseListener(): ViewerMouseListener[BlockadeMove, BlockadeBoard] = 
    new BlockadeViewerMouseListener(this)

  /**
   * @return the tooltip for the panel given a mouse event.
   */
  override def getToolTipText(e: MouseEvent): String = {
    val loc = getBoardRenderer.createLocation(e)
    val sb = new StringBuilder("<html><font=-3>")

    val space = controller.getBoard.asInstanceOf[BlockadeBoard].getPosition(loc)
    if (space != null && GameContext.getDebugMode > 0) {
      sb.append(space.toString)
      sb.append(if (space.isVisited) ":Visited" else "")
      sb.append(if (space.isHomeBase) 
        (if (space.isHomeBase(true)) " P1 Home" else "p2 Home") 
        else "")
    }
    sb.append("</font></html>")
    sb.toString
  }
}
