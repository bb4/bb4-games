// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku.analysis.differencers

import com.barrybecker4.game.twoplayer.gomoku.analysis.{Line, LineEvaluator, LineFactory}
import com.barrybecker4.game.twoplayer.gomoku.pattern.Patterns
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * Creates lines and remembers what lines were created.
 * @author Barry Becker
 */
class StubLineFactory extends LineFactory:

  private var lastLine: StubLine = scala.compiletime.uninitialized

  override def createLine(patterns: Patterns, weights: ParameterArray): Line =
    lastLine = new StubLine(new LineEvaluator(patterns, weights))
    lastLine

  /** return the last line that was created by the factory */
  def getLineContent: String = lastLine.getLineContent
