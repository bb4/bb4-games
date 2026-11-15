/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.cache

import com.barrybecker4.common.util.LRUCache
import com.barrybecker4.game.twoplayer.common.search.transposition.HashKey

/**
 * LRU cache for static evaluation scores keyed by Zobrist [[HashKey]].
 * Separate from the search transposition table; used by games that memoize worth().
 * See http://en.wikipedia.org/wiki/Transposition_table
 */
class ScoreCache extends LRUCache[HashKey, ScoreEntry](ScoreCache.MAX_ENTRIES):

  private var cacheHits = 0
  private var cacheMisses = 0

  /** @return times a stored score was retrieved. */
  def getCacheHits: Int = cacheHits

  /** @return times a lookup found nothing. */
  def getCacheMisses: Int = cacheMisses

  override def get(key: HashKey): Option[ScoreEntry] =
    val score = super.get(key)
    if score.isEmpty then cacheMisses += 1
    else cacheHits += 1
    score

  override def toString: String =
    s"ScoreCache [\nnumEntries=$numEntries hits$getCacheHits misses=$getCacheMisses\n]"

object ScoreCache:
  private val MAX_ENTRIES = 110000
