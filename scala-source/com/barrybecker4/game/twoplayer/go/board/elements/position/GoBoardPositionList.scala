/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements.position

import java.util.{Iterator, LinkedList}
import scala.jdk.CollectionConverters._

/**
 * A list of GoBoardPositions.
 *
 * @author Barry Becker
 */
class GoBoardPositionList extends LinkedList[GoBoardPosition] {

  /**
   * copy constructor.
   * @param positionList list to initialize with
   */
  def this(positionList: GoBoardPositionList) = {
    this()
    this.addAll(positionList)
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

  /**
   * Set the visited flag back to false for a set of stones.
   * Returns the whole list of stones to unvisited state.
   */
  def unvisitPositions(): Unit = {
    this.asScala.foreach(_.setVisited(false))
  }

  /**
   * Verify all stones in this list are marked unvisited.
   */
  def confirmUnvisited(): Unit = {
    this.asScala.foreach { pos =>
      assert(!pos.isVisited, s"$pos in $this was visited")
    }
  }

  /**
   * Verify no duplicate positions in this list
   * @param seed stone to start checking from.
   */
  def confirmNoDupes(seed: GoBoardPosition): Unit = {
    val stoneArray = this.toArray()

    for (i <- stoneArray.indices) {
      val st = stoneArray(i).asInstanceOf[GoBoardPosition]
      for (j <- i + 1 until stoneArray.length) {
        assert(!st.equals(stoneArray(j)), s"found a dupe=$st in $this\nthe seed = $seed")
      }
    }
  }

  /**
   * Confirm that this list contains some smaller list
   * @param smallerGroup smaller group
   * @return true if larger group contains smaller group.
   */
  def confirmStoneListContains(smallerGroup: GoBoardPositionList): Boolean = {
    smallerGroup.asScala.forall { smallPos =>
      val largeIt = this.iterator()
      var found = false
      while (largeIt.hasNext && !found) {
        val largePos = largeIt.next()
        if (largePos.getRow == smallPos.getRow && largePos.getCol == smallPos.getCol) {
          found = true
        }
      }
      found
    }
  }
}
