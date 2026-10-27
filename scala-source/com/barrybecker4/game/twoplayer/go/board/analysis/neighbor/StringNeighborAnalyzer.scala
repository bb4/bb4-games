/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.neighbor

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.go.board.{BoardValidator, GoBoard}
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionList}
import com.barrybecker4.game.twoplayer.go.board.elements.string.GoStringSet
import scala.jdk.CollectionConverters._

/**
 * Performs static analysis of a go board to determine strings.
 *
 * @author Barry Becker
 */
class StringNeighborAnalyzer(board_ : GoBoard) {

  private val validator_ : BoardValidator = new BoardValidator(board_)

  /**
   * Determines a string connected from a seed stone within a specified bounding area
   * Perform a breadth first search until all neighbors found.
   * Use the visited flag to indicate that a stone has been added to the string.
   * @return string from seed stone
   */
  def findStringFromInitialPosition(stone: GoBoardPosition, friendOwnedByP1: Boolean,
                                    returnToUnvisitedState: Boolean, neighborType: NeighborType,
                                    box: Box): GoBoardPositionList = {
    val stones = new GoBoardPositionList()
    val stack = new GoBoardPositionList()
    assert(box.contains(stone.getLocation), s"stone $stone not in $box")
    assert(!stone.isVisited, s"stone=$stone")
    stack.add(0, stone)
    while (!stack.isEmpty) {
      val s = stack.pop()
      if (!s.isVisited) {
        s.setVisited(true)
        stones.add(s)
        pushStringNeighbors(s, friendOwnedByP1, stack, true, neighborType, box)
      }
    }
    if (returnToUnvisitedState) {
      stones.unvisitPositions()
      if (GameContext.getDebugMode > 1)
        validator_.confirmAllUnvisited()
    }
    stones
  }

  /**
   * @param stone stone to find string neighbors for.
   * @return list of string neighbors of specified position.
   */
  def findStringNeighbors(stone: GoBoardPosition): GoStringSet = {
    val stringNbrs = new GoStringSet()
    val nobiNbrs = new GoBoardPositionList()

    pushStringNeighbors(stone, true, nobiNbrs, false)

    // add strings only once
    nobiNbrs.asScala.foreach { nbr =>
      stringNbrs.add(nbr.getString)
    }
    stringNbrs
  }

  /**
   * @return all string neighbors of specified position.
   */
  def pushStringNeighbors(s: GoBoardPosition, friendIsPlayer1: Boolean, stack: GoBoardPositionList,
                          samePlayerOnly: Boolean): Int = {
    pushStringNeighbors(s, friendIsPlayer1, stack, samePlayerOnly, NeighborType.OCCUPIED,
      new Box(new com.barrybecker4.common.geometry.ByteLocation(1, 1),
              new com.barrybecker4.common.geometry.ByteLocation(board_.getNumRows, board_.getNumCols)))
  }

  /**
   * Check all nobi neighbors (at most 4).
   * @param s the stone of which to check the neighbors of
   * @param stack the stack to add unvisited neighbors
   * @return number of stones added to the stack
   */
  private def pushStringNeighbors(s: GoBoardPosition, friendPlayer1: Boolean,
                                   stack: GoBoardPositionList, samePlayerOnly: Boolean,
                                   neighborType: NeighborType, bbox: Box): Int = {
    val r = s.getRow
    val c = s.getCol
    var numPushed = 0

    if (r > 1)
      numPushed += checkNeighbor(r, c, -1, 0, friendPlayer1, stack, samePlayerOnly, neighborType, bbox)
    if (c > 1)
      numPushed += checkNeighbor(r, c, 0, -1, friendPlayer1, stack, samePlayerOnly, neighborType, bbox)
    if (r + 1 <= bbox.getMaxRow)
      numPushed += checkNeighbor(r, c, 1, 0, friendPlayer1, stack, samePlayerOnly, neighborType, bbox)
    if (c + 1 <= bbox.getMaxCol)
      numPushed += checkNeighbor(r, c, 0, 1, friendPlayer1, stack, samePlayerOnly, neighborType, bbox)

    numPushed
  }

  /**
   * return 1 if this is a valid neighbor according to specification.
   * These are the immediately adjacent (nobi) nbrs within the specified rectangular bounds
   * @return number of neighbors added (0 or 1).
   */
  private def checkNeighbor(r: Int, c: Int, rowOffset: Int, colOffset: Int,
                            friendOwnedByPlayer1: Boolean, stack: GoBoardPositionList,
                            samePlayerOnly: Boolean, neighborType: NeighborType,
                            bbox: Box): Int = {
    val nbr = board_.getPosition(r + rowOffset, c + colOffset).asInstanceOf[GoBoardPosition]
    if (bbox.contains(nbr.getLocation)) {
      checkNeighbor(r, c, rowOffset, colOffset, friendOwnedByPlayer1, stack, samePlayerOnly, neighborType)
    } else {
      0
    }
  }

  /**
   * Check an immediately adjacent (nobi) nbr.
   *
   * @param r row
   * @param c column
   * @param rowOffset offset from row indicating position of ngbor to check
   * @param colOffset offset from column indicating position of ngbor to check
   * @param friendOwnedByPlayer1 need to specify this when the position checked, s,
   * is empty and has undefined ownership.
   * @param stack if nbr fits criteria then add to stack
   * @param samePlayerOnly mus the nbr be owned by the same player only
   * @param neighborType one of REGULAR_PIECE, UNOCCUPIED, or NOT_FRIEND
   * @return 1 if this is a valid neighbor of the type that we want
   */
  private def checkNeighbor(r: Int, c: Int, rowOffset: Int, colOffset: Int,
                            friendOwnedByPlayer1: Boolean, stack: GoBoardPositionList,
                            samePlayerOnly: Boolean, neighborType: NeighborType): Int = {
    val nbr = board_.getPosition(r + rowOffset, c + colOffset).asInstanceOf[GoBoardPosition]

    neighborType match {
      case NeighborType.FRIEND | NeighborType.OCCUPIED =>
        if (!nbr.isVisited && nbr.isOccupied &&
          (!samePlayerOnly || nbr.getPiece.isOwnedByPlayer1 == friendOwnedByPlayer1)) {
          stack.push(nbr)
          return 1
        }
      case NeighborType.UNOCCUPIED =>
        if (!nbr.isVisited && nbr.isUnoccupied) {
          stack.push(nbr)
          return 1
        }
      case NeighborType.NOT_FRIEND =>
        if (!nbr.isVisited &&
          (nbr.isUnoccupied ||
            (nbr.isOccupied && (nbr.getPiece.isOwnedByPlayer1 != friendOwnedByPlayer1)))) {
          stack.push(nbr)
          return 1
        }
      case _ => throw new IllegalArgumentException(s"Unsupported string neighbor type: $neighborType")
    }
    0
  }
}
