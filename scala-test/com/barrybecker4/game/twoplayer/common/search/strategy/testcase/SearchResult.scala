/* Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy.testcase

import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub
import com.barrybecker4.game.twoplayer.common.search.strategy.{EvaluationPerspective, SearchStrategy}

/**
 * Meta information about a move made during search.
 *
 * @author Barry Becker
 */
object SearchResult:
  /** this is the move id when there is no next move at all */
  val NO_MOVE = "NO_MOVE"

  /**
   * This does the actual search for the found move given the strategy to use
   * @param initialMove the root move in the game tree
   * @param searchStrategy the search strategy to use to find the result move
   */
  def apply(initialMove: TwoPlayerMoveStub, searchStrategy: SearchStrategy[TwoPlayerMoveStub, ?]): SearchResult =
    val foundMove = searchStrategy.search(initialMove, null)
    var moveId = SearchResult.NO_MOVE
    var inheritedValue = 0
    if foundMove != null then
      inheritedValue = foundMove.getInheritedValue
      if searchStrategy.getEvaluationPerspective == EvaluationPerspective.CURRENT_PLAYER then
        inheritedValue = if initialMove.isPlayer1 then -inheritedValue else inheritedValue
      moveId = foundMove.getId
    new SearchResult(moveId, inheritedValue, searchStrategy.getNumMovesConsidered)

class SearchResult(
    private val moveId: String,
    private val inheritedValue: Int,
    private val numMovesConsidered: Long
):

  /**
   * The result of a search.
   * @param moveId a unique identifier for this move. Usually it encodes the position in the game tree.
   * @param inheritedValue at the leaf, this is the result of board evaluation.
   *                       At interior nodes it is determined by the search algorithm.
   * @param numConsideredMoves the total number of moves considered to arrive at this result.
   */

  def getMoveId: String = moveId

  def getInheritedValue: Int = inheritedValue

  def getNumMovesConsidered: Long = numMovesConsidered

  override def toString: String =
    "moveId:" + moveId + " inheritedValue:" + inheritedValue + " numMovesConsidered=" + numMovesConsidered

  override def equals(o: Any): Boolean =
    o match
      case that: SearchResult =>
        inheritedValue == that.inheritedValue &&
          numMovesConsidered == that.numMovesConsidered &&
          (if moveId != null then moveId == that.moveId else that.moveId == null)
      case _ => false

  override def hashCode(): Int =
    var result = if moveId != null then moveId.hashCode else 0
    result = 31 * result + inheritedValue
    result = 31 * result + numMovesConsidered.toInt
    result
