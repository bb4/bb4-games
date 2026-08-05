/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements.eye

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.EyeTypeAnalyzer
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.{EyeInformation, EyeStatus}
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzer
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionList, GoBoardPositionSet, GoStone}
import com.barrybecker4.game.twoplayer.go.board.elements.string.GoString
import scala.jdk.CollectionConverters._

/**
 * A GoEye is composed of a strongly connected set of empty spaces (and possibly some dead enemy stones).
 * By strongly connected I mean nobi connections only.
 * A GoEye may be one of several different eye types enumerated below
 * A group needs 2 provably true eyes to live.
 *
 * @author Barry Becker
 */
class GoEye(spaces: GoBoardPositionList, board: GoBoard, g: IGoGroup, groupAnalyzer: GroupAnalyzer)
  extends GoString(spaces, board) with IGoEye {

  import scala.compiletime.uninitialized

  /** A set of positions that are in the eye space. Need not be empty. — must not `= null` after GoSet init */
  private var members_ : GoBoardPositionSet = uninitialized

  group_ = g
  ownedByPlayer1_ = g.isOwnedByPlayer1

  /** The kind of eye that this is. */
  private val eyeAnalyzer = new EyeTypeAnalyzer(this, board, groupAnalyzer)
  private val information_ : EyeInformation = eyeAnalyzer.determineEyeInformation()

  /** In addition to the type, an eye can have a status like nakade, unsettled, or aliveInAtari. */
  private val status_ : EyeStatus = information_.determineStatus(this, board)

  private var numCornerPoints_ : Int = 0
  private var numEdgePoints_ : Int = 0

  initializePositionCounts(board)

  override def getInformation: EyeInformation = information_

  override def getStatus: EyeStatus = status_

  override def getEyeTypeName: String = {
    if (information_ == null) "unknown eye type"
    else information_.getTypeName
  }

  override def getNumCornerPoints: Int = numCornerPoints_

  override def getNumEdgePoints: Int = numEdgePoints_

  override def initializeMembers(): Unit = {
    members_ = new GoBoardPositionSet()
  }

  private def initializePositionCounts(board: GoBoard): Unit = {
    numCornerPoints_ = 0
    numEdgePoints_ = 0
    getMembers.asScala.foreach { pos =>
      if (board.isCornerTriple(pos)) {
        numCornerPoints_ += 1
      }
      if (board.isOnEdge(pos)) {
        numEdgePoints_ += 1
      }
    }
  }

  /**
   * @return the hashSet containing the members
   */
  override def getMembers: GoBoardPositionSet = members_

  /**
   * @return true if the piece is an enemy of the string owner.
   * If the difference in health between the stones is great, then they are not really enemies
   * because one of them is dead. I used to do the "much weaker" test here but the group health
   * info is not guaranteed to be updated yet.
   */
  override def isEnemy(pos: GoBoardPosition): Boolean = {
    val g = getGroup
    assert(g != null, s"group for $this is null")
    if (pos.isUnoccupied) {
      return false
    }
    val stone = pos.getPiece.asInstanceOf[GoStone]

    assert(g.isOwnedByPlayer1 == isOwnedByPlayer1,
      s"Bad group ownership for eye=$this. Owning Group=$g")
    stone.isOwnedByPlayer1 != isOwnedByPlayer1
  }

  /**
   * Add a space to the eye string.
   * The space is either blank or a dead enemy stone.
   * Called only during initial construction.
   */
  override def addMemberInternal(space: GoBoardPosition, board: GoBoard): Unit = {
    if (getMembers.contains(space)) {
      GameContext.log(1, s"Warning: the eye, $this, already contains $space")
      assert((space.getString == null) || (this eq space.getString),
        s"bad space or bad owning string ${space.getString}")
    }
    space.setEye(this)
    getMembers.add(space)
  }

  override def clear(): Unit = {
    getMembers.asScala.foreach { pos =>
      pos.setEye(null)
      pos.setVisited(false)
    }
    setGroup(null)
    getMembers.clear()
  }

  override def toString: String = {
    new EyeSerializer(this).serialize()
  }

  override def getPrintPrefix: String = {
    s" Eye: $getEyeTypeName: "
  }
}
