/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common

import java.util.ArrayList
import scala.jdk.CollectionConverters._

/**
 * A list of game moves.
 * What kind of performance difference is there if this is a LinkedList instead of ArrayList?
 *
 * @author Barry Becker
 */
class MoveList[M <: Move] extends ArrayList[M] {

  private val lock = new Object()

  def this(list: MoveList[M]) = {
    this()
    this.addAll(list)
  }

  def copy(): MoveList[M] = {
    val copiedList = new MoveList[M]()
    lock.synchronized {
      this.asScala.foreach(m => copiedList.add(m.copy().asInstanceOf[M]))
    }
    copiedList
  }

  override def add(m: M): Boolean = {
    lock.synchronized {
      super.add(m)
    }
  }

  override def add(index: Int, move: M): Unit = {
    lock.synchronized {
      super.add(index, move)
    }
  }

  def getFirstMove: M = get(0)

  def getLastMove: M = {
    if (isEmpty) {
      return null.asInstanceOf[M]
    }
    get(size() - 1)
  }

  override def removeLast(): M = {
    lock.synchronized {
      remove(this.size - 1)
    }
  }

  def getNumMoves: Int = size()

  override def subList(first: Int, last: Int): MoveList[M] = {
    val subList = new MoveList[M]()
    lock.synchronized {
      subList.addAll(super.subList(first, last))
    }
    subList
  }

  def getRandomMove: M = getRandomMove(size())

  def getRandomMove(ofFirstN: Int): M = {
    val r = GameContext.random().nextInt(Math.min(ofFirstN, size()))
    get(r)
  }

  def getRandomMoveForThresh(percentLessThanBestThresh: Int): M = {
    val thresh = getFirstMove.getValue * (1.0 - percentLessThanBestThresh.toFloat / 100.0)
    var ct = 1
    var currentMove = getFirstMove
    val numMoves = size()
    while (currentMove.getValue > thresh && ct < numMoves) {
      currentMove = get(ct)
      ct += 1
    }
    val r = GameContext.random().nextInt(ct)
    get(r)
  }

  override def toString: String = {
    val bldr = new StringBuilder()
    var ct = 1
    this.asScala.foreach { m =>
      bldr.append(ct).append(") ").append(m.toString).append("\n")
      ct += 1
    }
    bldr.toString()
  }
}
