/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group

import com.barrybecker4.common.geometry.Box
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.group.{GroupChangeListener, IGoGroup}
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionSet}
import com.barrybecker4.game.twoplayer.go.board.elements.string.{GoStringSet, IGoString}

/**
 * @author Barry Becker
 */
class StubGoGroup(absHealth: Float, ownedByPlayer1: Boolean, numStones: Int) extends IGoGroup:

  def getAbsoluteHealth: Float = absHealth

  override def isOwnedByPlayer1: Boolean = ownedByPlayer1

  override def addChangeListener(listener: GroupChangeListener): Unit =
    throw new UnsupportedOperationException()

  def removeChangeListener(listener: GroupChangeListener): Unit =
    throw new UnsupportedOperationException()

  override def addMember(string: IGoString): Unit =
    throw new UnsupportedOperationException()

  override def getMembers: GoStringSet =
    throw new UnsupportedOperationException()

  override def isEnemy(pos: GoBoardPosition): Boolean =
    throw new UnsupportedOperationException()

  override def setVisited(visited: Boolean): Unit =
    throw new UnsupportedOperationException()

  override def size: Int =
    throw new UnsupportedOperationException()

  override def getLiberties(board: GoBoard): GoBoardPositionSet =
    throw new UnsupportedOperationException()

  override def getNumLiberties(board: GoBoard): Int =
    throw new UnsupportedOperationException()

  override def getNumStones: Int = numStones

  override def containsStone(stone: GoBoardPosition): Boolean =
    throw new UnsupportedOperationException()

  override def remove(string: IGoString): Unit =
    throw new UnsupportedOperationException()

  override def getStones: GoBoardPositionSet =
    throw new UnsupportedOperationException()

  override def updateTerritory(health: Float): Unit =
    throw new UnsupportedOperationException()

  override def findBoundingBox(): Box =
    throw new UnsupportedOperationException()

  override def toHtml: String =
    throw new UnsupportedOperationException()
