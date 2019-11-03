/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe;

import com.barrybecker4.game.twoplayer.gomoku.GoMokuBoard;

/**
 * Representation of a TicTacToe Game Board
 *
 * @author Barry Becker
 */
public class TicTacToeBoard extends GoMokuBoard {

    /**
     * Constructor.
     * Tic tac toe is always 3x3
     */
    public TicTacToeBoard() {
        setSize( 3, 3 );
    }


    @Override
    public TicTacToeBoard copy() {
        return new TicTacToeBoard(this);
    }


    private TicTacToeBoard(TicTacToeBoard pb) {
        super(pb);
    }

    @Override
    public int getMaxNumMoves() {
        return 9;
    }

    @Override
    public TicTacToeCandidateMoves getCandidateMoves() {
        return new TicTacToeCandidateMoves(this);
    }

}
