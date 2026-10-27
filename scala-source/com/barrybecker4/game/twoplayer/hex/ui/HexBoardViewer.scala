package com.barrybecker4.game.twoplayer.hex.ui

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer
import com.barrybecker4.game.twoplayer.hex.{HexBoard, HexController}

import java.awt.event.MouseEvent

/**
 *  Takes a HexController as input and displays the
 *  current state of the TicTactToe Game.
 */
class HexBoardViewer() extends AbstractTwoPlayerBoardViewer[TwoPlayerMove, HexBoard]:

  override def createController(): HexController =
    HexController()

  override def getBoardRenderer: GameBoardRenderer =
    HexBoardRenderer.getRenderer

  override def createViewerMouseListener(): HexViewerMouseListener =
    HexViewerMouseListener(this)

  /**
   * @return the tooltip for the panel given a mouse event.
   */
  override def getToolTipText(e: MouseEvent): String =
    if get2PlayerController().isProcessing || GameContext.getDebugMode < 1 then
      return ""

    val loc = getBoardRenderer.createLocation(e)
    val sb = StringBuilder("<html><font=-3>")

    val b = get2PlayerController().getBoard.asInstanceOf[HexBoard]
    val space = b.getPosition(loc)
    if space != null && GameContext.getDebugMode > 0 then
      sb.append(space.getLocation)
    else
      sb.append(loc)
    
    sb.append("</font></html>")
    sb.toString()
