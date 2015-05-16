// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.gomoku;

import com.barrybecker4.common.geometry.ByteLocation;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.BoardPosition;

/**
 * Representation of the candidate next moves on a GoMoku Game Board.
 * Immutable after construction.
 * @author Barry Becker
 */
public class CandidateMoves {

    /** 8 footprint offsets around a single position. */
    private static ByteLocation[] SPLAT = {
            new ByteLocation(-1, -1),
            new ByteLocation(-1, 0),
            new ByteLocation(-1, 1),
            new ByteLocation(0, -1),
            new ByteLocation(0, 1),
            new ByteLocation(1, -1),
            new ByteLocation(1, 0),
            new ByteLocation(1, 1),
    };

    private int numRows, numCols;

    /** this is an auxiliary structure to help determine candidate moves. should extract to sep class. */
    private boolean[][] candidateMoves_;

    /**
     * Constructor
     * @param board the gomoku board to determine candidate moves for
     */
    public CandidateMoves(GoMokuBoard board) {
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

    public void setCandidate(BoardPosition pos) {

        if ( pos != null && !pos.isOccupied())  {
            candidateMoves_[pos.getRow()][pos.getCol()] = true;
        }
    }

    /**
     * This method splats a footprint of trues around the current moves.
     * later we look for empty spots that are true for candidate moves.
     */
    protected void determineCandidateMoves(GoMokuBoard board)  {

        int i,j;
        boolean hasCandidates = false;
        for ( i = 1; i <= numRows; i++ ) {
            for ( j = 1; j <= numCols; j++ ) {
                if ( board.getPosition(i, j).isOccupied() ) {
                    Location location = new ByteLocation(i, j);
                    // set the surrounding footprint
                    for (Location offset : SPLAT) {
                        BoardPosition pos =
                                board.getPosition(location.incrementOnCopy(offset));
                        setCandidate(pos);
                    }
                    hasCandidates = true;
                }
            }
        }
        // edge case when no moves on the board - just use the center
        Location center = new ByteLocation(numRows / 2 + 1, numCols / 2 + 1);
        BoardPosition pos = board.getPosition(center);
        if (!hasCandidates) {
            setCandidate(pos);
        }
    }
}
