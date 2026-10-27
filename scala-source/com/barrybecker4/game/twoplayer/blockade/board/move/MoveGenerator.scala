/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board.move

import com.barrybecker4.game.common.{GameContext, MoveList}
import com.barrybecker4.game.common.board.BoardPosition
import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeBoardPosition}
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.WallPlacementFinder
import com.barrybecker4.game.twoplayer.blockade.board.path.{Path, PathList}
import com.barrybecker4.optimization.parameter.ParameterArray

import java.util.{LinkedList, List as JList}

/**
 * Generates candidate next moves for a game of Blockade given a current board state.
 *
 * @author Barry Becker
 */
class MoveGenerator(weights_ : ParameterArray, board: BlockadeBoard):

  /**
   * First find the opponent's shortest paths.
   * Then For each piece of the current player's NUM_HOME pieces, add a move that represents a step along
   * its shortest paths to the opponent homes and all reasonable wall placements.
   * To limit the number of wall placements we will restrict possibilities to those positions which
   * effect one of the *opponents* shortest paths.
   * @param lastMove last move that was made on the board
   * @return list of generated moves.
   */
  def generateMoves(lastMove: BlockadeMove): MoveList[BlockadeMove] =
    val moveList = new MoveList[BlockadeMove]()
    val player1 = (lastMove == null) || !lastMove.isPlayer1

    // There is one path from every piece to every opponent home (i.e. n*NUM_HOMES)
    val opponentPaths = board.findAllOpponentShortestPaths(player1)

    val pawnLocations: JList[BoardPosition] = new LinkedList[BoardPosition]()
    var row = 1
    while row <= board.getNumRows do
      var col = 1
      while col <= board.getNumCols do
        val p = board.getPosition(row, col)
        if p.isOccupied && p.getPiece.isOwnedByPlayer1 == player1 then
          pawnLocations.add(p)
          addMoves(p, moveList, opponentPaths, weights_)
        col += 1
      row += 1

    if moveList.isEmpty then
      GameContext.log(1, "There aren't any moves to consider for lastMove=" + lastMove
        + " Complete movelist =" + board.getMoveList + " \nThe pieces are at:" + pawnLocations)

    moveList

  /**
   * Find all the moves a piece can make from position p, and insert them into moveList.
   *
   * @param position the piece to check from its new location.
   * @param moveList add the potential moves to this existing list.
   * @param weights to use.
   * @return the number of moves added.
   */
  private def addMoves(position: BoardPosition, moveList: MoveList[BlockadeMove],
                       opponentPaths: PathList, weights: ParameterArray): Int =
    var numMovesAdded = 0

    // first find the NUM_HOMES shortest paths for p.
    val paths = board.findShortestPaths(position.asInstanceOf[BlockadeBoardPosition])

    val wallFinder = new WallPlacementFinder(board, opponentPaths, weights)

    // for each of these paths, add possible wall positions.
    // Take the first move from each shortest path and add the wall positions to it.
    val pathIt = paths.iterator
    while pathIt.hasNext do
      val path = pathIt.next()
      if path.getLength > 0 then
        numMovesAdded += addPotentialMoves(moveList, wallFinder, path)
      else
        GameContext.log(1, "one of the paths was empty : " + paths)

    numMovesAdded

  /**
   * Add reasonable next moves.
   * @return the number of moves added.
   */
  private def addPotentialMoves(moveList: MoveList[BlockadeMove], wallFinder: WallPlacementFinder, path: Path): Int =
    val firstStep = path.get(0)
    // make the move
    board.makeMove(firstStep)

    // after making the first move, the shortest paths may have changed somewhat.
    // unfortunately, I think we need to recalculate them.
    val newPos = board.getPosition(firstStep.getToRow, firstStep.getToCol)
    val ourPaths = board.findShortestPaths(newPos)

    val wallMoves = wallFinder.findWallPlacementsForMove(firstStep, ourPaths)
    GameContext.log(2, "num wall placements for Move = " + wallMoves.size)
    board.undoMove()

    // iterate through the wallMoves and add only the ones that are not there already
    val wallIt = wallMoves.iterator
    while wallIt.hasNext do
      val wallMove = wallIt.next()
      if !moveList.contains(wallMove) then
        moveList.add(wallMove)

    wallMoves.size
