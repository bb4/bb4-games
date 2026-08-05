/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements.group

import com.barrybecker4.common.geometry.{Box, ByteLocation}
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.GoSet
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionList, GoBoardPositionSet, GoStone}
import com.barrybecker4.game.twoplayer.go.board.elements.string.{GoStringSet, IGoString}
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._

/**
 * A GoGroup is composed of a loosely connected set of one or more same color strings.
 * A GoString by comparison, is composed of a strongly connected set of one or more same color stones.
 * Groups may be connected by diagonals or one space jumps, or uncut knights moves, but not nikken tobi.
 *
 * @author Barry Becker
 */
final class GoGroup extends GoSet with IGoGroup {

  import scala.compiletime.uninitialized

  /** a set of same color strings that are in the group. */
  private var members_ : GoStringSet = uninitialized

  /**
   * This is the cached number of liberties.
   * It updates whenever something has changed.
   */
  private var cachedLiberties_ : GoBoardPositionSet = uninitialized

  /** listeners to notify in the event that we change. */
  private var changeListeners: ListBuffer[GroupChangeListener] = uninitialized

  /**
   * Constructor. Create a new group containing the specified string.
   * @param string make the group from this string.
   */
  def this(string: IGoString) = {
    this()
    ownedByPlayer1_ = string.isOwnedByPlayer1
    getMembers.add(string)
    string.setGroup(this)
    commonInit()
  }

  /**
   * Constructor.
   * Create a new group containing the specified list of stones
   * Every stone in the list passed in must say that it is owned by this new group,
   * and every string must be wholly owned by this new group.
   * @param stones list of stones to create a group from.
   */
  def this(stones: GoBoardPositionList) = {
    this()
    ownedByPlayer1_ = stones.getFirst.getPiece.isOwnedByPlayer1
    stones.asScala.foreach { stone =>
      assimilateStone(stones, stone)
    }
    commonInit()
  }

  private def commonInit(): Unit = {
    changeListeners = ListBuffer[GroupChangeListener]()
  }

  override def addChangeListener(listener: GroupChangeListener): Unit = {
    changeListeners += listener
  }

  def removeChangeListener(listener: GroupChangeListener): Unit = {
    changeListeners -= listener
  }

  /**
   * @return true if the piece is an enemy of the set owner.
   * If the difference in health between the stones is great, then they are not really enemies
   * because one of them is dead.
   */
  override def isEnemy(pos: GoBoardPosition): Boolean = {
    assert(pos.isOccupied)
    val stone = pos.getPiece.asInstanceOf[GoStone]
    stone.isOwnedByPlayer1 != isOwnedByPlayer1
  }

  /**
   * @param stones stones to assimilate
   * @param stone the new stone to add to the group.
   */
  private def assimilateStone(stones: GoBoardPositionList, stone: GoBoardPosition): Unit = {
    assert(stone.getPiece.isOwnedByPlayer1 == ownedByPlayer1_,
      s"Stones in group must all be owned by the same player. stones=$stones")
    val string = stone.getString
    assert(string != null, s"There is no owning string for $stone")
    if (!getMembers.contains(string)) {
      assert(ownedByPlayer1_ == string.isOwnedByPlayer1, s"$string ownership not the same as $this")
      getMembers.add(string)
    }
    string.setGroup(this)
  }

  /**
   * Must be ordered (i.e. LinkedHashSet
   */
  override def initializeMembers(): Unit = {
    members_ = new GoStringSet()
  }

  /**
   * @return the hashSet containing the members
   */
  override def getMembers: GoStringSet = members_

  /**
   * make sure all the stones in the string are unvisited or visited, as specified
   */
  override def setVisited(visited: Boolean): Unit = {
    getMembers.asScala.foreach(_.setVisited(visited))
  }

  /**
   * add a string to the group.
   * @param string the string to add
   */
  override def addMember(string: IGoString): Unit = {
    assert(string.isOwnedByPlayer1 == ownedByPlayer1_,
      s"strings added to a group must have like ownership. String=$string. Group we are trying to add it to: $this")
    if (getMembers.contains(string)) {
      assert(string.getGroup eq this,
        s"The $this already contains the string, but the $string says its owning group is ${string.getGroup}")
      return
    }
    // remove it from the old group
    val oldGroup = string.getGroup
    if (oldGroup != null && (oldGroup ne this)) {
      oldGroup.remove(string)
    }
    string.setGroup(this)
    getMembers.add(string)
    broadcastChange()
  }

  /**
   * remove a string from this group
   * @param string the string to remove from the group
   */
  override def remove(string: IGoString): Unit = {
    if (string == null) {
      GameContext.log(2, s"attempting to remove $string string from group. $this")
      return
    }
    if (getMembers.isEmpty) {
      GameContext.log(2, s"attempting to remove $string from already empty group.")
      return
    }
    getMembers.remove(string)
    broadcastChange()
  }

  /**
   * Get the number of liberties that the group has.
   * @return the number of liberties that the group has
   */
  override def getLiberties(board: GoBoard): GoBoardPositionSet = {
    if (board == null) {
      return cachedLiberties_
    }

    val liberties = new GoBoardPositionSet()
    getMembers.asScala.foreach { str =>
      liberties.addAll(str.getLiberties(board))
    }
    cachedLiberties_ = liberties
    liberties
  }

  /**
   * Get number of liberties for our groups. If nothing cached, this may not be accurate.
   * @param board if null, then the number of liberties returned is just what is in the cache and may not be accurate.
   * @return number of cached liberties if board is null, else exact number of liberties for group.
   */
  override def getNumLiberties(board: GoBoard): Int = {
    getLiberties(board).size
  }

  /**
   * @return a list of the stones in this group.
   */
  override def getStones: GoBoardPositionSet = {
    val stones = new GoBoardPositionSet()
    getMembers.asScala.foreach { string =>
      stones.addAll(string.getMembers)
    }
    stones
  }

  /**
   * Calculate the number of stones in the group.
   * @return number of stones in the group.
   */
  override def getNumStones: Int = {
    getStones.size
  }

  /**
   * Set the health of strings in this group
   * @param health the health of the group
   */
  override def updateTerritory(health: Float): Unit = {
    getMembers.asScala.foreach { string =>
      if (string.isUnconditionallyAlive) {
        string.updateTerritory(if (ownedByPlayer1_) 1.0f else -1.0f)
      } else {
        string.updateTerritory(health)
      }
    }
  }

  /**
   * returns true if this group contains the specified stone
   * @param stone the stone to check for containment of
   * @return true if the stone is in this group
   */
  override def containsStone(stone: GoBoardPosition): Boolean = {
    getMembers.asScala.exists(_.getMembers.contains(stone))
  }

  /**
   * @return bounding box of set of stones/positions passed in
   */
  override def findBoundingBox(): Box = {
    var rMin = 10000 // something huge ( more than max rows)
    var rMax = 0
    var cMin = 10000 // something huge ( more than max cols)
    var cMax = 0

    // first determine a bounding rectangle for the group.
    getMembers.asScala.foreach { string =>
      string.getMembers.asScala.foreach { stone =>
        val row = stone.getRow
        val col = stone.getCol
        if (row < rMin) rMin = row
        if (row > rMax) rMax = row
        if (col < cMin) cMin = col
        if (col > cMax) cMax = col
      }
    }
    if (rMin > rMax) new Box(ByteLocation(0.toByte, 0.toByte), ByteLocation(0.toByte, 0.toByte)) 
    else new Box(ByteLocation(rMin.toByte, cMin.toByte), ByteLocation(rMax.toByte, cMax.toByte))
  }

  /**
   * get the textual representation of the group.
   * @return string form
   */
  override def toString: String = toString("\n")

  /**
   * get the html representation of the group.
   * @return html form
   */
  override def toHtml: String = toString("<br>")

  /**
   * @param newline string to use for the newline - eg "\n" or "<br>".
   * @return string form.
   */
  private def toString(newline: String): String = {
    val sb = new StringBuilder(" GROUP {" + newline)
    val it = getMembers.iterator()
    // print the member strings
    if (it.hasNext) {
      val p = it.next()
      sb.append("    ").append(p.toString)
    }
    while (it.hasNext) {
      val p = it.next()
      sb.append(',').append(newline).append("    ").append(p.toString)
    }
    sb.append(newline).append('}')
    sb.toString
  }

  /**
   * Notify our listeners (if any) that we have changed
   */
  private def broadcastChange(): Unit = {
    changeListeners.foreach(_.groupChanged())
  }
}
