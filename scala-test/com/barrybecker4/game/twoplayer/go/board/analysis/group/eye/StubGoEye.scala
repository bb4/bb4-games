// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.board.analysis.group.eye

import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.eye.information.{EyeInformation, EyeStatus}
import com.barrybecker4.game.twoplayer.go.board.elements.eye.{EyeSerializer, IGoEye}
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionSet}

/**
 * @author Barry Becker
 */
class StubGoEye(
  ownedByPlayer1: Boolean,
  status: EyeStatus,
  eyeInfo: EyeInformation,
  numCornerPoints: Int,
  numEdgePoints: Int,
  isUncondAlive: Boolean,
  numMembers: Int
) extends IGoEye:

  override def getStatus: EyeStatus = status

  override def getInformation: EyeInformation = eyeInfo

  override def getEyeTypeName: String = eyeInfo.getTypeName

  override def getNumCornerPoints: Int = numCornerPoints

  override def getNumEdgePoints: Int = numEdgePoints

  override def getGroup: IGoGroup = null

  override def getMembers: GoBoardPositionSet = null

  override def isEnemy(pos: GoBoardPosition): Boolean =
    throw new IllegalStateException("not supported")

  override def isOwnedByPlayer1: Boolean = ownedByPlayer1

  override def setVisited(visited: Boolean): Unit = ()

  override def size: Int = numMembers

  override def getLiberties(board: GoBoard): GoBoardPositionSet =
    throw new UnsupportedOperationException()

  override def getNumLiberties(board: GoBoard): Int =
    throw new UnsupportedOperationException()

  override def clear(): Unit =
    throw new UnsupportedOperationException()

  override def isUnconditionallyAlive: Boolean = isUncondAlive

  override def setUnconditionallyAlive(unconditionallyAlive: Boolean): Unit =
    throw new UnsupportedOperationException()

  /**
   * @param eye eye to test for rough equality with.
   * @return true if all the fields match the specified eye
   */
  def isMatch(eye: IGoEye): Boolean =
    val infoEqual = getInformation == eye.getInformation
    val pointsEqual =
      getNumCornerPoints == eye.getNumCornerPoints && getNumEdgePoints == eye.getNumEdgePoints
    isOwnedByPlayer1 == eye.isOwnedByPlayer1 &&
      isUnconditionallyAlive == eye.isUnconditionallyAlive &&
      getStatus == eye.getStatus &&
      infoEqual &&
      pointsEqual &&
      size == eye.size

  override def toString: String = new EyeSerializer(this).serialize()
