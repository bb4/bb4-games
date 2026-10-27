/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.analysis

import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns
import com.barrybecker4.optimization.parameter.ParameterArray

import java.util.{LinkedList, List as JList}

/**
 * Creates lines and remembers what lines were created.
 * @author Barry Becker
 */
class LineFactoryRecorder extends LineFactory:

  private val lines_ : JList[Line] = new LinkedList[Line]()

  override def createLine(patterns: Patterns, weights: ParameterArray): Line =
    val line = new Line(new LineEvaluator(patterns, weights))
    lines_.add(line)
    line

  def getCreatedLines: JList[Line] = lines_

  def clearLines(): Unit = lines_.clear()
