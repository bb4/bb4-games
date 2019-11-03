/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.SearchableHelper;
import com.barrybecker4.game.twoplayer.common.search.strategy.integration.ExpectedMoveMatrix;
import com.barrybecker4.game.twoplayer.common.search.strategy.integration.MoveInfo;
import com.barrybecker4.game.twoplayer.common.search.strategy.integration.NegaMaxStrategyTst;

/**
 * These results should be exactly the same as we get from minimax
 * because negamax is equivalent to minimax.
 * @author Barry Becker
 */
public class NegaMaxStrategyTest extends NegaMaxStrategyTst {

    @Override
    protected SearchableHelper createSearchableHelper() {
        return new TicTacToeHelper();
    }

    @Override
    protected ExpectedMoveMatrix getExpectedZeroLookAheadMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_ZERO_LOOKAHEAD_MOVES;
    }

    @Override
    protected ExpectedMoveMatrix getExpectedOneLevelLookAheadMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_ONE_LEVEL_LOOKAHEAD;
    }

    @Override
    protected ExpectedMoveMatrix getExpectedOneLevelWithQuiescenceMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_ONE_LEVEL_WITH_QUIESCENCE;
    }

    @Override
    protected ExpectedMoveMatrix getExpectedOneLevelWithQuiescenceAndABMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_ONE_LEVEL_WITH_QUIESCENCE_AND_AB;
    }

    @Override
    protected ExpectedMoveMatrix getExpectedTwoLevelLookAheadMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_TWO_LEVEL_LOOKAHEAD;
    }

    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelLookaheadMoves() {
        return new ExpectedMoveMatrix(   new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 1), 8, PLAYER2_PIECE), 228),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 48, PLAYER1_PIECE), 100),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 2), 28, PLAYER2_PIECE), 18),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 2), 0, PLAYER1_PIECE), 29),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 0, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 1), -12, PLAYER1_PIECE), 11)
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelBest20PercentMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_FOUR_LEVEL_BEST_20_PERCENT;
    }

    @Override
    protected ExpectedMoveMatrix getExpectedTwoLevelWithQuiescenceMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_TWO_LEVEL_WITH_QUIESCENCE;
    }

    @Override
    protected ExpectedMoveMatrix getExpectedTwoLevelWithQuiescenceAndABMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_TWO_LEVEL_WITH_QUIESCENCE_AND_AB;
    }

    @Override
    protected ExpectedMoveMatrix getExpectedThreeLevelWithQuiescenceMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_THREE_LEVEL_WITH_QUIESCENCE;
    }
    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelWithQuiescenceMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_FOUR_LEVEL_WITH_QUIESCENCE;
    }

    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelNoAlphaBetaMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_FOUR_LEVEL_NO_ALPHA_BETA;
    }
}
