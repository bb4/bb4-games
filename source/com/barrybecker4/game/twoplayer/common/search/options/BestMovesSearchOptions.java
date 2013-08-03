/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.search.options;

/**
 * The options for search strategies that use brute-force minimax search like MiniMax, NegaMax, NegaScout,
 * and also the memory and aspiration variations of these strategies.
 * These methods usually use a search window to do pruning of tree branches.
 *
 * @author Barry Becker
 */
public class BestMovesSearchOptions {

    /** Percentage of best moves to consider at each search ply. */
    private static final int DEFAULT_PERCENTAGE_BEST_MOVES = 50;

    /** No matter what the percentBestMoves is we should not prune if less than this number. */
    private static final int DEFAULT_MIN_BEST_MOVES = 10;

    /** Select best moves whose value is no less than this percent less than the highest value in the set. */
    private static final int DEFAULT_PERCENT_LESS_THAN_BEST_THRESH = 60;

    private int bestPercentage_ = DEFAULT_PERCENTAGE_BEST_MOVES;
    private int minBestMoves_ = DEFAULT_MIN_BEST_MOVES;
    private int percentLessThanBestThreshold_ = DEFAULT_PERCENT_LESS_THAN_BEST_THRESH;


    /**
     * Default Constructor
     */
    public BestMovesSearchOptions() {}

    /**
     * Constructor
     * @param bestPercentage default number of best moves to consider at each ply.
     * @param minBestMoves we will never consider fewer than this many moves when searching.
     * @param percentLessThanBestThresh Select best moves whose values is no less than this percent less
     *  than the highest value in the set.
     */
    public BestMovesSearchOptions(int bestPercentage, int minBestMoves, int percentLessThanBestThresh) {
        bestPercentage_ = bestPercentage;
        minBestMoves_ = minBestMoves;
        percentLessThanBestThreshold_ = percentLessThanBestThresh;
    }

    public int getDefaultPercentageBestMoves() {
        return DEFAULT_PERCENTAGE_BEST_MOVES;
    }

    public int getDefaultMinBestMoves() {
        return DEFAULT_MIN_BEST_MOVES;
    }

    public int getDefaultPercentLessThanBestThresh() {
        return DEFAULT_PERCENT_LESS_THAN_BEST_THRESH;
    }

    /**
     * @return  the percentage of top moves considered at each ply
     */
    public final int getPercentageBestMoves() {
        return bestPercentage_;
    }

    /**
     * @param bestPercentage  the percentage of top moves considered at each ply
     */
    public final void setPercentageBestMoves( int bestPercentage ) {
        bestPercentage_ = bestPercentage;
    }

    /**
     * @return  never return fewer than this many best moves.
     */
    public int getMinBestMoves() {
        return minBestMoves_;
    }

    public void setMinBestMoves(int minBest) {
        assert minBest > 0 : "minBest must be greater than 0. It was " + minBest;
        minBestMoves_ = minBest;
    }

    /**
     * @return  never return fewer than this many best moves.
     */
    public int getPercentLessThanBestThresh() {
        return percentLessThanBestThreshold_;
    }

    public void setPercentLessThanBestThresh(int percent) {
        assert percent >= 0 && percent <= 100 : "percent out of range : " + percent;
        percentLessThanBestThreshold_ = percent;
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder();
        bldr.append("bestPercentage: ").append(bestPercentage_);
        bldr.append("  minBestMoves:").append(minBestMoves_);
        bldr.append("  percentLessThanBestThreshold:").append(percentLessThanBestThreshold_);
        return bldr.toString();
    }

}