// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.model;

/**
 * @author Barry Becker
 */
public class ResultMaxTotals {

    double maxTotalTimeSeconds;
    int maxTotalMoves;


    public ResultMaxTotals(double maxTotalTime, int maxTotalMoves) {
        this.maxTotalTimeSeconds = maxTotalTime;
        this.maxTotalMoves = maxTotalMoves;
    }

}
