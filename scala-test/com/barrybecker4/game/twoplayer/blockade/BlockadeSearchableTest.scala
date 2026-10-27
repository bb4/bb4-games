/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.MoveList
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.search.{ISearchableHelper, TwoPlayerSearchableBaseTst}
import com.barrybecker4.optimization.parameter.ParameterArray
import org.junit.Assert.{assertEquals, assertFalse, assertTrue}

import java.util.List as JList

/**
 * Verify that all the methods in blockadeSearchable work as expected
 * @author Barry Becker
 */
class BlockadeSearchableTest extends TwoPlayerSearchableBaseTst:

  override protected def createSearchableHelper: ISearchableHelper =
    new BlockadeHelper()

  override protected def createInitialMove: TwoPlayerMove =
    new BlockadeMove(
      new ByteLocation(5, 5),
      new ByteLocation(5, 5),
      0,
      new GamePiece(true),
      new BlockadeWall(new BlockadeBoardPosition(1, 1), new BlockadeBoardPosition(2, 1))
    )

  override protected def getDebugLevel: Int = -1

  override def testNotDoneMidGame(): Unit =
    assertFalse(false)

  override def testDoneForMidGameWin(): Unit =
    assertFalse(false)

  override def testDoneEndGame(): Unit =
    assertFalse(false)

  override protected def getExpectedNumGeneratedMovesBeforeFirstMove: Int = 47

  override def testGenerateAllP1MovesMidGame(): Unit =
    checkGeneratedMoves("middleGameP1Turn", ExpectedSearchableResults.EXPECTED_ALL_MIDDLE_GAME_MOVES_P1)

  override def testGenerateTopP1MovesMidGame(): Unit =
    getBestMovesOptions.setPercentageBestMoves(20)
    checkGeneratedMoves("middleGameP1Turn", ExpectedSearchableResults.EXPECTED_TOP_MIDDLE_GAME_MOVES_P1)

  override def testGenerateAllP1MovesEndGame(): Unit =
    checkGeneratedMoves("endGameP1Turn", ExpectedSearchableResults.EXPECTED_ALL_END_GAME_MOVES_P1)

  override def testGenerateTopP1MovesEndGame(): Unit =
    getBestMovesOptions.setPercentageBestMoves(20)
    checkGeneratedMoves("endGameP1Turn", ExpectedSearchableResults.EXPECTED_TOP_END_GAME_MOVES_P1)

  override def testGenerateAllP2MovesMidGame(): Unit =
    checkGeneratedMoves("middleGameP2Turn", ExpectedSearchableResults.EXPECTED_ALL_MIDDLE_GAME_MOVES_P2)

  override def testGenerateTopP2MovesMidGame(): Unit =
    getBestMovesOptions.setPercentageBestMoves(20)
    checkGeneratedMoves("middleGameP2Turn", ExpectedSearchableResults.EXPECTED_TOP_MIDDLE_GAME_MOVES_P2)

  override def testGenerateAllP2MovesEndGame(): Unit =
    checkGeneratedMoves("endGameP2Turn", ExpectedSearchableResults.EXPECTED_ALL_END_GAME_MOVES_P2)

  override def testGenerateTopP2MovesEndGame(): Unit =
    getBestMovesOptions.setPercentageBestMoves(20)
    checkGeneratedMoves("endGameP2Turn", ExpectedSearchableResults.EXPECTED_TOP_END_GAME_MOVES_P2)

  private def checkGeneratedMoves(fileName: String, expectedMoves: Array[BlockadeMove]): Unit =
    restore(fileName)
    val wts: ParameterArray = weights
    val lastMove = getController.getLastMove.asInstanceOf[TwoPlayerMove]
    val moves = getController.getSearchable().generateMoves(lastMove, wts)

    if expectedMoves.length != moves.size then
      printMoves(fileName, moves)

    assertEquals("Unexpected number of generated moves.", expectedMoves.length, moves.size)

    val diffs = new StringBuilder("")
    for i <- 0 until moves.size do
      val move = moves.get(i).asInstanceOf[BlockadeMove]
      val expMove = expectedMoves(i)
      if move != expMove then
        diffs.append(i).append(") Unexpected moves.\n Expected ").append(expMove)
          .append(" \nBut got ").append(move).append("\n")

    if diffs.length > 0 then
      printMoves(fileName, moves)

    assertTrue(
      "There were unexpected generated moves for " + fileName + "\n" + diffs,
      diffs.length == 0
    )

  override def testGenerateUrgentMoves(): Unit =
    val moves: JList[?] = searchable.generateUrgentMoves(null, weights)
    assertTrue(
      "We expect move list to be empty since generateUrgentMoves is not implemented for Blockade.",
      moves.isEmpty
    )
