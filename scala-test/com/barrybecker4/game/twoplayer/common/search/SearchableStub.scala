/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.twoplayer.common.{AbstractSearchable, TwoPlayerBoard}
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy
import com.barrybecker4.game.twoplayer.common.search.transposition.HashKey
import com.barrybecker4.optimization.parameter.ParameterArray

import scala.jdk.CollectionConverters.*

/**
 * Stub implementation of Searchable to help test the search strategy classes without needing
 * a specific game implementation.
 * @author Barry Becker
 */
class SearchableStub(private var options_ : SearchOptions[TwoPlayerMoveStub, TwoPlayerBoard[TwoPlayerMoveStub]])
    extends AbstractSearchable[TwoPlayerMoveStub, TwoPlayerBoard[TwoPlayerMoveStub]](new MoveList[TwoPlayerMoveStub]()):

  /** Copy constructor */
  def this(stub: SearchableStub) =
    this(stub.getSearchOptions)
    setMoveList(new MoveList[TwoPlayerMoveStub](stub.getMoveList))

  override def getSearchOptions: SearchOptions[TwoPlayerMoveStub, TwoPlayerBoard[TwoPlayerMoveStub]] =
    options_

  override def copy(): Searchable[TwoPlayerMoveStub, TwoPlayerBoard[TwoPlayerMoveStub]] =
    new SearchableStub(this)

  override def makeInternalMove(m: TwoPlayerMoveStub): Unit =
    getMoveList.add(m)

  override def undoInternalMove(m: TwoPlayerMoveStub): Unit =
    getMoveList.removeLast()

  override def done(m: TwoPlayerMoveStub, recordWin: Boolean): Boolean =
    m.getInheritedValue >= SearchStrategy.WINNING_VALUE

  override def worth(lastMove: TwoPlayerMoveStub, weights: ParameterArray): Int =
    lastMove.getValue

  override def getBoard: TwoPlayerBoard[TwoPlayerMoveStub] = null

  override def generateMoves(lastMove: TwoPlayerMoveStub, weights: ParameterArray): MoveList[TwoPlayerMoveStub] =
    new MoveList[TwoPlayerMoveStub](lastMove.getChildren)

  override def generateUrgentMoves(lastMove: TwoPlayerMoveStub, weights: ParameterArray): MoveList[TwoPlayerMoveStub] =
    val urgentMoves = new MoveList[TwoPlayerMoveStub]()
    for m <- lastMove.getChildren.asScala do
      val move = m.asInstanceOf[TwoPlayerMoveStub]
      if move.isUrgent then
        urgentMoves.add(move)
    urgentMoves

  override def inJeopardy(lastMove: TwoPlayerMoveStub, weights: ParameterArray): Boolean =
    lastMove.causedUrgency

  override def getHashKey(): HashKey =
    val key = new HashKey()
    for m <- getMoveList.asScala do
      key.applyMove(m.asInstanceOf[TwoPlayerMoveStub].getToLocation, m.hashCode)
    key
