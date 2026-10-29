/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.move

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.{NeighborAnalyzer, NeighborType}
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionSet, GoStone}
import com.barrybecker4.game.twoplayer.go.board.elements.string.{GoStringSet, IGoString}
import scala.jdk.CollectionConverters._

/**
 * Describes a change in state from one board
 * position to the next in a Go game.
 */
class GoMove(destination: Location, value: Int, stone: GoStone)
  extends TwoPlayerMove(destination, value, stone) {

  /**
   * A linked list of the pieces that were captured with this move.
   * null if there were no captures.
   */
  private var captureList_ : GoCaptureList = null

  /** Copy constructor */
  def this(move: GoMove) = {
    this(move.getToLocation, move.getValue, move.getPiece.asInstanceOf[GoStone])
    if (move.captureList_ != null) {
      captureList_ = move.captureList_.copy()
    }
    this.isPass = move.isPass
    this.isResignation = move.isResignation
    this.setPlayer1(move.isPlayer1)
  }

  /**
   * make a deep copy of the move object
   */
  override def copy(): GoMove = new GoMove(this)

  /**
   * Check if the current move is suicidal.
   * Suicidal moves (ones that kill your own pieces) are illegal.
   * Usually a move is suicidal if you play on your last liberty.
   * However, if you kill an enemy string by playing on your last liberty,
   * then it is legal.
   * @return true if this move is suicidal.
   */
  def isSuicidal(board: GoBoard): Boolean = {
    val stone = board.getPosition(getToRow, getToCol).asInstanceOf[GoBoardPosition]

    val na = new NeighborAnalyzer(board)
    val nobiNbrs = na.getNobiNeighbors(stone, false, NeighborType.ANY)
    val occupiedNbrs = new GoBoardPositionSet()
    nobiNbrs.asScala.foreach { pos =>
      if (pos.isOccupied) {
        occupiedNbrs.add(pos)
      }
    }

    !hasLiberties(occupiedNbrs, nobiNbrs) && partOfDeadString(occupiedNbrs, board)
  }

  /**
   * Can't be suicidal if we have a liberty.
   * @return true if one or more liberties still available.
   */
  private def hasLiberties(occupiedNbrs: GoBoardPositionSet, nobiNbrs: GoBoardPositionSet): Boolean = {
    nobiNbrs.size > occupiedNbrs.size
  }

  /**
   * If the newly placed stone captures an opponent string, then we return false.
   * @param occupiedNbrs The 4 occupied Nbrs neighbors to check
   * @return true if the newly placed stone is part of a string that is now captured as a result of playing.
   */
  private def partOfDeadString(occupiedNbrs: GoBoardPositionSet, board: GoBoard): Boolean = {
    !occupiedNbrs.asScala.exists { nbr =>
      if (nbr.getPiece.isOwnedByPlayer1 == this.isPlayer1)
        nbr.getString.getNumLiberties(board) > 1
      else
        nbr.getString.getNumLiberties(board) == 1
    }
  }

  /**
   * It's a ko if its a single stone string, it captured exactly one stone, and it has one liberty.
   * @param board the go board
   * @return true if this move is part of a ko fight sequence.
   */
  def isKo(board: GoBoard): Boolean = {
    if (getNumCaptures == 1) {
      val pos = board.getPosition(getToLocation).asInstanceOf[GoBoardPosition]

      val nbrAnal = new NeighborAnalyzer(board)
      val enemyNbrs = nbrAnal.getNobiNeighbors(pos, isPlayer1, NeighborType.ENEMY)
      val numEnemyNbrs = enemyNbrs.size

      if (numEnemyNbrs == 3
        || board.isOnEdge(pos) && numEnemyNbrs == 2
        || board.isInCorner(pos) && numEnemyNbrs == 1) {
        return true
      }
    }
    false
  }

  /**
   * returns true if the specified move caused one or more opponent groups to be in atari
   *
   * @return a number > 0 if the move m caused an atari. The number gives the number of stones in atari.
   */
  def numStonesAtaried(board: GoBoard): Int = {
    if (isPassingMove) {
      return 0 // a pass cannot cause an atari
    }

    val pos = board.getPosition(getToRow, getToCol).asInstanceOf[GoBoardPosition]
    val na = new NeighborAnalyzer(board)
    val enemyNbrs = na.getNobiNeighbors(pos, NeighborType.ENEMY)
    val it = enemyNbrs.iterator()
    var numInAtari = 0
    val stringSet = new GoStringSet()
    while (it.hasNext) {
      val s = it.next()
      val atariedString = s.getString
      if (!stringSet.contains(atariedString) && atariedString.getNumLiberties(board) == 1) {
        numInAtari += atariedString.size
      }
      stringSet.add(atariedString) // once its in the set we won't check it again.
    }
    numInAtari
  }

  /**
   * I would like to avoid this setter.
   * @param captures captures to set. We make a defensive copy of them.
   */
  def setCaptures(captures: GoCaptureList): Unit = {
    captureList_ = captures.copy()
  }

  def getCaptures: GoCaptureList = captureList_

  def getNumCaptures: Int = {
    if (captureList_ != null) {
      captureList_.size
    } else {
      0
    }
  }

  /**
   * @return stringified form.
   */
  override def toString: String = {
    var s = super.toString
    if (captureList_ != null) {
      s += "num captured=" + captureList_.size
    }
    s
  }
}

object GoMove {
  /**
   * factory method for creating a passing move
   * @return new passing move
   */
  def createPassMove(value: Int, player1: Boolean): GoMove = {
    val m = new GoMove(new ByteLocation(1, 1), value, null)
    m.isPass = true
    m.setPlayer1(player1)
    m
  }

  /**
   * factory method for creating a resignation move
   * @return new resignation move
   */
  def createResignationMove(player1: Boolean): GoMove = {
    val m = new GoMove(new ByteLocation(1, 1), 0, null)
    m.isResignation = true
    m.setPlayer1(player1)
    m
  }
}
