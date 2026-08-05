/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.pattern

/**
 * Encapsulates the domain knowledge for n in a row game.
 * These are key patterns that can occur in the game and are weighted
 * by importance to let the computer play better.
 *
 * Do not add duplicate patterns or patterns that are the reverse of other patterns.
 *
 * @author Barry Becker
 */
abstract class Patterns {

  private val patternTable_ : PatternTable = new PatternTable()

  initializePatterns()

  /**
   * @param pattern  pattern to get the weight index for.
   * @param minpos index of first character in pattern
   * @param maxpos index of last character position in pattern.
   * @return weight index
   */
  def getWeightIndexForPattern(pattern: CharSequence, minpos: Int, maxpos: Int): Int = {
    patternTable_.getWeightIndexForPattern(pattern, minpos, maxpos)
  }

  /**
   * @param pattern  pattern to get the weight index for.
   * @return weight index
   */
  def getWeightIndexForPattern(pattern: CharSequence): Int = {
    patternTable_.getWeightIndexForPattern(pattern, 0, pattern.length - 1)
  }

  /**
   * @return how many in a row are needed to win. If M is five then the game is gomoku
   */
  def getWinRunLength: Int

  /**
   * @return patterns shorter than this are not interesting and have weight 0
   */
  def getMinInterestingLength: Int

  /**
   * @return total number of patterns represented
   */
  def getNumPatterns: Int

  /**
   * Initialize all the gomoku patterns.
   */
  protected def initializePatterns(): Unit = {
    for (i <- 0 until getNumPatterns) {
      setPatternWeightInTable(getPatternString(i), getWeightIndex(i))
    }
  }

  def getPatternString(i: Int): String

  def getWeightIndex(i: Int): Int

  protected def setPatternWeightInTable(pattern: String, wtIndex: Int): Unit = {
    patternTable_.setPatternWeightInTable(pattern, wtIndex)
  }
}

object Patterns {
  val UNOCCUPIED: Char = '_'
}
