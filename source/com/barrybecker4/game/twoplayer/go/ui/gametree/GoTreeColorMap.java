/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui.gametree;

import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;

import java.awt.*;

/**
 * Use to color the cells in the game tree by their values.
 * We will use this colormap for both the text tree and the graphical tree viewers
 * so they have consistent coloring.
 * Used to color the game tree rows, nodes, and arcs.
 *
 * @author Barry Becker
 */
class GoTreeColorMap extends ColorMap {

        private static final double[] myValues_ = {
                                  -SearchStrategy.WINNING_VALUE,
                                  -SearchStrategy.WINNING_VALUE/2.0,
                                  -SearchStrategy.WINNING_VALUE/10.0,
                                  -SearchStrategy.WINNING_VALUE/100.0,
                                   0.0,
                                   SearchStrategy.WINNING_VALUE/100.0,
                                   SearchStrategy.WINNING_VALUE/10.0,
                                   SearchStrategy.WINNING_VALUE/2.0,
                                   SearchStrategy.WINNING_VALUE};

        private static final Color[] myColors_ = {
                                  new Color(180, 0, 20),
                                  new Color(255, 0, 0),
                                  new Color(255, 190, 0),
                                  new Color(255, 255, 0),
                                  new Color(255, 255, 255),    // new Color(240, 240, 240, 120)
                                  new Color(0, 255, 100),
                                  new Color(0, 200, 255),
                                  new Color(0, 0, 255),
                                  new Color(0, 1, 180)
                                };

    /**
     * Create out tree cell colormap.
     */
    public GoTreeColorMap() {

        super(myValues_, myColors_);
    }
}
