/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis;

import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.twoplayer.common.cache.ScoreCache;
import com.barrybecker4.game.twoplayer.common.cache.ScoreEntry;
import com.barrybecker4.game.twoplayer.common.search.transposition.HashKey;
import com.barrybecker4.game.twoplayer.go.board.GoBoard;
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzer;
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap;
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup;
import com.barrybecker4.optimization.parameter.ParameterArray;
import scala.Option;

/**
 * Responsible for evaluating groups and territory on a go board.
 * we keep a cache of the score for each unique board position to avoid recalculating.
 *
 * @author Barry Becker
 */
public final class BoardEvaluator {

    /** If true, we attempt to hang onto worth values for board positions that we have already see before. */
    private static final boolean USE_SCORE_CACHING = false;

    private GoBoard board_;
    private WorthCalculator worthCalculator;
    private GroupAnalyzerMap analyzerMap_;
    private ScoreCache scoreCache;

    /**
     * Constructor.
     */
    public BoardEvaluator(GoBoard board, ScoreCache cache) {

        board_ = board;
        analyzerMap_ = new GroupAnalyzerMap();
        worthCalculator = new WorthCalculator(board, analyzerMap_);
        scoreCache = cache;
    }

    /**
     * @return the worth of the board from player1's perspective
     */
    public int worth(Move lastMove, ParameterArray weights, HashKey hashKey) {

        if (USE_SCORE_CACHING) {
            return cachedWorth(lastMove, weights, hashKey);
        } else {
            return worthCalculator.worth(lastMove, weights);
        }
    }

    /**
     * If we have a cached worth value for this board position, then use that instead of recomputing it.
     * Why doesn't playing with caching give same results as without? I think its because we can arrive at
     * identical board positions from different routes and they have different scores (may be related to ko)
     * @return statically evaluated value for the board.
     */
    private int cachedWorth( Move lastMove, ParameterArray weights, HashKey key) {

        // uncomment this to do caching.
        Option<ScoreEntry> cachedScore = scoreCache.get(key);
        ///////// comment this to do debugging
        //if (cachedScore != null) {
        //    System.out.println("scoreCache="+scoreCache);
        //    return cachedScore.getScore();
        //}

        int worth = worthCalculator.worth(lastMove, weights);

        if (cachedScore.isEmpty()) {
            scoreCache.put(key, new ScoreEntry(key, worth, board_.toString(), getWorthInfo()));
        }
        else {
            if (cachedScore.get().getScore() != worth) {
                String bldr = "\ncachedScore " +
                        cachedScore +
                        //append("\nfor key=").
                        //append(getHashKey()).
                        "\ndid not match " +
                        worth + " for \n" +
                        board_.toString() +
                        "\ncurrent info: " +
                        getWorthInfo() +
                        "using current key=" +
                        key;
                System.out.println(bldr);
                System.out.flush();
            }
        }
        return worth;
    }

    /**
     * Used only for debugging to understand how the worth was calculated.
     * @return the worthInfo from the calculator.
     */
    private WorthInfo getWorthInfo() {
        return worthCalculator.getWorthInfo();
    }

    /** don't really want to expose this, but need for rendering. */
    public GroupAnalyzer getGroupAnalyzer(IGoGroup group) {
        return analyzerMap_.getAnalyzer(group);
    }

    public ScoreCache getCache() {
        return scoreCache;
    }

    /**
     * Get estimate of territory for specified player.
     * @param forPlayer1 the player to get the estimate for
     * @param isEndOfGame then we need the estimate to be more accurate.
     * @return estimate of size of territory for specified player.
     */
    public int getTerritoryEstimate(boolean forPlayer1, boolean isEndOfGame) {
        return worthCalculator.getTerritoryEstimate(forPlayer1, isEndOfGame);
    }

    /**
     * @return the estimated difference in territory between the 2 sides.
     */
    public float updateTerritoryAtEndOfGame() {
        return worthCalculator.updateTerritoryAtEndOfGame();
    }
}
