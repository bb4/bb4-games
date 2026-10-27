/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.neighbor

import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.twoplayer.go.board.{GoBoard, GoProfiler}
import com.barrybecker4.game.twoplayer.go.board.elements.group.{GoGroup, GoGroupSet}
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionList, GoBoardPositionSet}
import scala.jdk.CollectionConverters._

/**
 * Performs static analysis of a go board to determine groups.
 *
 * @author Barry Becker
 */
class GroupNeighborAnalyzer(board_ : GoBoard) {

  private val stringAnalyzer_ : StringNeighborAnalyzer = new StringNeighborAnalyzer(board_)

  /**
   * determine a set of stones that have group connections to the specified stone.
   * This set of stones constitutes a group, but since stones cannot belong to more than
   * one group (or string) we must return a List.
   * Life connections are nobi links and diagonals that are not cut on both sides.
   * One-point jumps and knight moves are neighbors for relative health, not shared eyes.
   *
   * @param stone the stone to search from for group neighbors.
   * @param returnToUnvisitedState if true, then mark everything unvisited when done.
   * @return the list of stones in the group that was found.
   */
  def findGroupFromInitialPosition(stone: GoBoardPosition,
                                    returnToUnvisitedState: Boolean): GoBoardPositionList = {
    val stones = new GoBoardPositionList()
    // perform a breadth first search until all found.
    // use the visited flag to indicate that a stone has been added to the group
    val stack = new GoBoardPositionList()
    stack.add(0, stone)
    while (!stack.isEmpty) {
      val s = stack.remove(stack.size - 1)
      if (!s.isVisited) {
        s.setVisited(true)
        assert(s.getPiece.isOwnedByPlayer1 == stone.getPiece.isOwnedByPlayer1,
          s"$s does not have same ownership as $stone")
        stones.add(s)
        pushGroupNeighbors(s, s.getPiece.isOwnedByPlayer1, stack)
      }
    }
    if (returnToUnvisitedState) {
      stones.unvisitPositions()
    }
    stones
  }

  /**
   * @return all the groups on the board for both sides.
   */
  def findAllGroups(): GoGroupSet = {
    val groups = new GoGroupSet()

    for (i <- 1 to board_.getNumRows) {
      for (j <- 1 to board_.getNumCols) {
        val pos = board_.getPosition(i, j).asInstanceOf[GoBoardPosition]
        if (pos.isOccupied && !groups.containsPosition(pos)) {
          // would this run faster if second param was false?
          groups.add(new GoGroup(findGroupFromInitialPosition(pos, true)))
        }
      }
    }
    groups
  }

  /**
   * return a set of stones which are loosely connected to this stone.
   * Check the 16 purely group neighbors and 4 string neighbors
   *         ***
   *        **S**
   *        *SXS*
   *        **S**
   *         ***
   * @param stone (not necessarily occupied)
   * @param friendPlayer1 typically stone.isOwnedByPlayer1 value of stone unless it is blank.
   * @param samePlayerOnly if true then find group nbrs that are have same ownership as friendPlayer1
   * @return group neighbors for specified stone.
   */
  def findGroupNeighbors(stone: GoBoardPosition,
                         friendPlayer1: Boolean, samePlayerOnly: Boolean): GoBoardPositionSet = {
    val stack = new GoBoardPositionList()

    pushGroupNeighbors(stone, friendPlayer1, stack, samePlayerOnly)
    val nbrStones = new GoBoardPositionSet()
    nbrStones.addAll(stack)

    nbrStones
  }

  /**
   * Check all 20 neighbors (including diagonals, 1-space jumps, and knights moves).
   * Make sure diagonals are not cut nor 1-space jumps peeped.
   *
   * @param s the position containing a stone of which to check the neighbors of.
   * @param friendPlayer1 side to find groups stones for.
   * @param stack the stack to add unvisited neighbors.
   * @return number of stones added to the stack.
   */
  private def pushGroupNeighbors(s: GoBoardPosition, friendPlayer1: Boolean, stack: GoBoardPositionList): Int = {
    // Building a group: share life across nobi links and safe diagonals only.
    pushGroupNeighbors(s, friendPlayer1, stack, samePlayerOnly = true, includeLooseLinks = false)
  }

  /**
   * Check all 20 neighbors (including diagonals, 1-space jumps, and knights moves).
   * Make sure diagonals and 1-space jumps are not cut.
   * We currently push group neighbors even if they are part of a string that is in atari.
   *
   * @param s the position of a stone of which to check the neighbors of.
   * @param friendPlayer1 side to find group stones for.
   * @param stack the stack on which we add unvisited neighbors.
   * @return number of stones added to the stack.
   */
  private def pushGroupNeighbors(s: GoBoardPosition, friendPlayer1: Boolean, stack: GoBoardPositionList,
                                  samePlayerOnly: Boolean): Int = {
    // Neighbor queries keep jumps and knight moves so relative health can see them.
    pushGroupNeighbors(s, friendPlayer1, stack, samePlayerOnly, includeLooseLinks = true)
  }

  private def pushGroupNeighbors(s: GoBoardPosition, friendPlayer1: Boolean, stack: GoBoardPositionList,
                                  samePlayerOnly: Boolean, includeLooseLinks: Boolean): Int = {
    GoProfiler.getInstance.startGetGroupNeightbors()
    // start with the nobi string nbrs
    var numPushed = stringAnalyzer_.pushStringNeighbors(s, friendPlayer1, stack, samePlayerOnly)

    // now push the non-nobi group neighbors
    if (!samePlayerOnly)
      numPushed += pushEnemyDiagonalNeighbors(s, friendPlayer1, stack)

    // we only find pure group neighbors of the same color
    numPushed += pushPureGroupNeighbors(s, friendPlayer1, true, stack, includeLooseLinks)
    GoProfiler.getInstance.stopGetGroupNeighbors()
    numPushed
  }

  /**
   * Check all diagonal neighbors (at most 4).
   * @param s the stone of which to check the neighbors of
   * @param stack the stack to add unvisited neighbors
   * @return number of stones added to the stack
   */
  private def pushEnemyDiagonalNeighbors(s: GoBoardPosition, friendPlayer1: Boolean,
                                          stack: GoBoardPositionList): Int = {
    val r = s.getRow
    val c = s.getCol
    checkDiagonalNeighbors(r, c, !friendPlayer1, true, stack)
  }

  /**
   * Check all non-nobi group neighbors.
   * @param pos the stone of which to check the neighbors of
   * @param stack the stack to add unvisited neighbors
   * @param sameSideOnly if true push pure group nbrs of the same side only.
   * @return number of stones added to the stack
   */
  private def pushPureGroupNeighbors(pos: GoBoardPosition, friendPlayer1: Boolean, sameSideOnly: Boolean,
                                      stack: GoBoardPositionList, includeLooseLinks: Boolean): Int = {
    val r = pos.getRow
    val c = pos.getCol
    var numPushed = 0

    numPushed += checkDiagonalNeighbors(r, c, friendPlayer1, sameSideOnly, stack)
    if (includeLooseLinks) {
      numPushed += checkOneSpaceNeighbors(r, c, friendPlayer1, sameSideOnly, stack)
      numPushed += checkKogeimaNeighbors(r, c, friendPlayer1, sameSideOnly, stack)
    }

    numPushed
  }

  /**
   * @return diagonal neighbors.
   */
  private def checkDiagonalNeighbors(r: Int, c: Int, friendPlayer1: Boolean, sameSideOnly: Boolean,
                                      stack: GoBoardPositionList): Int = {
    val numRows = board_.getNumRows
    val numCols = board_.getNumCols
    var numPushed = 0

    if (r > 1 && c > 1)
      numPushed += checkDiagonalNeighbor(r, c, -1, -1, friendPlayer1, sameSideOnly, stack)
    if (r > 1 && c + 1 <= numCols)
      numPushed += checkDiagonalNeighbor(r, c, -1, 1, friendPlayer1, sameSideOnly, stack)
    if (r + 1 <= numRows && c + 1 <= numCols)
      numPushed += checkDiagonalNeighbor(r, c, 1, 1, friendPlayer1, sameSideOnly, stack)
    if (r + 1 <= numRows && c > 1)
      numPushed += checkDiagonalNeighbor(r, c, 1, -1, friendPlayer1, sameSideOnly, stack)
    numPushed
  }

  /**
   * @return the 1-space jumps from r,c
   */
  private def checkOneSpaceNeighbors(r: Int, c: Int, friendPlayer1: Boolean, sameSideOnly: Boolean,
                                      stack: GoBoardPositionList): Int = {
    val numRows = board_.getNumRows
    val numCols = board_.getNumCols
    var numPushed = 0

    if (r > 2)
      numPushed += checkOneSpaceNeighbor(r, c, -2, 0, friendPlayer1, sameSideOnly, stack)
    if (c > 2)
      numPushed += checkOneSpaceNeighbor(r, c, 0, -2, friendPlayer1, sameSideOnly, stack)
    if (r + 2 <= numRows)
      numPushed += checkOneSpaceNeighbor(r, c, 2, 0, friendPlayer1, sameSideOnly, stack)
    if (c + 2 <= numCols)
      numPushed += checkOneSpaceNeighbor(r, c, 0, 2, friendPlayer1, sameSideOnly, stack)
    numPushed
  }

  /**
   * @return the diagonal moves from r,c
   */
  private def checkKogeimaNeighbors(r: Int, c: Int, friendPlayer1: Boolean, sameSideOnly: Boolean,
                                     stack: GoBoardPositionList): Int = {
    val numRows = board_.getNumRows
    val numCols = board_.getNumCols
    var numPushed = 0

    if ((r > 2) && (c > 1))
      numPushed += checkKogeimaNeighbor(r, c, -2, -1, friendPlayer1, sameSideOnly, stack)
    if ((r > 2) && (c + 1 <= numCols))
      numPushed += checkKogeimaNeighbor(r, c, -2, 1, friendPlayer1, sameSideOnly, stack)

    if ((r + 2 <= numRows) && (c > 1))
      numPushed += checkKogeimaNeighbor(r, c, 2, -1, friendPlayer1, sameSideOnly, stack)
    if ((r + 2 <= numRows) && (c + 1 <= numCols))
      numPushed += checkKogeimaNeighbor(r, c, 2, 1, friendPlayer1, sameSideOnly, stack)

    if ((r > 1) && (c > 2))
      numPushed += checkKogeimaNeighbor(r, c, -1, -2, friendPlayer1, sameSideOnly, stack)
    if ((r + 1 <= numRows) && (c > 2))
      numPushed += checkKogeimaNeighbor(r, c, 1, -2, friendPlayer1, sameSideOnly, stack)

    if ((r > 1) && (c + 2 <= numCols))
      numPushed += checkKogeimaNeighbor(r, c, -1, 2, friendPlayer1, sameSideOnly, stack)
    if ((r + 1 <= numRows) && (c + 2 <= numCols))
      numPushed += checkKogeimaNeighbor(r, c, 1, 2, friendPlayer1, sameSideOnly, stack)
    numPushed
  }

  /**
   * We allow these connections as long as the diagonal has not been fully cut.
   * i.e. not an opponent stone on both sides of the cut (or the diag stone is not in atari).
   *
   * @param sameSideOnly if true then push nbrs on the same side, else push enemy nbrs
   * @return o or 1 depending on if diagonal neighbor
   */
  private def checkDiagonalNeighbor(r: Int, c: Int, rowOffset: Int, colOffset: Int,
                                     friendPlayer1: Boolean, sameSideOnly: Boolean,
                                     stack: GoBoardPositionList): Int = {
    val nbr = board_.getPosition(r + rowOffset, c + colOffset).asInstanceOf[GoBoardPosition]
    if (nbr.isUnoccupied) {
      return 0
    }
    // determine the side we are checking for (one or the other)
    val sideTest = if (sameSideOnly) friendPlayer1 else !friendPlayer1
    if ((nbr.getPiece.isOwnedByPlayer1 == sideTest) && !nbr.isVisited) {
      val diag1 = board_.getPosition(r + rowOffset, c)
      val diag2 = board_.getPosition(r, c + colOffset)
      if (!isDiagonalCut(diag1, diag2, sideTest)) {
        stack.add(0, nbr)
        return 1
      }
    }
    0
  }

  private def isDiagonalCut(diag1: BoardPosition, diag2: BoardPosition, sideTest: Boolean): Boolean = {
    (diag1.isOccupied && diag1.getPiece.isOwnedByPlayer1 != sideTest) &&
      (diag2.isOccupied && diag2.getPiece.isOwnedByPlayer1 != sideTest)
  }

  /**
   * Connected only add if not completely cut (there's no enemy stone in the middle).
   * @return return 1 or 0 depending on if there si a onespace neighbor
   */
  private def checkOneSpaceNeighbor(r: Int, c: Int, rowOffset: Int, colOffset: Int,
                                     friendPlayer1: Boolean, samePlayerOnly: Boolean,
                                     stack: GoBoardPositionList): Int = {
    val nbr = board_.getPosition(r + rowOffset, c + colOffset).asInstanceOf[GoBoardPosition]
    if (nbr.isOccupied &&
      (!samePlayerOnly || nbr.getPiece.isOwnedByPlayer1 == friendPlayer1) && !nbr.isVisited) {
      val oneSpacePt: BoardPosition =
        if (rowOffset == 0) {
          val col = c + (colOffset >> 1)
          board_.getPosition(r, col)
        } else {
          val row = r + (rowOffset >> 1)
          board_.getPosition(row, c)
        }
      if (!isOneSpaceCut(friendPlayer1, oneSpacePt)) {
        stack.add(0, nbr)
        return 1
      }
    }
    0
  }

  /**
   * We consider the link cut if there is an opponent piece between the 2 stones
   * eg: *|*
   * @return true if cut by eenemy stone.
   */
  private def isOneSpaceCut(friendPlayer1: Boolean, oneSpacePt: BoardPosition): Boolean = {
    if (oneSpacePt.isUnoccupied) {
      return false
    }
    oneSpacePt.getPiece.isOwnedByPlayer1 != friendPlayer1
  }

  /**
   * For the knight's move (kogeima) we consider it cut if there is an enemy stone at the base.
   * @param stack kogeima neighbors, if found, are added to this stack.
   * @return number of kogeima neighbors added.
   */
  private def checkKogeimaNeighbor(r: Int, c: Int, rowOffset: Int, colOffset: Int,
                                    friendPlayer1: Boolean, sameSideOnly: Boolean,
                                    stack: GoBoardPositionList): Int = {
    if (!board_.inBounds(r + rowOffset, c + colOffset)) {
      return 0
    }
    val nbr = board_.getPosition(r + rowOffset, c + colOffset).asInstanceOf[GoBoardPosition]

    if (nbr.isOccupied &&
      (!sameSideOnly || nbr.getPiece.isOwnedByPlayer1 == friendPlayer1) && !nbr.isVisited) {

      val (intermediate1, intermediate2): (BoardPosition, BoardPosition) =
        if (math.abs(rowOffset) == 2) {
          val rr = r + (rowOffset >> 1)
          (board_.getPosition(rr, c), board_.getPosition(rr, c + colOffset))
        } else {
          val cc = c + (colOffset >> 1)
          (board_.getPosition(r, cc), board_.getPosition(r + rowOffset, cc))
        }
      if (!isKogeimaCut(friendPlayer1, intermediate1, intermediate2)) {
        stack.add(0, nbr)
        return 1
      }
    }
    0
  }

  /**
   * Consider the knights move it cut if there is an opponent stone in one of the 2 spaces between.
   * @return true if cut by one or more enemy sontes.
   */
  private def isKogeimaCut(friendPlayer1: Boolean, intermediate1: BoardPosition, intermediate2: BoardPosition): Boolean = {
    (intermediate1.isOccupied
      && (intermediate1.getPiece.isOwnedByPlayer1 != friendPlayer1)) ||
      (intermediate2.isOccupied
        && (intermediate2.getPiece.isOwnedByPlayer1 != friendPlayer1))
  }
}
