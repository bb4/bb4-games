/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.viewer

import com.barrybecker4.game.common.Move
import com.barrybecker4.game.common.board.Board

import java.awt.event.{MouseEvent, MouseListener, MouseMotionListener}

/**
 *  Do nothing by default for all these. Subclasses must override some of them.
 *
 *  @author Barry Becker
 */
class ViewerMouseListener[M <: Move, B <: Board[M]](val viewer: GameBoardViewer[M, B])
  extends MouseListener with MouseMotionListener {

  def getRenderer: GameBoardRenderer = viewer.getBoardRenderer

  def reset(): Unit = {}

  /**
   * make the human move and show it on the screen,
   * then depending on the options, the computer may move.
   */
  override def mouseClicked(e: MouseEvent): Unit = {}
  override def mousePressed(e: MouseEvent): Unit = {}
  override def mouseReleased(e: MouseEvent): Unit = {}
  override def mouseEntered(e: MouseEvent): Unit = {}
  override def mouseExited(e: MouseEvent): Unit = {}

  // implement MouseMotionListener interface
  override def mouseDragged(e: MouseEvent): Unit = {}
  override def mouseMoved(e: MouseEvent): Unit = {}
}
