/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.search.ISearchableHelper
import com.barrybecker4.game.twoplayer.common.search.TwoPlayerSearchableBaseTst
import com.barrybecker4.game.twoplayer.tictactoe.ExpectedSearchableResults._
import org.junit.Assert.{assertFalse, assertTrue}
import org.junit.Test

/**
 * Verify that all the methods in TicTacToe searchable work as expected
 * @author Barry Becker
 */
class TicTacToeSearchableTest extends TwoPlayerSearchableBaseTst:

  override protected def createSearchableHelper: ISearchableHelper =
    new TicTacToeHelper()

  override protected def createInitialMove: TwoPlayerMove =
    TwoPlayerMove.createMove(2, 2, 0, new GamePiece(true))

  override protected def getDebugLevel: Int = -1

  override protected def getExpectedNumGeneratedMovesBeforeFirstMove: Int = 9

  override def testNotDoneMidGame(): Unit =
    restore("midGameCenterO")
    assertFalse(
      "Did not expect done in the middle of the game. ",
      searchable.done(getController.getLastMove.asInstanceOf[TwoPlayerMove], false)
    )

  override def testDoneForMidGameWin(): Unit =
    restore("wonGameO")
    assertTrue(
      "Expected done state for this game. ",
      searchable.done(getController.getLastMove.asInstanceOf[TwoPlayerMove], false)
    )

  override def testDoneEndGame(): Unit =
    restore("endGameNoMoreMoves")
    assertTrue(
      "Expected done state for this game because there are no more moves. ",
      searchable.done(getController.getLastMove.asInstanceOf[TwoPlayerMove], false)
    )

  override def testGenerateAllP1MovesMidGame(): Unit =
    checkGeneratedMoves("midGameCenterX", EXPECTED_ALL_MIDDLE_GAME_MOVES_CENTER_P1)
    checkGeneratedMoves("midGameCornerX", EXPECTED_ALL_MIDDLE_GAME_MOVES_CORNER_P1)
    checkGeneratedMoves("midGameEdgeX", EXPECTED_ALL_MIDDLE_GAME_MOVES_EDGE_P1)

  override def testGenerateTopP1MovesMidGame(): Unit =
    getBestMovesOptions.setPercentageBestMoves(20)
    checkGeneratedMoves("midGameCenterX", EXPECTED_TOP_MIDDLE_GAME_MOVES_CENTER_P1)
    checkGeneratedMoves("midGameCornerX", EXPECTED_TOP_MIDDLE_GAME_MOVES_CORNER_P1)
    checkGeneratedMoves("midGameEdgeX", EXPECTED_TOP_MIDDLE_GAME_MOVES_EDGE_P1)

  override def testGenerateAllP1MovesEndGame(): Unit =
    checkGeneratedMoves("endGameX", EXPECTED_ALL_END_GAME_MOVES_P1)

  override def testGenerateTopP1MovesEndGame(): Unit =
    getBestMovesOptions.setPercentageBestMoves(20)
    checkGeneratedMoves("endGameX", EXPECTED_TOP_END_GAME_MOVES_P1)

  override def testGenerateAllP2MovesMidGame(): Unit =
    checkGeneratedMoves("midGameCenterO", EXPECTED_ALL_MIDDLE_GAME_MOVES_CENTER_P2)
    checkGeneratedMoves("midGameCornerO", EXPECTED_ALL_MIDDLE_GAME_MOVES_CORNER_P2)
    checkGeneratedMoves("midGameEdgeO", EXPECTED_ALL_MIDDLE_GAME_MOVES_EDGE_P2)

  override def testGenerateTopP2MovesMidGame(): Unit =
    getBestMovesOptions.setPercentageBestMoves(20)
    checkGeneratedMoves("midGameCenterO", EXPECTED_TOP_MIDDLE_GAME_MOVES_CENTER_P2)
    checkGeneratedMoves("midGameCornerO", EXPECTED_TOP_MIDDLE_GAME_MOVES_CORNER_P2)
    checkGeneratedMoves("midGameEdgeO", EXPECTED_TOP_MIDDLE_GAME_MOVES_EDGE_P2)

  override def testGenerateAllP2MovesEndGame(): Unit =
    checkGeneratedMoves("endGameO", EXPECTED_ALL_END_GAME_MOVES_P2)

  override def testGenerateTopP2MovesEndGame(): Unit =
    checkGeneratedMoves("endGameO", EXPECTED_TOP_END_GAME_MOVES_P2)

  override def testGenerateUrgentMoves(): Unit =
    restore("urgentMoves")
    val moves =
      searchable.generateUrgentMoves(getController.getLastMove.asInstanceOf[TwoPlayerMove], weights)
    checkMoveListAgainstExpected("urgentMoves", EXPECTED_URGENT_MOVES, moves.asInstanceOf[MoveList[TwoPlayerMove]])

  @Test
  def testGenerateUrgentMovesP2(): Unit =
    restore("urgentMoves")
    val moves =
      searchable.generateUrgentMoves(getController.getLastMove.asInstanceOf[TwoPlayerMove], weights)
    checkMoveListAgainstExpected("urgentMoves: " + moves, EXPECTED_URGENT_MOVES, moves.asInstanceOf[MoveList[TwoPlayerMove]])

  private def checkGeneratedMoves(fileName: String, expectedMoves: Array[TwoPlayerMove]): Unit =
    restore(fileName)
    val lastMove = getController.getLastMove.asInstanceOf[TwoPlayerMove]
    val moves =
      getController.getSearchable().generateMoves(lastMove, weights)
    checkMoveListAgainstExpected(fileName, expectedMoves, moves.asInstanceOf[MoveList[TwoPlayerMove]])
