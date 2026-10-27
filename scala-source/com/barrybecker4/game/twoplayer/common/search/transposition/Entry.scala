/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.twoplayer.common.search.transposition

import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.search.SearchWindow

/**
 * An entry in the transposition table.
 * Fields are public vars so remaining Java strategies can access them as fields.
 */
class Entry[M <: TwoPlayerMove](
  bm: M,
  upper: Int = 0,
  lower: Int = 0,
  d: Int = 0
):
  // Public fields for Java field access (Scala constructor params alone are private fields + getters)
  var bestMove: M = bm
  var upperValue: Int = upper
  var lowerValue: Int = lower
  var depth: Int = d

  def this(bestMove: M, depth: Int, window: SearchWindow) =
    this(bestMove, window.beta, window.alpha, depth)

  def this(bestMove: M, value: Int) =
    this(bestMove, value, value, 0)

  override def toString: String =
    s"Entry depth=$depth bestMove=$bestMove range=[$lowerValue, $upperValue]"
