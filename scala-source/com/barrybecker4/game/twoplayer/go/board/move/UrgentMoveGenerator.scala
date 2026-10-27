// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.board.move

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.{NeighborAnalyzer, NeighborType}
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition
import com.barrybecker4.game.twoplayer.go.board.elements.string.GoStringSet
import scala.jdk.CollectionConverters._

/**
 * Responsible for determining a set of reasonable next moves.
 *
 * @author Barry Becker
 */
final class UrgentMoveGenerator(board_ : GoBoard) {

  /**
   * Keep candidates that capture, escape an atari, or put an enemy string into atari.
   * The urgent bit is set when the move is generated. Recompute it if a caller did not.
   */
  def generateUrgentMoves(moves: MoveList[GoMove], lastMove: TwoPlayerMove): MoveList[GoMove] = {
    val it = moves.iterator()
    while (it.hasNext) {
      val move = it.next()
      if (!move.isUrgent && UrgentMoveGenerator.isUrgentCandidate(move, board_)) {
        move.setUrgent(true)
      }
      if (!move.isUrgent) {
        it.remove()
      }
    }
    moves
  }
}

object UrgentMoveGenerator {
  /** Any string put into atari extends the search, including a single stone. */
  private val CRITICAL_GROUP_SIZE = 1

  /**
   * True when this candidate captures, plays the last liberty of a friendly string,
   * or takes one liberty from an enemy string that has two.
   * The point is still empty; call this before the move is played.
   */
  def isUrgentCandidate(move: GoMove, board: GoBoard): Boolean = {
    if (move == null || move.isPassOrResignation) {
      return false
    }
    val pos = board.getPosition(move.getToRow, move.getToCol).asInstanceOf[GoBoardPosition]
    if (pos.isOccupied) {
      return false
    }
    val neighbors = new NeighborAnalyzer(board).getNobiNeighbors(pos, move.isPlayer1, NeighborType.OCCUPIED)
    val seen = new GoStringSet()
    neighbors.asScala.exists { nbr =>
      val string = nbr.getString
      string != null && seen.add(string) && {
        val libs = string.getNumLiberties(board)
        val enemy = nbr.getPiece.isOwnedByPlayer1 != move.isPlayer1
        (enemy && (libs == 1 || libs == 2)) || (!enemy && libs == 1)
      }
    }
  }

  /** True when the move already on the board has put at least one enemy stone into atari. */
  def inJeopardy(move: GoMove, board: GoBoard): Boolean = {
    move != null && !move.isPassOrResignation && move.numStonesAtaried(board) >= CRITICAL_GROUP_SIZE
  }
}
