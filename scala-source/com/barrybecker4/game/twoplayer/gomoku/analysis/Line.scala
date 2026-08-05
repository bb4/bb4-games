/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.analysis

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.{BoardPosition, GamePiece}
import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns

/**
 * Represents a run of symbols to be evaluated on the board.
 * All the symbols in the line should be of the same side.
 * @author Barry Becker
 */
class Line(private val evaluator: LineEvaluator) {

  val line: StringBuilder = new StringBuilder()

  /**
   * Extend the line by an additional position.
   * @param pos the position to extend ourselves by.
   */
  def append(pos: BoardPosition): Unit = {
    assert(pos != null, "Cannot append at null board position.")
    if (pos.getPiece == null) {
      line.append(Patterns.UNOCCUPIED)
    }
    else {
      val symb = pos.getPiece.getSymbol
      line.append(symb)
    }
  }

  /**
   * We return the difference in value between how the board looked before the
   * move was played (from both points of view) to after the move was played
   * (from both points of view. It's important that we look at it from both
   * sides because creating a near win is noticed from the moving players point of view
   * while blocks are noted from the opposing viewpoint.
   *
   * @param position position in the string to compute value difference for.
   * @return the difference in worth after making a move compared with before.
   *
   */
  def computeValueDifference(position: Int): Int = {
    val symb = line.charAt(position)
    if (symb == Patterns.UNOCCUPIED) {
      throw new IllegalStateException(
        "The piece just played at position " + position + " in line " + line + " must be a symbol.")
    }
    val player1Perspective = (symb == GamePiece.P1_SYMB)

    val len = line.length
    if (len < evaluator.getMinInterestingLength) {
      return 0
    }

    line.setCharAt(position, Patterns.UNOCCUPIED)
    val maxpos = len - 1

    var oldScore = evaluator.evaluate(line, player1Perspective, position, 0, maxpos)
    oldScore += evaluator.evaluate(line, !player1Perspective, position, 0, maxpos)

    line.setCharAt(position, symb)
    var newScore = evaluator.evaluate(line, player1Perspective, position, 0, maxpos)
    newScore += evaluator.evaluate(line, !player1Perspective, position, 0, maxpos)

    newScore - oldScore
  }

  /**
   * debugging aid
   */
  def worthDebug(dir: String, pos: Int, diff: Int): Unit = {
    GameContext.log(0, dir + " " + line + "  Pos: " + pos + "  difference:" + diff)
  }

  def getLineEvaluator: LineEvaluator = evaluator

  override def toString: String = line.toString
}
