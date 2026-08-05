// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.board.move.wall

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.blockade.board.path.{Path, PathList, PlayerPathLengths}
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy
import com.barrybecker4.optimization.parameter.ParameterArray
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*

class WallPlacementFinder(
  private val board: BlockadeBoard,
  private val opponentPaths: PathList,
  private val weights: ParameterArray
):
  
  private val wallsFinder = WallsForMoveFinder(board)

  def findWallPlacementsForMove(firstStep: BlockadeMove, paths: PathList): java.util.List[BlockadeMove] =
    val moves = ListBuffer[BlockadeMove]()

    GameContext.log(2, s"$firstStep\nopponent paths=$opponentPaths\n [[")

    for opponentPath <- opponentPaths.asScala do
      assert(opponentPath != null, 
        s"    Opponent path was null. There are ${opponentPaths.size} opponent paths.")
      findWallPlacementsGivenOpponentPath(firstStep, paths, opponentPath, moves)

    GameContext.log(2, "]]")

    if moves.isEmpty then
      addMoveWithWallPlacement(firstStep, null, weights, moves)

    moves.asJava

  private def findWallPlacementsGivenOpponentPath(
      firstStep: BlockadeMove,
      paths: PathList,
      opponentPath: Path,
      moves: ListBuffer[BlockadeMove]
  ): Unit =
    for j <- 0 until opponentPath.getLength do
      addWallPlacementsForOpponentPathMove(firstStep, opponentPath.get(j), paths, moves)

    if moves.isEmpty then
      GameContext.log(2, s"No opponent moves found for $firstStep along opponentPath=$opponentPath")

  private def addWallPlacementsForOpponentPathMove(
      firstStep: BlockadeMove,
      move: BlockadeMove,
      paths: PathList,
      moves: ListBuffer[BlockadeMove]
  ): Unit =
    val walls = wallsFinder.getWallsForMove(move, paths)
    GameContext.log(2, s"    num walls for move $move  = ${walls.size}")

    if walls.isEmpty then
      GameContext.log(2, s"    ***No walls for move $move that do not interfere with our path")

    assert(walls.size <= 4, s"num walls = ${walls.size}")
    for wall <- walls.asScala do
      addMoveWithWallPlacement(firstStep, wall, weights, moves)

  private def addMoveWithWallPlacement(
      ourmove: BlockadeMove,
      wall: BlockadeWall,
      weights: ParameterArray,
      moves: ListBuffer[BlockadeMove]
  ): Unit =
    val value = 0
    val m = BlockadeMove.createMove(
      ourmove.getFromLocation,
      ourmove.getToLocation,
      value,
      ourmove.getPiece,
      wall
    )

    board.makeMove(m)
    val pathLengths = board.findPlayerPathLengths()
    board.undoMove()

    if pathLengths.isValid then
      m.setValue(pathLengths.determineWorth(SearchStrategy.WINNING_VALUE, weights))
      moves.addOne(m)
    else
      GameContext.log(2, s"Did not add $m because it was invalid.")
