// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.move.wall

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeBoardPosition, Homes}
import com.barrybecker4.game.twoplayer.blockade.board.analysis.BoardAnalyzer
import com.barrybecker4.game.twoplayer.blockade.board.path.PathList
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

class WallPlacementValidator(private val board: BlockadeBoard):

  def checkLegalWallPlacement(wall: BlockadeWall, location: Location, analyzer: BoardAnalyzer): String =
    var sError: String = null
    assert(wall != null)

    val oldWalls = getOldWalls(wall)

    if hasWallOverlap(wall) then
      sError = GameContext.getLabel("CANT_OVERLAP_WALLS")

    val pos = board.getPosition(location)
    if sError == null && hasWallIntersection(wall) then
      sError = GameContext.getLabel("CANT_INTERSECT_WALLS")
    else if sError == null && pos == null then
      sError = GameContext.getLabel("INVALID_WALL_PLACEMENT")
    else if sError == null && missingPath(analyzer) then
      sError = GameContext.getLabel("MUST_HAVE_ONE_PATH")

    for wallPos <- wall.getPositions.asScala do
      val origWall = oldWalls(wallPos)
      if wall.getIsVertical then
        wallPos.setEastWall(origWall.orNull)
      else
        wallPos.setSouthWall(origWall.orNull)

    sError

  private def getOldWalls(wall: BlockadeWall): Map[BlockadeBoardPosition, Option[BlockadeWall]] =
    val oldWalls = mutable.Map[BlockadeBoardPosition, Option[BlockadeWall]]()
    val vertical = wall.getIsVertical

    for pos <- wall.getPositions.asScala do
      val origWall = if vertical then pos.getEastWall else pos.getSouthWall
      oldWalls.put(pos, Option(origWall))

    oldWalls.toMap

  private def hasWallOverlap(wall: BlockadeWall): Boolean =
    val vertical = wall.getIsVertical
    for pos <- wall.getPositions.asScala do
      val origWall = if vertical then pos.getEastWall else pos.getSouthWall

      if (vertical && pos.isEastBlocked) || (!vertical && pos.isSouthBlocked) then
        return true

      if vertical then
        pos.setEastWall(wall)
      else
        pos.setSouthWall(wall)

    false

  private def hasWallIntersection(wall: BlockadeWall): Boolean =
    val vertical = wall.getIsVertical
    val pos = wall.getFirstPosition

    val secondPos = 
      if vertical then board.getPosition(pos.getRow, pos.getCol + 1)
      else board.getPosition(pos.getRow + 1, pos.getCol)

    (vertical && pos.isSouthBlocked && secondPos.isSouthBlocked) ||
      (!vertical && pos.isEastBlocked && secondPos.isEastBlocked)

  private def missingPath(analyzer: BoardAnalyzer): Boolean =
    val paths1 = analyzer.findAllOpponentShortestPaths(player1 = true)
    val paths2 = analyzer.findAllOpponentShortestPaths(player1 = false)
    GameContext.log(2, s"paths1.magnitude=${paths1.size} paths2.magnitude=${paths2.size}")

    val expectedNumPaths = getExpectedNumPaths
    paths1.size < expectedNumPaths || paths2.size < expectedNumPaths

  private def getExpectedNumPaths: Int =
    Homes.NUM_HOMES * Homes.NUM_HOMES
