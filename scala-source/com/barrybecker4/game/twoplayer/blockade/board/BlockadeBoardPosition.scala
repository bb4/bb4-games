/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.common.board.{BoardPosition, GamePiece}
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall

object BlockadeBoardPosition:
  def apply(row: Int, col: Int): BlockadeBoardPosition =
    new BlockadeBoardPosition(ByteLocation(row.toByte, col.toByte), null, null, null, false, false)

final class BlockadeBoardPosition(
  loc: Location,
  piece: GamePiece,
  private var southWall: BlockadeWall,
  private var eastWall: BlockadeWall,
  private val isPlayer1Home: Boolean,
  private val isPlayer2Home: Boolean
) extends BoardPosition(loc, piece):

  private var visited: Boolean = false

  def this(pos: BlockadeBoardPosition) =
    this(pos.getLocation, pos.getPiece, pos.southWall, pos.eastWall, 
         pos.isPlayer1Home, pos.isPlayer2Home)

  def this(row: Int, col: Int) =
    this(ByteLocation(row.toByte, col.toByte), null, null, null, false, false)

  override def clear(): Unit =
    super.clear()

  override def copy(): BoardPosition =
    new BlockadeBoardPosition(this)

  def setSouthWall(wall: BlockadeWall): Unit =
    southWall = wall

  def getSouthWall: BlockadeWall = southWall

  def setEastWall(wall: BlockadeWall): Unit =
    eastWall = wall

  def getEastWall: BlockadeWall = eastWall

  def setVisited(v: Boolean): Unit =
    visited = v

  def isVisited: Boolean = visited

  def isSouthBlocked: Boolean = southWall != null

  def isEastBlocked: Boolean = eastWall != null

  def isSouthOpen: Boolean = southWall == null

  def isEastOpen: Boolean = eastWall == null

  def isHomeBase: Boolean = isPlayer1Home || isPlayer2Home

  def isHomeBase(player1: Boolean): Boolean =
    if player1 then isPlayer1Home else isPlayer2Home

  def isOccupiedHomeBase(player1: Boolean): Boolean =
    isOccupied && (if player1 then isPlayer1Home else isPlayer2Home)

  def getNeighbor(d: Direction, board: BlockadeBoard): BlockadeBoardPosition =
    val row = getRow
    val col = getCol
    val offset = d.getOffset
    board.getPosition(row + offset.row, col + offset.col)

  def getStateIndex: Int =
    val ownerState = if isOccupied then (if getPiece.isOwnedByPlayer1 then 2 else 3) else 1
    ownerState * (1 + ((if southWall == null then 0 else 2) + (if eastWall == null then 0 else 1))) - 1
