// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.move

import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeBoardPosition, Direction}
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall

class MovePlacementValidator(private val board: BlockadeBoard):

  def isMoveBlockedByWall(move: BlockadeMove, wall: BlockadeWall): Boolean =
    board.addWall(wall)
    val blocked = isMoveBlocked(move)
    board.removeWall(wall)
    blocked

  def isMoveBlocked(move: BlockadeMove): Boolean =
    var blocked = false

    val fromRow = move.getFromRow
    val fromCol = move.getFromCol
    val start = board.getPosition(fromRow, fromCol)
    val west = start.getNeighbor(Direction.WEST, board)
    val north = start.getNeighbor(Direction.NORTH, board)
    var south, east: BlockadeBoardPosition = null

    // Match Java switch fall-through for paired directions (e.g. SOUTH_SOUTH also checks SOUTH).
    move.getDirection match
      case Direction.NORTH_NORTH =>
        val northNorth = start.getNeighbor(Direction.NORTH_NORTH, board)
        if northNorth.isSouthBlocked then blocked = true
        if north.isSouthBlocked then blocked = true
      case Direction.NORTH =>
        if north.isSouthBlocked then blocked = true
      case Direction.WEST_WEST =>
        val westWest = start.getNeighbor(Direction.WEST_WEST, board)
        if westWest.isEastBlocked then blocked = true
        if west.isEastBlocked then blocked = true
      case Direction.WEST =>
        if west.isEastBlocked then blocked = true
      case Direction.EAST_EAST =>
        east = start.getNeighbor(Direction.EAST, board)
        if east.isEastBlocked then blocked = true
        if start.isEastBlocked then blocked = true
      case Direction.EAST =>
        if start.isEastBlocked then blocked = true
      case Direction.SOUTH_SOUTH =>
        south = start.getNeighbor(Direction.SOUTH, board)
        if south.isSouthBlocked then blocked = true
        if start.isSouthBlocked then blocked = true
      case Direction.SOUTH =>
        if start.isSouthBlocked then blocked = true
      case Direction.NORTH_WEST =>
        val northWest = start.getNeighbor(Direction.NORTH_WEST, board)
        if !((west.isEastOpen && northWest.isSouthOpen) ||
          (north.isSouthOpen && northWest.isEastOpen)) then
          blocked = true
      case Direction.NORTH_EAST =>
        val northEast = start.getNeighbor(Direction.NORTH_EAST, board)
        if !((start.isEastOpen && northEast.isSouthOpen) ||
          (north.isSouthOpen && north.isEastOpen)) then
          blocked = true
      case Direction.SOUTH_WEST =>
        val southWest = start.getNeighbor(Direction.SOUTH_WEST, board)
        if !((west.isEastOpen && west.isSouthOpen) ||
          (start.isSouthOpen && southWest.isEastOpen)) then
          blocked = true
      case Direction.SOUTH_EAST =>
        south = start.getNeighbor(Direction.SOUTH, board)
        east = start.getNeighbor(Direction.EAST, board)
        if !((start.isEastOpen && east.isSouthOpen) ||
          (start.isSouthOpen && south.isEastOpen)) then
          blocked = true

    blocked
