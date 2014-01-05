// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.mancala;

import com.barrybecker4.game.twoplayer.common.TwoPlayerPlayerOptions;
import com.barrybecker4.game.twoplayer.common.search.options.BestMovesSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.BruteSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.MonteCarloSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;

import java.awt.Color;

/**
 * Player options specific to Mancala game.
 * @author Barry Becker
 */
class MancalaPlayerOptions extends TwoPlayerPlayerOptions {


    /** initial look ahead factor. */
    private static final int DEFAULT_LOOK_AHEAD = 6;

    /** Don't consider moves that are less than this percentage less than the best move. */
    private static final int DEFAULT_PERCENT_LESS_THAN_BEST_THRESH = 70;

    /** for any given ply never consider more than BEST_PERCENTAGE of the top moves. not used. */
    private static final int DEFAULT_PERCENTAGE_BEST_MOVES = 60;

    /** for any given ply never consider less than this many moves. */
    private static final int DEFAULT_MIN_BEST_MOVES = 4;


    MancalaPlayerOptions(String name, Color color) {
       super(name, color);
    }

    @Override
    protected SearchOptions createDefaultSearchOptions() {
        return new SearchOptions(new BruteSearchOptions(DEFAULT_LOOK_AHEAD, DEFAULT_LOOK_AHEAD + 2),
                                 new BestMovesSearchOptions(DEFAULT_PERCENTAGE_BEST_MOVES,
                                         DEFAULT_PERCENT_LESS_THAN_BEST_THRESH, DEFAULT_MIN_BEST_MOVES
                                 ),
                                 new MonteCarloSearchOptions(4000, 1.0, 25));
    }
}
