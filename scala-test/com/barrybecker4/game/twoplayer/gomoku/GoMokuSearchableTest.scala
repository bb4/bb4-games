/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku

import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.search.{ISearchableHelper, TwoPlayerSearchableBaseTst}
import com.barrybecker4.game.twoplayer.gomoku.ExpectedSearchableResults.*
import org.junit.jupiter.api.Assertions.{assertFalse, assertTrue}
import org.junit.jupiter.api.Test

/** Verify that all the methods in GoMokuSearchable work as expected @author Barry Becker */
class GoMokuSearchableTest extends TwoPlayerSearchableBaseTst:

  override protected def createSearchableHelper: ISearchableHelper =
    new GoMokuHelper()

  override protected def createInitialMove: TwoPlayerMove =
    TwoPlayerMove.createMove(5, 5, 0, new GamePiece(true))

  override protected def getExpectedNumGeneratedMovesBeforeFirstMove: Int = 1

  override def testNotDoneMidGame(): Unit =
    restore("midGameP1ToPlay")
    assertFalse(searchable.done(getController.getLastMove.asInstanceOf[TwoPlayerMove], false), "Did not expect done in the middle of the game. ")

  @Test
  def testAlmostWonP1ToPlay(): Unit =
    restore("almostWonP1ToPlay")
    assertFalse(searchable.done(getController.getLastMove.asInstanceOf[TwoPlayerMove], false), "Did not expect done even though very close. ")

  @Test
  def testAlmostWonP2ToPlay(): Unit =
    restore("almostWonP2ToPlay")
    assertFalse(searchable.done(getController.getLastMove.asInstanceOf[TwoPlayerMove], false), "Did not expect done even though very close. ")

  override def testDoneForMidGameWin(): Unit =
    restore("wonGameP1")
    assertTrue(searchable.done(getController.getLastMove.asInstanceOf[TwoPlayerMove], false), "Expected done state for this game. P1 should have won.")
    restore("wonGameP2")
    assertTrue(searchable.done(getController.getLastMove.asInstanceOf[TwoPlayerMove], false), "Expected done state for this game. P2 should have won.")

  override def testDoneEndGame(): Unit =
    restore("endGameNoMoreMoves")
    assertTrue(searchable.done(getController.getLastMove.asInstanceOf[TwoPlayerMove], false), "Expected done state for this game because there are no more moves. ")

  override def testGenerateAllP1MovesMidGame(): Unit =
    checkGeneratedMoves("midGameP1ToPlay8x8", EXPECTED_ALL_MIDDLE_GAME_MOVES_P1)

  override def testGenerateTopP1MovesMidGame(): Unit =
    getBestMovesOptions.setPercentageBestMoves(20)
    checkGeneratedMoves("midGameP1ToPlay8x8", EXPECTED_TOP_MIDDLE_GAME_MOVES_P1)

  override def testGenerateAllP1MovesEndGame(): Unit =
    checkGeneratedMoves("endGameP1ToPlay8x8", EXPECTED_ALL_END_GAME_MOVES_P1)

  override def testGenerateTopP1MovesEndGame(): Unit =
    getBestMovesOptions.setPercentageBestMoves(20)
    checkGeneratedMoves("endGameP1ToPlay8x8", EXPECTED_TOP_END_GAME_MOVES_P1)

  override def testGenerateAllP2MovesMidGame(): Unit =
    checkGeneratedMoves("midGameP2ToPlay8x8", EXPECTED_ALL_MIDDLE_GAME_MOVES_P2)

  override def testGenerateTopP2MovesMidGame(): Unit =
    getBestMovesOptions.setPercentageBestMoves(20)
    checkGeneratedMoves("midGameP2ToPlay8x8", EXPECTED_TOP_MIDDLE_GAME_MOVES_P2)

  override def testGenerateAllP2MovesEndGame(): Unit =
    checkGeneratedMoves("endGameP2ToPlay8x8", EXPECTED_ALL_END_GAME_MOVES_P2)

  override def testGenerateTopP2MovesEndGame(): Unit =
    getBestMovesOptions.setPercentageBestMoves(20)
    checkGeneratedMoves("endGameP2ToPlay8x8", EXPECTED_TOP_END_GAME_MOVES_P2)

  override def testGenerateUrgentMoves(): Unit =
    restore("urgentMoveP1ToPlay")
    val moves = searchable.generateUrgentMoves(
      getController.getLastMove.asInstanceOf[TwoPlayerMove],
      weights
    )
    checkMoveListAgainstExpected("urgentMoveP1ToPlay", EXPECTED_URGENT_MOVES_P1, moves)

  @Test
  def testGenerateUrgentMovesP2(): Unit =
    restore("urgentMoveP2ToPlay")
    val moves = searchable.generateUrgentMoves(
      getController.getLastMove.asInstanceOf[TwoPlayerMove],
      weights
    )
    checkMoveListAgainstExpected("urgentMoveP2ToPlay", EXPECTED_URGENT_MOVES_P2, moves)

  private def checkGeneratedMoves(fileName: String, expectedMoves: Array[TwoPlayerMove]): Unit =
    restore(fileName)
    val lastMove = getController.getLastMove.asInstanceOf[TwoPlayerMove]
    val moves = getController.getSearchable().generateMoves(lastMove, weights)
    checkMoveListAgainstExpected(fileName, expectedMoves, moves)
