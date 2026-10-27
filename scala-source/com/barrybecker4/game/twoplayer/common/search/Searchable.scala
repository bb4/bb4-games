/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.twoplayer.common.{TwoPlayerBoard, TwoPlayerController, TwoPlayerMove}
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy
import com.barrybecker4.game.twoplayer.common.search.transposition.HashKey
import com.barrybecker4.game.twoplayer.common.search.tree.IGameTreeViewable
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * Implementations of this interface allow searching for successive moves in a two player game.
 * The SearchStrategy classes call methods of this interface to do their search.
 * @see TwoPlayerController for the default implementation of this interface
 *
 * @author Barry Becker
 */
trait Searchable[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]]:

  /**
   * @return the search options having to do with search parameters.
   */
  def getSearchOptions: SearchOptions[M, B]

  /**
   * @param move the move to play.
   */
  def makeInternalMove(move: M): Unit

  /**
   * Takes back the most recent move.
   * @param move move to undo.
   */
  def undoInternalMove(move: M): Unit

  /**
   * @return the current strategy used for searching.
   */
  def getSearchStrategy: SearchStrategy[M, B]

  /**
   * **** SEARCH ******
   * @return the best move to use as the next move.
   */
  def searchForNextMove(weights: ParameterArray, lastMove: M, treeViewer: IGameTreeViewable): M

  /**
   * Given a move, determine whether the game is over.
   * If recordWin is true then the variables for player1/2HasWon can get set.
   * sometimes, like when we are looking ahead in search we do not want to set these.
   * @param move the move to check. If null then return true. This is typically the last move played
   * @param recordWin if true then the controller state will record wins
   * @return  true if the game is over.
   */
  def done(move: M, recordWin: Boolean): Boolean

  /**
   *  Statically evaluate a boards state to compute the value of the last move
   *  from player1's perspective.
   *  This function is a key function that must be created for each type of game added.
   *  If evaluating from player 1's perspective, then good moves for p1 are given a positive score.
   *
   * @param lastMove  the last move made
   *  @param weights  the polynomial weights to use in the polynomial evaluation function
   *  @return the worth of the board from the specified players point of view
   */
  def worth(lastMove: M, weights: ParameterArray): Int

  /**
   * Generate a list of good evaluated next moves given the last move.
   * This function is a key function that must be created for each type of game added.
   *
   * @param lastMove  the last move made if there was one. (null if first move of the game)
   * @param weights  the polynomial weights to use in the polynomial evaluation function.
   * @return list of possible next moves.
   */
  def generateMoves(lastMove: M, weights: ParameterArray): MoveList[M]

  /**
   * generate those moves that are critically urgent
   * If you generate too many, then you run the risk of an explosion in the search tree.
   * These moves should be sorted from most to least urgent
   *
   * @param lastMove  the last move made
   * @param weights  the polynomial weights to use in the polynomial evaluation function
   * @return a list of moves that the current player needs to urgently play or face imminent defeat.
   */
  def generateUrgentMoves(lastMove: M, weights: ParameterArray): MoveList[M]

  /**
   * returns true if the specified move caused one or more opponent pieces to become jeopardized
   * @return true if the move m is in jeopardy.
   */
  def inJeopardy(move: M, weights: ParameterArray): Boolean

  /** The current board state. */
  def getBoard: B

  /**
   * List of moves made so far.
   * @return list of moves made on the board.
   */
  def getMoveList: MoveList[M]

  /**
   * @return num moves played so far
   */
  def getNumMoves(): Int

  /**
   * @return a copy of our current state so we can make moves and not worry about undoing them.
   */
  def copy(): Searchable[M, B]

  /**
   * @return  the Zobrist hash for the currently searched position
   */
  def getHashKey(): HashKey
