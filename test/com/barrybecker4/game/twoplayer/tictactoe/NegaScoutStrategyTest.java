/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.SearchableHelper;
import com.barrybecker4.game.twoplayer.common.search.strategy.integration.ExpectedMoveMatrix;
import com.barrybecker4.game.twoplayer.common.search.strategy.integration.MoveInfo;
import com.barrybecker4.game.twoplayer.common.search.strategy.integration.NegaScoutStrategyTst;

/**
 * These results should be exactly the same as we get from minimax
 * because negamax is equivalent to minimax.
 * @author Barry Becker
 */
public class NegaScoutStrategyTest extends NegaScoutStrategyTst {

    private static final GamePiece PLAYER1_PIECE = new GamePiece(true);
    private static final GamePiece PLAYER2_PIECE = new GamePiece(false);

    @Override
    protected SearchableHelper createSearchableHelper() {
        return new TicTacToeHelper();
    }

    @Override
    protected ExpectedMoveMatrix getExpectedZeroLookAheadMoves() {
        return new ExpectedMoveMatrix(
            TwoPlayerMove.createMove(new ByteLocation(2, 2), 16, new GamePiece(true)),
            TwoPlayerMove.createMove(new ByteLocation(1, 1), -8, new GamePiece(false)),
            TwoPlayerMove.createMove(new ByteLocation(1, 2), 88, new GamePiece(true)),
            TwoPlayerMove.createMove(new ByteLocation(1, 3), -88, new GamePiece(false)),
            TwoPlayerMove.createMove(new ByteLocation(3, 1), 0, new GamePiece(true)),
            TwoPlayerMove.createMove(new ByteLocation(2, 3), -48, new GamePiece(false))
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedOneLevelLookAheadMoves() {
        return new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 2), 4, PLAYER2_PIECE), 8),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 48, PLAYER1_PIECE), 7),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 2), 28, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 2), 0, PLAYER1_PIECE), 5),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 0, PLAYER2_PIECE), 2),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 1), -12, PLAYER1_PIECE), 3)
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedOneLevelWithQuiescenceMoves() {
        return new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 2), 4, PLAYER2_PIECE), 8),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 8208, PLAYER1_PIECE), 13),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 2), 28, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 2), 8196, PLAYER1_PIECE), 12),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 0, PLAYER2_PIECE), 2),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 1), 8132, PLAYER1_PIECE), 5)
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedOneLevelWithQuiescenceAndABMoves() {
        return new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 2), 4, PLAYER2_PIECE), 8),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 8208, PLAYER1_PIECE), 13),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 2), 28, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 2), 8196, PLAYER1_PIECE), 12),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 0, PLAYER2_PIECE), 2),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 1), 8132, PLAYER1_PIECE), 5)
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedTwoLevelLookAheadMoves() {
        return new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 2), 4, PLAYER2_PIECE), 22),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 48, PLAYER1_PIECE), 19),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 2), 28, PLAYER2_PIECE), 10),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 2), 0, PLAYER1_PIECE), 17),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 0, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 3), -52, PLAYER1_PIECE), 9)
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelLookaheadMoves() {
        return new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 1), 8, PLAYER2_PIECE), 211),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 48, PLAYER1_PIECE), 101),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 2), 28, PLAYER2_PIECE), 18),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 2), 0, PLAYER1_PIECE), 28),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 0, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 1), -12, PLAYER1_PIECE), 11)
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelBest20PercentMoves() {
        return new ExpectedMoveMatrix(
             new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 2), 4, PLAYER2_PIECE), 73),   // as 61
             new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 48, PLAYER1_PIECE), 53),
             new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 2), 28, PLAYER2_PIECE), 18),
             new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 2), 0, PLAYER1_PIECE), 26),
             new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 0, PLAYER2_PIECE), 4),
             new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 1), -12, PLAYER1_PIECE), 11)
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedTwoLevelWithQuiescenceMoves() {
        return new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 2), 4, PLAYER2_PIECE), 35),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 48, PLAYER1_PIECE), 19),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 2), 28, PLAYER2_PIECE), 15),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 2), 0, PLAYER1_PIECE), 17),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 0, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 3), -52, PLAYER1_PIECE), 9)
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedTwoLevelWithQuiescenceAndABMoves() {
        return new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 2), 4, PLAYER2_PIECE), 8),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 8208, PLAYER1_PIECE), 13),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 2), 28, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 2), 8196, PLAYER1_PIECE), 12),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 0, PLAYER2_PIECE), 2),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 1), 8132, PLAYER1_PIECE), 5)
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedThreeLevelWithQuiescenceMoves() {
        return new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 2), 4, PLAYER2_PIECE), 76),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 3), 8272, PLAYER1_PIECE), 146),  // was 136
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 2), 28, PLAYER2_PIECE), 14),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 2), 0, PLAYER1_PIECE), 27),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 0, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 1), -12, PLAYER1_PIECE), 11)
        );
    }
    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelWithQuiescenceMoves() {
        return new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), -8240, PLAYER2_PIECE), 292), // was 292, 247, now 784
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 48, PLAYER1_PIECE), 101),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 2), 28, PLAYER2_PIECE), 18),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 2), 0, PLAYER1_PIECE), 28),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 0, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 1), -12, PLAYER1_PIECE), 11)
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelNoAlphaBetaMoves() {
        return new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 1), 8, PLAYER2_PIECE), 211), // was 197
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 48, PLAYER1_PIECE), 101),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 2), 28, PLAYER2_PIECE), 18),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(1, 2), 0, PLAYER1_PIECE), 28),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(2, 1), 0, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new ByteLocation(3, 1), -12, PLAYER1_PIECE), 11)
        );
    }
}