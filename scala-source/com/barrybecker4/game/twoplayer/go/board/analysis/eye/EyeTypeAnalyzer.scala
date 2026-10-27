/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye

import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information._
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzer
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.{NeighborAnalyzer, NeighborType}
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionSet}
import scala.jdk.CollectionConverters._

/**
 * Determine the type of an eye on the board.
 *
 * @author Barry Becker
 */
class EyeTypeAnalyzer(
  private val eye_ : IGoEye,
  private val board_ : GoBoard,
  private val groupAnalyzer_ : GroupAnalyzer
) {

  private val nbrAnalyzer_ = new NeighborAnalyzer(board_)

  /**
   * @return the eye type determined based on the properties and
   *     nbrs of the positions in the spaces_ list.
   */
  def determineEyeInformation(): EyeInformation = {
    val spaces = eye_.getMembers
    assert(spaces != null, "spaces_ is null")
    val size = spaces.size

    if (isFalseEye()) {
      return new FalseEyeInformation()
    }
    if (size == 1) {
      return new E1Information()
    }
    if (size == 2) {
      return new E2Information()
    }
    if (size == 3) {
      return new E3Information()
    }
    if (size > 3 && size < 8) {
      val bigEyeAnalyzer = new BigEyeAnalyzer(eye_)
      return bigEyeAnalyzer.determineEyeInformation()
    }
    new TerritorialEyeInformation()
  }

  /**
   * Iterate through the spaces_ in the eye.
   * if any are determined to be a false-eye, then return false-eye for the eye type.
   * @return true if we are a false eye.
   */
  private def isFalseEye(): Boolean = {
    val spaces = eye_.getMembers
    spaces.asScala.exists(space => isFalseEye(space))
  }

  /**
   * Generally if 3 or more of the nobi neighbors are friendly,
   * and 2 or more of the diagonal nbrs are not, then it is a false eye.
   * However, if against the edge or in the corner, then 2 or more friendly nobi nbrs
   * and 1 or more enemy diagonal nbrs are needed in order to call a false eye.
   * Those enemy diagonal neighbors need to be stronger otherwise they
   * should be considered too weak to cause a false eye.
   *
   * @param space check to see if this space is part of a false eye.
   * @return true if htis is a false eye.
   */
  private def isFalseEye(space: GoBoardPosition): Boolean = {
    val ourGroup = eye_.getGroup
    val groupP1 = ourGroup.isOwnedByPlayer1
    val nbrs = nbrAnalyzer_.getNobiNeighbors(space, groupP1, NeighborType.FRIEND)

    if (nbrs.size >= 2) {
      val numOppDiag = getNumOpponentDiagonals(space, groupP1)

      if (numOppDiag >= 2 && (nbrs.size >= 3)) {
        return true
      } else if (board_.isOnEdge(space) && numOppDiag >= 1) {
        return true
      }
    }
    false
  }

  /**
   * Check the diagonals for > 2 of the opponents pieces.
   * there are 2 cases: both opponent pieces on the same vertical or horizontal, or
   * the opponents pieces are on the opposite diagonals
   * @return  The number of diagonal points that are occupied by opponent stones (0, 1, or 2)
   */
  private def getNumOpponentDiagonals(space: GoBoardPosition, groupP1: Boolean): Int = {
    var numOppDiag = 0
    val r = space.getRow
    val c = space.getCol

    if (qualifiedOpponentDiagonal(-1, -1, r, c, groupP1)) numOppDiag += 1
    if (qualifiedOpponentDiagonal(-1, 1, r, c, groupP1)) numOppDiag += 1
    if (qualifiedOpponentDiagonal(1, -1, r, c, groupP1)) numOppDiag += 1
    if (qualifiedOpponentDiagonal(1, 1, r, c, groupP1)) numOppDiag += 1
    numOppDiag
  }

  /**
   * @return true of the enemy piece on the diagonal is relatively strong and there are group stones adjacent.
   */
  private def qualifiedOpponentDiagonal(rowOffset: Int, colOffset: Int, r: Int, c: Int, groupP1: Boolean): Boolean = {
    val diagPos = board_.getPosition(r + rowOffset, c + colOffset).asInstanceOf[GoBoardPosition]
    if (diagPos == null || diagPos.isUnoccupied || diagPos.getPiece.isOwnedByPlayer1 == groupP1) {
      return false
    }

    val pos1 = board_.getPosition(r + rowOffset, c)
    val pos2 = board_.getPosition(r, c + colOffset)

    (pos1.isOccupied && (pos1.getPiece.isOwnedByPlayer1 == groupP1) &&
      pos2.isOccupied && (pos2.getPiece.isOwnedByPlayer1 == groupP1) &&
      groupAnalyzer_.isTrueEnemy(diagPos))
  }
}
