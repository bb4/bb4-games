/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.hex;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * Calculates the worth of the hex game board from player one's standpoint
 *
 * @author Barry Becker
*/
public class WorthCalculator  {

    /**
     * @return the worth of the hex game board from player one's standpoint
     */
    public int calculateWorth(HexBoard board, TwoPlayerMove lastMove, ParameterArray weights) {
        double pathValue = calcPathValue(board);
        double centricityValue = calcCentricityValue(board, lastMove);

        double wt1 = weights.get(HexWeights.CENTRICITY_INDEX).getValue();
        double wt2 = weights.get(HexWeights.PATH_COST_INDEX).getValue();
        //System.out.println("wt1="+ wt1 + " centVal=" + centricityValue + " wt2=" + wt2 + " pathVal=" + pathValue);
        return (int) (wt1 * centricityValue + wt2 * pathValue);
    }

    private double calcPathValue(HexBoard board) {
         int pathValue = MathUtil.RANDOM().nextInt(100);

        int p1Cost = board.getP1PathCost();
        int p2Cost = board.getP2PathCost();
        boolean p1Connected = (p1Cost == 0);
        boolean p2Connected = (p2Cost == 0);
        if (p1Connected || p2Connected) {
            if (p1Connected) {
                pathValue = SearchStrategy.WINNING_VALUE;
            }
            else if (p2Connected) {
                pathValue = -SearchStrategy.WINNING_VALUE;
            }
        }
        else {
            pathValue = p2Cost - p1Cost;
        }
        return pathValue;
    }

    private double calcCentricityValue(HexBoard board, TwoPlayerMove lastMove) {
        double thresh = 10;
        double moveNum = board.getMoveList().size();
        double sign = lastMove.isPlayer1() ? -1 : 1;
        if (moveNum < thresh) {
            Location center = board.getCenter();
            Location lastMv = lastMove.getToLocation();
            double manhattanDist =
                    Math.abs(center.getRow() - lastMv.getRow())
                    + Math.abs(center.getCol() - lastMv.getCol());
            return sign * (thresh - moveNum) * manhattanDist / thresh;
        }
        return 0;
    }
}