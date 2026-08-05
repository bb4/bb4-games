// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis

import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns

/**
 * Extract a pattern from a line of symbols.
 * A pattern consists of a sequence of friendly and unoccupied positions.
 *
 * @author Barry Becker
 */
class PatternExtractor(line: CharSequence) {

  private val BACK = -1
  private val FORWARD = 1

  /**
   * Given the opponent symbol, find the pattern in the specified potion of the line.
   */
  def getPattern(opponentSymb: Char, pos: Int, minpos: Int, maxpos: Int): CharSequence = {
    if (line.charAt(pos) == opponentSymb) {
      return ""
    }
    val start = getEndPosition(line, opponentSymb, pos, minpos, BACK)
    val stop = getEndPosition(line, opponentSymb, pos, maxpos, FORWARD)
    line.subSequence(start, stop + 1)
  }

  /**
   * March in the direction specified until we hit 2 blanks, an opponent piece,
   * or the end of the line.
   * @return end position
   */
  protected def getEndPosition(line: CharSequence, opponentSymb: Char, pos: Int, extremePos: Int, direction: Int): Int = {
    var end = pos
    if ((line.charAt(pos) == opponentSymb) && (pos == extremePos)) {
      end -= direction
    }
    else {
      while ((direction * end < direction * extremePos) && (line.charAt(end + direction) != opponentSymb)
        && !next2Unoccupied(line, end, direction)) {
        end += direction
      }
    }
    end
  }

  private def next2Unoccupied(line: CharSequence, position: Int, dir: Int): Boolean = {
    (line.charAt(position) == Patterns.UNOCCUPIED
      && line.charAt(position + dir) == Patterns.UNOCCUPIED)
  }
}
