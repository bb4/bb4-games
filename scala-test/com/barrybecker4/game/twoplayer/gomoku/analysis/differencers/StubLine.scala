// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis.differencers

import com.barrybecker4.game.twoplayer.gomoku.analysis.{Line, LineEvaluator}

import java.util.List as JList

/**
 * Represents a run of symbols to be evaluated on the board.
 * @author Barry Becker
 */
class StubLine(evaluator: LineEvaluator) extends Line(evaluator):

  /** recorded set of patterns that we got weights for */
  var patternsChecked_ : JList[String] = scala.compiletime.uninitialized

  override def computeValueDifference(position: Int): Int = 0

  def getLineContent: String = line.toString
