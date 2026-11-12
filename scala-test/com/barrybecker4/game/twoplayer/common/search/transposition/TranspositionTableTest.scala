/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.transposition

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.twoplayer.common.{TwoPlayerBoard, TwoPlayerController, TwoPlayerMove}
import com.barrybecker4.game.twoplayer.common.search.{Progress, SearchWindow}
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerMoveStub
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.search.strategy.{SearchStrategy, SearchStrategyType}
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode
import com.barrybecker4.game.twoplayer.tictactoe.TicTacToeHelper
import org.junit.jupiter.api.Assertions.{assertEquals, assertFalse, assertTrue}
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Direct tests for [[TranspositionTable.entryExists]] and agreement between memory and plain NegaMax.
 */
class TranspositionTableTest:

  private var table: TranspositionTable[TwoPlayerMoveStub] = scala.compiletime.uninitialized

  @BeforeEach
  def setUp(): Unit =
    table = new TranspositionTable[TwoPlayerMoveStub]()

  @Test
  def testEntryExistsNoneIncrementsCacheMisses(): Unit =
    val lastMove = stubMove()
    val window = new SearchWindow(-50, 50)
    assertFalse(table.entryExists(None, lastMove, 2, window))
    assertEquals(1, table.getCacheMisses)
    assertEquals(0, table.getNearCacheHits)
    assertEquals(0, table.getCacheHits)

  @Test
  def testEntryExistsShallowDepthIncrementsNearCacheHits(): Unit =
    val lastMove = stubMove()
    val bestMove = stubMove(7, player1 = true)
    val entry = new Entry(bestMove, 10, 10, d = 1)
    val window = new SearchWindow(-50, 50)
    assertFalse(table.entryExists(Some(entry), lastMove, depth = 2, window))
    assertEquals(0, table.getCacheMisses)
    assertEquals(1, table.getNearCacheHits)
    assertEquals(0, table.getCacheHits)

  @Test
  def testEntryExistsExactValueAtSufficientDepth(): Unit =
    val score = 42
    val lastMove = stubMove()
    val bestMove = stubMove(9, player1 = false)
    val entry = new Entry(bestMove, score, score, d = 3)
    val window = new SearchWindow(-100, 100)
    assertTrue(table.entryExists(Some(entry), lastMove, depth = 2, window))
    assertEquals(1, table.getCacheHits)
    assertEquals(score, bestMove.getInheritedValue)
    assertEquals(-score, lastMove.getInheritedValue)

  @Test
  def testEntryExistsUpperBoundAtOrBelowAlpha(): Unit =
    val lastMove = stubMove()
    val bestMove = stubMove(3, player1 = true)
    val entry = new Entry(bestMove, upper = 5, lower = 15, d = 2)
    val window = new SearchWindow(alpha = 10, beta = 100)
    assertTrue(table.entryExists(Some(entry), lastMove, depth = 2, window))
    assertEquals(5, bestMove.getInheritedValue)
    assertEquals(-5, lastMove.getInheritedValue)

  @Test
  def testEntryExistsLowerBoundAtOrAboveBeta(): Unit =
    val lastMove = stubMove()
    val bestMove = stubMove(3, player1 = false)
    val entry = new Entry(bestMove, upper = 20, lower = 50, d = 2)
    val window = new SearchWindow(alpha = -100, beta = 40)
    assertTrue(table.entryExists(Some(entry), lastMove, depth = 2, window))
    assertEquals(50, bestMove.getInheritedValue)
    assertEquals(-50, lastMove.getInheritedValue)

  @Test
  def testEntryExistsBoundInsideWindowCountsHitButReturnsFalse(): Unit =
    val lastMove = stubMove()
    lastMove.setInheritedValue(999)
    val bestMove = stubMove(1, player1 = true)
    val entry = new Entry(bestMove, upper = 20, lower = 30, d = 2)
    val window = new SearchWindow(alpha = 0, beta = 100)
    assertFalse(table.entryExists(Some(entry), lastMove, depth = 2, window))
    assertEquals(1, table.getCacheHits)
    assertEquals(0, table.getNearCacheHits)
    assertEquals(0, table.getCacheMisses)
    assertEquals(999, lastMove.getInheritedValue)

  @Test
  def testNegaMaxMemoryAgreesWithNegaMaxOnTicTacToe(): Unit =
    val helper = new TicTacToeHelper()
    val controller =
      helper.createController.asInstanceOf[TwoPlayerController[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]]
    val searchOptions = helper.createSearchOptions.asInstanceOf[SearchOptions[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]]
    searchOptions.getBruteSearchOptions.setLookAhead(2)
    searchOptions.getBruteSearchOptions.setAlphaBeta(true)
    searchOptions.getBruteSearchOptions.setQuiescence(false)

    controller.restoreFromStream(helper.getTestResource(Progress.MIDDLE, player1 = true))
    val plainMid = searchWithStrategy(controller, searchOptions, SearchStrategyType.NEGAMAX)
    controller.restoreFromStream(helper.getTestResource(Progress.MIDDLE, player1 = true))
    val memoryMid = searchWithStrategy(controller, searchOptions, SearchStrategyType.NEGAMAX_W_MEMORY)
    assertEquals(plainMid.getValue, memoryMid.getValue, "Mid-game scores should match")

    controller.restoreFromStream(helper.getTestResource(Progress.BEGINNING, player1 = true))
    val plainStart = searchWithStrategy(controller, searchOptions, SearchStrategyType.NEGAMAX)
    controller.restoreFromStream(helper.getTestResource(Progress.BEGINNING, player1 = true))
    val memoryStart = searchWithStrategy(controller, searchOptions, SearchStrategyType.NEGAMAX_W_MEMORY)
    assertEquals(plainStart.getValue, memoryStart.getValue, "Opening scores should match")
    assertEquals(plainStart.getToLocation, memoryStart.getToLocation, "Unique best opening move location")

  private def searchWithStrategy(
      controller: TwoPlayerController[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]],
      searchOptions: SearchOptions[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]],
      strategyType: SearchStrategyType
  ): TwoPlayerMove =
    searchOptions.setSearchStrategyMethod(strategyType)
    val strategy = searchOptions.getSearchStrategy(
      controller.getSearchable(),
      controller.getComputerWeights().getDefaultWeights
    )
    val lastMove = controller.getLastMove
    val root = new SearchTreeNode(lastMove)
    strategy.asInstanceOf[SearchStrategy[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]].search(lastMove, root)

  private def stubMove(value: Int = 0, player1: Boolean = true): TwoPlayerMoveStub =
    new TwoPlayerMoveStub(value, player1, new ByteLocation(1, 1), null)
