/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard
import com.barrybecker4.game.twoplayer.common.search.{SearchWindow, SearchableStub, TwoPlayerMoveStub}
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Verifies [[AbstractBruteSearchStrategy.getNextMove]] consumes moves in best-first
 * (original front-of-list) order.
 */
class GetNextMoveOrderTest:

  @Test
  def testGetNextMovePreservesBestFirstOrder(): Unit =
    val strategy = new ProbeBruteStrategy()
    val list = new MoveList[TwoPlayerMoveStub]()
    list.add(stub(10))
    list.add(stub(20))
    list.add(stub(30))
    strategy.prepareMoveListForSearch(list)
    assertEquals(10, strategy.exposeBestFirst(list).getValue)

    val first = strategy.exposeGetNextMove(list)
    val second = strategy.exposeGetNextMove(list)
    val third = strategy.exposeGetNextMove(list)

    assertEquals(10, first.getValue)
    assertEquals(20, second.getValue)
    assertEquals(30, third.getValue)
    assertEquals(0, list.size(), "list should be fully consumed")
    assertEquals(3, strategy.getNumMovesConsidered)

  private def stub(value: Int): TwoPlayerMoveStub =
    new TwoPlayerMoveStub(value, true, new ByteLocation(1, 1), null)

  /** Minimal brute strategy exposing getNextMove for ordering checks. */
  private class ProbeBruteStrategy
      extends AbstractBruteSearchStrategy[TwoPlayerMoveStub, TwoPlayerBoard[TwoPlayerMoveStub]](
        new SearchableStub(new SearchOptions[TwoPlayerMoveStub, TwoPlayerBoard[TwoPlayerMoveStub]]()),
        null
      ):

    def exposeGetNextMove(list: MoveList[TwoPlayerMoveStub]): TwoPlayerMoveStub =
      getNextMove(list)

    def prepareMoveListForSearch(list: MoveList[TwoPlayerMoveStub]): Unit =
      prepareMovesForSearch(list)

    def exposeBestFirst(list: MoveList[TwoPlayerMoveStub]): TwoPlayerMoveStub =
      bestFirstFromPrepared(list)

    override protected def findBestMove(
        lastMove: TwoPlayerMoveStub,
        depth: Int,
        list: MoveList[TwoPlayerMoveStub],
        window: SearchWindow,
        parent: SearchTreeNode
    ): TwoPlayerMoveStub = lastMove

    override def getEvaluationPerspective: EvaluationPerspective =
      EvaluationPerspective.CURRENT_PLAYER
