/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.{AbstractGameProfiler, GameProfiler}
import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.twoplayer.blockade.board.analysis.{BoardAnalyzer, PossibleMoveAnalyzer}
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.{BlockadeWall, WallPlacementValidator}
import com.barrybecker4.game.twoplayer.blockade.board.path.{PathList, PlayerPathLengths}
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard
import scala.jdk.CollectionConverters.*

class BlockadeBoard(numRows: Int, numCols: Int) extends TwoPlayerBoard[BlockadeMove]:

  private var homes: Homes = Homes()
  private var boardAnalyzer: BoardAnalyzer = _
  private var wallValidator: WallPlacementValidator = _

  setSize(numRows, numCols)
  boardAnalyzer = BoardAnalyzer(this)
  wallValidator = WallPlacementValidator(this)

  private def this(b: BlockadeBoard) =
    this(b.getNumRows, b.getNumCols)
    positions_ = b.positions_.copy()
    boardAnalyzer = BoardAnalyzer(this)
    homes = Homes(b.homes)

  override def copy(): BlockadeBoard =
    BlockadeBoard(this)

  override def getPosition(row: Int, col: Int): BlockadeBoardPosition =
    super.getPosition(row, col).asInstanceOf[BlockadeBoardPosition]

  override def getPosition(loc: Location): BlockadeBoardPosition =
    super.getPosition(loc.row, loc.col).asInstanceOf[BlockadeBoardPosition]

  override def reset(): Unit =
    super.reset()

    if homes == null then
      homes = Homes()
    
    homes.init(getNumRows, getNumCols)

    for i <- 0 until Homes.NUM_HOMES do
      setPosition(homes.getPlayerHomes(player1 = true)(i))
      setPosition(homes.getPlayerHomes(player1 = false)(i))

  override def getPositionPrototype: BoardPosition =
    BlockadeBoardPosition(1, 1)

  override def getMaxNumMoves: Int =
    Integer.MAX_VALUE

  def getPlayerHomes(player1: Boolean): Array[BoardPosition] =
    homes.getPlayerHomes(player1)

  def getPossibleMoveList(position: BlockadeBoardPosition, op1: Boolean): java.util.List[BlockadeMove] =
    PossibleMoveAnalyzer(this).getPossibleMoveList(position, op1)

  def findAllOpponentShortestPaths(player1: Boolean): PathList =
    boardAnalyzer.findAllOpponentShortestPaths(player1)

  def findShortestPaths(position: BlockadeBoardPosition): PathList =
    boardAnalyzer.findShortestPaths(position)

  def checkLegalWallPlacement(wall: BlockadeWall, location: Location): String =
    wallValidator.checkLegalWallPlacement(wall, location, boardAnalyzer)

  def findPlayerPathLengths(): PlayerPathLengths =
    boardAnalyzer.findPlayerPathLengths()

  def addWall(wall: BlockadeWall): Unit =
    showWall(wall, show = true)

  def removeWall(wall: BlockadeWall): Unit =
    showWall(wall, show = false)

  private def showWall(wall: BlockadeWall, show: Boolean): Unit =
    val positions = wall.getPositions
    assert(positions.isEmpty || positions.size == 2, s"positions=$positions")

    for position <- positions.asScala do
      val otherPos = getPosition(position.getLocation)
      if wall.getIsVertical then
        assert(!show || !otherPos.isEastBlocked, "East blocked already!")
        otherPos.setEastWall(if show then wall else null)
      else
        assert(!show || !otherPos.isSouthBlocked, "South blocked already!")
        otherPos.setSouthWall(if show then wall else null)

  private def getProfiler: AbstractGameProfiler =
    GameProfiler.getInstance

  override def makeInternalMove(move: BlockadeMove): Boolean =
    getProfiler.startMakeMove()
    val toPos = getPosition(move.getToLocation)
    
    if toPos.isOccupiedHomeBase(!move.isPlayer1) then
      move.capturedOpponentPawn = toPos.getPiece
    
    toPos.setPiece(move.getPiece)

    if move.getWall != null then
      addWall(move.getWall)
    
    getPosition(move.getFromRow, move.getFromCol).clear()
    getProfiler.stopMakeMove()
    true

  override def undoInternalMove(move: BlockadeMove): Unit =
    getProfiler.startUndoMove()
    val startPos = getPosition(move.getFromRow, move.getFromCol)
    startPos.setPiece(move.getPiece)
    val toPos = getPosition(move.getToLocation)
    toPos.clear()
    
    if move.capturedOpponentPawn != null then
      toPos.setPiece(move.capturedOpponentPawn)
      move.capturedOpponentPawn = null

    if move.getWall != null then
      removeWall(move.getWall)
    
    getProfiler.stopUndoMove()

  override def getNumPositionStates: Int = 12

  override def getStateIndex(pos: BoardPosition): Int =
    pos.asInstanceOf[BlockadeBoardPosition].getStateIndex

  override def toString: String =
    val buf = new StringBuilder(100)
    for i <- 1 to getNumRows; j <- 1 to getNumCols do
      val pos = getPosition(i, j)
      if pos.isOccupied then
        buf.append(pos).append("\n")
      if pos.getEastWall != null then
        buf.append(s"East wall at: $i $j\n")
      if pos.getSouthWall != null then
        buf.append(s"South wall at: $i $j\n")
    buf.toString()
