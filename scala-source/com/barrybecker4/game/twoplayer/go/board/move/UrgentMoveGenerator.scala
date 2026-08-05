// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.board.move

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition
import scala.jdk.CollectionConverters._

/**
 * Responsible for determining a set of reasonable next moves.
 *
 * @author Barry Becker
 */
final class UrgentMoveGenerator(board_ : GoBoard) {

  /**
   * @param moves all possible moves from the current board state for player to move next.
   * @param lastMove last stone placed on board
   * @return urgent moves from this position.
   */
  final def generateUrgentMoves(moves: MoveList[GoMove], lastMove: TwoPlayerMove): MoveList[GoMove] = {
    val lastMovePlayed = lastMove.asInstanceOf[GoMove]

    // just keep the moves that take captures
    val it = moves.iterator()
    while (it.hasNext) {
      val move = it.next()

      // urgent if we capture or atari other stones.
      val isUrgent = move.getNumCaptures > 0 || putsGroupInAtari(lastMovePlayed)
      if (isUrgent) {
        move.setUrgent(true)
      } else {
        it.remove()
      }
    }
    moves
  }

  /**
   * Determine if the last move caused atari on another group (without putting ourselves in atari).
   * @param lastMovePlayed last position just played.
   * @return true if the lastMovePlayed puts the lastPositions string in atari.
   */
  private def putsGroupInAtari(lastMovePlayed: GoMove): Boolean = {
    val lastPos = board_.getPosition(lastMovePlayed.getToLocation).asInstanceOf[GoBoardPosition]
    lastMovePlayed.numStonesAtaried(board_) >= UrgentMoveGenerator.CRITICAL_GROUP_SIZE &&
      lastPos.getString.getNumLiberties(board_) > 1
  }
}

object UrgentMoveGenerator {
  /**
   * Size of group that needs to be in atari before we consider a group urgent.
   * Perhaps this should be one.
   */
  private val CRITICAL_GROUP_SIZE = 3

  /**
   * @return true if move is in danger (jeopardy).
   */
  def inJeopardy(move: GoMove, board: GoBoard): Boolean = {
    move.numStonesAtaried(board) >= CRITICAL_GROUP_SIZE
  }
}
