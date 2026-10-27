// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis

import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns

/**
 * Represents a run of symbols to be evaluated on the board.
 * @author Barry Becker
 */
class MockLineEvaluator(minInteresting: Int) extends LineEvaluator(null, null):

  override def getMinInterestingLength: Int = minInteresting

  /**
   * Return one if the move just placed is our symbol, else return -1.
   */
  override def evaluate(line: CharSequence, player1Perspective: Boolean, pos: Int, minpos: Int, maxpos: Int): Int =
    val symb = line.charAt(pos)
    if symb == Patterns.UNOCCUPIED then return 0
    val opponentSymb = if player1Perspective then GamePiece.P2_SYMB else GamePiece.P1_SYMB
    val sign = if symb == opponentSymb then -1 else 1
    if player1Perspective then sign else -sign
