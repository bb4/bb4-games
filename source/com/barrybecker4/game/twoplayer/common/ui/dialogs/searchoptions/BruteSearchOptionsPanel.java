/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.dialogs.searchoptions;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.common.search.options.BruteSearchOptions;
import com.barrybecker4.ui.components.NumberInput;

import javax.swing.*;

/**
 * Panel that shows the options for search strategies that use brute force (like minimax and derivatives).
 *
 * @author Barry Becker
 */
public class BruteSearchOptionsPanel extends JPanel {

    BruteSearchOptions bruteOptions_;

    private NumberInput lookAheadField_;
    private JCheckBox alphabetaCheckbox_;
    private JCheckBox quiescenceCheckbox_;


    private static final int MAX_ALLOWED_LOOKAHEAD = 20;

    /**
     * Constructor
     */
    public BruteSearchOptionsPanel(BruteSearchOptions sOptions) {
        bruteOptions_ = sOptions;
        initialize();
    }

    /**
     * update the options based on the user selections.
     */
    public void updateBruteOptionsOptions() {

        bruteOptions_.setAlphaBeta(alphabetaCheckbox_.isSelected());
        bruteOptions_.setQuiescence(quiescenceCheckbox_.isSelected());
        bruteOptions_.setLookAhead(lookAheadField_.getIntValue());
    }

    /**
     * create the panel with ui element for setting the options
     */
    protected void initialize() {

        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        // look ahead
        lookAheadField_ =
                new NumberInput(GameContext.getLabel("MOVES_TO_LOOKAHEAD"), bruteOptions_.getLookAhead(),
                                GameContext.getLabel("MOVES_TO_LOOKAHEAD_TIP"), 1, MAX_ALLOWED_LOOKAHEAD, true);

        this.add( lookAheadField_ );

        // alpha-beta pruning option
        alphabetaCheckbox_ =
                new JCheckBox( GameContext.getLabel("USE_PRUNING"), bruteOptions_.getAlphaBeta());
        alphabetaCheckbox_.setToolTipText( GameContext.getLabel("USE_PRUNING_TIP") );
        this.add( alphabetaCheckbox_ );

        // show profile info option
        quiescenceCheckbox_ = new JCheckBox( GameContext.getLabel("USE_QUIESCENCE"), bruteOptions_.getQuiescence() );
        quiescenceCheckbox_.setToolTipText( GameContext.getLabel("USE_QUIESCENCE_TIP") );
        this.add( quiescenceCheckbox_ );
    }
}
