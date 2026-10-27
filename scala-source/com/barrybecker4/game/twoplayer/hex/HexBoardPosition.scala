package com.barrybecker4.game.twoplayer.hex

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.common.board.Board
import com.barrybecker4.game.common.board.{BoardPosition, GamePiece}

/**
 *  The BoardPosition describes the physical marker at a location on the board.
 *  It may be empty if there is no piece there.
 * @see Board
 */
class HexBoardPosition(loc: Location, piece: GamePiece) extends BoardPosition(loc, piece):

  /**
   * constructor
   * @param row - y position on the board.
   * @param col - x position on the board.
   * @param piece - the piece to put at this position (use null if there is none).
   */
  def this(row: Int, col: Int, piece: GamePiece) =
    this(ByteLocation(row.toByte, col.toByte), piece)

  def this(pos: HexBoardPosition) =
    this(ByteLocation(pos.getRow.toByte, pos.getCol.toByte), pos.getPiece)

  /**
   * @return copy of this position.
   */
  override def copy(): HexBoardPosition =
    HexBoardPosition(this)

  /**
   * Get the euclidean distance from another board position
   * @param position to get the distance from
   * @return distance from another position
   */
  final def getDistanceFrom(position: HexBoardPosition): Double =
    HexBoardUtil.distanceBetween(location, position.getLocation)

  /**
   * @param position to check if neighboring
   * @return true if immediate neighbor (nobi neighbor)
   */
  final def isNeighbor(position: HexBoardPosition): Boolean =
    HexBoardUtil.isNeighbor(location, position.getLocation)
