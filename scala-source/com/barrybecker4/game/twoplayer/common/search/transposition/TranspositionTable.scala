/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.transposition

import com.barrybecker4.common.util.LRUCache
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.search.SearchWindow
import scala.Option

/**
 * A kind of LRU cache for game moves so that we do not need to
 * recompute scores. Since lookups can use more accurate scores, alpha-beta
 * pruning can be much more effective - thereby dramatically reducing the
 * search space.
 * This technique applies the memoization pattern to search.
 * See http://en.wikipedia.org/wiki/Transposition_table
 * http://pages.cs.wisc.edu/~mjr/Pente/
 * http://people.csail.mit.edu/plaat/mtdf.html
 *
 * @author Barry Becker
 */
class TranspositionTable[M <: TwoPlayerMove] extends LRUCache[HashKey, Entry[M]](TranspositionTable.MAX_ENTRIES) {

  private var cacheHits = 0
  private var cacheNearHits = 0
  private var cacheMisses = 0

  /**
   * if we can just look up the best move in the transposition table, then just do that.
   * Has side effect of updating the lastMove with the correct boundary if cache hit.
   * @return saved best move in entry
   */
  def entryExists(entry: Option[Entry[M]], lastMove: M, depth: Int, window: SearchWindow): Boolean = {
    if (entry.isDefined && entry.get.depth >= depth) {
      cacheHits += 1

      val e = entry.get
      if (e.upperValue <= window.alpha || e.upperValue == e.lowerValue) {
        e.bestMove.setInheritedValue(e.upperValue)
        lastMove.setInheritedValue(-e.upperValue)
        return true
      }
      if (e.lowerValue >= window.beta) {
        e.bestMove.setInheritedValue(e.lowerValue)
        lastMove.setInheritedValue(-e.lowerValue)
        return true
      }
    } else {
      if (entry.isDefined) cacheNearHits += 1
      else cacheMisses += 1
    }
    false
  }

  /**
   * @return the number of times we were able to retrieve a stored move that was useful to us.
   */
  def getCacheHits: Int = cacheHits

  /**
   * @return the number of times we were able to retrieve a stored move, but it was not useful to us.
   */
  def getNearCacheHits: Int = cacheNearHits

  /**
   * @return the number of times we looked but failed to find a stored move..
   */
  def getCacheMisses: Int = cacheMisses

  override def toString: String = {
    s"TranspositionTable [\nnumEntries=$numEntries hits$getCacheHits nearHits=$getNearCacheHits misses=$getCacheMisses\n]"
  }
}

object TranspositionTable {
  private val MAX_ENTRIES = 100000
}
