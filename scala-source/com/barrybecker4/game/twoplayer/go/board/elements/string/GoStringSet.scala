/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements.string

import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition
import java.util.LinkedHashSet
import scala.jdk.CollectionConverters._

/**
 * A set of GoStrings.
 *
 * @author Barry Becker
 */
class GoStringSet extends LinkedHashSet[IGoString] {

  /**
   * Copy constructor.
   * @param set set of strings to initialize with
   */
  def this(set: GoStringSet) = {
    this()
    this.addAll(set)
  }

  /**
   * @param pos position
   * @return the string that contains pos if any. Null if none.
   */
  def findStringContainingPosition(pos: GoBoardPosition): IGoString = {
    this.asScala.find(_.contains(pos)).orNull
  }
}
