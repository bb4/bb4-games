/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common

import com.barrybecker4.game.common.{AbstractGameProfiler, GameProfiler, MoveList}
import com.barrybecker4.game.twoplayer.common.search.Searchable
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy
import com.barrybecker4.game.twoplayer.common.search.tree.{IGameTreeViewable, SearchTreeNode}
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 * Base class for all classes that can search two player games for the next best move.
 *
 * @author Barry Becker
 */
abstract class AbstractSearchable[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](
  protected var moveList: MoveList[M]
) extends Searchable[M, B] {

  protected var strategy_ : SearchStrategy[M, B] = _

  def searchForNextMove(weights: ParameterArray, lastMove: M,
                        treeViewer: IGameTreeViewable): M = {
    getProfiler.startProfiling()

    strategy_ = getSearchOptions.getSearchStrategy(this, weights)

    var root: SearchTreeNode = null
    if (treeViewer != null) {
      strategy_.setGameTreeEventListener(treeViewer)
      root = treeViewer.getRootNode
    }

    val nextMove = strategy_.search(lastMove, root)
    getProfiler.stopProfiling(strategy_.getNumMovesConsidered)
    nextMove
  }

  def getSearchStrategy: SearchStrategy[M, B] = strategy_

  def getNumMoves(): Int = moveList.getNumMoves

  def getMoveList: MoveList[M] = moveList

  protected def setMoveList(ml: MoveList[M]): Unit = moveList = ml

  /** @return the search options to use */
  def getSearchOptions: SearchOptions[M, B]

  protected def getProfiler: AbstractGameProfiler = GameProfiler.getInstance
}
