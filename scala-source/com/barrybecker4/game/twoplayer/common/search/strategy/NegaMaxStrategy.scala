/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.twoplayer.common.{TwoPlayerMove, TwoPlayerBoard}
import com.barrybecker4.game.twoplayer.common.search.{SearchWindow, Searchable}
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 *  This strategy class defines the NegaMax search algorithm.
 *  Negamax is very much like minimax, but it avoids having separate
 *  sections of code for minimizing and maximizing search.
 *  The game tree it produces should be identical to minimax, but it is slightly more efficient.
 *
 *  @author Barry Becker
 */
class NegaMaxStrategy[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](
  controller: Searchable[M, B],
  weights: ParameterArray
) extends AbstractBruteSearchStrategy[M, B](controller, weights):

  override def search(lastMove: M, parent: SearchTreeNode): M =
    val window = getOptions.getBruteSearchOptions.getInitialSearchWindow
    searchInternal(lastMove, lookAhead_, new SearchWindow(window.beta, window.alpha), parent)

  override def getEvaluationPerspective: EvaluationPerspective =
    EvaluationPerspective.CURRENT_PLAYER

  override protected def findBestMove(lastMove: M, depth: Int, list: MoveList[M],
                                       window: SearchWindow, parent: SearchTreeNode): M =
    var i = 0
    var bestInheritedValue = -SearchStrategy.INFINITY
    var selectedMove: M = null.asInstanceOf[M]
    var bestMove = list.get(0)
    var done = false

    while !list.isEmpty() && !done do
      val theMove = getNextMove(list)
      if isPaused then
        return lastMove
      updatePercentDone(depth, list)

      searchable.makeInternalMove(theMove)
      val child = addNodeToTree(parent, theMove, window)
      i += 1

      selectedMove = searchInternal(theMove, depth - 1,
        new SearchWindow(-window.beta, -Math.max(window.alpha, bestInheritedValue)), child)

      searchable.undoInternalMove(theMove)

      if selectedMove != null then
        val selectedValue = -selectedMove.getInheritedValue
        theMove.setInheritedValue(selectedValue)

        if selectedValue > bestInheritedValue then
          bestMove = theMove
          bestInheritedValue = selectedValue
          if alphaBeta_ && bestInheritedValue >= window.beta then
            showPrunedNodesInTree(list, parent, i, selectedValue, window)
            done = true

    bestMove.setSelected(true)
    lastMove.setInheritedValue(-bestMove.getInheritedValue)
    bestMove

  override protected def fromPlayer1sPerspective(lastMove: M): Boolean =
    !lastMove.isPlayer1
