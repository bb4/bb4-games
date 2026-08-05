/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis

import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionSet}
import com.barrybecker4.game.twoplayer.go.board.elements.string.GoString

/**
 * Determines number of liberties on a string.
 *
 * @author Barry Becker
 */
class StringLibertyAnalyzer(val board: GoBoard, val string: GoString) {

  private var liberties_ : GoBoardPositionSet = scala.compiletime.uninitialized
  private var cacheValid = false

  /**
   * @return number of liberties that the string has
   */
  final def getLiberties: GoBoardPositionSet = {
    if (!cacheValid) {
      initializeLiberties()
    }
    liberties_
  }

  def invalidate(): Unit = {
    cacheValid = false
  }

  private def initializeLiberties(): Unit = {
    liberties_ = new GoBoardPositionSet()
    val members = string.getMembers

    for (stone <- members.toArray(new Array[GoBoardPosition](0))) {
      addLiberties(stone, board)
    }
    cacheValid = true
  }

  /**
   * only add liberties for this stone if they are not already in the set
   */
  private def addLiberties(stone: GoBoardPosition, board: GoBoard): Unit = {
    val r = stone.getRow
    val c = stone.getCol
    if (r > 1) {
      addLiberty(board.getPosition(r - 1, c))
    }
    if (r < board.getNumRows) {
      addLiberty(board.getPosition(r + 1, c))
    }
    if (c > 1) {
      addLiberty(board.getPosition(r, c - 1))
    }
    if (c < board.getNumCols) {
      addLiberty(board.getPosition(r, c + 1))
    }
  }

  /**
   * This assumes a HashSet will not allow you to add the same object twice (no dupes)
   * @param libertySpace the position of the liberty to add.
   */
  private def addLiberty(libertySpace: BoardPosition): Unit = {
    if (libertySpace.isUnoccupied)
      liberties_.add(libertySpace.asInstanceOf[GoBoardPosition])
  }
}
