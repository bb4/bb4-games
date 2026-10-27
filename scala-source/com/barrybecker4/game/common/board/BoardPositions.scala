/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.board

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.common.GameContext

/**
 * Represents the array of positions on the board.
 * Assumes an M*N grid.
 * Legal positions are [1, numRows_][1, numCols_]
 *
 * @author Barry Becker
 */
class BoardPositions(numRows: Int, numCols: Int) {

  var positions_ : Array[Array[BoardPosition]] = scala.compiletime.uninitialized
  private var numRows_ : Int = scala.compiletime.uninitialized
  private var numCols_ : Int = scala.compiletime.uninitialized
  private var rowsTimesCols_ : Int = scala.compiletime.uninitialized

  setSize(numRows, numCols)

  def this(b: BoardPositions) = {
    this(b.getNumRows, b.getNumCols)
    for (i <- 1 to getNumRows; j <- 1 to getNumCols) {
      positions_(i)(j) = b.getPosition(i, j).copy()
    }
  }

  def copy(): BoardPositions = new BoardPositions(this)

  def setSize(numRows: Int, numCols: Int): Unit = {
    numRows_ = numRows
    numCols_ = numCols
    rowsTimesCols_ = numRows_ * numCols_
    positions_ = Array.ofDim[BoardPosition](getNumRows + 1, getNumCols + 1)
  }

  def clear(proto: BoardPosition): Unit = {
    for (i <- 1 to getNumRows; j <- 1 to getNumCols) {
      proto.setLocation(new ByteLocation(i, j))
      positions_(i)(j) = proto.copy()
    }
  }

  final def getNumRows: Int = numRows_

  final def getNumCols: Int = numCols_

  final def getNumBoardSpaces: Int = rowsTimesCols_

  final def getPosition(row: Int, col: Int): BoardPosition = {
    if (row < 1 || row > numRows_ || col < 1 || col > numCols_) {
      return null
    }
    positions_(row)(col)
  }

  final def getPosition(loc: Location): BoardPosition =
    getPosition(loc.row, loc.col)

  def setPosition(pos: BoardPosition): Unit = {
    positions_(pos.getRow)(pos.getCol) = pos
  }

  override def equals(b: Any): Boolean = {
    if (!b.isInstanceOf[BoardPositions]) return false
    val board = b.asInstanceOf[BoardPositions]
    (1 to getNumRows).forall { i =>
      (1 to getNumCols).forall { j =>
        val p1 = this.getPosition(i, j)
        val p2 = board.getPosition(i, j)
        assert(p1 != null)
        assert(p2 != null)
        if (p1.isOccupied != p2.isOccupied) {
          GameContext.log(2, s"Inconsistent occupation status  p1=$p1 and p2=$p2")
          false
        } else if (p1.isOccupied) {
          val piece1 = p1.getPiece
          val piece2 = p2.getPiece
          if (piece1.isOwnedByPlayer1 != piece2.isOwnedByPlayer1 ||
              piece1.getType != piece2.getType) {
            GameContext.log(2, s"There was an inconsistency between p1=$p1 and $p2")
            false
          } else true
        } else true
      }
    }
  }

  override def hashCode(): Int = {
    var hash = 0
    val nRows = getNumRows
    val nCols = getNumCols
    for (i <- 1 to nRows) {
      val pos = (i - 1) * nCols
      for (j <- 1 to nCols) {
        val p1 = this.getPosition(i, j)
        assert(p1 != null)
        if (p1.isOccupied) {
          hash += 2 * (pos + j) + (if (p1.getPiece.isOwnedByPlayer1) 1 else 2)
        }
      }
    }
    hash
  }

  final def inBounds(r: Int, c: Int): Boolean =
    !(r < 1 || r > getNumRows || c < 1 || c > getNumCols)

  override def toString: String = {
    val bldr = new StringBuilder(1000)
    bldr.append("\n")
    val nRows = getNumRows
    val nCols = getNumCols
    for (i <- 1 to nRows) {
      for (j <- 1 to nCols) {
        val pos = this.getPosition(i, j)
        if (pos.isOccupied) {
          bldr.append(pos.getPiece)
        } else {
          bldr.append(" _ ")
        }
      }
      bldr.append("\n")
    }
    bldr.toString()
  }

  def isInCorner(position: BoardPosition): Boolean = {
    (position.getRow == 1 && position.getCol == 1) ||
    (position.getRow == getNumRows && position.getCol == getNumCols) ||
    (position.getRow == getNumRows && position.getCol == 1) ||
    (position.getRow == 1 && position.getCol == getNumCols)
  }

  def isOnEdge(position: BoardPosition): Boolean = {
    position.getRow == 1 || position.getRow == getNumRows ||
    position.getCol == 1 || position.getCol == getNumCols
  }
}
