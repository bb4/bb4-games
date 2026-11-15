/* Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.strategy

import com.barrybecker4.game.twoplayer.common.{TwoPlayerBoard, TwoPlayerController, TwoPlayerMove}
import com.barrybecker4.game.twoplayer.common.search.Progress
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode
import com.barrybecker4.game.twoplayer.tictactoe.TicTacToeHelper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Parity checks: memory strategies and MTD should agree with their non-memory bases
 * on shallow TicTacToe searches (move location and evaluation value).
 */
class MemoryMtdParityTest:

  @Test
  def testNegaMaxMemoryAgreesWithNegaMaxMidAndOpening(): Unit =
    assertStrategyParity(SearchStrategyType.NEGAMAX, SearchStrategyType.NEGAMAX_W_MEMORY)

  @Test
  def testNegaScoutMemoryAgreesWithNegaScoutMidAndOpening(): Unit =
    assertStrategyParity(SearchStrategyType.NEGASCOUT, SearchStrategyType.NEGASCOUT_W_MEMORY)

  @Test
  def testMtdNegaMaxReturnsLegalOpeningMove(): Unit =
    val helper = new TicTacToeHelper()
    val controller =
      helper.createController.asInstanceOf[TwoPlayerController[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]]
    val searchOptions =
      helper.createSearchOptions.asInstanceOf[SearchOptions[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]]
    configureShallow(searchOptions)

    controller.restoreFromStream(helper.getTestResource(Progress.BEGINNING, player1 = true))
    val mtd = searchWith(controller, searchOptions, SearchStrategyType.MTD_NEGAMAX)
    assertEquals(true, mtd != null, "MTD should return a move")
    assertEquals(true, mtd.getToLocation != null, "MTD opening move should have a board location")

  private def assertStrategyParity(plain: SearchStrategyType, withMemory: SearchStrategyType): Unit =
    val helper = new TicTacToeHelper()
    val controller =
      helper.createController.asInstanceOf[TwoPlayerController[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]]
    val searchOptions =
      helper.createSearchOptions.asInstanceOf[SearchOptions[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]]
    configureShallow(searchOptions)

    for progress <- Seq(Progress.MIDDLE, Progress.BEGINNING) do
      controller.restoreFromStream(helper.getTestResource(progress, player1 = true))
      val plainMove = searchWith(controller, searchOptions, plain)
      controller.restoreFromStream(helper.getTestResource(progress, player1 = true))
      val memoryMove = searchWith(controller, searchOptions, withMemory)
      assertEquals(
        plainMove.getValue,
        memoryMove.getValue,
        s"$progress: $plain vs $withMemory score"
      )
      if progress == Progress.BEGINNING then
        assertEquals(
          plainMove.getToLocation,
          memoryMove.getToLocation,
          s"Opening: $plain vs $withMemory location"
        )

  private def configureShallow(searchOptions: SearchOptions[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]): Unit =
    searchOptions.getBruteSearchOptions.setLookAhead(2)
    searchOptions.getBruteSearchOptions.setAlphaBeta(true)
    searchOptions.getBruteSearchOptions.setQuiescence(false)

  private def searchWith(
      controller: TwoPlayerController[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]],
      searchOptions: SearchOptions[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]],
      strategyType: SearchStrategyType
  ): TwoPlayerMove =
    searchOptions.setSearchStrategyMethod(strategyType)
    val strategy = searchOptions.getSearchStrategy(
      controller.getSearchable(),
      controller.getComputerWeights().getDefaultWeights
    )
    val lastMove = controller.getLastMove.get
    val root = new SearchTreeNode(lastMove)
    strategy.asInstanceOf[SearchStrategy[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]].search(lastMove, root)
