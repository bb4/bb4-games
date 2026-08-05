/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess

import com.barrybecker4.game.common.GameWeights

class ChessWeights extends GameWeights(
  ChessWeights.DEFAULT_CHESS_WEIGHTS,
  ChessWeights.MIN_CHESS_WEIGHTS,
  ChessWeights.MAX_CHESS_WEIGHTS,
  ChessWeights.CHESS_WEIGHT_SHORT_DESCRIPTIONS,
  ChessWeights.CHESS_WEIGHT_DESCRIPTIONS
)

object ChessWeights {
  private val DEFAULT_CHESS_WEIGHTS: Array[Double] = 
    Array(4.4, 14.0, 17.0, 29.0, 36.0, GameWeights.ASSUMED_WINNING_VALUE, 1.1)
  
  private val MAX_CHESS_WEIGHTS: Array[Double] = 
    Array(30.0, 200.0, 200.0, 200.0, 200.0, 2 * GameWeights.ASSUMED_WINNING_VALUE, 20.0)
  
  private val MIN_CHESS_WEIGHTS: Array[Double] = 
    Array(1.0, 2.0, 2.0, 2.0, 3.0, GameWeights.ASSUMED_WINNING_VALUE, 0.0)

  private val CHESS_WEIGHT_SHORT_DESCRIPTIONS: Array[String] = Array(
    "Pawn weight",
    "Knight weight",
    "Bishop weight",
    "Rook weight",
    "Queen weight",
    "King weight",
    "Pawn Advancement weight"
  )

  private val CHESS_WEIGHT_DESCRIPTIONS: Array[String] = Array(
    "Weight to associate with each remaining pawn",
    "Weight to associate with Knights",
    "Weight to associate with Bishops",
    "Weight to associate with Rooks",
    "Weight to associate with the Queen",
    "Weight to associate with the King",
    "Weight to associate with pawn advancement"
  )

  val PAWN_WEIGHT_INDEX: Int = 0
  val KNIGHT_WEIGHT_INDEX: Int = 1
  val BISHOP_WEIGHT_INDEX: Int = 2
  val ROOK_WEIGHT_INDEX: Int = 3
  val QUEEN_WEIGHT_INDEX: Int = 4
  val KING_WEIGHT_INDEX: Int = 5
  val PAWN_ADVANCEMENT_WEIGHT_INDEX: Int = 6
}
