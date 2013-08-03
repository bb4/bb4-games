/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.tictactoe;

import com.barrybecker4.game.twoplayer.pente.PenteBoard;

/**
 * Representation of a TicTacToe Game Board
 *
 * @author Barry Becker
 */
public class TicTacToeBoard extends PenteBoard {

    /**
     * Constructor.
     * Tic tac toe is always 3x3
     */
    public TicTacToeBoard() {
        setSize( 3, 3 );
    }

    @Override
    public int getMaxNumMoves() {
        return 9;
    }

    /**
     * All empty positions are candidate moves dor tic tac toe.
     * This is a bit similar than what we do for pente.
     */
    @Override
    public void determineCandidateMoves() {

        // first clear out what we had before
        candidateMoves.clear();

        int i,j;

        for ( i = 1; i <= getNumRows(); i++ )  {
            for ( j = 1; j <= getNumCols(); j++ )  {
                if ( !getPosition(i, j).isOccupied())
                    candidateMoves.setCandidate(i, j);
            }
        }
    }

}
