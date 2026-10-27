/* Copyright by Barry G. Becker, 2004-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.move

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.{GameContext, MoveList}
import com.barrybecker4.game.common.board.{Board, CaptureList}
import com.barrybecker4.game.twoplayer.common.{BestMoveFinder, TwoPlayerMove}
import com.barrybecker4.game.twoplayer.go.board.{GoBoard, GoProfiler, GoSearchable}
import com.barrybecker4.game.twoplayer.go.board.analysis.CandidateMoveAnalyzer
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}
import com.barrybecker4.optimization.parameter.ParameterArray
import scala.jdk.CollectionConverters._

/**
 * Responsible for determining a set of reasonable next moves.
 *
 * @author Barry Becker
 */
final class GoMoveGenerator(searchable_ : GoSearchable) {

  /**
   * @return all reasonably good next moves with statically evaluated scores.
   */
  final def generateEvaluatedMoves(lastMove: GoMove, weights: ParameterArray): MoveList[GoMove] = {
    val prof = GoProfiler.getInstance
    prof.startGenerateMoves()

    val moveList = generatePossibleMoves(lastMove)

    moveList.asScala.foreach { move =>
      setMoveValue(weights, move)
    }
    val finder = new BestMoveFinder[GoMove](searchable_.getSearchOptions.getBestMovesSearchOptions)
    val bestMoves = finder.getBestMoves(moveList)

    addPassingMoveIfNeeded(lastMove, bestMoves)

    prof.stopGenerateMoves()
    bestMoves
  }

  /**
   * @return all possible reasonable next moves. We try to limit to reasonable moves as best we can, but that
   * is difficult without static evaluation. At least no illegal moves will be returned.
   */
  final def generatePossibleMoves(lastMove: GoMove): MoveList[GoMove] = {
    val board = searchable_.getBoard
    val moveList = new MoveList[GoMove]()
    val nCols = board.getNumCols
    val nRows = board.getNumRows

    val candidateMoves = new CandidateMoveAnalyzer(board)

    val player1 = (lastMove == null) || !lastMove.isPlayer1
    val lastMoveValue = if (lastMove == null) 0 else lastMove.getValue

    for (i <- 1 to nCols) {
      for (j <- 1 to nRows) {
        // if its a candidate move and not an immediate take-back (which would break the rule of ko)
        if (candidateMoves.isCandidateMove(j, i) && !GoMoveGenerator.isTakeBack(j, i, lastMove, board)) {
          val m = new GoMove(new ByteLocation(j, i), lastMoveValue, new GoStone(player1))

          if (m.isSuicidal(board)) {
            GameContext.log(3, s"The move was a suicide (can't add it to the list): $m")
          } else {
            moveList.add(m)
          }
        }
      }
    }
    moveList
  }

  /**
   * Make the generated move, determine its value, set it into the move, and undo the move on the board.
   */
  private def setMoveValue(weights: ParameterArray, m: GoMove): Unit = {
    val prof = GoProfiler.getInstance
    prof.stopGenerateMoves()
    searchable_.makeInternalMove(m)

    m.setValue(searchable_.worth(m, weights))

    // now revert the board
    searchable_.undoInternalMove(m)
    prof.startGenerateMoves()
  }

  /**
   * If we are well into the game, include a passing move.
   * if none of the generated moves have an inherited value better than the passing move
   * (which just uses the value of the current move) then we should pass.
   */
  private def addPassingMoveIfNeeded(lastMove: TwoPlayerMove, moveList: MoveList[GoMove]): Unit = {
    val b = searchable_.getBoard
    if (searchable_.getNumMoves() > (b.getNumCols + b.getNumRows)) {
      val passMove = GoMove.createPassMove(lastMove.getValue, !(lastMove.isPlayer1))
      moveList.add(moveList.size, passMove)
    }
  }
}

object GoMoveGenerator {
  /**
   * It is a take-back move if the proposed move position (row,col) would immediately replace the last captured piece
   * and capture the stone that did the capturing.
   * @return true of this is an immediate take-back (not allowed in go - see "rule of ko")
   */
  def isTakeBack(row: Int, col: Int, lastMove: GoMove, board: GoBoard): Boolean = {
    if (lastMove == null) return false

    val captures = lastMove.getCaptures
    if (captures != null && captures.size == 1) {
      val capture = captures.getFirst.asInstanceOf[GoBoardPosition]
      if (capture.getRow == row && capture.getCol == col) {
        val lastStone = board.getPosition(lastMove.getToRow, lastMove.getToCol).asInstanceOf[GoBoardPosition]
        if (lastStone.getNumLiberties(board) == 1 && lastStone.getString.getMembers.size == 1) {
          GameContext.log(2, "it is a takeback ")
          return true
        }
      }
    }
    false
  }
}
