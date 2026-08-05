/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove

import scala.jdk.CollectionConverters.*

/**
 * Stub implementation of TwoPlayerMove to help test the search strategy classes without needing
 * a specific game implementation.
 * @author Barry Becker
 */
object TwoPlayerMoveStub:
  val ROOT_ID = "root"

class TwoPlayerMoveStub(
    value: Int,
    isPlayer1: Boolean,
    toLocation: Location,
    private var parent_ : TwoPlayerMoveStub
) extends TwoPlayerMove(toLocation, value, new GamePiece(isPlayer1)):

  private val children_ = new MoveList[TwoPlayerMoveStub]()
  private var causesUrgency: Boolean = false

  if parent_ != null then
    parent_.addChild(this)

  def this(
      value: Int,
      isPlayer1: Boolean,
      toLocation: Location,
      isUrgent: Boolean,
      causesUrgency: Boolean,
      parent: TwoPlayerMoveStub
  ) =
    this(value, isPlayer1, toLocation, parent)
    setUrgent(isUrgent)
    setCausesUrgency(causesUrgency)

  def getParent: TwoPlayerMoveStub = parent_

  private def addChild(move: TwoPlayerMoveStub): Unit =
    children_.add(move)

  def getChildren: MoveList[TwoPlayerMoveStub] = children_

  def setCausesUrgency(value: Boolean): Unit =
    causesUrgency = value

  def causedUrgency: Boolean = causesUrgency

  /**
   * Unique move id that defines where in the game tree this move resides.
   * Each character in the id is a digit that corresponds to traveling a path
   * from the nth child from root to this node.
   */
  def getId: String =
    if parent_ == null then
      TwoPlayerMoveStub.ROOT_ID
    else
      var current = this
      var parentMove = getParent
      var id = ""
      while parentMove != null do
        val index = parentMove.getChildren.indexOf(current)
        assert(
          index >= 0,
          getValue + " not found among " + getParent.getChildren.size + " parent children of " + getParent
        )
        id = Integer.toString(index) + id
        current = parentMove
        parentMove = parentMove.getParent
      id

  def print(): Unit =
    print("", this)

  private def print(indent: String, subtreeRoot: TwoPlayerMoveStub): Unit =
    System.out.println(indent + subtreeRoot)
    for move <- subtreeRoot.getChildren.asScala do
      print("  " + indent, move.asInstanceOf[TwoPlayerMoveStub])

  override def toString: String =
    "id:" + getId + " value:" + getValue + " inh val:" + getInheritedValue +
      (if isUrgent then " urgent" else " ") + (if causedUrgency then "jeopardy" else "")
