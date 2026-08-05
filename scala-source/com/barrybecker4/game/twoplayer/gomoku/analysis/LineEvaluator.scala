// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis

import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * Test that we can correctly evaluate a run of symbols on the board.
 * @author Barry Becker
 */
class LineEvaluator(val patterns_ : Patterns, private val weights_ : ParameterArray) {

  def getMinInterestingLength: Int = patterns_.getMinInterestingLength

  /**
   * Evaluate a line (vertical, horizontal, or diagonal) from the
   * specified player point of view.
   *
   * @param line the line to evaluate
   * @param player1Perspective if true, then the first player just moved, else the second player.
   *   Note: this value does not guarantee anything about the symbol at position pos.
   *   It can be either players symbol or unoccupied.
   * @param pos the position that was just played (symbol).
   * @param minpos starting pattern index in line (usually 0).
   * @param maxpos last pattern index position in line (usually one less than the line magnitude).
   * @return the worth of a (vertical, horizontal, left diagonal, or right diagonal) line.
   */
  def evaluate(line: CharSequence, player1Perspective: Boolean, pos: Int, minpos: Int, maxpos: Int): Int = {
    assert(pos >= minpos && pos <= maxpos)
    val length = maxpos - minpos + 1
    if (length < patterns_.getMinInterestingLength) {
      return 0
    }

    val opponentSymb = if (player1Perspective) GamePiece.P2_SYMB else GamePiece.P1_SYMB

    if ((line.charAt(pos) == opponentSymb) && pos != minpos && pos != maxpos) {
      return (evaluate(line, player1Perspective, pos - 1, minpos, pos - 1)
        + evaluate(line, player1Perspective, pos + 1, pos + 1, maxpos))
    }
    getWeight(line, opponentSymb, pos, minpos, maxpos)
  }

  /**
   * @return the weight for the pattern if its a recognizable pattern, else return 0.
   */
  private def getWeight(line: CharSequence, opponentSymb: Char, pos: Int, minpos: Int, maxpos: Int): Int = {
    val index = getWeightIndex(line, opponentSymb, pos, minpos, maxpos)

    if (index >= 0) {
      val weight = weights_.get(index).getValue.toInt
      if (opponentSymb == GamePiece.P2_SYMB) weight else -weight
    } else {
      0
    }
  }

  /**
   * In general, we march from the position pos in the middle towards the ends of the
   * string. Marching stops when we encounter one of the following
   * conditions:
   *  - 2 blanks in a row (@@ we may want to allow this)
   *  - an opponent's blocking piece
   *  - the end of a line.
   * @param minpos first symbol in the sting to evaluate
   * @param maxpos last symbol in the sting to evaluate
   * @return the index to use for getting the weight based on the pattern formed by this line.
   */
  def getWeightIndex(line: CharSequence, opponentSymb: Char, pos: Int, minpos: Int, maxpos: Int): Int = {
    val pattern = getPattern(line, opponentSymb, pos, minpos, maxpos)
    patterns_.getWeightIndexForPattern(pattern)
  }

  protected def getPattern(line: CharSequence, opponentSymb: Char, pos: Int, minpos: Int, maxpos: Int): CharSequence = {
    val extractor = new PatternExtractor(line)
    extractor.getPattern(opponentSymb, pos, minpos, maxpos)
  }
}
