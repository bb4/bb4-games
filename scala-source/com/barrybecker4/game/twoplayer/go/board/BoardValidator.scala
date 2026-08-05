/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer
import com.barrybecker4.game.twoplayer.go.board.elements.group.{GoGroup, GoGroupSet, IGoGroup}
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionList, GoBoardPositionSet}
import scala.jdk.CollectionConverters._

/**
 * Assert certain things are true about the board.
 * Helpful for debugging.
 *
 * @author Barry Becker
 */
class BoardValidator(board_ : GoBoard) {

  /**
   * Confirm no empty strings, stones in valid groups, all stones in unique groups,
   * and all stones in groups claimed.
   * @param pos position to check
   */
  def consistencyCheck(pos: GoBoardPosition): Unit = {
    board_.getGroups.confirmNoEmptyStrings()
    confirmStonesInValidGroups()
    board_.getGroups.confirmAllStonesInUniqueGroups()
    try {
      confirmAllStonesInGroupsClaimed(board_.getGroups)
    } catch {
      case e: AssertionError =>
        GameContext.log(0, s"The move was :$pos")
        throw e
    }
  }

  /**
   * verify that all the stones on the board are in the boards member list of groups.
   */
  def confirmStonesInValidGroups(): Unit = {
    val groups = board_.getGroups
    for (i <- 1 to board_.getNumRows) {
      for (j <- 1 to board_.getNumCols) {
        val space = board_.getPosition(i, j).asInstanceOf[GoBoardPosition]
        if (space.isOccupied) {
          groups.confirmStoneInValidGroup(space)
        }
      }
    }
  }

  /**
   * verify that all the stones are marked unvisited.
   */
  def confirmAllUnvisited(): Unit = {
    val stone = areAllUnvisited()
    if (stone != null)
      assert(false, s"$stone is marked visited")
  }

  /**
   * verify that all the stones are marked unvisited.
   * @return position that is still marked visited if any, else null.
   */
  private def areAllUnvisited(): GoBoardPosition = {
    for (i <- 1 to board_.getNumRows) {
      for (j <- 1 to board_.getNumCols) {
        val stone = board_.getPosition(i, j).asInstanceOf[GoBoardPosition]
        if (stone.isVisited)
          return stone
      }
    }
    null
  }

  /**
   * For every stone in every group specified in groups, verify that the group determined from using that stone as a
   * seed matches the group that is claims by ancestry.
   * (expensive to check)
   * @param groups we will check each stone in each of these groups.
   */
  def confirmAllStonesInGroupsClaimed(groups: GoGroupSet): Unit = {
    val na = new NeighborAnalyzer(board_)

    groups.asScala.foreach { parentGroup =>
      val parentGroupStones = parentGroup.getStones
      parentGroupStones.asScala.foreach { stone =>
        // compute the group from this stone and confirm it matches the parent group
        val g = na.findGroupFromInitialPosition(stone)

        // perhaps we should do something more than check the size.
        if (g.size != parentGroupStones.size) {
          groups.debugPrint(0, "Confirm stones in groups they Claim failed. \nGroups are:\n", showBlack = true, showWhite = true)
          val bldr = new StringBuilder()
          bldr.append(board_.toString)
          bldr.append("\n")
          bldr.append("\n\nIt seems that using different seeds yields different groups:")
          parentGroupStones.asScala.foreach { stone1 =>
            val gg = na.findGroupFromInitialPosition(stone)
            val title = s"\nSEED STONE = $stone1 found groups of size ${gg.size}"
            bldr.append(gg.toString(title))
          }
          GameContext.log(0, bldr.toString)
          GameContext.log(0,
            s"${g.toString(s"Calculated Group (seeded by $stone):") } is not equal to the expected parent group:\n$parentGroup")
        }
      }
    }
  }
}

object BoardValidator {
  def confirmNoNullMembers(group: GoGroup): Unit = {
    val it = group.getStones.iterator()
    var failed = false
    while (it.hasNext) {
      val s = it.next().asInstanceOf[GoBoardPosition]
      if (s.getPiece == null) failed = true
    }
    if (failed) {
      assert(false, s"Group contains an empty position: ${group.toString}")
    }
  }
}
