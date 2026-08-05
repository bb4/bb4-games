/* Copyright by Barry G. Becker, 2014-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.move

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.{MoveList, board}
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.common.{BestMoveFinder, TwoPlayerMove}
import com.barrybecker4.game.twoplayer.mancala.MancalaSearchable
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard
import com.barrybecker4.optimization.parameter.ParameterArray
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE
import scala.jdk.CollectionConverters.*

/**
  * Responsible for determining a set of reasonable next moves.
  *
  * @author Barry Becker
  */
class MancalaMoveGenerator:

  /**
    * @return all reasonably good next moves.
    */
  def generateMoves(
      searchable: MancalaSearchable,
      lastMove: TwoPlayerMove,
      weights: ParameterArray
  ): MoveList[MancalaMove] =
    val player1 = (lastMove == null) || !lastMove.isPlayer1
    generateMovesForPlayer(searchable, player1, weights)

  private def generateMovesForPlayer(
      searchable: MancalaSearchable,
      player1: Boolean,
      weights: ParameterArray
  ): MoveList[MancalaMove] =
    val moveList = MoveList[MancalaMove]()
    val board = searchable.getBoard
    val candidateStarts = board.getCandidateStartLocations(player1)

    for startLoc <- candidateStarts.asScala do
      val move = MancalaMove.createMove(player1, startLoc, 0, board.getBin(startLoc))

      // if the last move is in a players home bin, then they need to make a follow up move (potentially > 1).
      if board.moveAgainAfterMove(move) then
        val compoundMoves = generateMovesWithFollowUpForPlayer(move, board.copy())
        for compoundMove <- compoundMoves.asScala do
          determineMoveScore(searchable, weights, compoundMove)
          moveList.add(compoundMove)
      else
        determineMoveScore(searchable, weights, move)
        moveList.add(move)

    val finder = BestMoveFinder[MancalaMove](searchable.getSearchOptions.getBestMovesSearchOptions)
    finder.getBestMoves(moveList)

  private def determineMoveScore(
      searchable: MancalaSearchable,
      weights: ParameterArray,
      move: MancalaMove
  ): Unit =
    // this will actually set the captures on the move if any
    searchable.makeInternalMove(move)
    move.setValue(searchable.worth(move, weights))
    // now revert the board
    searchable.undoInternalMove(move)

  /**
    * @return list of compound moves based on an initial move that requires at least one follow up.
    */
  private def generateMovesWithFollowUpForPlayer(
      baseMove: MancalaMove,
      board: MancalaBoard
  ): MoveList[MancalaMove] =
    val moveList = MoveList[MancalaMove]()
    board.makeMove(baseMove)
    val candidateStarts = board.getCandidateStartLocations(baseMove.isPlayer1)

    for startLoc <- candidateStarts.asScala do
      val followUp = MancalaMove.createMove(baseMove.isPlayer1, startLoc, 0, board.getBin(startLoc))

      // there may be follow ups to the follow up.
      if board.moveAgainAfterMove(followUp) && candidateStarts.size > 1 then
        val followUps = generateMovesWithFollowUpForPlayer(followUp, board.copy())
        for m <- followUps.asScala do
          val newMove = baseMove.copy()
          newMove.setFollowUpMove(m)
          moveList.add(newMove)
      else
        val newMove = baseMove.copy()
        board.makeMove(followUp)
        newMove.setFollowUpMove(followUp)
        moveList.add(newMove)

    moveList

  /**
    * @return a list of urgent moves (i.e positions that can result in a win for either player.
    */
  def generateUrgentMoves(
      searchable: MancalaSearchable,
      lastMove: MancalaMove,
      weights: ParameterArray
  ): MoveList[MancalaMove] =
    // no urgent moves at start of game.
    if lastMove == null then
      return MoveList[MancalaMove]()

    val allMoves = findMovesForBothPlayers(searchable, lastMove, weights)

    // now keep only those that result in a win or loss.
    val urgentMoves = MoveList[MancalaMove]()
    val currentPlayer = !lastMove.isPlayer1

    for move <- allMoves.asScala do
      // if it is not a winning move, or we already have it, then skip
      if Math.abs(move.getValue) >= WINNING_VALUE && !contains(move, urgentMoves) then
        move.setUrgent(true)
        move.setPlayer1(currentPlayer)
        move.setPiece(GamePiece(currentPlayer))
        urgentMoves.add(move)

    urgentMoves

  /**
    * Consider both our moves and opponent moves.
    * @return Set of all next moves.
    */
  private def findMovesForBothPlayers(
      searchable: MancalaSearchable,
      lastMove: MancalaMove,
      weights: ParameterArray
  ): MoveList[MancalaMove] =
    val allMoves = MoveList[MancalaMove]()
    val moves = generateMoves(searchable, lastMove, weights)
    allMoves.addAll(moves)

    // unlike go, I don't know if we need this
    val oppLastMove = lastMove.copy()
    oppLastMove.setPlayer1(!lastMove.isPlayer1)
    val opponentMoves = generateMoves(searchable, oppLastMove, weights)
    for move <- opponentMoves.asScala do
      allMoves.add(move)

    allMoves

  /**
    * This version of contains does not consider the player when determining containment.
    * @return true of the {@link MoveList} contains the specified move.
    */
  private def contains(move: MancalaMove, moves: MoveList[MancalaMove]): Boolean =
    for m <- moves.asScala do
      val moveLocation = m.getToLocation
      if moveLocation.equals(move.getToLocation) then
        return true
    false
