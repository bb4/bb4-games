/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board.analysis

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeBoardPosition, Homes}
import com.barrybecker4.game.twoplayer.blockade.board.path.{PathList, PlayerPathLengths}
import scala.collection.mutable

class BoardAnalyzer(private val board: BlockadeBoard):
  
  private val pathFinder = ShortestPathFinder(board)

  def findAllOpponentShortestPaths(player1: Boolean): PathList =
    val numShortestPaths = Homes.NUM_HOMES * Homes.NUM_HOMES
    val opponentPaths = PathList()
    val hsPawns = mutable.LinkedHashSet[BlockadeBoardPosition]()

    for row <- 1 to board.getNumRows; col <- 1 to board.getNumCols do
      val pos = board.getPosition(row, col)
      if pos.isOccupied && pos.getPiece.isOwnedByPlayer1 != player1 then
        hsPawns.add(pos)
        assert(hsPawns.size <= Homes.NUM_HOMES, s"Error: too many opponent pieces: $hsPawns")
        val paths = findShortestPaths(pos)
        GameContext.log(2, s"about to add ${paths.size} more paths to " +
          s"${opponentPaths.size} maxAllowed=$numShortestPaths")
        opponentPaths.addAll(paths)

    opponentPaths

  def findShortestPaths(position: BlockadeBoardPosition): PathList =
    pathFinder.findShortestPaths(position)

  def findPlayerPathLengths(): PlayerPathLengths =
    val playerPaths = PlayerPathLengths()

    for row <- 1 to board.getNumRows; col <- 1 to board.getNumCols do
      val pos = board.getPosition(row, col)
      if pos.isOccupied then
        val piece = pos.getPiece
        val paths = board.findShortestPaths(pos)
        playerPaths.getPathLengthsForPlayer(piece.isOwnedByPlayer1).updatePathLengths(paths)

    playerPaths
