/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku

import com.barrybecker4.game.twoplayer.common.{TwoPlayerBoard, TwoPlayerController, TwoPlayerMove}
import com.barrybecker4.game.twoplayer.common.search.SearchableHelper

/** @author Barry Becker */
class GoMokuHelper extends SearchableHelper:

  override def createController: TwoPlayerController[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]] =
    new GoMokuController(10, 10).asInstanceOf[TwoPlayerController[TwoPlayerMove, TwoPlayerBoard[TwoPlayerMove]]]

  override def getTestCaseDir: String =
    SearchableHelper.EXTERNAL_TEST_CASE_DIR + "gomoku/cases/searchable/"

  override protected def getStartGameMoveFileName(player1: Boolean): String =
    if player1 then "x" else "y"

  override protected def getMiddleGameMoveFileName(player1: Boolean): String =
    if player1 then "x" else "y"

  override protected def getEndGameMoveFileName(player1: Boolean): String =
    if player1 then "x" else "y"
