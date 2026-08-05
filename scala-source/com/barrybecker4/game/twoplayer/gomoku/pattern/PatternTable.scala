// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.pattern

/**
 * Stores the key patterns that can occur in the game.
 * Acts as a fact hash lookup to find an index given a pattern.
 * Do not add duplicate patterns or patterns that are the reverse of other patterns.
 *
 * @author Barry Becker
 */
class PatternTable {

  private val TABLE_SIZE = 4096
  private var weightIndexTable_ : Array[Int] = _
  private val converter: PatternToIntConverter = new PatternToIntConverter()

  initTable()

  protected def initTable(): Unit = {
    weightIndexTable_ = new Array[Int](TABLE_SIZE)
    for (i <- 0 until TABLE_SIZE) {
      weightIndexTable_(i) = -1
    }
  }

  /**
   * @param pattern  pattern to get the weight index for.
   * @param minpos index of first character in pattern
   * @param maxpos index of last character position in pattern.
   * @return weight index
   */
  def getWeightIndexForPattern(pattern: CharSequence, minpos: Int, maxpos: Int): Int = {
    weightIndexTable_(converter.convertPatternToInt(pattern, minpos, maxpos))
  }

  def setPatternWeightInTable(pattern: String, wtIndex: Int): Unit = {
    var hash = converter.convertPatternToInt(pattern)
    weightIndexTable_(hash) = wtIndex

    // String.reverse — do not use StringBuilder.reverse (returns a new builder; discarding it skips the reverse entry)
    hash = converter.convertPatternToInt(pattern.reverse)
    weightIndexTable_(hash) = wtIndex
  }
}
