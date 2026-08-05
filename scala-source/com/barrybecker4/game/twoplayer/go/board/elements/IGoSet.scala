/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements

import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionSet}
import java.util.Set

/**
 * A collection of GoMembers of some type (e.g. strings, stones, groups, etc).
 *
 * @author Barry Becker
 */
trait IGoSet extends IGoMember {

  /**
   * @return the Set containing the members
   */
  def getMembers: Set[? <: IGoMember]

  /**
   * @return true if pos is our enemy.
   */
  def isEnemy(pos: GoBoardPosition): Boolean

  /**
   * @return true if owned by player 1 (i.e. black).
   */
  def isOwnedByPlayer1: Boolean

  /**
   * Mark all members unvisited.
   * @param visited whether or not the members should be marked visited or unvisited.
   */
  def setVisited(visited: Boolean): Unit

  /**
   * @return the number of elements in the set
   */
  def size: Int

  /**
   * Get the number of liberties (open surrounding spaces)
   * @param board go board
   * @return the liberties/positions for the set.
   */
  def getLiberties(board: GoBoard): GoBoardPositionSet

  /** @return the number of liberties. */
  def getNumLiberties(board: GoBoard): Int
}
