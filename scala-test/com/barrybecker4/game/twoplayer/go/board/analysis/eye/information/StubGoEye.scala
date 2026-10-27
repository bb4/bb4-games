/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.eye.information

import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionSet}
import java.util.List
import scala.jdk.CollectionConverters._

/**
 * @author Barry Becker
 */
class StubGoEye(members: GoBoardPositionSet) extends IGoEye:

  def this(list: List[GoBoardPosition]) =
    this({
      val m = new GoBoardPositionSet()
      m.addAll(list)
      m
    })

  override def getMembers: GoBoardPositionSet = members

  override def clear(): Unit =
    throw new UnsupportedOperationException()

  override def isEnemy(pos: GoBoardPosition): Boolean =
    throw new UnsupportedOperationException()

  override def isOwnedByPlayer1: Boolean = false

  override def setVisited(visited: Boolean): Unit = ()

  override def getInformation: EyeInformation =
    throw new UnsupportedOperationException()

  override def getStatus: EyeStatus =
    throw new UnsupportedOperationException()

  override def getEyeTypeName: String =
    throw new UnsupportedOperationException()

  override def getNumCornerPoints: Int =
    throw new UnsupportedOperationException()

  override def getNumEdgePoints: Int =
    throw new UnsupportedOperationException()

  override def getGroup: IGoGroup = null

  override def isUnconditionallyAlive: Boolean =
    throw new UnsupportedOperationException()

  override def setUnconditionallyAlive(unconditionallyAlive: Boolean): Unit =
    throw new UnsupportedOperationException()

  override def size: Int =
    throw new UnsupportedOperationException()

  override def getLiberties(board: GoBoard): GoBoardPositionSet =
    throw new UnsupportedOperationException()

  override def getNumLiberties(board: GoBoard): Int =
    throw new UnsupportedOperationException()
