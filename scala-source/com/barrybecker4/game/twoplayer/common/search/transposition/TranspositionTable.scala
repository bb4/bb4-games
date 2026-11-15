/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.transposition

import com.barrybecker4.common.util.LRUCache
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.search.SearchWindow

/**
 * LRU cache for searched positions so scores need not be recomputed.
 * Memoization for alpha-beta search; see http://en.wikipedia.org/wiki/Transposition_table
 */
class TranspositionTable[M <: TwoPlayerMove]
    extends LRUCache[HashKey, Entry[M]](TranspositionTable.MAX_ENTRIES):

  private var cacheHits = 0
  private var cacheNearHits = 0
  private var cacheMisses = 0

  /**
   * if we can just look up the best move in the transposition table, then just do that.
   * Has side effect of updating the lastMove with the correct boundary if cache hit.
   * @return true if a useful TT entry exists for this window/depth
   */
  def entryExists(entry: Option[Entry[M]], lastMove: M, depth: Int, window: SearchWindow): Boolean =
    entry match
      case Some(e) if e.depth >= depth =>
        cacheHits += 1
        if e.upperValue <= window.alpha || e.upperValue == e.lowerValue then
          e.bestMove.setInheritedValue(e.upperValue)
          lastMove.setInheritedValue(-e.upperValue)
          true
        else if e.lowerValue >= window.beta then
          e.bestMove.setInheritedValue(e.lowerValue)
          lastMove.setInheritedValue(-e.lowerValue)
          true
        else false
      case Some(_) =>
        cacheNearHits += 1
        false
      case None =>
        cacheMisses += 1
        false

  def getCacheHits: Int = cacheHits

  def getNearCacheHits: Int = cacheNearHits

  def getCacheMisses: Int = cacheMisses

  override def toString: String =
    s"TranspositionTable [\nnumEntries=$numEntries hits$getCacheHits nearHits=$getNearCacheHits misses=$getCacheMisses\n]"

object TranspositionTable:
  private val MAX_ENTRIES = 100000
