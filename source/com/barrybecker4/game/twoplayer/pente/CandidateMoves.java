// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.pente;

/**
 * Representation of a Pente Game Board.
 *
 * @author Barry Becker
 */
public class CandidateMoves {

    int numRows, numCols;
    /** this is an auxiliary structure to help determine candidate moves. should extract to sep class. */
    private boolean[][] candidateMoves_;

    /**
     * Constructor
     * @param numRows num rows
     * @param numCols num cols
     */
    public CandidateMoves(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.candidateMoves_ = new boolean[numRows + 2][numCols + 2];
    }

    /** For internal use only */
    private CandidateMoves() {}

    /** Copy constructor */
    public CandidateMoves copy() {
        CandidateMoves cmoves = new CandidateMoves();
        cmoves.candidateMoves_ = this.candidateMoves_.clone();
        return cmoves;
    }

    public boolean isCandidate(int i, int j)   {
        return candidateMoves_[i][j];
    }

    public void setCandidate(int i, int j) {
        candidateMoves_[i][j] = true;
    }

    /**
     * The candidateMoves has a border on all sides
     */
    public void clear() {
        for ( int i = 0; i <= numRows + 1; i++ )  {
            for ( int j = 0; j <= numCols + 1; j++ ) {
                candidateMoves_[i][j] = false;
            }
        }
    }
}
