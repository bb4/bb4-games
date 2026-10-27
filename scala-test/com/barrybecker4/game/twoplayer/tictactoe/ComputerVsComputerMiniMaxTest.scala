/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.twoplayer.common.{TwoPlayerController, TwoPlayerMove, TwoPlayerPlayerOptions}
import com.barrybecker4.game.twoplayer.common.search.ISearchableHelper
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType
import org.junit.Assert.assertEquals
import org.junit.{Before, Test}

import scala.compiletime.uninitialized

/** @author Barry Becker */
class ComputerVsComputerMiniMaxTest:

  protected var controller: TwoPlayerController[?, ?] = uninitialized
  protected var searchOptions: SearchOptions[?, ?] = uninitialized
  protected var helper: ISearchableHelper = uninitialized

  @Before
  def setUp(): Unit =
    helper = new TicTacToeHelper()
    controller = helper.createController
    initializeSearchOptions()
    searchOptions.setSearchStrategyMethod(SearchStrategyType.MINIMAX)
    searchOptions.getBruteSearchOptions.setLookAhead(4)
    searchOptions.getBruteSearchOptions.setAlphaBeta(false)

  private def initializeSearchOptions(): Unit =
    searchOptions = helper.createSearchOptions
    val players: PlayerList = controller.getPlayers
    // Match Java's unchecked cast of SearchOptions onto player options.
    val p1 = players.getPlayer1.getOptions.asInstanceOf[TwoPlayerPlayerOptions[TwoPlayerMove, TicTacToeBoard]]
    val p2 = players.getPlayer2.getOptions.asInstanceOf[TwoPlayerPlayerOptions[TwoPlayerMove, TicTacToeBoard]]
    p1.setSearchOptions(searchOptions.asInstanceOf[SearchOptions[TwoPlayerMove, TicTacToeBoard]])
    p2.setSearchOptions(searchOptions.asInstanceOf[SearchOptions[TwoPlayerMove, TicTacToeBoard]])

  /** In a computer vs computer game, the second move must be in the corner, not on the edge */
  @Test
  def testSecondMoveInCorner(): Unit =
    controller.computerMovesFirst()
    var move = controller.getLastMove.asInstanceOf[TwoPlayerMove]
    assertEquals("Unexpected first move for computer.", new ByteLocation(2, 2), move.getToLocation)

    controller.requestComputerMove(false, true)
    move = controller.getLastMove.asInstanceOf[TwoPlayerMove]
    assertEquals("Unexpected second move for computer.", new ByteLocation(1, 1), move.getToLocation)
