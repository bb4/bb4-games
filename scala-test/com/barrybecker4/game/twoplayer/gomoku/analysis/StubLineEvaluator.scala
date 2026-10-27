// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis

import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns
import com.barrybecker4.optimization.parameter.ParameterArray

import java.util.{LinkedList, List as JList}

/**
 * Represents a run of symbols to be evaluated on the board.
 * @author Barry Becker
 */
class StubLineEvaluator(patterns: Patterns, weights: ParameterArray)
  extends LineEvaluator(patterns, weights):

  /** recorded set of patterns that we got weights for */
  private val patternsChecked_ : JList[CharSequence] = new LinkedList[CharSequence]()

  def getPatternsChecked: JList[CharSequence] = patternsChecked_

  override def getWeightIndex(line: CharSequence, opponentSymb: Char, pos: Int, minpos: Int, maxpos: Int): Int =
    val pattern = getPattern(line, opponentSymb, pos, minpos, maxpos)
    patternsChecked_.add(pattern)
    patterns_.getWeightIndexForPattern(pattern)
