/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements.position

import java.util.{LinkedHashSet, Set}
import scala.jdk.CollectionConverters._

/**
 * A set of GoBoardPositions.
 *
 * @author Barry Becker
 */
class GoBoardPositionSet extends LinkedHashSet[GoBoardPosition] {

  def this(set: Set[GoBoardPosition]) = {
    this()
    this.addAll(set)
  }

  /**
   * Note: there is no concept of order.
   * @return one of the members
   */
  def getOneMember: GoBoardPosition = iterator().next()

  /**
   * Set the visited flag back to false for a set of stones.
   * Returns the whole list of stones to unvisited state.
   */
  def unvisitPositions(): Unit = {
    this.asScala.foreach(_.setVisited(false))
  }

  /**
   * pretty print a list of all the current groups (and the strings they contain)
   * @return string form of list of stones.
   */
  def toString(title: String): String = {
    val buf = new StringBuilder(title)
    buf.append("\n  ")
    this.asScala.foreach { stone =>
      buf.append(stone.toString).append(", ")
    }
    buf.substring(0, buf.length - 2)
  }
}
