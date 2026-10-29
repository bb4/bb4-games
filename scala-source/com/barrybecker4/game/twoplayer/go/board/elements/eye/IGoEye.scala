/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements.eye

import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.{EyeInformation, EyeStatus}
import com.barrybecker4.game.twoplayer.go.board.elements.IGoSet
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPositionSet

/**
 * A GoEye is composed of a strongly connected set of empty spaces (and possibly some dead enemy stones).
 */
trait IGoEye extends IGoSet {

  def getStatus: EyeStatus

  def getInformation: EyeInformation

  def getEyeTypeName: String

  def getNumCornerPoints: Int

  def getNumEdgePoints: Int

  /**
   * @return the group that this eye belongs to.
   */
  def getGroup: IGoGroup

  /**
   * @return the hashSet containing the members
   */
  override def getMembers: GoBoardPositionSet

  /**
   * empty the positions from the eye.
   */
  def clear(): Unit

  /**
   * @return true if unconditionally alive.
   */
  def isUnconditionallyAlive: Boolean

  def setUnconditionallyAlive(unconditionallyAlive: Boolean): Unit
}
