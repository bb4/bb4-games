/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe

import com.barrybecker4.game.twoplayer.common.{TwoPlayerBoard, TwoPlayerController, TwoPlayerMove}
import com.barrybecker4.game.twoplayer.common.search.SearchableHelper
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions

/** @author Barry Becker */
class TicTacToeHelper extends SearchableHelper:

  override def createSearchOptions: SearchOptions[?, ?] =
    val options = new SearchOptions()
    options.getBruteSearchOptions.setLookAhead(2)
    options.getBruteSearchOptions.setAlphaBeta(true)
    options.getBestMovesSearchOptions.setPercentLessThanBestThresh(0)
    options.getBestMovesSearchOptions.setPercentageBestMoves(100)
    options.getBruteSearchOptions.setQuiescence(false)
    options

  override def createController: TwoPlayerController[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]] =
    new TicTacToeController().asInstanceOf[TwoPlayerController[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]]

  override def getTestCaseDir: String =
    SearchableHelper.EXTERNAL_TEST_CASE_DIR + "tictactoe/cases/searchable/"

  override protected def getStartGameMoveFileName(player1: Boolean): String =
    if player1 then "midGameCenterX" else "midGameCenterO"

  override protected def getMiddleGameMoveFileName(player1: Boolean): String =
    if player1 then "lateMidGameX" else "lateMidGameO"

  override protected def getEndGameMoveFileName(player1: Boolean): String =
    if player1 then "endGameX" else "endGameO"
