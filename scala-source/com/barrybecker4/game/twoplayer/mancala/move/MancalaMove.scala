/* Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.move

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBin

/**
  * Describes a change in state from one board
  * position to the next in a Mancala game.
  *
  * @author Barry Becker
  */
class MancalaMove(
  player1: Boolean,
  origin: Location,
  private val stonesMoved: Byte,
  value: Int
) extends TwoPlayerMove(origin, value, GamePiece(player1)):

  /**
    * If the first move results in placing a stone in the players storage, then they are allowed to go again.
    * This can happen more than once in a row.
    */
  private var followUpMove: MancalaMove = scala.compiletime.uninitialized

  /**
    * Since moves, have to be undone, if there were captures, then they need to be remembered.
    * On the final move, the opposite side is cleared of stones. That also needs to be remembered.
    * To do this, keep a map of location to number of stones captured
    */
  private var captures: Captures = Captures()

  /**
    * copy constructor
    */
  private def this(move: MancalaMove) =
    this(move.isPlayer1, move.getFromLocation, move.stonesMoved, move.getValue)
    this.followUpMove = move.followUpMove

  def setFollowUpMove(followUp: MancalaMove): Unit =
    followUpMove = followUp

  def getFollowUpMove: MancalaMove = followUpMove

  /**
    * These get set when the move is made and restored when the move is undone.
    * @param captures stones captured in the process of making this move.
    */
  def setCaptures(captures: Captures): Unit =
    this.captures = captures

  def getCaptures: Captures = captures

  /**
    * make a deep copy.
    */
  override def copy(): MancalaMove =
    MancalaMove(this)

  /**
    * @return the bin where the stones were drawn.
    */
  def getFromLocation: Location =
    // Note that the toLocation from the parent class is used internally.
    this.toLocation

  def getNumStonesSeeded: Byte = stonesMoved

  override def equals(obj: Any): Boolean =
    if !super.equals(obj) then return false
    obj match
      case that: MancalaMove =>
        if stonesMoved != that.stonesMoved then return false
        if followUpMove != null then
          if !followUpMove.equals(that.followUpMove) then return false
        else if that.followUpMove != null then return false
        if captures != null then
          captures.equals(that.captures)
        else
          that.captures == null
      case _ => false

  override def hashCode(): Int =
    var result = super.hashCode()
    result = 31 * result + stonesMoved.toInt
    result = 31 * result + (if followUpMove != null then followUpMove.hashCode() else 0)
    result = 31 * result + (if captures != null then captures.hashCode() else 0)
    result

  def toString(tab: String): String =
    val bldr = new StringBuilder(tab + super.toString)
    val ntab = "\n" + tab
    bldr.append(ntab).append("stones moved = ").append(stonesMoved)
    if !getCaptures.isEmpty then
      bldr.append(ntab).append("captures = ").append(getCaptures)
    if getFollowUpMove != null then
      bldr.append(ntab).append("[")
      bldr.append("\n").append(getFollowUpMove.toString(tab + "   "))
      bldr.append(ntab).append("]")
    bldr.toString

  override def toString: String = toString("")

object MancalaMove:
  /**
    * factory method for getting new moves. It uses recycled objects if possible.
    * @return the newly created move.
    */
  def createMove(isPlayer1: Boolean, loc: Location, value: Int, bin: MancalaBin): MancalaMove =
    MancalaMove(isPlayer1, loc, bin.getNumStones, value)
