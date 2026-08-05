package com.barrybecker4.game.twoplayer.hex

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.math.MathUtil

/**
 * Representation of the candidate next moves on a Hex Game Board.
 * Candidate moves should be neighbors of existing moves + borders + some others.
 * Immutable after construction.
 */
class HexCandidateMoves(private val numRows: Int, private val numCols: Int,
                       private val candidateMoves_ : Array[Array[Boolean]]):

  def this(board: HexBoard) =
    this(board.getNumRows, board.getNumCols, 
         Array.ofDim[Boolean](board.getNumRows + 2, board.getNumCols + 2))
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
  protected def determineCandidateMoves(board: HexBoard): Unit =
    for i <- 1 to numRows do
      for j <- 1 to numCols do
        if board.getPosition(i, j).isOccupied then
          val location = ByteLocation(i.toByte, j.toByte)
          for offset <- HexCandidateMoves.SPLAT do
            val pos = board.getPosition(location.incrementOnCopy(offset))
            setCandidate(pos)

    for k <- 0 until HexCandidateMoves.NUM_RANDOM_PLACEMENTS do
      val loc = ByteLocation(
        (MathUtil.RANDOM.nextInt(numRows) + 1).toByte,
        (MathUtil.RANDOM.nextInt(numCols) + 1).toByte)
      val pos = board.getPosition(loc)
      setCandidate(pos)

object HexCandidateMoves:
  /** 6 footprint offsets around a single hexagonal position. */
  private val SPLAT = Array(
    ByteLocation(-1, 0),
    ByteLocation(-1, 1),
    ByteLocation(0, 1),
    ByteLocation(1, 1),
    ByteLocation(1, 0),
    ByteLocation(0, -1)
  )

  private val NUM_RANDOM_PLACEMENTS = 20
