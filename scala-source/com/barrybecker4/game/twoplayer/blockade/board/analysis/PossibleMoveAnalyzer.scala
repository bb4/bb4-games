/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board.analysis

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeBoardPosition, Direction}
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*

class PossibleMoveAnalyzer(private val board: BlockadeBoard):

  private var position: BlockadeBoardPosition = scala.compiletime.uninitialized
  private var fromLocation: Location = scala.compiletime.uninitialized
  private var opponentPlayer1: Boolean = scala.compiletime.uninitialized
  private var possibleMoveList: ListBuffer[BlockadeMove] = scala.compiletime.uninitialized
  private var westPos, eastPos, northPos, southPos: BlockadeBoardPosition = scala.compiletime.uninitialized

  def getPossibleMoveList(pos: BlockadeBoardPosition, oppIsPlayer1: Boolean): java.util.List[BlockadeMove] = synchronized {
    initialize(pos, oppIsPlayer1)

    val eastOpen = !position.isEastBlocked && eastPos != null
    addIf1HopNeeded(eastOpen, eastPos, 0, 1)

    val southOpen = !position.isSouthBlocked && southPos != null
    addIf1HopNeeded(southOpen, southPos, 1, 0)

    val westOpen = checkSouthOptions(eastOpen, southOpen)
    checkWest(westOpen)
    checkNorthOptions(eastOpen, westOpen)
    checkEast(eastOpen)

    possibleMoveList.asJava
  }

  private def initialize(pos: BlockadeBoardPosition, oppIsPlayer1: Boolean): Unit =
    position = pos
    possibleMoveList = ListBuffer.empty
    opponentPlayer1 = oppIsPlayer1
    fromLocation = position.getLocation

    westPos = position.getNeighbor(Direction.WEST, board)
    eastPos = position.getNeighbor(Direction.EAST, board)
    northPos = position.getNeighbor(Direction.NORTH, board)
    southPos = position.getNeighbor(Direction.SOUTH, board)

  private def checkSouthOptions(eastOpen: Boolean, southOpen: Boolean): Boolean =
    if southPos != null then
      val toLocation = fromLocation.incrementOnCopy(2, 0)
      addIf2HopLegal(southOpen, southPos.isSouthBlocked, toLocation)

      val southEastPos = position.getNeighbor(Direction.SOUTH_EAST, board)
      addIfDiagonalLegal(southEastPos, eastOpen && !eastPos.isSouthBlocked,
        southOpen && !southPos.isEastBlocked)

    var westOpen = false
    if westPos != null then
      val southWestPos = position.getNeighbor(Direction.SOUTH_WEST, board)
      westOpen = !westPos.isEastBlocked
      addIf1HopNeeded(westOpen, westPos, 0, -1)

      addIfDiagonalLegal(southWestPos, westOpen && !westPos.isSouthBlocked,
        southOpen && !southWestPos.isEastBlocked)

    westOpen

  private def checkWest(westOpen: Boolean): Unit =
    val westWestPos = position.getNeighbor(Direction.WEST_WEST, board)
    if westWestPos != null then
      val toLocation = fromLocation.incrementOnCopy(0, -2)
      addIf2HopLegal(westOpen, westWestPos.isEastBlocked, toLocation)

  private def checkNorthOptions(eastOpen: Boolean, westOpen: Boolean): Unit =
    var northOpen = false
    if northPos != null then
      val northEastPos = position.getNeighbor(Direction.NORTH_EAST, board)
      northOpen = !northPos.isSouthBlocked
      addIf1HopNeeded(northOpen, northPos, -1, 0)
      addIfDiagonalLegal(northEastPos, eastOpen && !northEastPos.isSouthBlocked,
        northOpen && !northPos.isEastBlocked)

    val northNorthPos = position.getNeighbor(Direction.NORTH_NORTH, board)
    if northNorthPos != null then
      val toLocation = fromLocation.incrementOnCopy(-2, 0)
      addIf2HopLegal(northOpen, northNorthPos.isSouthBlocked, toLocation)

    val northWestPos = position.getNeighbor(Direction.NORTH_WEST, board)
    if northWestPos != null then
      addIfDiagonalLegal(northWestPos, westOpen && !northWestPos.isSouthBlocked,
        northOpen && !northWestPos.isEastBlocked)

  private def checkEast(eastOpen: Boolean): Unit =
    if eastPos != null then
      val toLocation = fromLocation.incrementOnCopy(0, 2)
      addIf2HopLegal(eastOpen, eastPos.isEastBlocked, toLocation)

  private def addIf1HopNeeded(directionOpen: Boolean, dirPosition: BlockadeBoardPosition,
                               rowOffset: Int, colOffset: Int): Unit =
    val fromRow = fromLocation.row
    val fromCol = fromLocation.col
    val dirDirPosition = board.getPosition(fromRow + 2 * rowOffset, fromCol + 2 * colOffset)
    val pawnBlockingPath = dirDirPosition != null && dirDirPosition.getPiece != null

    if directionOpen && !dirPosition.isVisited &&
      (pawnBlockingPath || dirPosition.isHomeBase(opponentPlayer1)) then
      possibleMoveList.addOne(
        BlockadeMove.createMove(
          ByteLocation(fromRow.toByte, fromCol.toByte),
          ByteLocation((fromRow + rowOffset).toByte, (fromCol + colOffset).toByte),
          0, position.getPiece, null
        )
      )
      GameContext.log(2, s"ADDED 1 HOP$dirPosition")

  private def addIf2HopLegal(directionOpen: Boolean, blocked: Boolean, toLocation: Location): Unit =
    val dirDirPosition = board.getPosition(toLocation)
    if directionOpen && dirDirPosition != null && !blocked &&
      (dirDirPosition.isUnoccupied || dirDirPosition.isHomeBase(opponentPlayer1)) &&
      !dirDirPosition.isVisited then
      possibleMoveList.addOne(
        BlockadeMove.createMove(fromLocation, toLocation, 0, position.getPiece, null)
      )

  private def addIfDiagonalLegal(diagonalPos: BlockadeBoardPosition,
                                  horizontalPathOpen: Boolean, verticalPathOpen: Boolean): Unit =
    if diagonalPos == null then
      return

    if (horizontalPathOpen || verticalPathOpen) &&
      (diagonalPos.isUnoccupied || diagonalPos.isHomeBase(opponentPlayer1)) &&
      !diagonalPos.isVisited then
      possibleMoveList.addOne(
        BlockadeMove.createMove(fromLocation, diagonalPos.getLocation, 0, position.getPiece, null)
      )
    else if diagonalPos.isOccupied then
      val horzPos = board.getPosition(fromLocation.row, diagonalPos.getCol)
      val vertPos = board.getPosition(diagonalPos.getRow, fromLocation.col)

      if horizontalPathOpen && horzPos.isUnoccupied && !horzPos.isVisited then
        possibleMoveList.addOne(
          BlockadeMove.createMove(fromLocation, horzPos.getLocation, 0, position.getPiece, null)
        )
      if verticalPathOpen && vertPos.isUnoccupied && !vertPos.isVisited then
        possibleMoveList.addOne(
          BlockadeMove.createMove(fromLocation, vertPos.getLocation, 0, position.getPiece, null)
        )
