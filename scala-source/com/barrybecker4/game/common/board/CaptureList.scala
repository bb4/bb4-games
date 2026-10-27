/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.board

import java.util.LinkedList
import scala.jdk.CollectionConverters._

/**
 * This class represents a linked list of captured pieces.
 * It provides convenience methods for removing and restoring those
 * pieces to a game board.
 *
 * @see Board
 * @author Barry Becker
 */
class CaptureList extends LinkedList[BoardPosition] {

  def this(captureList: CaptureList) = {
    this()
    captureList.asScala.foreach(capture => add(capture.copy()))
  }

  def copy(): CaptureList = new CaptureList(this)

  def removeFromBoard(board: Board[?]): Unit = {
    modifyCaptures(board, remove = true)
  }

  def restoreOnBoard(board: Board[?]): Unit = {
    modifyCaptures(board, remove = false)
  }

  def modifyCaptures(board: Board[?], remove: Boolean): Unit = {
    this.asScala.foreach { capture =>
      val pos = board.getPosition(capture.getRow, capture.getCol)
      assert(pos != null, s"Captured position was null $capture")
      if (remove)
        pos.setPiece(null)
      else
        pos.setPiece(capture.getPiece.copy())
    }
  }

  def alreadyCaptured(p: BoardPosition): Boolean = {
    this.asScala.exists { capture =>
      capture.getRow == p.getRow &&
      capture.getCol == p.getCol &&
      capture.getPiece.getType == p.getPiece.getType
    }
  }

  override def toString: String = {
    var s = " These piece(s) were captured by this move:\n"
    this.asScala.foreach { p =>
      s += s"(${p.toString}),"
    }
    s
  }
}
