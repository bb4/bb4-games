/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.WinProbabilityCalculator
import com.barrybecker4.game.twoplayer.common.search.options.MaximizationStyle
import com.barrybecker4.game.twoplayer.common.search.tree.NodeAttributes

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*

/**
 * A node in the in memory UCT tree created during search.
 * derived from http://senseis.xmp.net/?UCT%2FDiscussion
 *
 * @author Barry Becker
 */
class UctNode[M <: TwoPlayerMove](val move: M):

  /** The number of times we have visited this node in the tree. */
  private var numVisits: Int = 0

  /** The number of times we have won a random game that starts from this node. */
  private var numWins: Float = 0.0f

  /** List of child nodes (moves)  */
  private var children: java.util.List[UctNode[M]] = null

  /**
   * not sure what this is for. See http://senseis.xmp.net/?UCT. Make a param.
   * Seems to make the exploreExploit constant balance at 1.
   */
  private val DENOM_CONST = 5.0

  /** Some big number. */
  private val BIG = 1000.0

  def getMove: M = move

  def getNumVisits: Int = numVisits

  /**
   * Increment our number of wins we won again at this node.
   * A tie counts as only half a win.
   * @param player1Score if 1 then p1 won; if 0, then p1 lost, else 0.5 - considered a tie (inconclusive)
   */
  def update(player1Score: Double): Unit =
    numVisits += 1
    numWins += (if move.isPlayer1 then player1Score else 1.0 - player1Score).toFloat

  /**
   * Note that the winRate returned is for the player who is about to move.
   *
   * @return ratio of wins to visits. Return a tie score if never visited.
   */
  def getWinRate: Float =
    if numVisits == 0 then
      WinProbabilityCalculator.getChanceOfPlayer1Winning(move.getValue)
    else
      numWins / numVisits.toFloat

  def getChildren: java.util.List[UctNode[M]] = children

  def hasChildren: Boolean = children != null

  /**
   * Add the children to the node.
   * @param moves child moves to add.
   * @return the number of children added
   */
  def addChildren(moves: MoveList[M]): Int =
    var numKids = 0
    children = new java.util.LinkedList[UctNode[M]]()
    val it = moves.iterator()
    while it.hasNext do
      val m = it.next()
      val newNode = UctNode[M](m)
      children.add(newNode)
      numKids += 1
    numKids

  /**
   * @return the bestNode to the child with the highest winrate (or maybe numvisits - make option?)
   */
  def findBestChildMove(style: MaximizationStyle): M =
    var bestNode: UctNode[M] = null
    if hasChildren then
      val it = children.iterator()
      while it.hasNext do
        val child = it.next()
        if style == MaximizationStyle.WIN_RATE then
          if bestNode == null || child.getWinRate > bestNode.getWinRate then
            bestNode = child
        else if style == MaximizationStyle.NUM_VISITS then
          if bestNode == null || child.getNumVisits > bestNode.getNumVisits then
            bestNode = child

    if bestNode == null then null.asInstanceOf[M] else bestNode.move

  /**
   * This is the secret sauce at the core of the UCT algorithm.
   * See http://www-958.ibm.com/software/data/cognos/manyeyes/visualizations/uct-search-parameters
   * For analysis of effect of parameters on UCT value returned.
   *
   * @param exploreExploitRatio bigger values mean more exploration as opposed to exploitation of known good moves.
   * @param parentVisits the number of times our parent node has been visited.
   * @return the uct value which is somewhat related to the winRate
   *    and a ratio of our visits and out next siblings visits.
   */
  def calculateUctValue(exploreExploitRatio: Double, parentVisits: Int): Double =
    if numVisits > 0 then
      val uct = exploreExploitRatio * Math.sqrt(Math.log(parentVisits) / (DENOM_CONST * numVisits))
      getWinRate + uct
    else
      // always play a random unexplored move first.
      // Something bigger than any win rate, yet higher if better move.
      BIG + getWinRate

  def getAttributes: NodeAttributes =
    val attributes = NodeAttributes()
    // Order matches historical Java HashMap toString expectations in UctNodeTest
    attributes.put("wins", numWins.toString)
    attributes.put("visits", numVisits.toString)
    attributes.put("winRate", FormatUtil.formatNumber(getWinRate))
    attributes

  override def toString: String =
    move.toString + " " + getAttributes.toString
