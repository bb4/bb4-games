/* Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.game.common.{GameContext, MoveList}
import com.barrybecker4.game.twoplayer.common.{TwoPlayerMove, TwoPlayerBoard}
import com.barrybecker4.game.twoplayer.common.search.Searchable
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.search.tree.{IGameTreeViewable, NodeAttributes, SearchTreeNode}
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 *  This is an abstract base class for a search strategy.
 *  It's subclasses define the key search algorithms for 2 player zero sum games with perfect information.
 *
 *  @author Barry Becker
 */
abstract class AbstractSearchStrategy[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](
  /** the interface implemented by the generic game controller that provides standard methods. */
  protected val searchable: Searchable[M, B],
  /** weights coefficients for the evaluation polynomial that indirectly determines the best move.   */
  protected val weights_ : ParameterArray
) extends SearchStrategy[M, B]:

  /** keep track of the number of moves searched so far. Long because there could be quite a few. */
  protected var movesConsidered: Long = 0

  /** approximate percent of search that is complete at given moment. */
  protected var percentDone: Int = 0

  /** True when search is paused. */
  @volatile private var paused_ : Boolean = false

  /** The optional ui component that will be updated to reflect the current search tree.  */
  private var gameTree_ : IGameTreeViewable = _

  override def getOptions: SearchOptions[M, B] =
    searchable.getSearchOptions

  /**
   * Show the node in the game tree (if one is used. It is used if parent not null).
   *
   * @param list of pruned nodes
   * @param parent the tree node entry above the current position.
   * @param i th child.
   * @param attributes name value pairs
   *   type either PRUNE_ALPHA or PRUNE_BETA - pruned by comparison with Alpha or Beta.
   */
  protected def addPrunedNodesInTree(list: MoveList[M], parent: SearchTreeNode,
                                      i: Int, attributes: NodeAttributes): Unit =
    if gameTree_ != null then
      gameTree_.addPrunedNodes(list, parent, i, attributes)

  /**
   * add a move to the visual game tree (if parent not null).
   * @param parent of the node we are adding to the gameTree
   * @param theMove current move being added.
   * @param attributes arbitrary name value pairs to display for the new node in the tree.
   * @return the node added to the tree.
   */
  protected def addNodeToTree(parent: SearchTreeNode, theMove: M,
                               attributes: NodeAttributes): SearchTreeNode =
    var child: SearchTreeNode = null
    if gameTree_ != null then
      child = new SearchTreeNode(theMove, attributes)
      gameTree_.addNode(parent, child)
    child

  /**
   * @return true if the move list is empty.
   */
  protected def emptyMoveList(list: MoveList[M], lastMove: M): Boolean =
    if !list.isEmpty() then return false

    // If there are no next moves, the inherited value is the value
    lastMove.setInheritedValue(lastMove.getValue)
    true

  override final def getNumMovesConsidered: Long = movesConsidered

  override final def getPercentDone: Int = percentDone

  /**
   * Set an optional ui component that will update when the search tree is modified.
   * @param listener game tree listener
   */
  override def setGameTreeEventListener(listener: IGameTreeViewable): Unit =
    gameTree_ = listener

  /**
   * For minimax this is always true, but it depends on the player for the nega type searches.
   * @return true if we should evaluate the board from the point of view of player one.
   */
  protected def fromPlayer1sPerspective(lastMove: M): Boolean = true

  protected def hasGameTree(): Boolean = gameTree_ != null

  // these methods give an external thread debugging controls over the search

  override def pause(): Unit =
    paused_ = true

  override final def isPaused: Boolean = paused_

  override def continueProcessing(): Unit =
    paused_ = false
