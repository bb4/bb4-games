/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements.group

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.go.board.elements.IGoSet
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition
import com.barrybecker4.game.twoplayer.go.board.elements.string.IGoString
import java.util.LinkedHashSet
import scala.jdk.CollectionConverters._

/**
 * A set of GoGroups.
 */
class GoGroupSet extends LinkedHashSet[IGoGroup] {

  /**
   * Check all the groups of the same color to see if the stone is already in one of them
   * @param pos position on the board to check
   * @return true if the specified position is in one of our groups.
   */
  def containsPosition(pos: GoBoardPosition): Boolean = {
    // if there is no stone in the position, then it cannot be part of a group
    if (!pos.isOccupied) {
      return false
    }

    this.asScala.exists { group =>
      group.isOwnedByPlayer1 == pos.getPiece.isOwnedByPlayer1 && group.containsStone(pos)
    }
  }

  /**
   * create a nice list of all the current groups (and the strings they contain)
   * @return String containing the current groups
   */
  override def toString: String = toString(showBlack = true, showWhite = true)

  /**
   * create a nice list of all the current groups (and the strings they contain)
   * @return String containing the current groups
   */
  def toString(showBlack: Boolean, showWhite: Boolean): String = {
    val groupText = new StringBuilder()
    val blackGroupsText = new StringBuilder(if (showBlack) "The black groups are :\n" else "")
    val whiteGroupsText = new StringBuilder((if (showBlack) "\n" else "") + (if (showWhite) "The white groups are :\n" else ""))

    this.asScala.foreach { group =>
      if (group.isOwnedByPlayer1 && showBlack) {
        blackGroupsText.append(group)
      } else if (!group.isOwnedByPlayer1 && showWhite) {
        whiteGroupsText.append(group)
      }
    }
    groupText.append(blackGroupsText)
    groupText.append(whiteGroupsText)

    groupText.toString
  }

  /**
   * pretty print a list of all the current groups (and the strings they contain)
   */
  def debugPrint(logLevel: Int): Unit = {
    debugPrint(logLevel, "---The groups are:", showBlack = true, showWhite = true)
  }

  /**
   * pretty print a list of all the current groups (and the strings they contain)
   */
  def debugPrint(logLevel: Int, title: String, showBlack: Boolean, showWhite: Boolean): Unit = {
    if (logLevel <= GameContext.getDebugMode) {
      GameContext.log(logLevel, title)
      GameContext.log(logLevel, this.toString(showBlack, showWhite))
      GameContext.log(logLevel, "----")
    }
  }

  // --- methods for ensuring internal consistency ----

  /**
   * for every stone one the board verify that it belongs to exactly one group
   */
  def confirmAllStonesInUniqueGroups(): Unit = {
    this.asScala.foreach(confirmStonesInOneGroup)
  }

  /**
   * confirm that the stones in this group are not contained in any other group.
   */
  def confirmStonesInOneGroup(group: IGoGroup): Unit = {
    group.getMembers.asScala.foreach { string =>
      this.asScala.foreach { g =>  // for each group on the board
        if (!g.equals(group)) {
          g.getMembers.asScala.foreach { s =>   // for each string in that group
            if (string.equals(s)) {
              debugPrint(0)
              assert(false, s"ERROR: $s contained by 2 groups")
            }
            confirmStoneInStringAlsoInGroup(s, g)
          }
        }
      }
    }
  }

  /**
   * verify that we contain no empty strings.
   */
  def confirmNoEmptyStrings(): Unit = {
    this.asScala.foreach { g =>
      g.asInstanceOf[IGoSet].getMembers.asScala.foreach { s =>
        val string = s.asInstanceOf[IGoString]
        assert(string.size > 0, s"There is an empty string in ${string.getGroup}")
      }
    }
  }

  /**
   * @param stone verify that this stone has a valid string and a group in the board's member list.
   */
  def confirmStoneInValidGroup(stone: GoBoardPosition): Unit = {
    val str = stone.getString
    assert(str != null, s"$stone does not belong to any string!")
    val g = str.getGroup
    var valid = false
    val gIt = this.iterator()
    while (!valid && gIt.hasNext) {
      val g1 = gIt.next()
      valid = g.equals(g1)
    }
    if (!valid) {
      this.debugPrint(0, "Confirm stones in valid groups failed. The groups are:",
        g.isOwnedByPlayer1, !g.isOwnedByPlayer1)
      assert(false,
        s"Error: This $stone does not belong to a valid group: $g \nThe valid groups are: $this")
    }
  }

  /**
   * Make sure that every stone in the string belongs in this group
   * @param str every stone in this string should also be in the specified group
   * @param group group that contains all the stones in the string.
   */
  private def confirmStoneInStringAlsoInGroup(str: IGoString, group: IGoGroup): Unit = {
    str.getMembers.asScala.foreach { pos =>
      if (pos.getGroup != null && !group.equals(pos.getGroup)) {
        this.debugPrint(0, "Confirm stones in one group failed. Groups are:", showBlack = true, showWhite = true)
        assert(false, s"$pos does not just belong to ${pos.getGroup} as its ancestry indicates. It also belongs to $group")
      }
    }
  }
}
