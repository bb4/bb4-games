/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements.string

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.StringLibertyAnalyzer
import com.barrybecker4.game.twoplayer.go.board.elements.GoSet
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionList, GoBoardPositionSet, GoStone}
import scala.jdk.CollectionConverters._

/**
 * A GoString is composed of a strongly connected set of one or more same color stones.
 * By strongly connected I mean nobi connections only.
 * A GoGroup by comparison, is composed of a set of one or more same color strings.
 * Groups may be connected by diagonals, or ikken tobi, or kogeima (knight's move).
 *
 * @author Barry Becker
 */
class GoString extends GoSet with IGoString {

  import scala.compiletime.uninitialized

  /** a set of the stones that are in the string — no `= null` (would run after GoSet.initializeMembers) */
  private var members_ : GoBoardPositionSet = uninitialized

  /** The group to which this string belongs. */
  var group_ : IGoGroup = uninitialized

  /** If true, then we are an eye in an unconditionally alive group (according to Benson's algorithm). */
  private var unconditionallyAlive_ : Boolean = false

  /** Keep track of number of liberties instead of computing each time (for performance). */
  private var libertyAnalyzer_ : StringLibertyAnalyzer = uninitialized

  /**
   * Constructor. Create a new string containing the specified stone.
   */
  def this(stone: GoBoardPosition, board: GoBoard) = {
    this()
    assert(stone.isOccupied)
    ownedByPlayer1_ = stone.getPiece.isOwnedByPlayer1
    getMembers.add(stone)
    stone.setString(this)
    group_ = null
    libertyAnalyzer_ = new StringLibertyAnalyzer(board, this)
  }

  /**
   * Constructor.
   * Create a new string containing the specified list of stones
   */
  def this(stones: GoBoardPositionList, board: GoBoard) = {
    this()
    assert(stones != null && stones.size > 0, "Tried to create list from empty list")
    val stone = stones.getFirst.getPiece.asInstanceOf[GoStone]
    // GoEye constructor calls this method. For eyes the stone is null.
    if (stone != null) {
      ownedByPlayer1_ = stone.isOwnedByPlayer1
    }
    stones.asScala.foreach { pos =>
      addMemberInternal(pos, board)
    }
    libertyAnalyzer_ = new StringLibertyAnalyzer(board, this)
  }

  /**
   * @return the set of member positions
   */
  override def getMembers: GoBoardPositionSet = members_

  /**
   * @param pos position to look for.
   * @return true if we contain the specified position.
   */
  override def contains(pos: GoBoardPosition): Boolean = members_.contains(pos)

  override def initializeMembers(): Unit = {
    members_ = new GoBoardPositionSet()
  }

  override final def setGroup(group: IGoGroup): Unit = {
    group_ = group
  }

  override def getGroup: IGoGroup = group_

  /**
   * add a stone to the string
   */
  def addMember(stone: GoBoardPosition, board: GoBoard): Unit = {
    addMemberInternal(stone, board)
    libertyAnalyzer_.invalidate()
  }

  /**
   * Add a stone to the string
   */
  def addMemberInternal(stone: GoBoardPosition, board: GoBoard): Unit = {
    assert(stone.isOccupied, s"trying to add empty space to string. stone=$stone")
    assert(stone.getPiece.isOwnedByPlayer1 == this.isOwnedByPlayer1,
      "stones added to a string must have like ownership")
    if (getMembers.contains(stone)) {
      assert((stone.getString == null) || (this eq stone.getString),
        s"bad stone $stone or bad owning string ${stone.getString}")
    }
    // if the stone is already owned by another string, we need to verify that that other string has given it up.
    if (stone.getString != null) {
      stone.getString.remove(stone, board)
    }

    stone.setString(this)
    getMembers.add(stone)
  }

  /**
   * merge a string into this one
   */
  final def merge(string: IGoString, board: GoBoard): Unit = {
    if (this eq string) {
      GameContext.log(1, s"Warning: merging $string into itself")
      // its a self join
      return
    }

    val stringMembers = new GoBoardPositionSet()
    stringMembers.addAll(string.getMembers)
    // must remove these after iterating otherwise we get a ConcurrentModificationException
    string.getGroup.remove(string)
    string.getMembers.clear()

    val it = stringMembers.iterator()
    while (it.hasNext) {
      val stone = it.next()
      val myString = stone.getString
      if (myString != null && (myString ne string)) {
        myString.remove(stone, board)
      }
      stone.setString(null)
      addMemberInternal(stone, board)
    }
    stringMembers.clear()
    libertyAnalyzer_.invalidate()
  }

  /**
   * remove a stone from this string.
   * What happens if the string gets split as a result?
   * The caller should handle this case since we cannot create new strings here.
   */
  override final def remove(stone: GoBoardPosition, board: GoBoard): Unit = {
    removeInternal(stone)
    libertyAnalyzer_.invalidate()
  }

  def removeInternal(stone: GoBoardPosition): Unit = {
    val removed = getMembers.remove(stone)
    assert(removed, s"failed to remove $stone from $this")
    stone.setString(null)
    if (getMembers.isEmpty) {
      group_.remove(this)
    }
  }

  override def getNumLiberties(board: GoBoard): Int = {
    getLiberties(board).size
  }

  /**
   * return the set of liberty positions that the string has
   * @param board the go board
   */
  override final def getLiberties(board: GoBoard): GoBoardPositionSet = {
    libertyAnalyzer_.getLiberties
  }

  /**
   * If the libertyPos is occupied, then we subtract this liberty, else add it.
   * @param libertyPos position to check for liberty
   */
  def changedLiberty(libertyPos: GoBoardPosition): Unit = {
    libertyAnalyzer_.invalidate()
  }

  /**
   * Set the health of members equal to the specified value
   * @param health range = [0-1]
   */
  override final def updateTerritory(health: Float): Unit = {
    getMembers.asScala.foreach { pos =>
      val stone = pos.getPiece.asInstanceOf[GoStone]
      stone.setHealth(health)
    }
  }

  /**
   * @return true if the piece at the specified position is an enemy of the string owner
   */
  override def isEnemy(pos: GoBoardPosition): Boolean = {
    assert(group_ != null, s"group for $this is null")
    assert(pos.isOccupied, s"pos not occupied: =$pos")
    val stone = pos.getPiece.asInstanceOf[GoStone]

    assert(getGroup.isOwnedByPlayer1 == isOwnedByPlayer1, s"$getGroup string=$this")
    stone.isOwnedByPlayer1 != isOwnedByPlayer1
  }

  /**
   * make sure all the stones in the string are visited/unvisited as specified.
   */
  override final def setVisited(visited: Boolean): Unit = {
    getMembers.asScala.foreach(_.setVisited(visited))
  }

  def getPrintPrefix: String = " STRING("

  /**
   * @return true if unconditionally alive.
   */
  override def isUnconditionallyAlive: Boolean = unconditionallyAlive_

  override def setUnconditionallyAlive(unconditionallyAlive: Boolean): Unit = {
    this.unconditionallyAlive_ = unconditionallyAlive
  }

  /**
   * @return true if any of the stones in the string are blank (should never happen)
   */
  final def areAnyBlank: Boolean = {
    getMembers.asScala.exists(_.isUnoccupied)
  }

  /**
   * @return a string representation for the string.
   */
  override def toString: String = {
    val sb = new StringBuilder(getPrintPrefix)
    sb.append(" UA=").append(isUnconditionallyAlive).append(" ")
    val it = getMembers.iterator()
    if (it.hasNext) {
      val p = it.next()
      sb.append(p.toString)
    }
    while (it.hasNext) {
      val p = it.next()
      sb.append(", ")
      sb.append(p.toString)
    }
    sb.append(')')
    sb.toString
  }
}
