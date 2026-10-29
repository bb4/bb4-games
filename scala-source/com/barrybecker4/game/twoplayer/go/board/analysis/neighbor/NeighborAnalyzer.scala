/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.neighbor

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.game.twoplayer.go.board.{GoBoard, GoProfiler}
import com.barrybecker4.game.twoplayer.go.board.elements.group.GoGroupSet
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionList, GoBoardPositionSet}
import com.barrybecker4.game.twoplayer.go.board.elements.string.{GoString, GoStringSet, IGoString}

/**
 * Performs static analysis of a go board to determine strings and
 * other analysis involving neighbor locations.
 * Also acts as a facade to the other neighbor analyzers in this class.
 */
class NeighborAnalyzer(board: GoBoard) {

  private val groupNbrAnalyzer: GroupNeighborAnalyzer = new GroupNeighborAnalyzer(board)
  private val stringNbrAnalyzer: StringNeighborAnalyzer = new StringNeighborAnalyzer(board)
  private val nobiAnalyzer_ : NobiNeighborAnalyzer = new NobiNeighborAnalyzer(board)

  /**
   * @param empties a list of unoccupied positions.
   * @return a list of stones bordering the set of empty board positions.
   */
  def findOccupiedNobiNeighbors(empties: GoBoardPositionList): GoBoardPositionSet = {
    nobiAnalyzer_.findOccupiedNobiNeighbors(empties)
  }

  /**
   * get neighboring stones of the specified stone.
   * @param stone the stone (or space) whose neighbors we are to find (it must contain a piece).
   * @param neighborType (EYE, NOT_FRIEND etc)
   * @return a set of stones that are immediate (nobi) neighbors.
   */
  def getNobiNeighbors(stone: GoBoardPosition, neighborType: NeighborType): GoBoardPositionSet = {
    getNobiNeighbors(stone, stone.getPiece.isOwnedByPlayer1, neighborType)
  }

  /**
   * get neighboring stones of the specified stone.
   * @param stone the stone (or space) whose neighbors we are to find.
   * @param friendOwnedByP1 need to specify this in the case that the stone is a blank space and has undefined ownership.
   * @param neighborType (EYE, NOT_FRIEND etc)
   * @return a set of stones that are immediate (nobi) neighbors.
   */
  def getNobiNeighbors(stone: GoBoardPosition, friendOwnedByP1: Boolean,
                       neighborType: NeighborType): GoBoardPositionSet = {
    nobiAnalyzer_.getNobiNeighbors(stone, friendOwnedByP1, neighborType)
  }

  /**
   * determine a set of stones that are tightly connected to the specified stone.
   * This set of stones constitutes a string, but since stones cannot belong to more than
   * one string we must return a List.
   * @param stone he stone from which to begin searching for the string
   * @param returnToUnvisitedState if true then the stones will all be marked unvisited when done searching
   * @return find string.
   */
  def findStringFromInitialPosition(stone: GoBoardPosition,
                                     returnToUnvisitedState: Boolean): GoBoardPositionList = {
    findStringFromInitialPosition(
      stone, stone.getPiece.isOwnedByPlayer1, returnToUnvisitedState, NeighborType.OCCUPIED,
      new Box(new com.barrybecker4.common.geometry.ByteLocation(1, 1), 
              new com.barrybecker4.common.geometry.ByteLocation(board.getNumRows, board.getNumCols)))
  }

  /**
   * Determines a string connected from a seed stone within a specified bounding area.
   * @return string from seed stone.
   */
  def findStringFromInitialPosition(stone: GoBoardPosition, friendOwnedByP1: Boolean,
                                     returnToUnvisitedState: Boolean, neighborType: NeighborType, box: Box): GoBoardPositionList = {
    GoProfiler.getInstance.startFindStrings()
    val stones =
      stringNbrAnalyzer.findStringFromInitialPosition(stone, friendOwnedByP1, returnToUnvisitedState,
        neighborType, box)
    GoProfiler.getInstance.stopFindStrings()

    stones
  }

  /**
   * @param position stone or space to find string neighbors of.
   * @return string neighbors
   */
  def findStringNeighbors(position: GoBoardPosition): GoStringSet = {
    stringNbrAnalyzer.findStringNeighbors(position)
  }

  /**
   * This version assumes that the stone is occupied.
   * @return the list of stones in the group that was found.
   */
  def findGroupNeighbors(position: GoBoardPosition, samePlayerOnly: Boolean): GoBoardPositionSet = {
    assert(position.getPiece != null, s"Position $position does not have a piece!")
    findGroupNeighbors(position, position.getPiece.isOwnedByPlayer1, samePlayerOnly)
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
   * @return group neighbors
   */
  def findGroupNeighbors(stone: GoBoardPosition, friendPlayer1: Boolean,
                         samePlayerOnly: Boolean): GoBoardPositionSet = {
    groupNbrAnalyzer.findGroupNeighbors(stone, friendPlayer1, samePlayerOnly)
  }

  /**
   * determine a set of stones that are loosely connected to the specified stone.
   * This set of stones constitutes a group, but since stones cannot belong to more than
   * one group (or string) we must return a List.
   *
   * @param stone the stone to search from for group neighbors.
   * @return the list of stones in the group that was found.
   */
  def findGroupFromInitialPosition(stone: GoBoardPosition): GoBoardPositionList = {
    findGroupFromInitialPosition(stone, true)
  }

  /**
   * determine a set of stones that have group connections to the specified stone.
   * This set of stones constitutes a group, but since stones cannot belong to more than
   * one group (or string) we must return a List.
   * Group connections include nobi, ikken tobi, and kogeima.
   *
   * @param stone the stone to search from for group neighbors.
   * @param returnToUnvisitedState if true, then mark everything unvisited when done.
   * @return the list of stones in the group that was found.
   */
  def findGroupFromInitialPosition(stone: GoBoardPosition, returnToUnvisitedState: Boolean): GoBoardPositionList = {
    groupNbrAnalyzer.findGroupFromInitialPosition(stone, returnToUnvisitedState)
  }

  /**
   * Finds all the groups.
   * Careful about running this without running findAllStringsOnBoard() first
   * Otherwise you could end up with board positions in multiple groups.
   * @return all the groups on the board
   */
  def findAllGroupsOnBoard: GoGroupSet = {
    groupNbrAnalyzer.findAllGroups()
  }

  /**
   * This is an expensive operation because it clears everything we know on the board
   * and recreates all the string from first principals.
   * It has the side effect of updating the board state. Careful.
   * You will need to run findAllGroups on
   * @return all the strings on the board
   */
  def determineAllStringsOnBoard(): GoStringSet = {
    clearEyes()
    val strings = new GoStringSet()
    for (i <- 1 to board.getNumRows) {
      for (j <- 1 to board.getNumCols) {
        val pos = board.getPosition(i, j).asInstanceOf[GoBoardPosition]
        if (pos.isOccupied) {
          val existingString = strings.findStringContainingPosition(pos)
          if (existingString == null) {
            val str = new GoString(findStringFromInitialPosition(pos, true), board)
            strings.add(str)
            pos.setString(str)
          } else {
            pos.setString(existingString)
          }
        }
      }
    }
    strings
  }

  /**
   * Gets rid of everything we know about the board. Careful.
   */
  private def clearEyes(): Unit = {
    for (i <- 1 to board.getNumRows) {
      for (j <- 1 to board.getNumCols) {
        val pos = board.getPosition(i, j).asInstanceOf[GoBoardPosition]
        pos.setEye(null)
        pos.setString(null)
        pos.setVisited(false)
      }
    }
  }
}
