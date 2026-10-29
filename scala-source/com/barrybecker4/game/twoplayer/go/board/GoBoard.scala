/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.CornerChecker
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer
import com.barrybecker4.game.twoplayer.go.board.elements.group.GoGroupSet
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition
import com.barrybecker4.game.twoplayer.go.board.elements.string.{GoString, GoStringSet, IGoString}
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import com.barrybecker4.game.twoplayer.go.board.update.{BoardUpdater, CaptureCounts}
import scala.jdk.CollectionConverters._

/**
 * Representation of a Go Game Board
 * There are a lot of data structures to organize the state of the pieces.
 * For example, we update strings, and groups (and eventually armies) after each move.
 * After updating we can use these structures to estimate territory for each side.
 */
final class GoBoard(size: Int, numHandicapStones: Int) extends TwoPlayerBoard[GoMove] {

  /** This is a set of active groups. Groups are composed of strings. */
  private var groups_ : GoGroupSet = scala.compiletime.uninitialized

  /** Handicap stones are on the star points, unless the board is very small */
  private var handicap_ : HandicapStones = scala.compiletime.uninitialized

  /** Updates the board's structure after making or undoing moves */
  private var boardUpdater_ : BoardUpdater = scala.compiletime.uninitialized

  setSize(size, size)
  setHandicap(numHandicapStones)
  init(new CaptureCounts())

  /**
   * Copy constructor
   */
  def this(board: GoBoard) = {
    this(board.getNumRows, 0)

    // Call parent copy logic
    copyFrom(board)

    handicap_ = board.handicap_
    val analyzer = new NeighborAnalyzer(this)
    analyzer.determineAllStringsOnBoard()
    groups_ = analyzer.findAllGroupsOnBoard

    init(board.boardUpdater_.getCaptureCounts)
  }

  private def copyFrom(board: GoBoard): Unit = {
    for (i <- 1 to board.getNumRows) {
      for (j <- 1 to board.getNumCols) {
        val pos = board.getPosition(i, j)
        if (pos != null) {
          setPosition(pos.copy().asInstanceOf[GoBoardPosition])
        }
      }
    }
  }

  override def copy(): GoBoard = {
    getProfiler.startCopyBoard()
    val b = new GoBoard(this)
    getProfiler.stopCopyBoard()
    b
  }

  def setPosition(pos: GoBoardPosition): Unit = {
    super.setPosition(pos)
  }

  /**
   * Start over from the beginning and reinitialize everything.
   * The first time through we need to initialize the star-point positions.
   */
  override def reset(): Unit = {
    super.reset()
    groups_ = new GoGroupSet()

    setHandicap(getHandicap)
    init(new CaptureCounts())
  }

  private def init(capCounts: CaptureCounts): Unit = {
    boardUpdater_ = new BoardUpdater(this, capCounts)
  }

  override def getPositionPrototype: BoardPosition = {
    new GoBoardPosition(1, 1, null, null)
  }

  def setHandicap(numHandicapStones: Int): Unit = {
    handicap_ = new HandicapStones(numHandicapStones, getNumRows)
    makeMoves(handicap_.getHandicapMoves)
  }

  /**
   * get the number of handicap stones used in this game.
   * @return number of handicap stones
   */
  def getHandicap: Int = {
    if (handicap_ == null) {
      return 0
    }
    handicap_.getNumber
  }

  /**
   * in go there is not really a theoretical limit to the number of moves,
   * but practically if we exceed this then we award the game to whoever is ahead.
   * @return the maximum number of moves ever expected for this game.
   */
  override def getMaxNumMoves: Int = {
    2 * positions_.getNumBoardSpaces
  }

  /**
   * Num different states.
   * This is used primarily for the Zobrist hash. You do not need to override if you do not use it.
   * The states are player1, player2, or empty (we may want to add ko).
   * @return number of different states this position can have.
   */
  override def getNumPositionStates: Int = 3

  def getHandicapPositions: java.util.List[?] = handicap_.getStarPoints

  /**
   * @return typical number of moves in a go game.
   */
  override def getTypicalNumMoves: Int = {
    positions_.getNumBoardSpaces - getNumRows
  }

  /**
   * get the current set of active groups. Should be read only. Do not modify.
   * @return all the valid groups on the board (for both sides)
   */
  def getGroups: GoGroupSet = groups_

  def setGroups(groups: GoGroupSet): Unit = {
    groups_ = groups
  }

  /**
   * Adjust the liberties on the strings (both black and white) that we touch.
   * @param liberty either occupied or not depending on if we are placing the stone or removing it.
   */
  def adjustLiberties(liberty: GoBoardPosition): Unit = {
    val na = new NeighborAnalyzer(this)
    val stringNbrs = na.findStringNeighbors(liberty)
    stringNbrs.asScala.foreach { sn =>
      sn.asInstanceOf[GoString].changedLiberty(liberty)
    }
  }

  /**
   * Make sure that all the positions on the board are reset to the unvisited state.
   */
  def unvisitAll(): Unit = {
    for (i <- 1 to getNumRows) {
      for (j <- 1 to getNumCols) {
        val pos = getPosition(i, j).asInstanceOf[GoBoardPosition]
        pos.setVisited(false)
      }
    }
  }

  private def getProfiler: GoProfiler = GoProfiler.getInstance

  /**
   * given a move specification, execute it on the board.
   * This places the players symbol at the position specified by move, and updates groups,
   * removes captures, and counts territory.
   *
   * @return false if the move is somehow invalid
   */
  override def makeInternalMove(move: GoMove): Boolean = {
    getProfiler.startMakeMove()

    // if its a passing move, there is nothing to do
    if (move.isPassOrResignation) {
      GameContext.log(2, if (move.isPassingMove) "Making passing move" else "Resigning")
      getProfiler.stopMakeMove()
      return true
    }

    val valid = super.makeInternalMove(move)
    boardUpdater_.updateAfterMove(move)

    getProfiler.stopMakeMove()
    valid
  }

  /**
   * for Go, undoing a move means changing that space back to a blank, restoring captures, and updating groups.
   * @param move the move to undo.
   */
  override def undoInternalMove(move: GoMove): Unit = {
    getProfiler.startUndoMove()

    // A pass or resignation does not place a stone. Resignation is stored at (1,1).
    if (move.isPassOrResignation) {
      getProfiler.stopUndoMove()
      return
    }

    boardUpdater_.updateAfterRemove(move)
    getProfiler.stopUndoMove()
  }

  def getNumCaptures(player1StonesCaptured: Boolean): Int = {
    boardUpdater_.getNumCaptures(player1StonesCaptured)
  }

  /**
   * Corner triples are the 3 points closest to a corner
   * @param position position to see if in corner of board.
   * @return true if the specified BoardPosition is on the order of the board
   */
  def isCornerTriple(position: BoardPosition): Boolean = {
    new CornerChecker(getNumRows, getNumCols).isCornerTriple(position)
  }

  /**
   * @return either the number of black or white stones.
   */
  def getNumStones(forPlayer1: Boolean): Int = {
    var numStones = 0

    // we should be able to just sum all the position scores now.
    for (i <- 1 to getNumRows) {
      for (j <- 1 to getNumCols) {
        val pos = getPosition(i, j).asInstanceOf[GoBoardPosition]
        if (pos.isOccupied && pos.getPiece.isOwnedByPlayer1 == forPlayer1) {
          numStones += 1
        }
      }
    }
    numStones
  }

  override def toString: String = {
    val rows = getNumRows
    val cols = getNumCols
    val buf = new StringBuilder((rows + 2) * (cols + 2))

    buf.append("   ")
    for (j <- 1 to rows) {
      buf.append(j % 10)
    }
    buf.append(' ')
    buf.append("\n  ")
    for (j <- 1 to cols + 2) {
      buf.append('-')
    }
    buf.append('\n')

    for (i <- 1 to rows) {
      buf.append(i / 10)
      buf.append(i % 10)
      buf.append('|')
      for (j <- 1 to cols) {
        val space = getPosition(i, j).asInstanceOf[GoBoardPosition]
        if (space.isOccupied) {
          buf.append(if (space.getPiece.isOwnedByPlayer1) 'X' else 'O')
        } else {
          buf.append(' ')
        }
      }
      buf.append('|')
      buf.append('\n')
    }
    buf.toString
  }
}
