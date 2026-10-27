/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements.eye

import java.util.LinkedHashSet

/**
 * A set of GoEyes.
 *
 * @author Barry Becker
 */
class GoEyeSet extends LinkedHashSet[IGoEye] {

  /**
   * Copy constructor.
   * @param set eye set
   */
  def this(set: GoEyeSet) = {
    this()
    this.addAll(set)
  }
}
