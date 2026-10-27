/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board.move

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoardPosition, Direction}
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import scala.jdk.CollectionConverters.*

object BlockadeMove:
  def createMove(origin: Location, destination: Location, 
                 value: Int, piece: GamePiece, wall: BlockadeWall): BlockadeMove =
    BlockadeMove(origin, destination, value, piece, wall)

class BlockadeMove(
  private val fromLocation: Location,
  destination: Location,
  value: Int,
  piece: GamePiece,
  private var wall: BlockadeWall
) extends TwoPlayerMove(destination, value, piece):

  private val direction: Direction = initDirection()
  
  var capturedOpponentPawn: GamePiece = scala.compiletime.uninitialized

  private def this(move: BlockadeMove) =
    this(move.fromLocation, move.getToLocation, move.getValue, move.getPiece, move.wall)
    this.capturedOpponentPawn = move.capturedOpponentPawn

  override def copy(): BlockadeMove =
    BlockadeMove(this)

  private def initDirection(): Direction =
    val rowDif = toLocation.row - fromLocation.row
    val colDif = toLocation.col - fromLocation.col
    Direction.getDirection(rowDif, colDif)

  override def equals(obj: Any): Boolean = obj match
    case mv: BlockadeMove =>
      getFromLocation.equals(mv.getFromLocation) &&
        getToLocation.equals(mv.getToLocation) &&
        ((wall == null && mv.getWall == null) || (wall != null && wall.equals(mv.getWall))) &&
        (isPlayer1 == mv.isPlayer1)
    case _ => false

  override def hashCode(): Int =
    val wallHash = if wall == null then 0 else wall.hashCode()
    100 * fromLocation.row + 99 * fromLocation.col +
      30 * getToLocation.row + getToLocation.col + wallHash + (if isPlayer1 then 54321 else 0)

  def getFromRow: Int = fromLocation.row

  def getFromCol: Int = fromLocation.col

  def getFromLocation: Location = fromLocation

  def getWall: BlockadeWall = wall

  def setWall(w: BlockadeWall): Unit =
    wall = w

  def getDirection: Direction = direction

  override def getConstructorString: String =
    val wallCreator = if getWall != null then
      val it = getWall.getPositions.iterator()
      val p1 = it.next()
      val p2 = it.next()
      s"new BlockadeWall(new BlockadeBoardPosition(${p1.getRow}, ${p1.getCol}), new BlockadeBoardPosition(${p2.getRow}, ${p2.getCol}))"
    else "null"

    val pieceCreator = if getPiece != null then
      s"new GamePiece(${getPiece.isOwnedByPlayer1})"
    else "null"

    s"createMove(${getFromLocation.row},${getFromLocation.col},  ${getToLocation.row},${getToLocation.col}, $getValue, $pieceCreator, $wallCreator),"

  override def toString: String =
    val s = super.toString
    val wallStr = if wall != null then " " + wall.toString else " (no wall placed)"
    s + wallStr + s" ($fromLocation)->($getToLocation)"
