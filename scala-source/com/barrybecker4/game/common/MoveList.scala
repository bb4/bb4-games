/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common

import java.util.ArrayList
import scala.jdk.CollectionConverters._

/**
 * A list of game moves.
 *
 * Not safe for concurrent mutation of the same instance; search / UI paths that
 * share move history across threads must use [[copy]].
 */
class MoveList[M <: Move] extends ArrayList[M]:

  def this(list: MoveList[M]) =
    this()
    this.addAll(list)

  def copy(): MoveList[M] =
    val copiedList = new MoveList[M]()
    this.asScala.foreach(m => copiedList.add(m.copy().asInstanceOf[M]))
    copiedList

  def getFirstMove: M = get(0)

  def getLastMove: Option[M] =
    if isEmpty then None
    else Some(get(size() - 1))

  override def removeLast(): M =
    remove(this.size - 1)

  def getNumMoves: Int = size()

  override def subList(first: Int, last: Int): MoveList[M] =
    val subList = new MoveList[M]()
    subList.addAll(super.subList(first, last))
    subList

  def getRandomMove: M = getRandomMove(size())

  def getRandomMove(ofFirstN: Int): M =
    val r = GameContext.random().nextInt(Math.min(ofFirstN, size()))
    get(r)

  /**
   * Pick uniformly among leading moves whose values are within
   * `percentLessThanBestThresh` percent of the best (first) move's value.
   *
   * '''Precondition:''' the list is non-empty and sorted by value descending
   * (best first), as produced by move-generation / BestMoveFinder for UCT.
   *
   * Walks from the front until a move's value is &lt;= the threshold
   * `best * (1 - percent/100)`, including that boundary move, then returns a
   * uniform random choice among the prefix.
   */
  def getRandomMoveForThresh(percentLessThanBestThresh: Int): M =
    require(!isEmpty, "getRandomMoveForThresh requires a non-empty move list")
    val thresh = getFirstMove.getValue * (1.0 - percentLessThanBestThresh.toFloat / 100.0)
    var ct = 1
    var currentMove = getFirstMove
    val numMoves = size()
    while currentMove.getValue > thresh && ct < numMoves do
      currentMove = get(ct)
      ct += 1
    val r = GameContext.random().nextInt(ct)
    get(r)

  override def toString: String =
    val bldr = new StringBuilder()
    var ct = 1
    this.asScala.foreach { m =>
      bldr.append(ct).append(") ").append(m.toString).append("\n")
      ct += 1
    }
    bldr.toString()
