/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe

import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns

/**
 * Encapsulates the domain knowledge for TicTacToe.
 * These are key patterns that can occur in the game and are weighted
 * by importance to let the computer play better.
 *
 * @author Barry Becker
 */
final class TicTacToePatterns extends Patterns {

  initializePatterns()

  /**
   * This is how many in a row are needed to win
   */
  override def getWinRunLength: Int = TicTacToePatterns.WIN_RUN_LENGTH

  override def getNumPatterns: Int = TicTacToePatterns.NUM_PATTERNS

  override def getMinInterestingLength: Int = 2

  override def getPatternString(i: Int): String = TicTacToePatterns.patternString(i)

  override def getWeightIndex(i: Int): Int = TicTacToePatterns.weightIndex(i)
}

object TicTacToePatterns {
  val WIN_RUN_LENGTH = 3

  private val NUM_PATTERNS = 5

  private val patternString = Array[String](
    "_X", "_X_", "_XX", "X_X", "XXX"
  )

  /** index into weights array. @see TicTacTowWeights */
  private val weightIndex = Array[Int](
    0, 1, 2, 3, 4
  )
}
