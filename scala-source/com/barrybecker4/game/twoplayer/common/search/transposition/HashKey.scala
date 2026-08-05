/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.transposition

import com.barrybecker4.common.geometry.Location

/**
 * As an experiment to see the sequence of moves that led to a certain hash key,
 * include the move history in the key itself. Should never do this in practice.
 *
 * @author Barry Becker
 */
class HashKey(private var key: Long = 0L) {

  def this() = this(0L)

  /**
   * copy constructor
   */
  def this(hashKey: HashKey) = this(hashKey.getKey)

  def copy(): HashKey = new HashKey(this)

  def applyMove(move: Location, specialNumber: Long): Unit = {
    key ^= specialNumber
  }

  def matches(otherKey: Long): Boolean = key == otherKey

  override def equals(o: Any): Boolean = o match {
    case hashKey: HashKey => key == hashKey.key
    case _ => false
  }

  def getKey: Long = key

  override def hashCode(): Int = key.hashCode()

  override def toString: String = java.lang.Long.toBinaryString(key)
}
