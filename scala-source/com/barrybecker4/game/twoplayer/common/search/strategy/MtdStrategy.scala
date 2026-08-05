/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard
import com.barrybecker4.game.twoplayer.common.search.SearchWindow
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.search.tree.IGameTreeViewable
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode

/**
 * Memory enhanced Test Driver search.
 * This strategy class defines the MTD search algorithm.
 * See http://people.csail.mit.edu/plaat/mtdf.html
 *
 * TODO: add iterative deepening (https://chessprogramming.wikispaces.com/MTD(f))
 *
 * @author Barry Becker
 */
final class MtdStrategy[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](
    private val searchWithMemory: MemorySearchStrategy[M, B]
) extends SearchStrategy[M, B]:

  override def getOptions: SearchOptions[M, B] = searchWithMemory.getOptions

  override def search(lastMove: M, parent: SearchTreeNode): M =
    val selectedMove = searchInternal(lastMove, 0, parent)
    if selectedMove != null then selectedMove else lastMove

  /**
   * @param lastMove last move played on board.
   * @param f upper or lower bound
   * @param parent non-null if showing game tree.
   * @return best next move
   */
  private def searchInternal(lastMove: M, f: Int, parent: SearchTreeNode): M =
    var g = f
    var upperBound = SearchStrategy.INFINITY
    var lowerBound = -SearchStrategy.INFINITY

    var selectedMove: M = null.asInstanceOf[M]
    while lowerBound < upperBound do
      val beta = if g == lowerBound then g + 1 else g

      getOptions.getBruteSearchOptions.setInitialSearchWindow(SearchWindow(beta - 1, beta))
      selectedMove = searchWithMemory.search(lastMove, parent)
      g = -selectedMove.getInheritedValue

      if g < beta then upperBound = g
      else lowerBound = g

    selectedMove

  override final def getNumMovesConsidered: Long = searchWithMemory.getNumMovesConsidered

  override final def getPercentDone: Int = searchWithMemory.getPercentDone

  /**
   * Set an optional ui component that will update when the search tree is modified.
   * @param listener listener
   */
  override def setGameTreeEventListener(listener: IGameTreeViewable): Unit =
    searchWithMemory.setGameTreeEventListener(listener)

  override def getEvaluationPerspective: EvaluationPerspective = EvaluationPerspective.CURRENT_PLAYER

  override def pause(): Unit =
    searchWithMemory.pause()

  override final def isPaused: Boolean = searchWithMemory.isPaused

  override def continueProcessing(): Unit =
    searchWithMemory.continueProcessing()
