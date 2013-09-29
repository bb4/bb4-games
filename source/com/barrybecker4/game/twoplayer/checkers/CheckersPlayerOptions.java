// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.checkers;

import com.barrybecker4.game.twoplayer.common.TwoPlayerPlayerOptions;
import com.barrybecker4.game.twoplayer.common.search.options.BestMovesSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.BruteSearchOptions;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;

import java.awt.*;

/**
 * @author Barry Becker
 */
public class CheckersPlayerOptions extends TwoPlayerPlayerOptions {

    public CheckersPlayerOptions(String name, Color color) {
        super(name, color);
    }

    @Override
    protected SearchOptions createDefaultSearchOptions() {
        return new SearchOptions(new BruteSearchOptions(4), new BestMovesSearchOptions(100, 0, 10));
    }
}
