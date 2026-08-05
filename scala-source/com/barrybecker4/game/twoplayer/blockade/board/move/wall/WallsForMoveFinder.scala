// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.move.wall

import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, Direction}
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.blockade.board.path.PathList

class WallsForMoveFinder(private val board: BlockadeBoard):

  def getWallsForMove(move: BlockadeMove, friendlyPaths: PathList): BlockadeWallList =
    val accumulator = WallAccumulator(board)

    val fromRow = move.getFromRow
    val fromCol = move.getFromCol
    val b = board
    val origPos = board.getPosition(fromRow, fromCol)
    val westPos = origPos.getNeighbor(Direction.WEST, b)
    val eastPos = origPos.getNeighbor(Direction.EAST, b)
    val northPos = origPos.getNeighbor(Direction.NORTH, b)
    val southPos = origPos.getNeighbor(Direction.SOUTH, b)

    // Match Java switch fall-through for paired directions.
    move.getDirection match
      case Direction.EAST_EAST =>
        accumulator.checkAddWallsForDirection(eastPos, friendlyPaths, Direction.EAST)
        accumulator.checkAddWallsForDirection(origPos, friendlyPaths, Direction.EAST)
      case Direction.EAST =>
        accumulator.checkAddWallsForDirection(origPos, friendlyPaths, Direction.EAST)
      case Direction.WEST_WEST =>
        accumulator.checkAddWallsForDirection(westPos, friendlyPaths, Direction.WEST)
        accumulator.checkAddWallsForDirection(origPos, friendlyPaths, Direction.WEST)
      case Direction.WEST =>
        accumulator.checkAddWallsForDirection(origPos, friendlyPaths, Direction.WEST)
      case Direction.SOUTH_SOUTH =>
        accumulator.checkAddWallsForDirection(southPos, friendlyPaths, Direction.SOUTH)
        accumulator.checkAddWallsForDirection(origPos, friendlyPaths, Direction.SOUTH)
      case Direction.SOUTH =>
        accumulator.checkAddWallsForDirection(origPos, friendlyPaths, Direction.SOUTH)
      case Direction.NORTH_NORTH =>
        accumulator.checkAddWallsForDirection(northPos, friendlyPaths, Direction.NORTH)
        accumulator.checkAddWallsForDirection(origPos, friendlyPaths, Direction.NORTH)
      case Direction.NORTH =>
        accumulator.checkAddWallsForDirection(origPos, friendlyPaths, Direction.NORTH)
      case Direction.NORTH_WEST =>
        accumulator.checkAddWallsForDirection(origPos, friendlyPaths, Direction.NORTH_WEST)
      case Direction.NORTH_EAST =>
        accumulator.checkAddWallsForDirection(origPos, friendlyPaths, Direction.NORTH_EAST)
      case Direction.SOUTH_WEST =>
        accumulator.checkAddWallsForDirection(origPos, friendlyPaths, Direction.SOUTH_WEST)
      case Direction.SOUTH_EAST =>
        accumulator.checkAddWallsForDirection(origPos, friendlyPaths, Direction.SOUTH_EAST)

    accumulator.getAccumulatedList
