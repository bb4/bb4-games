/* Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.board

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.Move
import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard
import com.barrybecker4.game.twoplayer.mancala.move.{Captures, MancalaMove, MoveMaker, MoveUndoer}
import scala.jdk.CollectionConverters.*

/**
  * Representation of a Mancala Game Board.
  *
  * @author Barry Becker
  */
class MancalaBoard(numCols: Int) extends TwoPlayerBoard[MancalaMove]:

  /** traditionally each bin starts with 3 stones. */
  private val INITIAL_STONES_PER_BIN: Byte = 3

  /** used to navigate around the bins on in the board */
  private val navigator = BinNavigator(numCols)

  setSize(2, numCols)

  /**
    * default constructor
    * The first and last columns are for the home bases.
    */
  def this() = this(8)

  protected def this(mb: MancalaBoard) =
    this(mb.getNumCols)
    // Copy constructor logic is handled by parent TwoPlayerBoard

  override def copy(): MancalaBoard =
    val newBoard = MancalaBoard(getNumCols)
    for row <- 1 to getNumRows do
      for col <- 1 to getNumCols do
        val pos = getPosition(row, col)
        if pos.getPiece != null then
          newBoard.getPosition(row, col).setPiece(pos.getPiece.asInstanceOf[MancalaBin].copy())
    newBoard

  /**
    * Reset the board to its initial state.
    */
  override def reset(): Unit =
    super.reset()
    for row <- 1 to getNumRows do
      for col <- 2 until getNumCols do
        getPosition(row, col).setPiece(MancalaBin(row == 1, INITIAL_STONES_PER_BIN, false))
    getPosition(1, 1).setPiece(MancalaBin(true, 0.toByte, true))
    getPosition(1, getNumCols).setPiece(MancalaBin(false, 0.toByte, true))

  override def getPositionPrototype: BoardPosition =
    BoardPosition(1, 1, null)

  /**
    * This is just a conservative rough guess.
    * My reasoning is that the end of round is inevitable because
    * once a stone enters the home bin, it can never come out.
    * However, a stone does not always go in a home every turn (but they always move closer), and
    * some turns more than one may go in.
    * So my estimate is 3 times the number of columns times the starting number
    * of stones in each bin. Multiplying by 2 instead of 3 would probably be a more
    * accurate estimate, but we want an upper limit.
    */
  override def getMaxNumMoves: Int =
    getNumCols * INITIAL_STONES_PER_BIN * 3 + 1

  /**
    * {@inheritDoc}
    */
  override def makeInternalMove(move: MancalaMove): Boolean =
    MoveMaker(this).makeMove(move)

  /**
    * {@inheritDoc}
    */
  override def undoInternalMove(move: MancalaMove): Unit =
    MoveUndoer(this).undoMove(move)

  def getHomeBin(player1: Boolean): MancalaBin =
    getBin(navigator.getHomeLocation(player1))

  def isEmpty: Boolean =
    isSideClear(true) && isSideClear(false)

  def isSideClear(player1: Boolean): Boolean =
    var sum = 0
    var currentLoc = navigator.getHomeLocation(!player1)
    for i <- 0 until getNumCols - 2 do
      currentLoc = navigator.getNextLocation(currentLoc)
      sum += getBin(currentLoc).getNumStones
    sum == 0

  /**
    * @return true if the player's move will land the last stone in their own home bin.
    */
  def moveAgainAfterMove(move: Move): Boolean =
    val m = move.asInstanceOf[MancalaMove]
    val lastLoc = findLastStop(m)
    val bin = getBin(lastLoc)
    bin.isHome && bin.isOwnedByPlayer1 == m.isPlayer1

  private def findLastStop(move: MancalaMove): Location =
    var numSeeds = move.getNumStonesSeeded
    var lastLoc = move.getFromLocation
    assert(numSeeds > 0, s"Number of seeds must be positive. It was $numSeeds")

    while numSeeds > 0 do
      lastLoc = navigator.getNextLocation(lastLoc)
      val bin = this.getBin(lastLoc)
      if !(bin.isHome && move.isPlayer1 != bin.isOwnedByPlayer1) then
        numSeeds = (numSeeds - 1).toByte

    lastLoc

  /**
    * @param player1 the player's who's bins to consider
    * @return a list of all player bin locations that have one stone or more.
    */
  def getCandidateStartLocations(player1: Boolean): java.util.List[Location] =
    val locations = new java.util.LinkedList[Location]()
    var currentLoc = getHomeLocation(!player1)
    for i <- 0 until getNumCols - 2 do
      currentLoc = getNextLocation(currentLoc)
      if getBin(currentLoc).getNumStones > 0 then
        locations.add(currentLoc)
    locations

  def getBin(loc: Location): MancalaBin =
    assert(loc != null)
    val bin = getPosition(loc).getPiece.asInstanceOf[MancalaBin]
    assert(bin != null, s" Could not find mancala bin at $loc")
    bin

  /**
    * Clear off the remaining stones on the specified players side and put them in his store.
    * @param player1 player's whose side to clear.
    */
  def clearSide(player1: Boolean, captures: Captures): Unit =
    val homeBin = getHomeBin(player1)
    var currentLoc = getHomeLocation(!player1)
    for i <- 0 until getNumCols - 2 do
      currentLoc = getNextLocation(currentLoc)
      val bin = getBin(currentLoc)
      if bin.getNumStones > 0 then
        captures.put(currentLoc, bin.getNumStones)
        homeBin.increment(bin.takeStones())

  def getHomeLocation(player1: Boolean): Location =
    navigator.getHomeLocation(player1)

  def getNextLocation(loc: Location): Location =
    navigator.getNextLocation(loc)

  def getOppositeLocation(loc: Location): Location =
    navigator.getOppositeLocation(loc)

  /**
    * Num different states.
    * This is used primarily for the Zobrist hash. You do not need to override if you do not use it.
    * For mancala there are a lot of potential states. A bin can be empty, or it can have any number of stones.
    * @return number of different states this position can have.
    */
  override def getNumPositionStates: Int =
    getNumCols * INITIAL_STONES_PER_BIN
