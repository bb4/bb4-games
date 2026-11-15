package com.barrybecker4.game.twoplayer.go

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone
import com.barrybecker4.game.twoplayer.go.board.move.GoMove
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/** Reproduce assert from play: B(3,3) W(3,4) B(4,3) then computer NegaScout search. */
class TestSearchUndoAfterB43 extends GoTestCase:

  override protected def getBoardSize: Int = 9

  /** Match default Go player options used in the UI. */
  override protected def setOptionOverrides(sOptions: SearchOptions[?, ?]): Unit =
    sOptions.setSearchStrategyMethod(SearchStrategyType.NEGASCOUT)
    sOptions.getBruteSearchOptions.setLookAhead(4)
    sOptions.getBruteSearchOptions.setAlphaBeta(true)
    sOptions.getBruteSearchOptions.setQuiescence(true)
    sOptions.getBruteSearchOptions.setMaxQuiescentDepth(4)
    sOptions.getBestMovesSearchOptions.setPercentageBestMoves(60)
    sOptions.getBestMovesSearchOptions.setPercentLessThanBestThresh(0)
    sOptions.getBestMovesSearchOptions.setMinBestMoves(6)

  @Test
  def computerSearchAfterSecondBlackMove(): Unit =
    controller.makeMove(new GoMove(new ByteLocation(3, 3), 0, new GoStone(true)))
    controller.makeMove(new GoMove(new ByteLocation(3, 4), 0, new GoStone(false)))
    controller.makeMove(new GoMove(new ByteLocation(4, 3), 0, new GoStone(true)))
    assertEquals(3, controller.getNumMoves)

    // White to move — same path as clicking the second black stone in the UI
    controller.requestComputerMove(false, true)

    assertEquals(4, controller.getNumMoves, "search must leave exactly one new game move")
