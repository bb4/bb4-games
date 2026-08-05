/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers

import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard
import com.barrybecker4.optimization.parameter.ParameterArray
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy

class BoardEvaluator(
    val board: TwoPlayerBoard[_],
    val weights: ParameterArray
) {

  def calculateWorth: Int = {
    val score = new BoardEvaluator.Score()

    for (row <- 1 to CheckersBoard.SIZE) {
      val odd = row % 2
      for (j <- 1 to CheckersBoard.SIZE / 2) {
        val col = 2 * j - odd
        accumulateScore(row, score, board.getPosition(row, col))
      }
    }

    score.finalScore()
  }

  private def accumulateScore(row: Int, score: BoardEvaluator.Score, p: BoardPosition): Unit = {
    if (p.isOccupied) {
      val piece = p.getPiece.asInstanceOf[CheckersPiece]
      val isPlayer1 = piece.isOwnedByPlayer1
      val advancement = if (isPlayer1) row else CheckersBoard.SIZE - row
      val pieceScore = calcPieceScore(piece.isKing, advancement)
      
      if (isPlayer1) {
        score.positive += pieceScore
      } else {
        score.negative -= pieceScore
      }
    }
  }

  private def calcPieceScore(isKing: Boolean, advancement: Int): Int = {
    var score = 0
    if (isKing) {
      score += weights.get(CheckersWeights.KINGED_WEIGHT_INDEX).getValue.toInt
    } else {
      score += weights.get(CheckersWeights.PIECE_WEIGHT_INDEX).getValue.toInt
      score += (weights.get(CheckersWeights.ADVANCEMENT_WEIGHT_INDEX).getValue * advancement).toInt
    }
    score
  }
}

object BoardEvaluator {
  private class Score {
    var positive: Float = 0
    var negative: Float = 0

    private def sum(): Int = (positive + negative).toInt

    def finalScore(): Int = {
      if (positive == 0) {
        -SearchStrategy.WINNING_VALUE
      } else if (negative == 0) {
        SearchStrategy.WINNING_VALUE
      } else {
        sum()
      }
    }
  }
}
