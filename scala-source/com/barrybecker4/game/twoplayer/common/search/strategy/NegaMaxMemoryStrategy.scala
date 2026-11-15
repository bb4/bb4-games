/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.twoplayer.common.{TwoPlayerMove, TwoPlayerBoard}
import com.barrybecker4.game.twoplayer.common.search.{SearchWindow, Searchable}
import com.barrybecker4.game.twoplayer.common.search.transposition.{Entry, TranspositionTable}
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode
import com.barrybecker4.optimization.parameter.ParameterArray

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
) extends NegaMaxStrategy[M, B](controller, weights)
    with MemorySearchStrategy[M, B]
    with TranspositionProbe[M, B]:

  /** Stores positions that have already been evaluated, so we do not need to repeat work. */
  private val lookupTable: TranspositionTable[M] = new TranspositionTable[M]()

  override protected def transpositionTable: TranspositionTable[M] = lookupTable

  override def getTranspositionTable(): TranspositionTable[?] = lookupTable

  override def search(lastMove: M, parent: SearchTreeNode): M =
    // Zero-width seed at the root (Millington/Funge memory NegaMax); MTD builds on this.
    val window = getOptions.getBruteSearchOptions.getInitialSearchWindow
    val g = window.getMidPoint
    searchInternal(lastMove, lookAhead_, new SearchWindow(g, g), parent)

  override def getEvaluationPerspective: EvaluationPerspective =
    EvaluationPerspective.CURRENT_PLAYER

  override def searchInternal(lastMove: M, depth: Int,
                                         window: SearchWindow, parent: SearchTreeNode): M =
    // Keep legacy fall-through when entryExists is true but bounds do not force a cutoff.
    val key = searchable.getHashKey()
    val entry = lookupTable.get(key)
    if lookupTable.entryExists(entry, lastMove, depth, window) then
      entry match
        case Some(e) if e.lowerValue > window.alpha =>
          e.bestMove.setInheritedValue(e.lowerValue)
          return e.bestMove
        case Some(e) if e.upperValue < window.beta =>
          e.bestMove.setInheritedValue(e.upperValue)
          return e.bestMove
        case _ => ()

    val done = searchable.done(lastMove, false)
    if depth <= 0 || done then
      if doQuiescentSearch(depth, done, lastMove) then
        val qMove = quiescentSearch(lastMove, depth, window, parent)
        if qMove != null then
          storeQuiescentLeaf(key, qMove)
          return qMove

      val sign = if fromPlayer1sPerspective(lastMove) then 1 else -1
      val value = sign * lastMove.getValue
      lastMove.setInheritedValue(value)
      storeExactLeaf(key, lastMove, value)
      return lastMove

    val list = searchable.generateMoves(lastMove, weights_)

    if depth == lookAhead_ then
      numTopLevelMoves_ = list.size

    if emptyMoveList(list, lastMove) then return null.asInstanceOf[M]

    prepareMovesForSearch(list)
    findBestMove(lastMove, depth, list, window, parent)

  override def findBestMove(lastMove: M, depth: Int, list: MoveList[M],
                                       window: SearchWindow, parent: SearchTreeNode): M =
    var i = 0
    var bestInheritedValue = -SearchStrategy.INFINITY
    var selectedMove: TwoPlayerMove = null

    var bestMove = bestFirstFromPrepared(list)
    val entry = new Entry[M](bestMove, depth, window)

    while !list.isEmpty() do
      val theMove = getNextMove(list)
      if isPaused then
        return lastMove
      updatePercentDone(depth, list)

      searchable.makeInternalMove(theMove)
      try
        val child = addNodeToTree(parent, theMove, window)
        i += 1
        selectedMove = searchInternal(theMove, depth - 1, window.negateAndSwap(), child)
      finally
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
            return finalizeBestMove(bestMove, lastMove)

    storeBestMove(window.alpha, entry, bestMove.getInheritedValue)
    finalizeBestMove(bestMove, lastMove)

  /**
   * Store off the best move so we do not need to analyze it again.
   */
  private def storeBestMove(alpha: Int, entry: Entry[M], bestValue: Int): Unit =
    if bestValue <= alpha then
      entry.upperValue = bestValue
    else
      entry.lowerValue = bestValue
    lookupTable.put(searchable.getHashKey(), entry)
