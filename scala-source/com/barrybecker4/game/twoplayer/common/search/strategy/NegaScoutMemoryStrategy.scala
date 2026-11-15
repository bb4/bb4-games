/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.twoplayer.common.{TwoPlayerMove, TwoPlayerBoard}
import com.barrybecker4.game.twoplayer.common.search.{SearchWindow, Searchable}
import com.barrybecker4.game.twoplayer.common.search.transposition.{Entry, TranspositionTable}
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * NegaScout (PVS) with a transposition table.
 * See http://people.csail.mit.edu/plaat/mtdf.html and http://en.wikipedia.org/wiki/Negascout
 */
final class NegaScoutMemoryStrategy[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](
  controller: Searchable[M, B],
  weights: ParameterArray
) extends NegaScoutStrategy[M, B](controller, weights)
    with MemorySearchStrategy[M, B]
    with TranspositionProbe[M, B]:

  private val lookupTable: TranspositionTable[M] = new TranspositionTable[M]()

  override protected def transpositionTable: TranspositionTable[M] = lookupTable

  override def getTranspositionTable(): TranspositionTable[?] = lookupTable

  override def searchInternal(lastMove: M, depth: Int,
                                         window: SearchWindow, parent: SearchTreeNode): M =
    probeTranspositionHit(lastMove, depth, window) match
      case Some(hit) => return hit
      case None => ()

    val key = searchable.getHashKey()
    val done = searchable.done(lastMove, false)
    if depth <= 0 || done then
      if doQuiescentSearch(depth, done, lastMove) then
        val qMove = quiescentSearch(lastMove, depth, window, parent)
        if qMove != null then
          storeQuiescentLeaf(key, qMove)
          return qMove

      val sign = if fromPlayer1sPerspective(lastMove) then 1 else -1
      lastMove.setInheritedValue(sign * lastMove.getValue)
      storeExactLeaf(key, lastMove, -lastMove.getInheritedValue)
      return lastMove

    val list = searchable.generateMoves(lastMove, weights_)

    if depth == lookAhead_ then
      numTopLevelMoves_ = list.size

    if emptyMoveList(list, lastMove) then return null.asInstanceOf[M]

    prepareMovesForSearch(list)
    findBestMove(lastMove, depth, list, window, parent)

  override def findBestMove(lastMove: M, depth: Int, list: MoveList[M],
                                       window: SearchWindow, parent: SearchTreeNode): M =
    var newBeta = window.beta
    var selectedMove: TwoPlayerMove = null

    var bestMove = bestFirstFromPrepared(list)
    val entry = new Entry[M](bestMove, depth, window)

    while !list.isEmpty do
      val theMove = getNextMove(list)
      if isPaused then
        return lastMove
      updatePercentDone(depth, list)

      searchable.makeInternalMove(theMove)
      var child: SearchTreeNode = null
      try
        child = addNodeToTree(parent, theMove, window)
        selectedMove = searchInternal(theMove, depth - 1, SearchWindow(-newBeta, -window.alpha), child)
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
          return finalizeBestMove(bestMove, lastMove)

        if window.alpha >= newBeta then
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
              return finalizeBestMove(bestMove, lastMove)

        newBeta = window.alpha + 1

    storeBestMove(window, entry, bestMove.getInheritedValue)
    finalizeBestMove(bestMove, lastMove)

  private def storeBestMove(window: SearchWindow, entry: Entry[M], bestValue: Int): Unit =
    if bestValue <= window.alpha then
      entry.upperValue = bestValue
    else if bestValue < window.beta then
      entry.lowerValue = bestValue
      entry.upperValue = bestValue
    else if bestValue >= window.beta then
      entry.lowerValue = bestValue
    lookupTable.put(searchable.getHashKey(), entry)
