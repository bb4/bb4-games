/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.pattern

/**
 * Simple patterns for use with unit tests.
 *
 * @author Barry Becker
 */
class SimplePatterns extends Patterns:

  override def getWinRunLength: Int = 2

  override def getNumPatterns: Int = 6

  override def getMinInterestingLength: Int = 2

  override def getPatternString(i: Int): String = SimplePatterns.patternString(i)

  override def getWeightIndex(i: Int): Int = SimplePatterns.weightIndex(i)

object SimplePatterns:
  private val patternString: Array[String] = Array("_X", "X_X", "_X_", "XX", "_XX", "_XX_")

  /** index into weights array. */
  private val weightIndex: Array[Int] = Array(0, 0, 1, 2, 2, 2)
