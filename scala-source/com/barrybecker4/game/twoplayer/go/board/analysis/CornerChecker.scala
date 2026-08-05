/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis

import com.barrybecker4.game.common.board.BoardPosition

/**
 * Used to check to see if a position is in the corner of the board.
 * @author Barry Becker
 */
final class CornerChecker(numRows: Int, numCols: Int) {

  /**
   * Corner triples are the 3 points closest to a corner
   * @param position position to see if in corner of board.
   * @return true if the specified BoardPosition is on the corner of the board
   */
  def isCornerTriple(position: BoardPosition): Boolean = {
    isULCornerTriple(position) || isURCornerTriple(position) ||
      isLLCornerTriple(position) || isLRCornerTriple(position)
  }

  private def isULCornerTriple(position: BoardPosition): Boolean = {
    (position.getRow == 1 && position.getCol == 1) ||
      (position.getRow == 2 && position.getCol == 1) ||
      (position.getRow == 1 && position.getCol == 2)
  }

  private def isURCornerTriple(position: BoardPosition): Boolean = {
    (position.getRow == 1 && position.getCol == numCols) ||
      (position.getRow == 2 && position.getCol == numCols) ||
      (position.getRow == 1 && position.getCol == numCols - 1)
  }

  private def isLLCornerTriple(position: BoardPosition): Boolean = {
    (position.getRow == numRows && position.getCol == 1) ||
      (position.getRow == numRows && position.getCol == 2) ||
      (position.getRow == numRows - 1 && position.getCol == 1)
  }

  private def isLRCornerTriple(position: BoardPosition): Boolean = {
    (position.getRow == numRows && position.getCol == numCols) ||
      (position.getRow == numRows - 1 && position.getCol == numCols) ||
      (position.getRow == numRows && position.getCol == numCols - 1)
  }
}
