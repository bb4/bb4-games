// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.pente;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.BoardPosition;

/**
 * Representation of the candidate next moves on a Pente Game Board.
 * Immutable after construction.
 * @author Barry Becker
 */
public class CandidateMoves {

    private PenteBoard board;
    protected int numRows, numCols;

    /** this is an auxiliary structure to help determine candidate moves. should extract to sep class. */
    protected boolean[][] candidateMoves_;

    /**
     * Constructor
     * @param board the pente board to determine candidate moves for
     */
    public CandidateMoves(PenteBoard board) {
        this.board = board;
        this.numRows = board.getNumRows();
        this.numCols = board.getNumCols();
        this.candidateMoves_ = new boolean[numRows + 2][numCols + 2];
        determineCandidateMoves(board);
    }

    /** For internal use only */
    protected CandidateMoves() {}

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

    public void setCandidate(int i, int j) {
        BoardPosition pos = board.getPosition(i, j);
        if ( pos != null && !board.getPosition(i, j).isOccupied())  {
            candidateMoves_[i][j] = true;
        }
    }

    /**
     * This method splats a footprint of trues around the current moves.
     * later we look for empty spots that are true for candidate moves.
     */
    protected void determineCandidateMoves(PenteBoard board)  {

        int i,j;
        boolean hasCandidates = false;
        for ( i = 1; i <= numRows; i++ ) {
            for ( j = 1; j <= numCols; j++ ) {
                if ( board.getPosition(i, j).isOccupied() ) {
                    // set the surrounding footprint
                    setCandidate(i - 1, j - 1);
                    setCandidate(i - 1, j);
                    setCandidate(i - 1, j + 1);
                    setCandidate(i, j - 1);
                    setCandidate(i, j + 1);
                    setCandidate(i + 1, j - 1);
                    setCandidate(i + 1, j);
                    setCandidate(i + 1, j + 1);
                    hasCandidates = true;
                }
            }
        }
        // edge case when no moves on the board - just use the center
        Location center = new ByteLocation(numRows / 2 + 1, numCols / 2 + 1);
        if (!hasCandidates && !board.getPosition(center).isOccupied()) {
            setCandidate(center.getRow(), center.getCol());
        }
    }
}
