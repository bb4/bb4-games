/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.twoplayer.common.{TwoPlayerMove, TwoPlayerBoard}
import com.barrybecker4.game.twoplayer.common.search.{SearchWindow, Searchable}
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * This strategy class defines the MiniMax search algorithm.
 * This is the simplest search strategy to which the other variants are compared.
 * @author Barry Becker
 */
final class MiniMaxStrategy[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](
  controller: Searchable[M, B],
  weights: ParameterArray
) extends AbstractBruteSearchStrategy[M, B](controller, weights):

  override protected def findBestMove(lastMove: M, depth: Int, list: MoveList[M],
                                       window: SearchWindow, parent: SearchTreeNode): M =
    var i = 0
    var selectedValue: Int = 0
    var selectedMove: M = null.asInstanceOf[M]
    // lastMove is the opponent player.
    // if player 1, then search for a high score, else search for a low score.
    val player1ToMove = !lastMove.isPlayer1
    var bestInheritedValue = if player1ToMove then -SearchStrategy.INFINITY else SearchStrategy.INFINITY

    var bestMove = list.get(0)
    var done = false
    while !list.isEmpty() && !done do
      if isPaused then
        return lastMove

      val theMove = getNextMove(list)
      updatePercentDone(depth, list)

      searchable.makeInternalMove(theMove)
      val child = addNodeToTree(parent, theMove, window)
      i += 1

      // recursive call
      selectedMove = searchInternal(theMove, depth - 1, window.copy(), child)

      searchable.undoInternalMove(theMove)

      if selectedMove != null then
        selectedValue = selectedMove.getInheritedValue
        if player1ToMove then
          if selectedValue > bestInheritedValue then
            bestMove = theMove
            bestInheritedValue = bestMove.getInheritedValue
        else if selectedValue < bestInheritedValue then
          bestMove = theMove
          bestInheritedValue = bestMove.getInheritedValue

        if alphaBeta_ && pruneAtCurrentNode(window, selectedValue, player1ToMove) then
          showPrunedNodesInTree(list, parent, i, selectedValue, window)
          done = true

    bestMove.setSelected(true)
    lastMove.setInheritedValue(bestMove.getInheritedValue)
    bestMove

  /**
   * Note: The SearchWindow may be adjusted as a side effect.
   * @param player1ToMove true if player one's turn to move
   * @return whether or not we should prune the current subtree.
   */
  private def pruneAtCurrentNode(window: SearchWindow, selectedValue: Int, player1ToMove: Boolean): Boolean =
    if player1ToMove && selectedValue > window.beta then
      if selectedValue > window.alpha then
        return true
      else
        window.beta = selectedValue

    if !player1ToMove && selectedValue < window.alpha then
      if selectedValue < window.beta then
        return true
      else
        window.alpha = selectedValue

    false

  override protected def fromPlayer1sPerspective(lastMove: M): Boolean = true

  override def getEvaluationPerspective: EvaluationPerspective =
    EvaluationPerspective.ALWAYS_PLAYER1
