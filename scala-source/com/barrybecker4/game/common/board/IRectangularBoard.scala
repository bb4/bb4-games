/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.board

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.Move

/**
 * This is the interface that all rectangular game boards should implement.
 * We assume that the board is composed of a 2D array of BoardPositions.
 *
 * Providing both an interface and an abstract implementation is a pattern
 * which maximizes flexibility in a framework. The interface defines the
 * public contract. The abstract class may be package private if we don't
 * want to expose it. Or other classes may implement this interface without
 * extending the abstract base class.
 *
 * @see Board for the base implementation of this interface
 * @author Barry Becker
 */
trait IRectangularBoard[M <: Move] extends IBoard[M] {

  override def reset(): Unit

  def setSize(numRows: Int, numCols: Int): Unit

  def getNumRows: Int

  def getNumCols: Int

  def getMaxNumMoves: Int

  def getPosition(row: Int, col: Int): BoardPosition

  def getPosition(loc: Location): BoardPosition

  def inBounds(r: Int, c: Int): Boolean

  override def copy(): IRectangularBoard[M]
}
