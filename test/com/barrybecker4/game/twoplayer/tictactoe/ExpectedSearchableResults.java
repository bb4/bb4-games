/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;

/**
 * Expected generated moves for search tests.
 * @author Barry Becker
 */
public class ExpectedSearchableResults {

    private static final GamePiece PLAYER1_PIECE = new GamePiece(true);
    private static final GamePiece PLAYER2_PIECE = new GamePiece(false);

    static final TwoPlayerMove[] EXPECTED_ALL_MIDDLE_GAME_MOVES_CENTER_P1 = {
        TwoPlayerMove.createMove(new ByteLocation(1, 2), 4, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(2, 1), 4, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(2, 3), 4, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(3, 2), 4, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(1, 1), 8, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(1, 3), 8, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(3, 1), 8, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(3, 3), 8, PLAYER2_PIECE),
    };


    static final TwoPlayerMove[] EXPECTED_ALL_MIDDLE_GAME_MOVES_CORNER_P1 = {
        TwoPlayerMove.createMove(new ByteLocation(1, 2), -8, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(2, 1), -8, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(2, 2), -8, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(2, 3), -4, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(3, 2), -4, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(1, 3), 4, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(3, 1), 4, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(3, 3), 4, PLAYER2_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_ALL_MIDDLE_GAME_MOVES_EDGE_P1 = {
        TwoPlayerMove.createMove(new ByteLocation(2, 2), -4, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(1, 2), 0, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(2, 3), 0, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(3, 2), 0, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(1, 1), 4, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(1, 3), 4, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(3, 1), 4, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(3, 3), 4, PLAYER2_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_TOP_MIDDLE_GAME_MOVES_CENTER_P1 =  {
        TwoPlayerMove.createMove(new ByteLocation(1, 2), 0, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(2, 1), 0, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(2, 3), 0, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(3, 2), 0, PLAYER2_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_TOP_MIDDLE_GAME_MOVES_CORNER_P1 =  {
        TwoPlayerMove.createMove(new ByteLocation(1, 2), -8, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(2, 1), -8, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(2, 2), -8, PLAYER2_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_TOP_MIDDLE_GAME_MOVES_EDGE_P1 =  {
        TwoPlayerMove.createMove(new ByteLocation(2, 2), -4, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(1, 2), 0, PLAYER2_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_ALL_END_GAME_MOVES_P1 = {
        TwoPlayerMove.createMove(new ByteLocation(2, 1), 0, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(2, 3), 0, PLAYER2_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_TOP_END_GAME_MOVES_P1 = {
        TwoPlayerMove.createMove(new ByteLocation(2, 1), 0, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(2, 3), 0, PLAYER2_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_ALL_MIDDLE_GAME_MOVES_CENTER_P2 = {
        TwoPlayerMove.createMove(new ByteLocation(2, 1), 48, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(1, 2), 48, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(3, 2), 44, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(2, 3), 44, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(3, 1), 36, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(1, 3), 36, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(3, 3), -4, PLAYER1_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_ALL_MIDDLE_GAME_MOVES_CORNER_P2 = {

        TwoPlayerMove.createMove(new ByteLocation(2, 1), 32, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(1, 2), 32, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(3, 1), 28, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(1, 3), 28, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(3, 2), -4, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(2, 3), -4, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(3, 3), -8, PLAYER1_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_ALL_MIDDLE_GAME_MOVES_EDGE_P2 = {
        TwoPlayerMove.createMove(new ByteLocation(3, 1), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(1, 1), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(2, 3), -4, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(3, 3), -12, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(1, 3), -12, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(3, 2), -16, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(1, 2), -16, PLAYER1_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_TOP_MIDDLE_GAME_MOVES_CENTER_P2 =  {
        TwoPlayerMove.createMove(new ByteLocation(2, 1), 48, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(1, 2), 48, PLAYER1_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_TOP_MIDDLE_GAME_MOVES_CORNER_P2 =  {
        TwoPlayerMove.createMove(new ByteLocation(2, 1), 28, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(1, 2), 28, PLAYER1_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_TOP_MIDDLE_GAME_MOVES_EDGE_P2 =  {
        TwoPlayerMove.createMove(new ByteLocation(3, 1), 24, PLAYER1_PIECE),
       TwoPlayerMove.createMove(new ByteLocation(1, 1), 24, PLAYER1_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_ALL_END_GAME_MOVES_P2 = {
        TwoPlayerMove.createMove(new ByteLocation(3, 1), -12, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(1, 3), -12, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(3, 3), -52, PLAYER1_PIECE)
    };

    static final TwoPlayerMove[] EXPECTED_TOP_END_GAME_MOVES_P2 = {
        TwoPlayerMove.createMove(new ByteLocation(3, 1), -12, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(1, 3), -12, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new ByteLocation(3, 3), -52, PLAYER1_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_URGENT_MOVES = {
        TwoPlayerMove.createMove(new ByteLocation(3, 2), -8168, new GamePiece(false)),
        TwoPlayerMove.createMove(new ByteLocation(1, 3), -8180, new GamePiece(false)),
    };

    private ExpectedSearchableResults() {
    }
}
