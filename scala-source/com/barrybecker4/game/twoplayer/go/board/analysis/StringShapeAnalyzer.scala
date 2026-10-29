/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis

import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}

/**
 * Checks the shape of the string just formed on the board
 * to determine if it is good or bad shape.
 */
class StringShapeAnalyzer(val board_ : GoBoard) {

  private val EMPTY_TRIANGLE_PENALTY = 1
  private val CLUMP_OF_FOUR_PENALTY = 1

  /**
   * @return a measure of the severity - a number corresponding to the number of clumps
   *  of 4 or empty triangles that this stone is connected to.
   * returns 0 if does not form bad shape at all. Large numbers indicate worse shape.
   * Possible bad shapes are :
   *  SHAPE_EMPTY_TRIANGLE :
   *   X -
   *   X X
   * SHAPE_CLUMP_OF_4 :
   *   X X
   *   X X
   */
  def formsBadShape(position: GoBoardPosition): Int = {
    val stone = position.getPiece.asInstanceOf[GoStone]
    val r = position.getRow
    val c = position.getCol

    checkBadShape(stone, r, c,  1, -1) +
    checkBadShape(stone, r, c, -1, -1) +
    checkBadShape(stone, r, c,  1,  1) +
    checkBadShape(stone, r, c, -1,  1)
  }

  /**
   *  There are 3 empty triangle cases based on where the stone is:
   *       adj1    diag   X       XX     X
   *       space adj2    XX     X      XX
   *
   * @param stone that forms part of the empty triangle
   * @return the amount to penalize for bad shape.
   */
  private def checkBadShape(stone: GoStone, r: Int, c: Int, incr: Int, incc: Int): Int = {
    val player1 = stone.isOwnedByPlayer1

    if (!board_.inBounds(r + incr, c + incc)) {
      return 0
    }
    val adjacent1 = board_.getPosition(r + incr, c)
    val adjacent2 = board_.getPosition(r, c + incc)
    val diagonal = board_.getPosition(r + incr, c + incc)

    var severityScore = 0

    if (adjacent1.isOccupied && adjacent2.isOccupied) {
      if (adjacent1.getPiece.isOwnedByPlayer1 == player1
        && adjacent2.getPiece.isOwnedByPlayer1 == player1)
        severityScore += getBadShapeAux(diagonal, player1)
    }

    if (adjacent1.isOccupied && diagonal.isOccupied) {
      if (adjacent1.getPiece.isOwnedByPlayer1 == player1
        && diagonal.getPiece.isOwnedByPlayer1 == player1)
        severityScore += getBadShapeAux(adjacent2, player1)
    }

    if (adjacent2.isOccupied && diagonal.isOccupied) {
      if (adjacent2.getPiece.isOwnedByPlayer1 == player1
        && diagonal.getPiece.isOwnedByPlayer1 == player1)
        severityScore += getBadShapeAux(adjacent1, player1)
    }
    severityScore
  }

  private def getBadShapeAux(nearbySpace: BoardPosition, player1: Boolean): Int = {
    if (nearbySpace.isUnoccupied) {
      return EMPTY_TRIANGLE_PENALTY
    }
    if (nearbySpace.getPiece.isOwnedByPlayer1 == player1) {
      return CLUMP_OF_FOUR_PENALTY
    }
    0
  }
}
