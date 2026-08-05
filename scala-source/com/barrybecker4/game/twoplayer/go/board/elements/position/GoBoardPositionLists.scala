/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements.position

import java.util.LinkedList
import scala.jdk.CollectionConverters._

/**
 * A list of GoBoardPositionLists.
 *
 * @author Barry Becker
 */
class GoBoardPositionLists extends LinkedList[GoBoardPositionList] {

  /**
   * copy constructor.
   * @param positionList list to initialize with
   */
  def this(positionList: GoBoardPositionLists) = {
    this()
    this.addAll(positionList)
  }

  /**
   * Set the visited flag back to false for a list of lists of stones
   */
  def unvisitPositionsInLists(): Unit = {
    this.asScala.foreach(_.unvisitPositions())
  }
}
