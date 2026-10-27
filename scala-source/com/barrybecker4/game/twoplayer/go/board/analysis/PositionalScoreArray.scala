// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.board.analysis

import scala.collection.mutable

/**
 * Used to keep track of evaluating a measure of score passed only on values at positions.
 *
 * @author Barry Becker
 */
final class PositionalScoreArray private (size: Int) {

  import PositionalScoreArray._

  /** a lookup table of scores to attribute to the board positions when calculating the worth */
  private val positionalScores_ : Array[Array[Float]] = createPositionalScoreArray(size)

  def getValue(i: Int, j: Int): Float = positionalScores_(i - 1)(j - 1)

  /**
   * Create the lookup table of scores to attribute to the board positions when calculating the worth.
   * These weights are counted more heavily at te beginning of the game.
   * @return lookup of position scores.
   */
  private def createPositionalScoreArray(size: Int): Array[Array[Float]] = {
    val positionalScore = Array.ofDim[Float](size, size)

    for (row <- 1 to size) {
      val rowmin = math.min(row, size - row + 1)
      for (col <- 1 to size) {
        val colmin = math.min(col, size - col + 1)
        // default neutral value
        positionalScore(row - 1)(col - 1) = 0.0f

        val lineNo = math.min(rowmin, colmin)
        if (lineNo < LINE_VALS.length) {
          if (rowmin == colmin) {
            // corners get emphasized
            positionalScore(row - 1)(col - 1) = 1.5f * LINE_VALS(lineNo - 1)
          } else {
            positionalScore(row - 1)(col - 1) = LINE_VALS(lineNo - 1)
          }
        }
      }
    }
    positionalScore
  }

  override def toString: String = {
    val bldr = new StringBuilder()
    val size = positionalScores_.length

    for (i <- 1 to size) {
      bldr.append("{")
      for (j <- 1 to size) {
        bldr.append(s"${getValue(i, j)}f, ")
      }
      bldr.append("},\n")
    }
    bldr.toString
  }
}

object PositionalScoreArray {
  /**
   * Keep a cache of the immutable score arrays.
   * We create them only when needed, and probably will never create more than one.
   */
  private val scoreArrays = mutable.HashMap[Int, PositionalScoreArray]()

  /** we assign a value to a stone based on the line on which it falls when calculating worth. */
  private val LINE_VALS = Array(
    -2.5f,   // first line — strongly discourage early edge plays
    0.0f,    // second line
    1.0f,    // third line
    0.9f,    // fourth line
    0.1f     // fifth line
  )

  /**
   * Factory method that will retrieve a positional array of the right size.
   * It will create it if needed, but only the first time a given size is requested.
   * @param size size of the score array
   */
  def getArray(size: Int): PositionalScoreArray = {
    scoreArrays.getOrElseUpdate(size, new PositionalScoreArray(size))
  }
}
