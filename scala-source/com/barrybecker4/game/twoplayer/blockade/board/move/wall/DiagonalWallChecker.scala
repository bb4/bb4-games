// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.move.wall

import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeBoardPosition}

class DiagonalWallChecker(board: BlockadeBoard) extends WallChecker(board):

  def checkWalls(
      topLeft: BlockadeBoardPosition,
      topRight: BlockadeBoardPosition,
      bottomLeft: BlockadeBoardPosition
  ): BlockadeWallList =
    val leftWall = topLeft.isSouthBlocked
    val rightWall = topRight.isSouthBlocked
    val topWall = topLeft.isEastBlocked
    val bottomWall = bottomLeft.isEastBlocked
    var wallsToCheck = BlockadeWallList()

    if !(leftWall || rightWall || topWall || bottomWall) then
      wallsToCheck.add(BlockadeWall(topLeft, topRight))
      wallsToCheck.add(BlockadeWall(topLeft, bottomLeft))
    else if leftWall && bottomWall then
      wallsToCheck = handleDirectionCase(topRight, 0, 1, wallsToCheck)
      wallsToCheck = handleDirectionCase(topLeft, -1, 0, wallsToCheck)
    else if topWall && rightWall then
      wallsToCheck = handleDirectionCase(topLeft, 0, -1, wallsToCheck)
      wallsToCheck = handleDirectionCase(bottomLeft, 1, 0, wallsToCheck)
    else if topWall && leftWall then
      wallsToCheck = handleDirectionCase(topRight, 0, 1, wallsToCheck)
      wallsToCheck = handleDirectionCase(bottomLeft, 1, 0, wallsToCheck)
    else if rightWall && bottomWall then
      wallsToCheck = handleDirectionCase(topLeft, -1, 0, wallsToCheck)
      wallsToCheck = handleDirectionCase(topLeft, 0, -1, wallsToCheck)
    else if leftWall then
      wallsToCheck.add(BlockadeWall(topLeft, bottomLeft))
      wallsToCheck = handleDirectionCase(topRight, 0, 1, wallsToCheck)
    else if rightWall then
      wallsToCheck.add(BlockadeWall(topLeft, bottomLeft))
      wallsToCheck = handleDirectionCase(topLeft, 0, -1, wallsToCheck)
    else if topWall then
      wallsToCheck.add(BlockadeWall(topLeft, topRight))
      wallsToCheck = handleDirectionCase(bottomLeft, 1, 0, wallsToCheck)
    else // bottomWall
      wallsToCheck.add(BlockadeWall(topLeft, topRight))
      wallsToCheck = handleDirectionCase(topLeft, -1, 0, wallsToCheck)

    wallsToCheck

  private def handleDirectionCase(
      pos: BlockadeBoardPosition,
      rowOffset: Int,
      colOffset: Int,
      wallsToCheck: BlockadeWallList
  ): BlockadeWallList =
    val offsetPos = board.getPosition(pos.getRow + rowOffset, pos.getCol + colOffset)
    val isVertical = rowOffset != 0

    if offsetPos != null then
      val dirOpen = if isVertical then offsetPos.isEastOpen else offsetPos.isSouthOpen
      if dirOpen then
        wallsToCheck.add(BlockadeWall(pos, offsetPos))

    wallsToCheck
