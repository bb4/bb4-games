/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements.string

import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.IGoSet
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionSet}

/**
 * Makes some unit tests much simpler if we create the tests to use this trait instead
 * of the full-blown GoString or GoEye class.
 */
trait IGoString extends IGoSet {

  /**
   * @return set of member positions.
   */
  override def getMembers: GoBoardPositionSet

  /**
   * @return the group that this string belongs to.
   */
  def getGroup: IGoGroup

  /**
   * @param pos position to test for inclusion in this string
   * @return true if the specified position is a member of this string.
   */
  def contains(pos: GoBoardPosition): Boolean

  /**
   * @return true if this string is unconditionally alive according to Benson's algorithm.
   */
  def isUnconditionallyAlive: Boolean

  def setUnconditionallyAlive(unconditionallyAlive: Boolean): Unit

  def remove(stone: GoBoardPosition, board: GoBoard): Unit

  def updateTerritory(health: Float): Unit

  def setGroup(group: IGoGroup): Unit
}
