// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.common.board.BoardPosition

import scala.compiletime.uninitialized

/**
 * Representation of the candidate next moves on a GoMoku Game Board.
 * Immutable after construction.
 * @author Barry Becker
 */
class CandidateMoves protected ():

  /** 8 footprint offsets around a single position. */
  private val SPLAT: Array[ByteLocation] = Array(
    new ByteLocation(-1, -1),
    new ByteLocation(-1, 0),
    new ByteLocation(-1, 1),
    new ByteLocation(0, -1),
    new ByteLocation(0, 1),
    new ByteLocation(1, -1),
    new ByteLocation(1, 0),
    new ByteLocation(1, 1)
  )

  private var numRows: Int = 0
  private var numCols: Int = 0

  /** this is an auxiliary structure to help determine candidate moves. should extract to sep class. */
  private var candidateMoves_ : Array[Array[Boolean]] = uninitialized

  /**
   * Constructor
   * @param board the gomoku board to determine candidate moves for
   */
  def this(board: GoMokuBoard) =
    this()
    this.numRows = board.getNumRows
    this.numCols = board.getNumCols
    this.candidateMoves_ = Array.ofDim[Boolean](numRows + 2, numCols + 2)
    determineCandidateMoves(board)

  /**
   * We consider only those spaces bordering on non-empty spaces.
   * In theory all empties should be considered, but in practice only
   * those bordering existing moves are likely to be favorable.
   *
   * @return true if this position is a possible next move
   */
  def isCandidateMove(row: Int, col: Int): Boolean =
    candidateMoves_(row)(col)

  def setCandidate(pos: BoardPosition): Unit =
    if pos != null && !pos.isOccupied then
      candidateMoves_(pos.getRow)(pos.getCol) = true

  /**
   * This method splats a footprint of trues around the current moves.
   * later we look for empty spots that are true for candidate moves.
   */
  protected def determineCandidateMoves(board: GoMokuBoard): Unit =
    var hasCandidates = false
    var i = 1
    while i <= numRows do
      var j = 1
      while j <= numCols do
        if board.getPosition(i, j).isOccupied then
          val location: Location = new ByteLocation(i, j)
          for offset <- SPLAT do
            val pos = board.getPosition(location.incrementOnCopy(offset))
            setCandidate(pos)
          hasCandidates = true
        j += 1
      i += 1
    // edge case when no moves on the board - just use the center
    val center: Location = new ByteLocation(numRows / 2 + 1, numCols / 2 + 1)
    val pos = board.getPosition(center)
    if !hasCandidates then
      setCandidate(pos)
