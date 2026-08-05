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
 *  This strategy class defines the NegaMax with memory search algorithm.
 *  This version stores the values of moves that have already been searched.
 *  Based on pseudo code from Artificial Intelligence for Games by Millington and Funge.
 *
 *  @author Barry Becker
 */
final class NegaMaxMemoryStrategy[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](
  controller: Searchable[M, B],
  weights: ParameterArray
) extends NegaMaxStrategy[M, B](controller, weights) with MemorySearchStrategy[M, B]:

  /** Stores positions that have already been evaluated, so we do not need to repeat work. */
  private val lookupTable: TranspositionTable[M] = new TranspositionTable[M]()

  override def getTranspositionTable(): TranspositionTable[?] = lookupTable

  override def search(lastMove: M, parent: SearchTreeNode): M =
    // need to negate alpha and beta on initial call.
    val window = getOptions.getBruteSearchOptions.getInitialSearchWindow
    val g = window.getMidPoint
    searchInternal(lastMove, lookAhead_, new SearchWindow(g, g), parent)

  override def getEvaluationPerspective: EvaluationPerspective =
    EvaluationPerspective.CURRENT_PLAYER

  override def searchInternal(lastMove: M, depth: Int,
                                         window: SearchWindow, parent: SearchTreeNode): M =
    val key = searchable.getHashKey()
    val entry = lookupTable.get(key)
    if lookupTable.entryExists(entry, lastMove, depth, window) then
      val e = entry.get
      if e.lowerValue > window.alpha then
        e.bestMove.setInheritedValue(e.lowerValue)
        return e.bestMove
      else if e.upperValue < window.beta then
        e.bestMove.setInheritedValue(e.upperValue)
        return e.bestMove

    var theEntry = new Entry[M](lastMove, depth, new SearchWindow(-SearchStrategy.INFINITY, SearchStrategy.INFINITY))

    val done = searchable.done(lastMove, false)
    if depth <= 0 || done then
      if doQuiescentSearch(depth, done, lastMove) then
        val qMove = quiescentSearch(lastMove, depth, window, parent)
        if qMove != null then
          theEntry = new Entry[M](qMove, qMove.getInheritedValue)
          lookupTable.put(key, theEntry)
          return qMove

      val sign = if fromPlayer1sPerspective(lastMove) then 1 else -1
      val value = sign * lastMove.getValue
      lastMove.setInheritedValue(value)
      theEntry.lowerValue = value
      theEntry.upperValue = value
      lookupTable.put(key, theEntry)
      return lastMove

    val list = searchable.generateMoves(lastMove, weights_)

    if depth == lookAhead_ then
      numTopLevelMoves_ = list.size

    if emptyMoveList(list, lastMove) then return null.asInstanceOf[M]

    findBestMove(lastMove, depth, list, window, parent)

  override def findBestMove(lastMove: M, depth: Int, list: MoveList[M],
                                       window: SearchWindow, parent: SearchTreeNode): M =
    var i = 0
    var bestInheritedValue = -SearchStrategy.INFINITY
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
      i += 1

      selectedMove = searchInternal(theMove, depth - 1, window.negateAndSwap(), child)

      searchable.undoInternalMove(theMove)

      if selectedMove != null then
        val selectedValue = -selectedMove.getInheritedValue
        theMove.setInheritedValue(selectedValue)

        if selectedValue > bestInheritedValue then
          bestMove = theMove
          entry.bestMove = theMove
          bestInheritedValue = selectedValue
          if alphaBeta_ && bestInheritedValue >= window.beta then
            showPrunedNodesInTree(list, parent, i, selectedValue, window)
            storeBestMove(window.alpha, entry, bestMove.getInheritedValue)
            bestMove.setSelected(true)
            lastMove.setInheritedValue(-bestMove.getInheritedValue)
            return bestMove

    storeBestMove(window.alpha, entry, bestMove.getInheritedValue)
    bestMove.setSelected(true)
    lastMove.setInheritedValue(-bestMove.getInheritedValue)
    bestMove

  /**
   * Store off the best move so we do not need to analyze it again.
   */
  private def storeBestMove(alpha: Int, entry: Entry[M], bestValue: Int): Unit =
    if bestValue <= alpha then
      entry.upperValue = bestValue
    else
      entry.lowerValue = bestValue
    lookupTable.put(searchable.getHashKey(), entry)
