/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard
import com.barrybecker4.game.twoplayer.common.search.SearchWindow
import com.barrybecker4.game.twoplayer.common.search.Searchable
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * This strategy class defines the NegaScout search algorithm.
 * (sometimes known as principal variation search (PVS) )
 * Negascout is very much like negamax except that it uses a 0 sized search window
 * and iterative deepening.
 * See http://en.wikipedia.org/wiki/Negascout
 *
 *  @author Barry Becker
 */
class NegaScoutStrategy[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](
    controller: Searchable[M, B],
    weights: ParameterArray
) extends NegaMaxStrategy[M, B](controller, weights):

  override protected def findBestMove(
      lastMove: M,
      depth: Int,
      list: MoveList[M],
      window: SearchWindow,
      parent: SearchTreeNode
  ): M =
    var i = 0
    var newBeta = window.beta
    var selectedMove: M = null.asInstanceOf[M]
    var bestMove = list.getFirstMove

    while !list.isEmpty do
      val theMove = getNextMove(list)
      if isPaused then
        return lastMove
      updatePercentDone(depth, list)

      searchable.makeInternalMove(theMove)
      val child = addNodeToTree(parent, theMove, window)

      // search with minimal search window
      selectedMove = searchInternal(theMove, depth - 1, SearchWindow(-newBeta, -window.alpha), child)
      searchable.undoInternalMove(theMove)

      if selectedMove != null then
        val selectedValue = -selectedMove.getInheritedValue
        theMove.setInheritedValue(selectedValue)

        if selectedValue > window.alpha then
          window.alpha = selectedValue

        if window.alpha >= window.beta then // beta cut-off
          theMove.setInheritedValue(window.alpha)
          bestMove = theMove
          // break out of while loop
          return finalizeBestMove(bestMove, lastMove)

        if window.alpha >= newBeta then
          // re-search with narrower window (typical alpha beta search).
          searchable.makeInternalMove(theMove)
          selectedMove = searchInternal(theMove, depth - 1, window.negateAndSwap(), child)
          if selectedMove != null then
            searchable.undoInternalMove(theMove)

            val selectedValue2 = -selectedMove.getInheritedValue
            theMove.setInheritedValue(selectedValue2)
            bestMove = theMove

            if window.alpha >= window.beta then
              showPrunedNodesInTree(list, parent, i, selectedValue2, window)
              // break out of while loop
              return finalizeBestMove(bestMove, lastMove)

        i += 1
        newBeta = window.alpha + 1

    finalizeBestMove(bestMove, lastMove)

  private def finalizeBestMove(bestMove: M, lastMove: M): M =
    bestMove.setSelected(true)
    lastMove.setInheritedValue(-bestMove.getInheritedValue)
    bestMove
