// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.move.wall

import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeBoardPosition, Direction}

class StraightWallChecker(board: BlockadeBoard) extends WallChecker(board):

  def checkWallsForEast(eastPos: BlockadeBoardPosition, pos: BlockadeBoardPosition): BlockadeWallList =
    val wallsToCheck = BlockadeWallList()
    if eastPos != null && !pos.isEastBlocked then
      val northPos = pos.getNeighbor(Direction.NORTH, board)
      val southPos = pos.getNeighbor(Direction.SOUTH, board)
      val northEastPos = pos.getNeighbor(Direction.NORTH_EAST, board)
      if northPos != null && !northPos.isEastBlocked &&
        !(northPos.isSouthBlocked && northPos.getSouthWall == northEastPos.getSouthWall) then
        wallsToCheck.add(BlockadeWall(pos, northPos))
      if southPos != null && !southPos.isEastBlocked &&
        !(pos.isSouthBlocked && pos.getSouthWall == eastPos.getSouthWall) then
        wallsToCheck.add(BlockadeWall(pos, southPos))
    wallsToCheck

  def checkWallsForWest(westPos: BlockadeBoardPosition, pos: BlockadeBoardPosition): BlockadeWallList =
    val wallsToCheck = BlockadeWallList()
    if westPos != null && !westPos.isEastBlocked then
      val northPos = pos.getNeighbor(Direction.NORTH, board)
      val southPos = pos.getNeighbor(Direction.SOUTH, board)
      val northWestPos = pos.getNeighbor(Direction.NORTH_WEST, board)
      if northPos != null && !northWestPos.isEastBlocked &&
        !(northWestPos.isSouthBlocked && northWestPos.getSouthWall == northPos.getSouthWall) then
        wallsToCheck.add(BlockadeWall(westPos, northWestPos))
      val southWestPos = pos.getNeighbor(Direction.SOUTH_WEST, board)
      if southPos != null && !southWestPos.isEastBlocked &&
        !(westPos.isSouthBlocked && westPos.getSouthWall == pos.getSouthWall) then
        wallsToCheck.add(BlockadeWall(westPos, southWestPos))
    wallsToCheck

  def checkWallsForNorth(northPos: BlockadeBoardPosition, pos: BlockadeBoardPosition): BlockadeWallList =
    val wallsToCheck = BlockadeWallList()
    if northPos != null && !northPos.isSouthBlocked then
      val westPos = pos.getNeighbor(Direction.WEST, board)
      val northWestPos = pos.getNeighbor(Direction.NORTH_WEST, board)
      if westPos != null && !northWestPos.isSouthBlocked &&
        !(westPos.isEastBlocked && westPos.getEastWall == northWestPos.getEastWall) then
        wallsToCheck.add(BlockadeWall(northPos, northWestPos))
      val northEastPos = pos.getNeighbor(Direction.NORTH_EAST, board)
      if northEastPos != null && !northEastPos.isSouthBlocked &&
        !(pos.isEastBlocked && pos.getEastWall == northPos.getEastWall) then
        wallsToCheck.add(BlockadeWall(northPos, northEastPos))
    wallsToCheck

  def checkWallsForSouth(southPos: BlockadeBoardPosition, pos: BlockadeBoardPosition): BlockadeWallList =
    val wallsToCheck = BlockadeWallList()
    if southPos != null && !pos.isSouthBlocked then
      val westPos = pos.getNeighbor(Direction.WEST, board)
      val eastPos = pos.getNeighbor(Direction.EAST, board)
      if eastPos != null && !eastPos.isSouthBlocked &&
        !(pos.isEastBlocked && pos.getEastWall == southPos.getEastWall) then
        wallsToCheck.add(BlockadeWall(pos, eastPos))
      val southWestPos = pos.getNeighbor(Direction.SOUTH_WEST, board)
      if westPos != null && !westPos.isSouthBlocked &&
        !(westPos.isEastBlocked && westPos.getEastWall == southWestPos.getEastWall) then
        wallsToCheck.add(BlockadeWall(pos, westPos))
    wallsToCheck
