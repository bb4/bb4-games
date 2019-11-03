/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.dialogs.searchoptions;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.common.search.options.MonteCarloSearchOptions;
import com.barrybecker4.ui.components.NumberInput;

import javax.swing.*;

/**
 * Panel that shows the options for search strategies that use monte carlo methods (like UCT derivatives).
 *
 * @author Barry Becker
 */
public class MonteCarloOptionsPanel extends JPanel {

    MonteCarloSearchOptions monteCarloOptions_;

    private NumberInput maxSimulationsField_;

    private NumberInput exploreExploitRatioField_;

    private NumberInput randomLookHeadField_;

    /** It would be unreasonable to run more than this many simulations. */
    private static final int ABS_MAX_NUM_SIMULATIONS = 100000000;

    /** It would be unreasonable to have a exploreExploit ration more than this. */
    private static final double ABS_MAX_EE_RATIO = 100;

    /**
     * Constructor
     */
    public MonteCarloOptionsPanel(MonteCarloSearchOptions sOptions) {
        monteCarloOptions_ = sOptions;
        initialize();
    }

    /**
     * update the options based on the user preferences.
     */
    public void updateMonteCarloOptionsOptions() {

        monteCarloOptions_.setMaxSimulations(maxSimulationsField_.getIntValue());
        monteCarloOptions_.setExploreExploitRatio(exploreExploitRatioField_.getValue());
        monteCarloOptions_.setRandomLookAhead(randomLookHeadField_.getIntValue());
    }

    /**
     * create the panel with ui element for setting the options
     */
    protected void initialize() {

        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        maxSimulationsField_ =
            new NumberInput(GameContext.getLabel("MAX_NUM_SIMULATIONS"), monteCarloOptions_.getMaxSimulations(),
                            GameContext.getLabel("MAX_NUM_SIMULATIONS_TIP"), 1, ABS_MAX_NUM_SIMULATIONS, true);
        exploreExploitRatioField_ =
            new NumberInput(GameContext.getLabel("EXPLORE_EXPLOIT_RATIO"), monteCarloOptions_.getExploreExploitRatio(),
                            GameContext.getLabel("EXPLORE_EXPLOIT_RATIO_TIP"), 0, ABS_MAX_EE_RATIO, false);
        randomLookHeadField_ =
            new NumberInput(GameContext.getLabel("RANDOM_LOOK_AHEAD"), monteCarloOptions_.getRandomLookAhead(),
                            GameContext.getLabel("RANDOM_LOOK_AHEAD_TIP"), 1, 1000, true);
        add( maxSimulationsField_ );
        add( exploreExploitRatioField_ );
        add( randomLookHeadField_ );
    }

}
