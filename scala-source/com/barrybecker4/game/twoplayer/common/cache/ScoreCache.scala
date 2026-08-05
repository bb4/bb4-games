/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.cache

import com.barrybecker4.common.util.LRUCache
import com.barrybecker4.game.twoplayer.common.search.transposition.HashKey
import scala.Option

/**
 * A kind of LRU cache for game moves so that we do not need to
 * recompute scores. Since lookups can use more accurate scores, alpha-beta
 * pruning can be much more effective - thereby dramatically reducing the
 * search space.
 * This technique applies the memoization pattern to search.
 * See http://en.wikipedia.org/wiki/Transposition_table
 *     http://pages.cs.wisc.edu/~mjr/Pente/
 *     shttp://people.csail.mit.edu/plaat/mtdf.html
 *
 * @author Barry Becker
 */
class ScoreCache extends LRUCache[HashKey, ScoreEntry](ScoreCache.MAX_ENTRIES) {

  private var cacheHits = 0
  private var cacheMisses = 0

  /**
   * @return the number of times we were able to retrieve a stored move that was useful to us.
   */
  def getCacheHits: Int = cacheHits

  /**
   * @return the number of times we looked but failed to find a stored move..
   */
  def getCacheMisses: Int = cacheMisses

  override def get(key: HashKey): Option[ScoreEntry] = {
    val score = super.get(key)
    if (!score.isDefined)
      cacheMisses += 1
    else
      cacheHits += 1
    score
  }

  override def toString: String = {
    s"ScoreCache [\nnumEntries=$numEntries hits$getCacheHits misses=$getCacheMisses\n]"
  }
}

object ScoreCache {
  private val MAX_ENTRIES = 110000
}
