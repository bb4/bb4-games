/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.pattern

/**
 *  Encapsulates the domain knowledge for GoMoku.
 *  Its primary client is the GoMokuController class.
 *  These are key patterns that can occur in the game and are weighted
 *  by importance to let the computer play better.
 *
 *  @author Barry Becker
 */
class GoMokuPatterns extends Patterns {

  override def getWinRunLength: Int = GoMokuPatterns.WIN_RUN_LENGTH

  override def getMinInterestingLength: Int = 3

  override def getNumPatterns: Int = GoMokuPatterns.NUM_PATTERNS

  override def getPatternString(i: Int): String = GoMokuPatterns.PAIRS(i).pattern

  override def getWeightIndex(i: Int): Int = GoMokuPatterns.PAIRS(i).weight
}

object GoMokuPatterns {
  val WIN_RUN_LENGTH = 5

  private val NUM_PATTERNS = 210

  private case class Pair(pattern: String, weight: Int)

  private val PAIRS = Array(
    Pair("_XX", 0), Pair("XXX", 1), Pair("_XXX", 2), Pair("X_XX", 2), Pair("_X_X", 3),
    Pair("_XX_", 4), Pair("_X_X_", 4), Pair("_X_XX", 4), Pair("_XX_X", 4), Pair("_XXX_", 5),
    Pair("_XXXX", 6), Pair("X_X_X", 3), Pair("X_XXX", 6), Pair("XX_XX", 6), Pair("XXXXX", 8),
    Pair("_X_X_X", 4), Pair("_X_XX_", 5), Pair("_X_XXX", 6), Pair("_XX_XX", 6), Pair("_XXX_X", 6),
    Pair("_XXXX_", 7), Pair("_XXXXX", 8), Pair("X_X_XX", 4), Pair("X_XX_X", 4), Pair("X_XXXX", 6),
    Pair("XX_XXX", 6), Pair("XXXXXX", 9), Pair("_X_X_X_", 4), Pair("_X_X_XX", 4), Pair("_X_XX_X", 5),
    Pair("_X_XXX_", 6), Pair("_X_XXXX", 6), Pair("_XX_X_X", 5), Pair("_XX_XX_", 6), Pair("_XX_XXX", 6),
    Pair("_XXX_XX", 6), Pair("_XXXX_X", 7), Pair("_XXXXX_", 8), Pair("_XXXXXX", 9), Pair("X_X_X_X", 4),
    Pair("X_X_XXX", 6), Pair("X_XX_X_", 5), Pair("X_XX_XX", 6), Pair("X_XXX_X", 7), Pair("X_XXXXX", 8),
    Pair("XX_X_XX", 3), Pair("XX_XXXX", 6), Pair("XXX_XXX", 6), Pair("XXXXXXX", 10), Pair("_X_X_X_X", 5),
    Pair("_X_X_XX_", 5), Pair("_X_X_XXX", 6), Pair("_X_XX_XX", 6), Pair("_X_XXX_X", 7), Pair("_X_XXXX_", 7),
    Pair("_X_XXXXX", 8), Pair("_XX_X_XX", 5), Pair("_XX_XX_X", 6), Pair("_XX_XXX_", 6), Pair("_XX_XXXX", 6),
    Pair("_XXX_X_X", 6), Pair("_XXX_XX_", 6), Pair("_XXX_XXX", 6), Pair("_XXXX_X_", 7), Pair("_XXXX_XX", 7),
    Pair("_XXXXX_X", 8), Pair("_XXXXXX_", 9), Pair("_XXXXXXX", 10), Pair("X_X_X_XX", 5), Pair("X_X_XX_X", 5),
    Pair("X_X_XXXX", 6), Pair("X_XX_XXX", 6), Pair("X_XXX_XX", 7), Pair("X_XXXX_X", 7), Pair("X_XXXXXX", 9),
    Pair("XX_X_XXX", 6), Pair("XX_XX_XX", 7), Pair("XX_XXXXX", 8), Pair("XXX_XXXX", 6), Pair("XXXXXXXX", 10),
    Pair("_X_X_X_X_", 5), Pair("_X_X_X_XX", 5), Pair("_X_X_XX_X", 5), Pair("_X_X_XXX_", 6), Pair("_X_X_XXXX", 6),
    Pair("_X_XX_X_X", 5), Pair("_X_XX_XX_", 6), Pair("_X_XX_XXX", 6), Pair("_X_XXX_X_", 7), Pair("_X_XXX_XX", 7),
    Pair("_X_XXXX_X", 7), Pair("_X_XXXXX_", 8), Pair("_X_XXXXXX", 9), Pair("_XX_X_X_X", 5), Pair("_XX_X_XX_", 5),
    Pair("_XX_X_XXX", 6), Pair("_XX_XX_XX", 7), Pair("_XX_XXX_X", 7), Pair("_XX_XXXX_", 7), Pair("_XX_XXXXX", 8),
    Pair("_XXX_X_XX", 6), Pair("_XXX_XX_X", 6), Pair("_XXX_XXX_", 6), Pair("_XXX_XXXX", 6), Pair("_XXXX_X_X", 7),
    Pair("_XXXX_XXX", 7), Pair("_XXXXX_XX", 8), Pair("_XXXXXX_X", 9), Pair("_XXXXXXX_", 10), Pair("_XXXXXXXX", 10),
    Pair("X_X_X_X_X", 5), Pair("X_X_X_XXX", 6), Pair("X_X_XX_XX", 6), Pair("X_X_XXX_X", 7), Pair("X_X_XXXXX", 8),
    Pair("X_XX_X_XX", 5), Pair("X_XX_XX_X", 6), Pair("X_XX_XXXX", 6), Pair("X_XXX_XXX", 7), Pair("X_XXXX_XX", 7),
    Pair("X_XXXXX_X", 8), Pair("X_XXXXXXX", 10), Pair("XX_X_X_XX", 5), Pair("XX_X_XXXX", 6), Pair("XX_XX_XX_", 7),
    Pair("XX_XX_XXX", 7), Pair("XX_XXX_XX", 7), Pair("XX_XXXXXX", 9), Pair("XXX_X_XXX", 7), Pair("XXX_XXXXX", 8),
    Pair("XXXX_XXXX", 6), Pair("XXXXXXXXX", 10), Pair("_X_X_X_X_X", 5), Pair("_X_X_X_XX_", 5), Pair("_X_X_X_XXX", 6),
    Pair("_X_X_XX_X_", 5), Pair("_X_X_XX_XX", 6), Pair("_X_X_XXX_X", 7), Pair("_X_X_XXXX_", 7), Pair("_X_X_XXXXX", 8),
    Pair("_X_XX_X_X_", 5), Pair("_X_XX_X_XX", 5), Pair("_X_XX_XX_X", 6), Pair("_X_XX_XXX_", 6), Pair("_X_XX_XXXX", 6),
    Pair("_X_XXX_X_X", 7), Pair("_X_XXX_XX_", 7), Pair("_X_XXX_XXX", 7), Pair("_X_XXXX_X_", 7), Pair("_X_XXXX_XX", 7),
    Pair("_X_XXXXX_X", 8), Pair("_X_XXXXXX_", 9), Pair("_X_XXXXXXX", 10), Pair("_XX_X_X_XX", 5), Pair("_XX_X_XX_X", 5),
    Pair("_XX_X_XXX_", 6), Pair("_XX_X_XXXX", 6), Pair("_XX_XX_X_X", 6), Pair("_XX_XX_XX_", 7), Pair("_XX_XX_XXX", 7),
    Pair("_XX_XXX_XX", 7), Pair("_XX_XXXX_X", 7), Pair("_XX_XXXXX_", 8), Pair("_XX_XXXXXX", 9), Pair("_XXX_X_X_X", 6),
    Pair("_XXX_X_XXX", 7), Pair("_XXX_XX_XX", 7), Pair("_XXX_XXX_X", 7), Pair("_XXX_XXXX_", 7), Pair("_XXX_XXXXX", 8),
    Pair("_XXXX_X_XX", 7), Pair("_XXXX_XX_X", 7), Pair("_XXXX_XXXX", 7), Pair("_XXXXX_X_X", 8), Pair("_XXXXX_XXX", 8),
    Pair("_XXXXXX_XX", 9), Pair("_XXXXXXX_X", 10), Pair("_XXXXXXXX_", 10), Pair("_XXXXXXXXX", 10), Pair("X_X_X_X_XX", 5),
    Pair("X_X_X_XX_X", 5), Pair("X_X_X_XXXX", 6), Pair("X_X_XX_X_X", 5), Pair("X_X_XX_XXX", 6), Pair("X_X_XXX_XX", 7),
    Pair("X_X_XXXX_X", 7), Pair("X_X_XXXXXX", 8), Pair("X_XX_X_XXX", 6), Pair("X_XX_XX_XX", 7), Pair("X_XX_XXX_X", 7),
    Pair("X_XX_XXXXX", 8), Pair("X_XXX_X_XX", 7), Pair("X_XXX_XXXX", 7), Pair("X_XXXX_XXX", 7), Pair("X_XXXXX_XX", 8),
    Pair("X_XXXXXX_X", 9), Pair("X_XXXXXXXX", 10), Pair("XX_X_X_XX_", 5), Pair("XX_X_X_XXX", 6), Pair("XX_X_XX_XX", 6),
    Pair("XX_X_XXXXX", 8), Pair("XX_XX_XXXX", 7), Pair("XX_XXX_XXX", 7), Pair("XX_XXXX_XX", 7), Pair("XX_XXXXXXX", 10),
    Pair("XXX_X_XXX_", 7), Pair("XXX_X_XXXX", 7), Pair("XXX_XX_XXX", 7), Pair("XXX_XXXXX_", 8), Pair("XXX_XXXXXX", 9)
  )
}
