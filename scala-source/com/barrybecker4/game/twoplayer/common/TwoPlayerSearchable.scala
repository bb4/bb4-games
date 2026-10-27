/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.search.transposition.HashKey
import com.barrybecker4.game.twoplayer.common.search.transposition.ZobristHash
import com.barrybecker4.optimization.parameter.ParameterArray
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy

/**
 * For searching two player games
 *
 * @author Barry Becker
 */
abstract class TwoPlayerSearchable[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](
  val board_ : B,
  val players: PlayerList
) extends AbstractSearchable[M, B](board_.getMoveList):

  /** helps to find the best moves. */
  protected val bestMoveFinder_ : BestMoveFinder[M] = new BestMoveFinder[M](getSearchOptions.getBestMovesSearchOptions)

  /** Used to generate hashkeys. */
  protected val hash: ZobristHash = new ZobristHash(board_)

  /**
   * Copy constructor.
   */
  def this(searchable: TwoPlayerSearchable[M, B]) =
    this(searchable.getBoard.copy().asInstanceOf[B], searchable.players.clone().asInstanceOf[PlayerList])

  def getBoard: B = board_

  /**
   * Make the specified move.
   * It is actually ok if the same player moves twice in the case where we are looking for urgent moves.
   * @param move the move to play.
   */
  def makeInternalMove(move: M): Unit =
    getBoard.makeMove(move)

    if move.isPassOrResignation then
      hash.applyPassingMove()
    else
      val loc = move.getToLocation
      hash.applyMove(loc, getBoard.getStateIndex(getBoard.getPosition(loc)))

  /**
   * takes back the most recent move.
   * @param move move to undo
   */
  def undoInternalMove(move: M): Unit =
    val lastMove = moveList.getLastMove
    assert(move != null)
    assert(move.equals(lastMove), s"The move we are trying to undo ($move) in list=$moveList was not equal to the last move ($lastMove). all move=${getBoard.getMoveList}")

    val loc = move.getToLocation

    if !move.isPassingMove then
      hash.applyMove(loc, getBoard.getStateIndex(getBoard.getPosition(loc)))

    getBoard.undoMove()

  /**
   * Evaluates from player 1's perspective
   * @return an integer value for the worth of the move.
   *  must be between -SearchStrategy.WINNING_VALUE and SearchStrategy.WINNING_VALUE.
   */
  def worth(lastMove: M, weights: ParameterArray): Int

  override def getSearchOptions: SearchOptions[M, B] =
    getCurrentPlayer.getOptions.asInstanceOf[TwoPlayerPlayerOptions[M, B]].getSearchOptions

  /**
   * Given a move, determine whether the game is over.
   * If recordWin is true, then the variables for player1/2HasWon can get set.
   *  sometimes, like when we are looking, ahead we do not want to set these.
   * @param move the move to check. If null then return true. This is typically the last move played.
   * @param recordWin if true then the controller state will record wins
   */
  def done(move: M, recordWin: Boolean): Boolean =
    // the game can't be over if no moves have been made yet.
    if moveList.getNumMoves == 0 then
      return false

    if players.anyPlayerWon then
      GameContext.log(1, "Game over because one of the players has won.")
      return true

    if moveList.getNumMoves > 0 && move == null then
      val currentPlayer = getCurrentPlayer
      GameContext.log(1, s"Game is over because there are no more moves for player $currentPlayer")
      if recordWin then
        currentPlayer.setWon(true)
      return true

    val absValue = Math.abs(move.getValue)
    val won = absValue >= SearchStrategy.WINNING_VALUE

    if won && recordWin then
      if move.getValue >= SearchStrategy.WINNING_VALUE then
        players.getPlayer1.setWon(true)
      else
        players.getPlayer2.setWon(true)

    val maxMovesExceeded = moveList.getNumMoves >= getBoard.getMaxNumMoves

    maxMovesExceeded || won

  /**
   * @return the player who's turn it is to move next.
   */
  private def getCurrentPlayer: Player =
    val move = moveList.getLastMove.asInstanceOf[TwoPlayerMove]
    if move == null || !move.isPlayer1 then players.getPlayer1 else players.getPlayer2

  /**
   * @return true if the specified move caused one or more opponent pieces to become jeopardized
   */
  def inJeopardy(move: M, weights: ParameterArray): Boolean = false

  /**
   * @return get a hash key that represents this board state (with negligibly small chance of conflict)
   */
  def getHashKey(): HashKey = hash.getKey
