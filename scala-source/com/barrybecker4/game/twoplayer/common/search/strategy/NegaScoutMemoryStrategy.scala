/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.twoplayer.common.{TwoPlayerMove, TwoPlayerBoard}
import com.barrybecker4.game.twoplayer.common.search.{SearchWindow, Searchable}
import com.barrybecker4.game.twoplayer.common.search.transposition.{Entry, HashKey, TranspositionTable}
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode
import com.barrybecker4.optimization.parameter.ParameterArray
import scala.Option

/**
 *  This strategy class defines the NegaScout with memory search algorithm.
 *  This version stores the values of moves that have already been searched.
 *  See http://people.csail.mit.edu/plaat/mtdf.html
 *  and http://en.wikipedia.org/wiki/Negascout
 *
 *  @author Barry Becker
 */
final class NegaScoutMemoryStrategy[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](
  controller: Searchable[M, B],
  weights: ParameterArray
) extends NegaScoutStrategy[M, B](controller, weights) with MemorySearchStrategy[M, B]:

  /** Stores positions that have already been evaluated, so we do not need to repeat work. */
  private val lookupTable: TranspositionTable[M] = new TranspositionTable[M]()

  override def getTranspositionTable(): TranspositionTable[?] = lookupTable

  override def searchInternal(lastMove: M, depth: Int,
                                         window: SearchWindow, parent: SearchTreeNode): M =
    val key = searchable.getHashKey()
    val entry = lookupTable.get(key)
    if lookupTable.entryExists(entry, lastMove, depth, window) then
      return entry.get.bestMove

    val done = searchable.done(lastMove, false)
    if depth <= 0 || done then
      if doQuiescentSearch(depth, done, lastMove) then
        val qMove = quiescentSearch(lastMove, depth, window, parent)
        if qMove != null then
          val theEntry = new Entry[M](qMove, qMove.getInheritedValue)
          lookupTable.put(key, theEntry)
          return qMove

      val sign = if fromPlayer1sPerspective(lastMove) then 1 else -1
      lastMove.setInheritedValue(sign * lastMove.getValue)
      val theEntry = new Entry[M](lastMove, -lastMove.getInheritedValue)
      lookupTable.put(key, theEntry)
      return lastMove

    val list = searchable.generateMoves(lastMove, weights_)

    if depth == lookAhead_ then
      numTopLevelMoves_ = list.size

    if emptyMoveList(list, lastMove) then return null.asInstanceOf[M]

    findBestMove(lastMove, depth, list, window, parent)

  override def findBestMove(lastMove: M, depth: Int, list: MoveList[M],
                                       window: SearchWindow, parent: SearchTreeNode): M =
    var newBeta = window.beta
    var selectedMove: TwoPlayerMove = null

    var bestMove = list.get(0)
    val entry = new Entry[M](bestMove, depth, window)

    while !list.isEmpty() do
      val theMove = getNextMove(list)
      if isPaused then
        return lastMove
      updatePercentDone(depth, list)

      searchable.makeInternalMove(theMove)
      val child = addNodeToTree(parent, theMove, window)

      // search with minimal search window
      try
        selectedMove = searchInternal(theMove, depth - 1, new SearchWindow(-newBeta, -window.alpha), child)
      finally
        searchable.undoInternalMove(theMove)

      if selectedMove != null then
        var selectedValue = -selectedMove.getInheritedValue
        theMove.setInheritedValue(selectedValue)

        if selectedValue > window.alpha then
          window.alpha = selectedValue

        if window.alpha >= window.beta then
          theMove.setInheritedValue(window.alpha)
          bestMove = theMove
          storeBestMove(window, entry, bestMove.getInheritedValue)
          bestMove.setSelected(true)
          lastMove.setInheritedValue(-bestMove.getInheritedValue)
          return bestMove

        if window.alpha >= newBeta then
          // re-search with narrower window (typical alpha beta search).
          searchable.makeInternalMove(theMove)
          try
            selectedMove = searchInternal(theMove, depth - 1, window.negateAndSwap(), child)
          finally
            searchable.undoInternalMove(theMove)

          if selectedMove != null then
            selectedValue = -selectedMove.getInheritedValue
            theMove.setInheritedValue(selectedValue)
            bestMove = theMove

            if window.alpha >= window.beta then
              storeBestMove(window, entry, bestMove.getInheritedValue)
              bestMove.setSelected(true)
              lastMove.setInheritedValue(-bestMove.getInheritedValue)
              return bestMove

        newBeta = window.alpha + 1

    storeBestMove(window, entry, bestMove.getInheritedValue)
    bestMove.setSelected(true)
    lastMove.setInheritedValue(-bestMove.getInheritedValue)
    bestMove

  /**
   * Store off the best move so we do not need to analyze it again.
   */
  private def storeBestMove(window: SearchWindow, entry: Entry[M], bestValue: Int): Unit =
    if bestValue <= window.alpha then
      entry.upperValue = bestValue
    else if bestValue < window.beta then
      entry.lowerValue = bestValue
      entry.upperValue = bestValue
    else if bestValue >= window.beta then
      entry.lowerValue = bestValue
    lookupTable.put(searchable.getHashKey(), entry)
