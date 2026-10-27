/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.options

import com.barrybecker4.game.twoplayer.common.TwoPlayerOptions

/**
 * Go specific game options.
 * @author Barry Becker
 */
class GoOptions(preferredTone: String) extends TwoPlayerOptions(preferredTone):

  private var komi_ : Float = GoOptions.DEFAULT_KOMI

  def this() =
    this(null)

  def this(preferredTone: String, komi: Float) =
    this(preferredTone)
    setKomi(komi)

  def getKomi: Float = komi_

  def setKomi(komi: Float): Unit =
    this.komi_ = komi

object GoOptions:
  val DEFAULT_KOMI = 5.5f
