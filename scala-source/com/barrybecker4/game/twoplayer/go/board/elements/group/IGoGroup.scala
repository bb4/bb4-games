/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements.group

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.game.twoplayer.go.board.elements.IGoSet
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionSet}
import com.barrybecker4.game.twoplayer.go.board.elements.string.{GoStringSet, IGoString}

/**
 * Makes some unit tests much simpler if we create the tests to use this trait instead
 * of the full-blown GoGroup class.
 *
 * @author Barry Becker
 */
trait IGoGroup extends IGoSet {

  def addMember(string: IGoString): Unit

  override def getMembers: GoStringSet

  override def isOwnedByPlayer1: Boolean

  def addChangeListener(listener: GroupChangeListener): Unit

  def getNumStones: Int

  def containsStone(stone: GoBoardPosition): Boolean

  def remove(string: IGoString): Unit

  def getStones: GoBoardPositionSet

  def updateTerritory(health: Float): Unit

  def findBoundingBox(): Box

  def toHtml: String
}
