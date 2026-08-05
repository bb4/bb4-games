/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.board

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.{GameContext, Move, MoveList}

/**
 * the Board describes the physical layout of the game.
 * It is an abstract class that provides a common implementation for many of the
 * methods in the IRectangularBoard.
 * Assumes an M*N grid.
 * Legal positions are [1, numRows_][1, numCols_]
 *
 * Games like gomoku, go, chess, checkers, go-moku,
 * shoji, othello, connect4, squares, Stratego, Blockade fit this pattern.
 * Other games like Risk, Galactic Empire, or Monopoly and might be supportable in the future.
 * They are harder because they do not have perfect information (i.e. they use dice).
 * and have multiple players.
 *
 * @author Barry Becker
 */
abstract class Board[M <: Move] extends IRectangularBoard[M] {

  var positions_ : BoardPositions = _

  private var moveList_ : MoveList[M] = new MoveList[M]()

  def this(b: Board[M]) = {
    this()
    this.setSize(b.getNumRows, b.getNumCols)
    moveList_ = b.moveList_.copy()
    positions_ = b.positions_.copy()
  }

  override def reset(): Unit = {
    getMoveList.clear()
    positions_.clear(getPositionPrototype)
  }

  def getPositionPrototype: BoardPosition = new BoardPosition(1, 1, null)

  override def setSize(numRows: Int, numCols: Int): Unit = {
    GameContext.log(3, s"Board rows cols== $numRows, $numCols")
    positions_ = new BoardPositions(numRows, numCols)
    reset()
  }

  final override def getNumRows: Int = positions_.getNumRows

  final override def getNumCols: Int = positions_.getNumCols

  def getMoveList: MoveList[M] = moveList_

  def getTypicalNumMoves: Int = 40

  override def getPosition(row: Int, col: Int): BoardPosition =
    positions_.getPosition(row, col)

  override def getPosition(loc: Location): BoardPosition =
    getPosition(loc.row, loc.col)

  def setPosition(pos: BoardPosition): Unit = {
    positions_.setPosition(pos)
  }

  override def makeMove(move: M): Boolean = {
    val legal = makeInternalMove(move)
    getMoveList.add(move)
    legal
  }

  override def undoMove(): M = {
    if (!getMoveList.isEmpty) {
      val move = getMoveList.removeLast()
      undoInternalMove(move)
      move
    } else {
      null.asInstanceOf[M]
    }
  }

  override def equals(b: Any): Boolean = {
    if (!b.isInstanceOf[Board[_]]) return false
    val board = b.asInstanceOf[Board[_]]
    board.positions_.equals(positions_)
  }

  override def hashCode(): Int = positions_.hashCode()

  def makeInternalMove(move: M): Boolean

  def undoInternalMove(move: M): Unit

  final override def inBounds(r: Int, c: Int): Boolean =
    positions_.inBounds(r, c)

  override def toString: String = positions_.toString

  def isInCorner(position: BoardPosition): Boolean =
    positions_.isInCorner(position)

  def isOnEdge(position: BoardPosition): Boolean =
    positions_.isOnEdge(position)
}
