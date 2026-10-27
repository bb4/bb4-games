/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.elements.position

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.IGoMember
import com.barrybecker4.game.twoplayer.go.board.elements.eye.IGoEye
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.string.IGoString

/**
 * The GoBoardPosition describes the physical marker at a location on the board.
 * It can be empty or occupied. If occupied, then it is either black or white and has a string owner.
 * A GoBoardPosition may have an eye owner if it is part of a group's eye.
 *
 * @author Barry Becker
 */
final class GoBoardPosition(row: Int, col: Int, string: IGoString, stone: GoStone)
  extends BoardPosition(row, col, stone) with IGoMember {

  /** the string (connected set of stones) to which this stone belongs. */
  private var string_ : IGoString = string

  /**
   * if non-null then this position belongs to an eye string.
   * The group owner of the eye is different than the owner of the string.
   */
  private var eye_ : IGoEye = null

  /**
   * when true the stone has been visited already during a search.
   * This is a temporary state that is used for some traversal operations.
   */
  private var visited_ : Boolean = false

  /** the amount this position contributes to the overall score. */
  private var scoreContribution_ : Double = 0.0

  /**
   * Create a deep copy of this position.
   */
  def this(pos: GoBoardPosition) = {
    this(pos.location.row, pos.location.col, pos.string_,
      if (pos.piece == null) null else pos.piece.asInstanceOf[GoStone].copy())
    this.setEye(pos.getEye)
    this.setVisited(pos.isVisited)
  }

  /**
   * @return copy of this position.
   */
  override def copy(): GoBoardPosition = new GoBoardPosition(this)

  /**
   * @param string the string owner we are assign to this stone.
   */
  def setString(string: IGoString): Unit = {
    this.string_ = string
  }

  /**
   * @return the string owner for this stone.
   * There may be none if its blank and part of an eye, in that case null is returned.
   */
  def getString: IGoString = string_

  /**
   * @return the group owner.
   * There is only one group owner that has the same ownership (color) as this stone.
   * The stone may also belong to an eye in an opponent group, however.
   */
  def getGroup: IGoGroup = {
    if (string_ != null) {
      return string_.getGroup
    }
    if (eye_ != null) {
      return eye_.getGroup
    }
    null
  }

  /**
   * @param eye the eye owner this space is to be assigned to
   */
  def setEye(eye: IGoEye): Unit = {
    this.eye_ = eye
  }

  /**
   * @return the eye that this space belongs to. May be null if no eye owner.
   */
  def getEye: IGoEye = eye_

  /**
   * @return true if the string this stone belongs to is in atari
   */
  def isInAtari(b: GoBoard): Boolean = {
    getString != null && getString.getNumLiberties(b) == 1
  }

  def setVisited(visited: Boolean): Unit = {
    this.visited_ = visited
  }

  def isVisited: Boolean = visited_

  /**
   * @return true if this position is part of an eye.
   */
  def isInEye: Boolean = eye_ != null

  /**
   * we must recalculate the number of liberties every time because it changes often.
   * @return the number of liberties the specified position has.
   */
  def getNumLiberties(board: GoBoard): Int = {
    var numLiberties = 0
    val row = getRow
    val col = getCol
    if (row > 1 && board.getPosition(row - 1, col).isUnoccupied) {
      numLiberties += 1
    }
    if (row < board.getNumRows && board.getPosition(row + 1, col).isUnoccupied) {
      numLiberties += 1
    }
    if (col > 1 && board.getPosition(row, col - 1).isUnoccupied) {
      numLiberties += 1
    }
    if (col < board.getNumCols && board.getPosition(row, col + 1).isUnoccupied) {
      numLiberties += 1
    }
    numLiberties
  }

  /**
   * make it show an empty board position.
   */
  def clear(board: GoBoard): Unit = {
    val string = getString

    if (string != null) {
      string.remove(this, board)
    } else {
      assert(isUnoccupied)
    }
    clear()
  }

  /**
   * make it show an empty board position.
   */
  override def clear(): Unit = {
    super.clear()
    setString(null)
    setEye(null)
    scoreContribution_ = 0
    setVisited(false)
  }

  /**
   * @return a string representation of the go board position
   */
  override def getDescription: String = {
    super.getDescription + " score:" + FormatUtil.formatNumber(scoreContribution_)
  }

  /**
   * @return a string representation of the go board position
   */
  override def toString: String = {
    super.toString + " s:" + FormatUtil.formatNumber(scoreContribution_) + " hc:" + hashCode()
  }

  def getScoreContribution: Double = scoreContribution_

  def setScoreContribution(scoreContribution: Double): Unit = {
    this.scoreContribution_ = scoreContribution
  }
}
