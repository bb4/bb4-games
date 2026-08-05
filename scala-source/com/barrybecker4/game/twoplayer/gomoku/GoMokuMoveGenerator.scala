/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.common.{BestMoveFinder, TwoPlayerMove}
import com.barrybecker4.optimization.parameter.ParameterArray

import scala.jdk.CollectionConverters._
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE

/**
 * Responsible for determining a set of reasonable next moves.
 *
 * @author Barry Becker
 */
class GoMokuMoveGenerator {

  /**
   * @return all reasonably good next moves.
   */
  def generateMoves(searchable: GoMokuSearchable[_ <: GoMokuBoard], lastMove: TwoPlayerMove, weights: ParameterArray): MoveList[TwoPlayerMove] = {
    val moveList = new MoveList[TwoPlayerMove]()

    val pb = searchable.getBoard
    val candMoves = pb.getCandidateMoves

    val player1 = (lastMove == null) || !lastMove.isPlayer1

    val ncols = pb.getNumCols
    val nrows = pb.getNumRows

    for (i <- 1 to nrows) {
      for (j <- 1 to ncols) {
        if (candMoves.isCandidateMove(i, j)) {
          val lastValue = if (lastMove == null) 0 else lastMove.getValue
          val move = TwoPlayerMove.createMove(i, j, lastValue, new GamePiece(player1))

          searchable.makeInternalMove(move)
          move.setValue(searchable.worth(move, weights))
          searchable.undoInternalMove(move)
          moveList.add(move)
        }
      }
    }
    val finder = new BestMoveFinder[TwoPlayerMove](searchable.getSearchOptions.getBestMovesSearchOptions)
    finder.getBestMoves(moveList)
  }

  /**
   * @return a list of urgent moves (i.e positions that can result in a win for either player.
   */
  def generateUrgentMoves(searchable: GoMokuSearchable[_ <: GoMokuBoard], lastMove: TwoPlayerMove, weights: ParameterArray): MoveList[TwoPlayerMove] = {
    if (lastMove == null) {
      return new MoveList[TwoPlayerMove]()
    }
    val allMoves = findMovesForBothPlayers(searchable, lastMove, weights)

    val urgentMoves = new MoveList[TwoPlayerMove]()
    val currentPlayer = !lastMove.isPlayer1

    for (move <- allMoves.asScala) {
      if (Math.abs(move.getValue) >= WINNING_VALUE && !contains(move, urgentMoves)) {
        move.setUrgent(true)
        move.setPlayer1(currentPlayer)
        move.setPiece(new GamePiece(currentPlayer))
        urgentMoves.add(move)
      }
    }
    urgentMoves
  }

  /**
   * Consider both our moves and opponent moves.
   * @return Set of all next moves.
   */
  private def findMovesForBothPlayers(searchable: GoMokuSearchable[_ <: GoMokuBoard], lastMove: TwoPlayerMove, weights: ParameterArray): MoveList[TwoPlayerMove] = {
    val allMoves = new MoveList[TwoPlayerMove]()

    val moves = generateMoves(searchable, lastMove, weights)
    allMoves.addAll(moves)

    val oppLastMove = lastMove.copy()
    oppLastMove.setPlayer1(!lastMove.isPlayer1)
    val opponentMoves = generateMoves(searchable, oppLastMove, weights)
    for (m <- opponentMoves.asScala) {
      allMoves.add(m)
    }

    allMoves
  }

  /**
   * This version of contains does not consider the player when determining containment.
   * @return true of the MoveList contains the specified move.
   */
  private def contains(move: TwoPlayerMove, moves: MoveList[TwoPlayerMove]): Boolean = {
    for (m <- moves.asScala) {
      val moveLocation = m.getToLocation
      if (moveLocation.equals(move.getToLocation)) {
        return true
      }
    }
    false
  }
}
