// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.pattern

/**
 * Responsible for Converting pattern strings to an integer.
 *
 * @author Barry Becker
 */
class PatternToIntConverter {

  /**
   * each pattern can be represented as a unique integer.
   * this integer can be used like a hash for a quick lookup of the weight
   * in the weightIndexTable
   * @return integer identifier for pattern.
   */
  def convertPatternToInt(pattern: String): Int = {
    val buf = new StringBuilder(pattern)
    convertPatternToInt(buf, 0, pattern.length - 1)
  }

  /**
   * Each pattern can be represented as a unique integer.
   * This integer can be used like a hash for a quick lookup of the weight
   * in the weightIndexTable.
   * @return integer representation of pattern
   */
  def convertPatternToInt(pattern: CharSequence, minpos: Int, maxpos: Int): Int = {
    var power = 1
    var sum = 0

    for (i <- maxpos to minpos by -1) {
      if (pattern.charAt(i) != Patterns.UNOCCUPIED) {
        sum += power
      }
      power <<= 1
    }
    sum + power
  }

  /**
   * Each pattern can be represented as a unique integer.
   * This integer can be used like a hash for a quick lookup of the weight
   * in the weightIndexTable.
   * This version makes sure we have only a single kind of nonempty character.
   * @return integer representation of pattern
   */
  def convertPatternToIntSafe(pattern: CharSequence, minpos: Int, maxpos: Int): Int = {
    var power = 1
    var sum = 0
    var i = minpos
    if (pattern.length == 0) return 1
    while (pattern.charAt(i) == Patterns.UNOCCUPIED && i < maxpos) i += 1
    val occupiedSymb = pattern.charAt(i)

    i = maxpos
    while (i >= minpos) {
      val c = pattern.charAt(i)
      if (c != Patterns.UNOCCUPIED) {
        assert(c == occupiedSymb)
        sum += power
      }
      power <<= 1
      i -= 1
    }
    sum + power
  }
}
