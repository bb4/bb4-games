/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.game.twoplayer.common.{TwoPlayerBoard, TwoPlayerMove}
import com.barrybecker4.game.twoplayer.common.search.{SearchWindow, Searchable}
import com.barrybecker4.game.twoplayer.common.search.transposition.{Entry, HashKey, TranspositionTable}

/**
 * Shared transposition-table probe and leaf-store helpers for memory brute-force strategies.
 */
trait TranspositionProbe[M <: TwoPlayerMove, B <: TwoPlayerBoard[M]]:

  protected def searchable: Searchable[M, B]
  protected def transpositionTable: TranspositionTable[M]

  /**
   * @return best move from the TT when [[TranspositionTable.entryExists]] reports a useful hit.
   */
  protected def probeTranspositionHit(lastMove: M, depth: Int, window: SearchWindow): Option[M] =
    val entry = transpositionTable.get(searchable.getHashKey())
    if transpositionTable.entryExists(entry, lastMove, depth, window) then
      entry.map(_.bestMove)
    else None

  /** Store an exact leaf evaluation in the TT. */
  protected def storeExactLeaf(key: HashKey, move: M, value: Int): Unit =
    val entry = new Entry[M](move, value)
    entry.lowerValue = value
    entry.upperValue = value
    transpositionTable.put(key, entry)

  /** Store a quiescence result in the TT. */
  protected def storeQuiescentLeaf(key: HashKey, move: M): Unit =
    transpositionTable.put(key, new Entry[M](move, move.getInheritedValue))
