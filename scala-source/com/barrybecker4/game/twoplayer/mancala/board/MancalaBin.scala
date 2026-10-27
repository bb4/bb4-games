package com.barrybecker4.game.twoplayer.mancala.board

import com.barrybecker4.game.common.board.GamePiece

/**
  * @author Barry Becker
  */
class MancalaBin(
  player1: Boolean,
  private var numStones: Byte,
  val isHome: Boolean
) extends GamePiece(player1):

  /**
    * @return create a deep copy
    */
  override def copy(): MancalaBin =
    MancalaBin(isOwnedByPlayer1, numStones, isHome)

  def getNumStones: Byte = numStones

  /** get and then clear this bins stones */
  def takeStones(): Int =
    val num = numStones
    numStones = 0
    num

  def increment(): Unit =
    this.numStones = (this.numStones + 1).toByte

  /**
    * @param num the number of stones to increment this bin by. May be negative.
    */
  def increment(num: Int): Unit =
    this.numStones = (this.numStones + num).toByte
    assert(this.numStones >= 0, s"The number of stones ($numStones) went below 0 when we added $num.")

  override def toString: String =
    val homeStr = if isHome then "Home" else ""
    s"${homeStr}Bin($getNumStones)"
