// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.hex;

import com.barrybecker4.game.twoplayer.gomoku.CandidateMoves;

/**
 * Representation of the candidate next moves on a Hex Game Board.
 * Candidate moves should be neighbors of existing moves + borders + some others.
 * Immutable after construction.
 * @author Barry Becker
 */
public class HexCandidateMoves extends CandidateMoves {

    private int numRows, numCols;

    /** this is an auxiliary structure to help determine candidate moves. should extract to sep class. */
    private boolean[][] candidateMoves_;

    /**
     * Constructor
     * @param board the gomoku board to determine candidate moves for
     */
    HexCandidateMoves(HexBoard board) {
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
     *
     */
    protected void determineCandidateMoves(HexBoard board) {

        for (int i = 1; i <= numRows; i++ )  {
            for (int j = 1; j <= numCols; j++ )  {
                if (!board.getPosition(i, j).isOccupied())
                    candidateMoves_[i][j] = true;
            }
        }
    }
}
