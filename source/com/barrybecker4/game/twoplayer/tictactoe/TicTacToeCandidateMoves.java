// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.tictactoe;

import com.barrybecker4.game.twoplayer.gomoku.CandidateMoves;
import com.barrybecker4.game.twoplayer.gomoku.GoMokuBoard;

/**
 * Representation of the candidate next moves on a TicTacToe Game Board.
 * Immutable after construction.
 * @author Barry Becker
 */
public class TicTacToeCandidateMoves extends CandidateMoves {

    private int numRows, numCols;

    /** this is an auxiliary structure to help determine candidate moves. should extract to sep class. */
    private boolean[][] candidateMoves_;

    /**
     * Constructor
     * @param board the gomoku board to determine candidate moves for
     */
    TicTacToeCandidateMoves(TicTacToeBoard board) {
        this.numRows = board.getNumRows();
        this.numCols = board.getNumCols();
        this.candidateMoves_ = new boolean[numRows + 2][numCols + 2];
        determineCandidateMoves(board);
    }

    /**
     * We consider only those spaces bordering on non-empty spaces.
     * In theory all empties should be considered, but in practice only
     * those bordering existing moves are likely to be favorable.
     *
     * @return true if this position is a possible next move
     */
    public boolean isCandidateMove( int row, int col )  {
        return candidateMoves_[row][col];
    }

    /**
     * All empty positions are candidate moves dor tic tac toe.
     * This is a bit similar than what we do for gomoku.
     */
    @Override
    protected void determineCandidateMoves(GoMokuBoard board) {

        for (int i = 1; i <= numRows; i++ )  {
            for (int j = 1; j <= numCols; j++ )  {
                if (!board.getPosition(i, j).isOccupied())
                    candidateMoves_[i][j] = true;
            }
        }
    }
}
