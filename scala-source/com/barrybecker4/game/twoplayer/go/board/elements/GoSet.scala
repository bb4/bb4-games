/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements

/**
 * A GoSet is an abstract class representing a set of go entities
 * (stones, strings, groups, or armies)
 */
abstract class GoSet extends IGoSet {

  /** true if this set of stones is owned by player one (black) */
  var ownedByPlayer1_ : Boolean = false

  initializeMembers()

  /**
   * @return true if set is owned by player one
   */
  override final def isOwnedByPlayer1: Boolean = ownedByPlayer1_

  /**
   * @return the number of stones in the set
   */
  override final def size: Int = getMembers.size

  /**
   * Some initialization of the set members.
   */
  def initializeMembers(): Unit
}
