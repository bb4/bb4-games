/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.neighbor

import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionList, GoBoardPositionSet, GoStone}
import scala.jdk.CollectionConverters._

/**
 * Performs static analysis of a go board to determine
 * immediately adjacent, strongly connected (nobi) neighbor locations.
 */
class NobiNeighborAnalyzer(board_ : GoBoard) {

  /**
   * @param empties a list of unoccupied positions.
   * @return a list of stones bordering the set of empty board positions.
   */
  def findOccupiedNobiNeighbors(empties: GoBoardPositionList): GoBoardPositionSet = {
    val allNbrs = new GoBoardPositionSet()
    empties.asScala.foreach { empty =>
      assert(empty.isUnoccupied)
      val nbrs = getNobiNeighbors(empty, false, NeighborType.OCCUPIED)
      // add these nbrs to the set of all nbrs
      // (dupes automatically culled because HashSets only have unique members)
      allNbrs.addAll(nbrs)
    }
    allNbrs
  }

  /**
   * get neighboring stones of the specified stone.
   * @param stone the stone (or space) whose neighbors we are to find.
   * @param friendOwnedByP1 need to specify this in the case that the stone is a blank space
   * and has undefined ownership.
   * @param neighborType (EYE, NOT_FRIEND etc)
   * @return a set of stones that are immediate (nobi) neighbors.
   */
  def getNobiNeighbors(stone: GoBoardPosition, friendOwnedByP1: Boolean,
                       neighborType: NeighborType): GoBoardPositionSet = {
    val nbrs = new GoBoardPositionSet()
    val row = stone.getRow
    val col = stone.getCol

    if (row > 1)
      NobiNeighborAnalyzer.addNobiNeighbor(board_.getPosition(row - 1, col).asInstanceOf[GoBoardPosition],
        friendOwnedByP1, nbrs, neighborType)
    if (row + 1 <= board_.getNumRows)
      NobiNeighborAnalyzer.addNobiNeighbor(board_.getPosition(row + 1, col).asInstanceOf[GoBoardPosition],
        friendOwnedByP1, nbrs, neighborType)
    if (col > 1)
      NobiNeighborAnalyzer.addNobiNeighbor(board_.getPosition(row, col - 1).asInstanceOf[GoBoardPosition],
        friendOwnedByP1, nbrs, neighborType)
    if (col + 1 <= board_.getNumCols)
      NobiNeighborAnalyzer.addNobiNeighbor(board_.getPosition(row, col + 1).asInstanceOf[GoBoardPosition],
        friendOwnedByP1, nbrs, neighborType)

    nbrs
  }
}

object NobiNeighborAnalyzer {
  /**
   * Get an adjacent neighbor stones restricted to the desired type.
   *
   * @param nbrStone the neighbor to check
   * @param friendOwnedByP1 type of the center stone (can't use center.owner since center may be unnoccupied)
   * @param nbrs hashset of the ngbors matching the criteria.
   * @param neighborType one of the defined neighbor types.
   */
  private def addNobiNeighbor(nbrStone: GoBoardPosition, friendOwnedByP1: Boolean,
                              nbrs: GoBoardPositionSet, neighborType: NeighborType): Unit = {
    val correctNeighborType = neighborType match {
      case NeighborType.ANY =>
        true
      case NeighborType.OCCUPIED =>
        // note friendOwnedByP1 is intentionally ignored
        nbrStone.isOccupied
      case NeighborType.UNOCCUPIED =>
        // note friendOwnedByP1 is intentionally ignored
        nbrStone.isUnoccupied
      case NeighborType.ENEMY => // the opposite color
        if (nbrStone.isUnoccupied)
          return
        val st = nbrStone.getPiece.asInstanceOf[GoStone]
        st.isOwnedByPlayer1 != friendOwnedByP1
      case NeighborType.FRIEND => // the same color
        if (nbrStone.isUnoccupied)
          return
        nbrStone.getPiece.isOwnedByPlayer1 == friendOwnedByP1
      case NeighborType.NOT_FRIEND => // the opposite color or empty
        val stone = nbrStone.getPiece.asInstanceOf[GoStone]
        nbrStone.isUnoccupied || stone.isOwnedByPlayer1 != friendOwnedByP1
    }
    if (correctNeighborType) {
      nbrs.add(nbrStone)
    }
  }
}
