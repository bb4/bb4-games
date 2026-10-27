/* Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.ui

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.{BoardPosition, IRectangularBoard}
import com.barrybecker4.game.common.ui.viewer.{GameBoardRenderer, ViewerMouseListener}
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer
import com.barrybecker4.game.twoplayer.mancala.{MancalaController, board, move}
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard
import com.barrybecker4.game.twoplayer.mancala.move.MancalaMove
import java.awt.event.MouseEvent

/**
  * Takes a MancalaController as input and displays the
  * current state of the Mancala Game. The MancalaController contains a MancalaBoard
  * which describes this state.
  *
  * @author Barry Becker
  */
class MancalaBoardViewer extends AbstractTwoPlayerBoardViewer[MancalaMove, MancalaBoard]:

  override def createController(): MancalaController =
    MancalaController()

  override def getBoardRenderer: GameBoardRenderer =
    MancalaBoardRenderer.getRenderer

  override def createViewerMouseListener() =
    MancalaViewerMouseListener(this).asInstanceOf

  /**
    * @return the tooltip for the panel given a mouse event.
    */
  override def getToolTipText(e: MouseEvent): String =
    if get2PlayerController().isProcessing || GameContext.getDebugMode < 1 then
      return ""

    val loc = getBoardRenderer.createLocation(e)
    val sb = new StringBuilder("<html><font=-3>")

    val space = controller.getBoard.asInstanceOf[IRectangularBoard[?]].getPosition(loc)
    if space != null && GameContext.getDebugMode > 0 then
      sb.append(space.getLocation)
    else
      sb.append(loc)
    sb.append("</font></html>")
    sb.toString
