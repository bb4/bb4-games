/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group.eye

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.group.{GroupAnalyzer, GroupAnalyzerMap}
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.{NeighborAnalyzer, NeighborType}
import com.barrybecker4.game.twoplayer.go.board.elements.eye.{GoEye, GoEyeSet}
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionList, GoBoardPositionLists}

/**
 * Analyzes the eye spaces within a group to determine if they are real eyes.
 */
class EyeSpaceAnalyzer(val group: IGoGroup, val analyzerMap: GroupAnalyzerMap) {

  private var board: GoBoard = scala.compiletime.uninitialized
  private var boundingBox: Box = scala.compiletime.uninitialized
  private var nbrAnalyzer: NeighborAnalyzer = scala.compiletime.uninitialized

  def setBoard(board: GoBoard): Unit = {
    this.board = board
    nbrAnalyzer = new NeighborAnalyzer(board)
    boundingBox = group.findBoundingBox()
  }

  /**
   * Determine the set of eyes within a group
   * @return the set of eyes that are in this group.
   */
  def determineEyes(): GoEyeSet = {
    assert(board != null, "The board must be set before determining eyes.")
    val excludedSpaceLists = createExcludedLists()
    findEyesFromCandidates(excludedSpaceLists)
  }

  /**
   * Eliminate all the stones and spaces that are in the bounding rectangle,
   * but not in the group. We do this by marching around the perimeter cutting out
   * the strings of empty or opponent spaces that do not belong.
   * Note: we do not go all the way to the edge. If the border of a group includes an edge of the board,
   * then empty spaces there are most likely eyes (but not necessarily).
   * @return list of lists of eye space spaces find real eye from (and to unvisit at the end)
   */
  private def createExcludedLists(): GoBoardPositionLists = {
    val lists = new GoBoardPositionLists()
    val ownedByPlayer1 = group.isOwnedByPlayer1

    if (boundingBox.getArea == 0) return lists
    val rMin = boundingBox.getMinRow
    val rMax = boundingBox.getMaxRow
    val cMin = boundingBox.getMinCol
    val cMax = boundingBox.getMaxCol

    if (cMin > 1) {
      for (r <- rMin to rMax) {
        excludeSeed(board.getPosition(r, cMin).asInstanceOf[GoBoardPosition],
          ownedByPlayer1, lists)
      }
    }
    if (cMax < board.getNumCols) {
      for (r <- rMin to rMax) {
        excludeSeed(board.getPosition(r, cMax).asInstanceOf[GoBoardPosition],
          ownedByPlayer1, lists)
      }
    }
    if (rMin > 1) {
      for (c <- cMin to cMax) {
        excludeSeed(board.getPosition(rMin, c).asInstanceOf[GoBoardPosition],
          ownedByPlayer1, lists)
      }
    }
    if (rMax < board.getNumRows) {
      for (c <- cMin to cMax) {
        excludeSeed(board.getPosition(rMax, c).asInstanceOf[GoBoardPosition],
          ownedByPlayer1, lists)
      }
    }

    clearEyes(rMin, rMax, cMin, cMax)
    lists
  }

  /**
   * Do a paint fill on each of the empty unvisited spaces.
   * Most of these remaining empty spaces are connected to an eye of some type.
   * There will be some that fill spaces between black and white stones.
   * Don't count these as eyes unless the stones of the opposite color are much weaker -
   * in which case they are assumed dead and hence part of the eye.
   * @param excludedSpaceLists space lists to exclude from consideration because they are outside the group.
   * @return set of eyes in this group
   */
  private def findEyesFromCandidates(excludedSpaceLists: GoBoardPositionLists): GoEyeSet = {
    val eyes = new GoEyeSet()
    val ownedByPlayer1 = group.isOwnedByPlayer1
    val groupAnalyzer = analyzerMap.getAnalyzer(group)

    val innerBox = createBoxExcludingBorder(boundingBox)
    for (r <- innerBox.getMinRow until innerBox.getMaxRow) {
      for (c <- innerBox.getMinCol until innerBox.getMaxCol) {
        val space = board.getPosition(r, c).asInstanceOf[GoBoardPosition]
        assert(space != null, s"pos r=$r c=$c")
        if (!space.isVisited && space.isUnoccupied && !space.isInEye) {
          val eyeSpaces =
            nbrAnalyzer.findStringFromInitialPosition(
              space, ownedByPlayer1, false, NeighborType.NOT_FRIEND,
              boundingBox)
          excludedSpaceLists.add(eyeSpaces)
          if (confirmEye(eyeSpaces)) {
            val eye = new GoEye(eyeSpaces, board, group, groupAnalyzer)
            eyes.add(eye)
          }
          else {
            GameContext.log(3, eyeSpaces.toString("This list of stones was rejected as being an eye: "))
          }
        }
      }
    }
    excludedSpaceLists.unvisitPositionsInLists()
    eyes
  }

  /**
   * @param box to reduce by the outside edge.
   * @return A new bounding box where we shave off the outer edge, unless on the edge of the board.
   */
  private def createBoxExcludingBorder(box: Box): Box = {
    val maxRow = board.getNumRows
    val maxCol = board.getNumCols

    val innerMinRow = if (box.getMinRow > 1) Math.min(box.getMinRow + 1, maxRow) else 1
    val innerMinCol = if (box.getMinCol > 1) Math.min(box.getMinCol + 1, maxCol) else 1

    new Box(
      new com.barrybecker4.common.geometry.ByteLocation(innerMinRow, innerMinCol),
      new com.barrybecker4.common.geometry.ByteLocation(
        if (box.getMaxRow < maxRow) Math.max(box.getMaxRow, innerMinRow) else maxRow + 1,
        if (box.getMaxCol < maxCol) Math.max(box.getMaxCol, innerMinCol) else maxCol + 1
      )
    )
  }

  /**
   * Make sure all the positions do not cache their eye
   */
  private def clearEyes(rMin: Int, rMax: Int, cMin: Int, cMax: Int): Unit = {
    for (r <- rMin to rMax) {
      for (c <- cMin to cMax) {
        board.getPosition(r, c).asInstanceOf[GoBoardPosition].setEye(null)
      }
    }
  }

  /**
   * Mark as visited all the non-friend (empty or enemy) spaces connected to the specified seed.
   *
   * @param space seed
   * @param lists list of stones connected to the seed stone
   */
  private def excludeSeed(space: GoBoardPosition, groupOwnership: Boolean,
                          lists: GoBoardPositionLists): Unit = {
    if (!space.isVisited
      && (space.isUnoccupied || space.getPiece.isOwnedByPlayer1 != group.isOwnedByPlayer1)) {
      val exclusionList =
        nbrAnalyzer.findStringFromInitialPosition(space, groupOwnership, false,
          NeighborType.NOT_FRIEND, boundingBox)

      val it = exclusionList.iterator()
      val groupAnalyzer = analyzerMap.getAnalyzer(group)

      while (it.hasNext) {
        val p = it.next().asInstanceOf[GoBoardPosition]
        if (p.isOccupied) {
          if (!groupAnalyzer.isTrueEnemy(p)) {
            p.setVisited(false)
            it.remove()
          }
        }
      }

      if (exclusionList.size > 0) {
        lists.add(exclusionList)
      }
    }
  }

  /**
   * Check this list of stones to confirm that enemy stones don't border it.
   * If they do, then it is not an eye - return false.
   *
   * I used to attempt to compare the health of the position relative to the surrounding group
   * to see if it is dead enough to still consider an eye, but then realized there is a chicken-egg
   * problem in that we don't really know the liveness until we know the eyes.
   *
   * @param eyeList the candidate string of stones to test for eye status
   * @return true if the list of stones is an eye
   */
  private def confirmEye(eyeList: GoBoardPositionList): Boolean = {
    if (eyeList == null)
      return false

    !eyeList.toArray(new Array[GoBoardPosition](0)).exists { position =>
      boundingBox.isOnEdge(position.getLocation) && !withinBorderEdge(position)
    }
  }

  /**
   * Positions marked E are considered on edge of edge.
   * Note that we are within the edge border if the position
   * is both on the bounding box corner and the board corner.
   *
   *   E****        ******
   *       *    or  *    *
   *       E        E    E
   *
   * @param position the position to check.
   * @return true if on edge of border edge
   */
  private def withinBorderEdge(position: GoBoardPosition): Boolean = {
    val isOnbboxCorner = boundingBox.isOnCorner(position.getLocation)
    val isInCorner = board.isInCorner(position)
    val edgeOfEdge = isOnbboxCorner ^ isInCorner
    board.isOnEdge(position) && !edgeOfEdge
  }
}
