/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.game.common.{GameContext, MoveList}
import com.barrybecker4.game.twoplayer.common.{TwoPlayerMove, TwoPlayerBoard}
import com.barrybecker4.game.twoplayer.common.search.{SearchWindow, Searchable}
import com.barrybecker4.game.twoplayer.common.search.options.{BruteSearchOptions, SearchOptions}
import com.barrybecker4.game.twoplayer.common.search.tree.{NodeAttributes, SearchTreeNode}
import com.barrybecker4.optimization.parameter.ParameterArray
import java.util.List

/**
 *  This is an abstract base class for all minimax based brute force search strategy.
 *  It's subclasses define the key search algorithms for 2 player zero sum games with perfect information.
 *
 *  @author Barry Becker
 */
abstract class AbstractBruteSearchStrategy[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](
  searchable: Searchable[M, B],
  weights: ParameterArray
) extends AbstractSearchStrategy[M, B](searchable, weights):

  /** if true, then use alpha-beta pruning. */
  final val alphaBeta_ : Boolean = {
    val opts = getOptions
    val bruteOpts = opts.getBruteSearchOptions
    bruteOpts.getAlphaBeta
  }

  /** If true, then use additional quiescent search to extent the search tree for urgent moves. */
  private final val quiescence_ : Boolean = {
    val opts = getOptions
    val bruteOpts = opts.getBruteSearchOptions
    bruteOpts.getQuiescence
  }

  /** the number of plys to look ahead when searching. */
  final val lookAhead_ : Int = {
    val opts = getOptions
    val bruteOpts = opts.getBruteSearchOptions
    bruteOpts.getLookAhead
  }

  /** don't search more levels ahead than this during quiescent search. */
  private var maxQuiescentDepth_ : Int = {
    val opts = getOptions
    val bruteOpts = opts.getBruteSearchOptions
    bruteOpts.getMaxQuiescentDepth
  }

  /**
   * Number of moves to consider at the top ply.
   * we use this number to determine how far into the search that we are.
   */
  var numTopLevelMoves_ : Int = 0

  GameContext.log(2, s"alpha beta=$alphaBeta_ quiescence=$quiescence_ lookAhead = $lookAhead_")

  override def getOptions: SearchOptions[M, B] =
    searchable.getSearchOptions

  override def search(lastMove: M, parent: SearchTreeNode): M =
    val window = getOptions.getBruteSearchOptions.getInitialSearchWindow
    searchInternal(lastMove, lookAhead_, window, parent)

  def searchInternal(lastMove: M, depth: Int, window: SearchWindow, parent: SearchTreeNode): M =
    val done = searchable.done(lastMove, false)
    if depth <= 0 || done then
      if doQuiescentSearch(depth, done, lastMove) then
        return quiescentSearch(lastMove, depth, window, parent)
      else
        val sign = if fromPlayer1sPerspective(lastMove) then 1 else -1
        lastMove.setInheritedValue(sign * lastMove.getValue)
        return lastMove

    // generate a list of all (or bestPercent) candidate next moves, and pick the best one
    val list = searchable.generateMoves(lastMove, weights_)

    if depth == lookAhead_ then
      numTopLevelMoves_ = list.size

    if emptyMoveList(list, lastMove) then
      updatePercentDone(depth, list)
      // if there are no possible next moves, return the lastMove (we hit the end of the game).
      return lastMove

    findBestMove(lastMove, depth, list, window, parent)

  /**
   * Get the next move and increment the number of moves considered.
   * @return next move in sorted generated next move list.
   */
  protected def getNextMove(list: MoveList[M]): M =
    movesConsidered += 1
    list.remove(0)

  /**
   * Search more if quiescense is on, depth is negative, but not yet at -maxQuiescentDepth
   * and the last moved played created an urgent situation.
   * @return true of we should continue searching a bit to find a stable/quiescent move.
   */
  protected def doQuiescentSearch(depth: Int, done: Boolean, lastMove: M): Boolean =
    if !quiescence_ then return false
    val inJeopardy = searchable.inJeopardy(lastMove, weights_)
    depth > -maxQuiescentDepth_ && !done && inJeopardy

  /**
   * This continues the search in situations where the board position is not stable.
   * For example, perhaps we are in the middle of a piece exchange (chess), or a large group is in atari (go).
   * @return best quiescent move
   */
  def quiescentSearch(lastMove: M, depth: Int, window: SearchWindow, parent: SearchTreeNode): M =
    val urgentMoves = searchable.generateUrgentMoves(lastMove, weights_)
    if emptyMoveList(urgentMoves, lastMove) then return null.asInstanceOf[M]

    findBestMove(lastMove, depth, urgentMoves, window, parent)

  /**
   * This is the part of the search algorithm that varies most among the search strategies.
   * That is why I break it out into a separate overridable method.
   *
   * @param lastMove the most recent move made by one of the players.
   * @param depth how deep in this local game tree that we are to search.
   *   When depth becomes 0 we are at a leaf and should terminate (unless its an urgent move and quiescence is on).
   * @param list generated list of next moves to search.
   * @param window search window - alpha and beta
   * @param parent for constructing a ui tree. If null no game tree is constructed.
   * @return the chosen move (ie the best move) (may be null if no next move).
   */
  protected def findBestMove(lastMove: M, depth: Int, list: MoveList[M],
                              window: SearchWindow, parent: SearchTreeNode): M

  /**
   * Show the node in the game tree (if one is used. It is used if parent not null).
   *
   * @param list of pruned nodes
   * @param parent the tree node entry above the current position.
   * @param i th child.
   */
  protected def showPrunedNodesInTree(list: MoveList[M], parent: SearchTreeNode,
                                       i: Int, selectedValue: Int, window: SearchWindow): Unit =
    if hasGameTree() then
      super.addPrunedNodesInTree(list, parent, i, NodeAttributes.createPrunedNode(selectedValue, window))

  /**
   * add a move to the visual game tree (if parent not null).
   * @return the node added to the tree.
   */
  protected def addNodeToTree(parent: SearchTreeNode, theMove: M, window: SearchWindow): SearchTreeNode =
    var attributes: NodeAttributes = null
    if hasGameTree() then
      attributes = NodeAttributes.createInnerNode(theMove, window)
    addNodeToTree(parent, theMove, attributes)

  /**
   * Update the percentage done searching variable for the progress bar
   * if we are at the top level (otherwise this is a no-op).
   */
  protected def updatePercentDone(depth: Int, remainingNextMoves: List[?]): Unit =
    if depth == lookAhead_ then
      percentDone = if numTopLevelMoves_ == 0 then 100
                    else 100 * (numTopLevelMoves_ - remainingNextMoves.size) / numTopLevelMoves_
