/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common

/**
 * This base class describes a change in state from one board
 * position to the next in a game.
 *
 * @author Barry Becker
 */
class Move() extends Comparable[Move] {

  private var value_ : Int = 0

  def this(move: Move) = {
    this()
    value_ = move.value_
  }

  def copy(): Move = new Move(this)

  override def compareTo(move: Move): Int = {
    if (getValue < move.getValue) {
      -1
    } else if (getValue > move.getValue) {
      1
    } else {
      0
    }
  }

  override def toString: String = s"The value of this move is $value_"

  def getValue: Int = value_

  def setValue(value: Int): Unit = {
    this.value_ = value
  }
}
