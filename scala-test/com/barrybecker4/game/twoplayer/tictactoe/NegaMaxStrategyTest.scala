/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe

import com.barrybecker4.common.geometry.ByteLocation
import com.barrybecker4.game.common.board.GamePiece
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove
import com.barrybecker4.game.twoplayer.common.search.SearchableHelper
import com.barrybecker4.game.twoplayer.common.search.strategy.integration.{ExpectedMoveMatrix, MoveInfo, NegaMaxStrategyTst}

/**
 * These results should be exactly the same as we get from minimax
 * because negamax is equivalent to minimax.
 * @author Barry Becker
 */
class NegaMaxStrategyTest extends NegaMaxStrategyTst:

  override protected def createSearchableHelper: SearchableHelper =
    new TicTacToeHelper()

  override protected def getExpectedZeroLookAheadMoves: ExpectedMoveMatrix =
    ExpectedSearchStrategyResults.EXPECTED_ZERO_LOOKAHEAD_MOVES

  override protected def getExpectedOneLevelLookAheadMoves: ExpectedMoveMatrix =
    ExpectedSearchStrategyResults.EXPECTED_ONE_LEVEL_LOOKAHEAD

  override protected def getExpectedOneLevelWithQuiescenceMoves: ExpectedMoveMatrix =
    ExpectedSearchStrategyResults.EXPECTED_ONE_LEVEL_WITH_QUIESCENCE

  override protected def getExpectedOneLevelWithQuiescenceAndABMoves: ExpectedMoveMatrix =
    ExpectedSearchStrategyResults.EXPECTED_ONE_LEVEL_WITH_QUIESCENCE_AND_AB

  override protected def getExpectedTwoLevelLookAheadMoves: ExpectedMoveMatrix =
    ExpectedSearchStrategyResults.EXPECTED_TWO_LEVEL_LOOKAHEAD

  override protected def getExpectedFourLevelLookaheadMoves: ExpectedMoveMatrix =
    new ExpectedMoveMatrix(
      new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 1), 8, new GamePiece(false)), 228),
      new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 48, new GamePiece(true)), 100),
      new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 2), 28, new GamePiece(false)), 18),
      new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 2), 0, new GamePiece(true)), 29),
      new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 0, new GamePiece(false)), 4),
      new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 1), -12, new GamePiece(true)), 11)
    )

  override protected def getExpectedFourLevelBest20PercentMoves: ExpectedMoveMatrix =
    ExpectedSearchStrategyResults.EXPECTED_FOUR_LEVEL_BEST_20_PERCENT

  override protected def getExpectedTwoLevelWithQuiescenceMoves: ExpectedMoveMatrix =
    ExpectedSearchStrategyResults.EXPECTED_TWO_LEVEL_WITH_QUIESCENCE

  override protected def getExpectedTwoLevelWithQuiescenceAndABMoves: ExpectedMoveMatrix =
    ExpectedSearchStrategyResults.EXPECTED_TWO_LEVEL_WITH_QUIESCENCE_AND_AB

  override protected def getExpectedThreeLevelWithQuiescenceMoves: ExpectedMoveMatrix =
    ExpectedSearchStrategyResults.EXPECTED_THREE_LEVEL_WITH_QUIESCENCE

  override protected def getExpectedFourLevelWithQuiescenceMoves: ExpectedMoveMatrix =
    ExpectedSearchStrategyResults.EXPECTED_FOUR_LEVEL_WITH_QUIESCENCE

  override protected def getExpectedFourLevelNoAlphaBetaMoves: ExpectedMoveMatrix =
    ExpectedSearchStrategyResults.EXPECTED_FOUR_LEVEL_NO_ALPHA_BETA
