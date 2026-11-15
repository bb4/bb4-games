/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.WinProbabilityCalculator
import com.barrybecker4.game.twoplayer.common.search.Searchable
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode
import com.barrybecker4.optimization.parameter.ParameterArray

/**
 *  Implementation of Upper Confidence Tree (UCT) search strategy.
 *  This method uses a monte carlo (stochastic) method and is fundamentally different than minimax and its derivatives.
 *  It's subclasses define the key search algorithms for 2 player zero sum games with perfect information.
 *
 *   - add option to use concurrency. Need lock on uctNodes
 *
 *  @author Barry Becker
 */
class UctStrategy[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]](
    searchable: Searchable[M, B],
    weights: ParameterArray
) extends AbstractSearchStrategy[M, B](searchable, weights):

  /** ratio of exploration to exploitation (of known good moves) while searching.  */
  private val exploreExploitRatio: Double = getOptions.getMonteCarloSearchOptions.getExploreExploitRatio

  /** Number of moves to play in a random game from the starting move state */
  private val numRandomLookAhead: Int = getOptions.getMonteCarloSearchOptions.getRandomLookAhead

  /** When selecting a random move for a random game, select from only this many of the top moves. */
  private val percentLessThanBestThresh: Int = getOptions.getBestMovesSearchOptions.getPercentLessThanBestThresh

  override def getOptions: SearchOptions[M, B] = searchable.getSearchOptions

  override def getEvaluationPerspective: EvaluationPerspective = EvaluationPerspective.ALWAYS_PLAYER1

  override def search(lastMove: M, parent: SearchTreeNode): M =
    var numSimulations = 0
    val maxSimulations = getOptions.getMonteCarloSearchOptions.getMaxSimulations
    val root = UctNode[M](lastMove)

    while numSimulations < maxSimulations do
      playSimulation(root, parent)
      numSimulations += 1
      percentDone = (100 * numSimulations) / maxSimulations

    root.findBestChildMove(getOptions.getMonteCarloSearchOptions.getMaxStyle)

  /**
   * This recursive method ultimately expands the in-memory game tree by one node and updates that node's parents.
   *
   * @return chance of player1 winning when running a simulation from this board position.
   *   This probability is a number between 0 and 1 inclusive.
   */
  def playSimulation(lastMoveNode: UctNode[M], parent: SearchTreeNode): Float =
    var player1Score: Float = 0.0f

    if lastMoveNode.getNumVisits == 0 then
      player1Score = playRandomGame(lastMoveNode.move)
      movesConsidered += 1
    else
      var nextNode: UctNode[M] = null

      if !searchable.done(lastMoveNode.move, false) then
        if !lastMoveNode.hasChildren then
          val added = lastMoveNode.addChildren(searchable.generateMoves(lastMoveNode.move, weights_))
          if added == 0 then
            GameContext.log(0, "No moves added for " + lastMoveNode)
          addNodesToTree(parent, lastMoveNode.getChildren)
        nextNode = uctSelect(lastMoveNode)

      // may be null if there are no move valid moves or lastMoveNode won the game.
      if nextNode != null then
        searchable.makeInternalMove(nextNode.move)
        val nextParent = if parent != null then parent.findChild(nextNode.move) else null
        player1Score = playSimulation(nextNode, nextParent)
        searchable.undoInternalMove(nextNode.move)
      else
        player1Score = WinProbabilityCalculator.getChanceOfPlayer1Winning(lastMoveNode.move.getValue)

    lastMoveNode.update(player1Score)
    if parent != null then
      parent.attributes = lastMoveNode.getAttributes
    player1Score

  /**
   * Selects the best child of parentNode.
   * @return the best child of parentNode. May be null if there are no next moves.
   */
  private def uctSelect(parentNode: UctNode[M]): UctNode[M] =
    var bestUct = -1.0
    var selected: UctNode[M] = null

    val it = parentNode.getChildren.iterator()
    while it.hasNext do
      val child = it.next()
      val uctValue = child.calculateUctValue(exploreExploitRatio, parentNode.getNumVisits)
      if uctValue > bestUct then
        bestUct = uctValue
        selected = child

    selected

  /**
   * Plays a semi-random game from the current node position on the live board,
   * then undoes all playout moves so the search position is restored.
   * @return a score (0 = p1 lost; 0.5 = tie; or 1= p1 won) indication p1 advantage.
   */
  private def playRandomGame(lastMove: M): Float =
    val startNumMoves = searchable.getNumMoves()
    try
      playRandomMove(lastMove, startNumMoves)
    finally
      while searchable.getNumMoves() > startNumMoves do
        val m = searchable.getMoveList.getLastMove.get.asInstanceOf[M]
        searchable.undoInternalMove(m)

  /**
   * Semi-random playout: prefer moves near the best static value.
   * Moves are made on [[searchable]]; [[playRandomGame]] undoes them.
   *
   * @param startNumMoves move count before this playout began.
   * @return p1 win chance in [0, 1].
   */
  private def playRandomMove(lastMove: M, startNumMoves: Int): Float =
    val numRandMoves = searchable.getNumMoves() - startNumMoves
    if numRandMoves >= numRandomLookAhead || searchable.done(lastMove, false) then
      val score = searchable.worth(lastMove, weights_)
      lastMove.setValue(score)
      return WinProbabilityCalculator.getChanceOfPlayer1Winning(score)

    val moves = searchable.generateMoves(lastMove, weights_)
    if moves.size == 0 then
      return WinProbabilityCalculator.getChanceOfPlayer1Winning(lastMove.getValue)

    val randomMove = moves.getRandomMoveForThresh(percentLessThanBestThresh)
    searchable.makeInternalMove(randomMove)
    playRandomMove(randomMove, startNumMoves)

  /**
   * add a move to the visual game tree (if parent not null).
   * If the new node is already in the tree, do not add it, but maybe update values.
   */
  protected def addNodesToTree(parent: SearchTreeNode, childUctNodes: java.util.List[UctNode[M]]): Unit =
    if parent == null then return

    val it = childUctNodes.iterator()
    while it.hasNext do
      val child = it.next()
      addNodeToTree(parent, child.move, child.getAttributes)
