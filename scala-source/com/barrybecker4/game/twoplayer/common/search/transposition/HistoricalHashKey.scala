/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.transposition

import com.barrybecker4.common.geometry.Location
import scala.collection.mutable.ListBuffer

/**
 * A version of the HashKey that maintains its history.
 *
 * @author Barry Becker
 */
class HistoricalHashKey(key: Long = 0L) extends HashKey(key) {

  val moveHistory: ListBuffer[String] = ListBuffer.empty
  val keyHistory: ListBuffer[Long] = ListBuffer.empty

  def this(hashKey: HashKey) = {
    this(hashKey.getKey)
  }

  override def copy(): HashKey = {
    val keyCopy = new HistoricalHashKey(this)
    keyCopy.moveHistory.addAll(this.moveHistory)
    keyCopy.keyHistory.addAll(this.keyHistory)
    keyCopy
  }

  override def applyMove(move: Location, specialNumber: Long): Unit = {
    super.applyMove(move, specialNumber)

    val index = keyHistory.indexOf(specialNumber)
    if (index >= 0) {
      keyHistory.remove(index)
      moveHistory.remove(index)
    } else {
      keyHistory.addOne(specialNumber)
      val moveStr = if (move == null) "ko" else move.toString
      moveHistory.addOne(s"{$moveStr $specialNumber}")
    }
  }

  override def equals(o: Any): Boolean = o match {
    case hashKey: HistoricalHashKey => getKey == hashKey.getKey
    case _ => false
  }

  def undoKeyHistory(): Long = {
    var currentKey = getKey
    for (keyHist <- keyHistory) {
      currentKey ^= keyHist
    }
    currentKey
  }

  override def toString: String = {
    val bldr = new StringBuilder()
    for (hkey <- keyHistory) {
      bldr.append(java.lang.Long.toBinaryString(hkey)).append(", ")
    }
    s"key=${java.lang.Long.toBinaryString(getKey)}\nhistory=\n$moveHistory\nkeyHistory=\n${bldr.toString}"
  }
}
