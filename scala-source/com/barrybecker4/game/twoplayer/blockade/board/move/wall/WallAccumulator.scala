// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.move.wall

import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeBoardPosition, Direction}
import com.barrybecker4.game.twoplayer.blockade.board.path.{Path, PathList}
import scala.jdk.CollectionConverters.*

class WallAccumulator(private val board: BlockadeBoard):
  
  private val wallsList: BlockadeWallList = BlockadeWallList()

  def getAccumulatedList: BlockadeWallList = wallsList

  def checkAddWallsForDirection(
      pos: BlockadeBoardPosition,
      paths: PathList,
      direction: Direction
  ): BlockadeWallList =
    val b = board
    var wallsToCheck = BlockadeWallList()
    val westPos = pos.getNeighbor(Direction.WEST, b)
    val eastPos = pos.getNeighbor(Direction.EAST, b)
    val northPos = pos.getNeighbor(Direction.NORTH, b)
    val southPos = pos.getNeighbor(Direction.SOUTH, b)
    val straightChecker = StraightWallChecker(b)
    val diagonalChecker = DiagonalWallChecker(b)

    direction match
      case Direction.EAST =>
        wallsToCheck = straightChecker.checkWallsForEast(eastPos, pos)
      case Direction.WEST =>
        wallsToCheck = straightChecker.checkWallsForWest(westPos, pos)
      case Direction.NORTH =>
        wallsToCheck = straightChecker.checkWallsForNorth(northPos, pos)
      case Direction.SOUTH =>
        wallsToCheck = straightChecker.checkWallsForSouth(southPos, pos)
      case Direction.NORTH_WEST =>
        val northWestPos = pos.getNeighbor(Direction.NORTH_WEST, b)
        wallsToCheck = diagonalChecker.checkWalls(northWestPos, northPos, westPos)
      case Direction.NORTH_EAST =>
        val northEastPos = pos.getNeighbor(Direction.NORTH_EAST, b)
        wallsToCheck = diagonalChecker.checkWalls(northPos, northEastPos, pos)
      case Direction.SOUTH_WEST =>
        val southWestPos = pos.getNeighbor(Direction.SOUTH_WEST, b)
        wallsToCheck = diagonalChecker.checkWalls(westPos, pos, southWestPos)
      case Direction.SOUTH_EAST =>
        wallsToCheck = diagonalChecker.checkWalls(pos, eastPos, southPos)
      case _ =>

    assert(validProposedWalls(wallsToCheck),
      s"Trying to place \n$wallsToCheck\nwhere they already exist in direction $direction\non board\n$board")

    getBlockedWalls(wallsToCheck, paths)

  def validProposedWalls(wallsToCheck: BlockadeWallList): Boolean =
    wallsToCheck.asScala.forall { wall =>
      wall.getPositions.asScala.forall { pos =>
        if wall.getIsVertical then !pos.isEastBlocked
        else !pos.isSouthBlocked
      }
    }

  private def getBlockedWalls(wallsToCheck: BlockadeWallList, paths: PathList): BlockadeWallList =
    for wall <- wallsToCheck.asScala if wall != null && !arePathsBlockedByWall(paths, wall) do
      wallsList.add(wall)
    wallsList

  private def arePathsBlockedByWall(paths: PathList, wall: BlockadeWall): Boolean =
    assert(wall != null)
    paths.asScala.exists(path => path.isBlockedByWall(wall, board))
