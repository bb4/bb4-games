/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board.path;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.blockade.BlockadeWeights;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Contains all the path lengths from each pawn to opponent homes
 *
 * @author Barry Becker
 */
public class PlayerPathLengths {

   /** path lengths from pawns to opponent homes for each of the 2 players.  */
   private PathLengths[] pathLengths = new PathLengths[2];


   /**
    * Constructor
    */
    public PlayerPathLengths() {
       pathLengths[0] = new PathLengths();
       pathLengths[1] = new PathLengths();
    }

    public PathLengths getPathLengthsForPlayer(boolean player1) {
        return pathLengths[player1 ?  0: 1];
    }

    /**
     * @return true if all the path lengths are valid so far.
     */
    public boolean isValid() {
        return pathLengths[0].isValid() && pathLengths[1].isValid();
    }

    /**
     * Calculate the worth of the recently placed move based on the path lengths.
     */
    public int determineWorth(double winningValue, ParameterArray weights) {

        double value;
        int p1 = 0;
        int p2 = 1;

        // If it landed on an opponents home base, then return a winning value.
        // It has landed if any of the shortest paths are 0.
        if (pathLengths[p1].shortestLength == 0 ) {
            GameContext.log(3, "**WON**");
            value = winningValue;
        }
        else if ( pathLengths[p2].shortestLength == 0 ) {
            GameContext.log(3, "**WON**");
            value = -winningValue;
        }
        else {
            int shortestLengthDiff = pathLengths[p2].shortestLength - pathLengths[p1].shortestLength;
            int secondShortestDiff = pathLengths[p2].secondShortestLength - pathLengths[p1].secondShortestLength;
            int furthestLengthDiff = pathLengths[p2].furthestLength - pathLengths[p1].furthestLength;

            value =  weights.get(BlockadeWeights.CLOSEST_WEIGHT_INDEX).getValue() *  shortestLengthDiff
                       + weights.get(BlockadeWeights.SECOND_CLOSEST_WEIGHT_INDEX).getValue() * secondShortestDiff
                       + weights.get(BlockadeWeights.FURTHEST_WEIGHT_INDEX).getValue() * furthestLengthDiff;
        }
        return (int)value;
    }

    /**
     */
    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder();
        bldr.append("PlayerPathLengths:");
        bldr.append("\n\r player1:").append(getPathLengthsForPlayer(true));
        bldr.append("\n\r player2:").append(getPathLengthsForPlayer(false));
        return bldr.toString();
    }

}
