/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis

import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition
import com.barrybecker4.game.twoplayer.go.board.elements.string.IGoString
import scala.collection.mutable
import scala.jdk.CollectionConverters._

/**
 * Creates a set of reasonable next moves for a given player.
 * In theory, all empties should be considered, but in practice, we keep
 * a shorter list of reasonable moves lest things get intractable.
 * Immutable.
 */
class CandidateMoveAnalyzer(board_ : GoBoard) {

  import CandidateMoveAnalyzer._

  private val size_ : Int = board_.getNumRows

  /** this is an auxiliary structure to help determine candidate moves. */
  private val candidateMoves_ : Array[Array[Boolean]] = Array.ofDim[Boolean](size_ + 1, size_ + 1)

  initialize()

  /**
   * If there is any chance at all that this could be a reasonable move, true is returned.
   *
   * @return true if this position is a reasonable next move.
   */
  final def isCandidateMove(row: Int, col: Int): Boolean = candidateMoves_(row)(col)

  /**
   * @return number of candidate moves found.
   */
  final def getNumCandidates: Int = {
    var num = 0
    for (i <- 1 to size_) {
      for (j <- 1 to size_) {
        if (isCandidateMove(i, j)) num += 1
      }
    }
    num
  }

  /**
   * we start with a default list of good starting moves, and
   * add to it all moves within 2 spaces of those that have already been played.
   *
   * Fill a 2 stone wide strip on the 3rd and 4rth lines of the board.
   * This includes the star points and many others as candidates to consider
   */
  private def initialize(): Unit = {
    for (i <- 3 to size_ - 2) {
      tryToAddCandidateMove(board_.getPosition(i, 3))
      tryToAddCandidateMove(board_.getPosition(i, 4))
      tryToAddCandidateMove(board_.getPosition(i, size_ - 2))
      tryToAddCandidateMove(board_.getPosition(i, size_ - 3))
    }
    for (j <- 5 to size_ - 4) {
      tryToAddCandidateMove(board_.getPosition(3, j))
      tryToAddCandidateMove(board_.getPosition(4, j))
      tryToAddCandidateMove(board_.getPosition(size_ - 2, j))
      tryToAddCandidateMove(board_.getPosition(size_ - 3, j))
    }
    // also make the center space a candidate move
    tryToAddCandidateMove(board_.getPosition((size_ + 1) >> 1, (size_ + 1) >> 1))

    determineAdjacentCandidates()
    addFirstLineLibertiesOfWeakStrings()
  }

  /**
   * A descent or an atari escape on the first line is a candidate even when the
   * string is not otherwise allowed to spawn first-line points. Strings with more
   * than two liberties do not open the edge.
   */
  private def addFirstLineLibertiesOfWeakStrings(): Unit = {
    val seen = mutable.Set.empty[IGoString]
    for (i <- 1 to size_) {
      for (j <- 1 to size_) {
        val pos = board_.getPosition(i, j).asInstanceOf[GoBoardPosition]
        if (pos.isOccupied && pos.getString != null && seen.add(pos.getString)) {
          val string = pos.getString
          if (string.getNumLiberties(board_) <= 2) {
            string.getLiberties(board_).asScala.foreach { liberty =>
              if (isFirstLine(liberty.getRow, liberty.getCol)) {
                tryToAddCandidateMove(liberty)
              }
            }
          }
        }
      }
    }
  }

  /**
   * this method splats a footprint of trues around the current moves.
   * later we look for empty spots that are true for candidate moves
   */
  private def determineAdjacentCandidates(): Unit = {
    for (i <- 1 to size_) {
      for (j <- 1 to size_) {
        val pos = board_.getPosition(i, j).asInstanceOf[GoBoardPosition]
        if (pos.isOccupied) {
          addCandidateMoves(pos)
        }
      }
    }
  }

  /**
   * This method splats a footprint of trues around the specified move.
   * Interior stones must not spawn first-line candidates (opening edge blunders).
   * Stones already on the 1st/2nd line may, so edge fights stay searchable.
   * @param stone position to add candidate moves around.
   */
  private def addCandidateMoves(stone: GoBoardPosition): Unit = {
    val seedNearEdge = lineNumber(stone.getRow, stone.getCol) <= EDGE_BAND_LINE
    val startrow = math.max(stone.getRow - CANDIDATE_MOVE_OFFSET, 1)
    val stoprow = math.min(stone.getRow + CANDIDATE_MOVE_OFFSET, size_)
    val startcol = math.max(stone.getCol - CANDIDATE_MOVE_OFFSET, 1)
    val stopcol = math.min(stone.getCol + CANDIDATE_MOVE_OFFSET, size_)
    for (i <- startrow to stoprow) {
      for (j <- startcol to stopcol) {
        if (seedNearEdge || !isFirstLine(i, j)) {
          val pos = board_.getPosition(i, j).asInstanceOf[GoBoardPosition]
          tryToAddCandidateMove(pos)
        }
      }
    }
  }

  /**
   * Add only if unoccupied and not an unconditionally alive eye.
   * never add a stone from either side to an unconditionally alive eye. There is no advantage to it.
   * @param position the position to try adding as a possible candidate move.
   */
  private def tryToAddCandidateMove(position: BoardPosition): Unit = {
    val pos = position.asInstanceOf[GoBoardPosition]

    if (pos.isUnoccupied && !(pos.getEye != null && pos.getEye.isUnconditionallyAlive)) {
      candidateMoves_(pos.getRow)(pos.getCol) = true
    }
  }

  private def isFirstLine(row: Int, col: Int): Boolean =
    row == 1 || col == 1 || row == size_ || col == size_

  private def lineNumber(row: Int, col: Int): Int = {
    val rowmin = math.min(row, size_ - row + 1)
    val colmin = math.min(col, size_ - col + 1)
    math.min(rowmin, colmin)
  }
}

object CandidateMoveAnalyzer {
  /** Distance from already played stones that we will splat a footprint of candidate stones to add. */
  private val CANDIDATE_MOVE_OFFSET = 2

  /** Stones on this line or closer may expand candidates onto the first line. */
  private val EDGE_BAND_LINE = 2
}
