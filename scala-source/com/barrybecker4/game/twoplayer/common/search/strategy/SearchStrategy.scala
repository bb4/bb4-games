/* Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.game.twoplayer.common.{TwoPlayerBoard, TwoPlayerMove}
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.search.tree.{IGameTreeViewable, SearchTreeNode}

/**
 * Interface for all SearchStrategies for 2 player games with perfect information.
 *
 * @author Barry Becker
 */
trait SearchStrategy[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]] extends SearchProgress {

  /**
   * The search algorithm.
   * This method is the crux of all 2 player zero sum games with perfect information.
   * Derived classes work by narrowing a bound on the value of the optimal move.
   *
   * @param lastMove the most recent move made by one of the players.
   * @param parent for constructing a ui tree. If null, no game tree is constructed.
   * @return the chosen move (ie the best move) (may be null if no next move).
   */
  def search(lastMove: M, parent: SearchTreeNode): M

  /**
   * @return parameters for defining the search.
   */
  def getOptions: SearchOptions[M, B]

  /**
   * An optional game tree event listener. There can be at most one.
   * @param listener event listener
   */
  def setGameTreeEventListener(listener: IGameTreeViewable): Unit

  /**
   * Moves are either evaluated from the current player's perspective, or always from player 1's perspective.
   * Currently only used by tests to understand how to evaluate moves at a given ply.
   */
  def getEvaluationPerspective: EvaluationPerspective
}

object SearchStrategy {
  /** anything greater than this is considered a won game. */
  val WINNING_VALUE = 4096

  /** For our purposes, this is effectively infinity. */
  val INFINITY = 10000000
}
