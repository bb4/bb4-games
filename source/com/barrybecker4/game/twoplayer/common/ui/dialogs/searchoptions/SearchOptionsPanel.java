/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.dialogs.searchoptions;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.common.search.SearchAttribute;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.barrybecker4.ui.components.RadioButtonPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * The algorithm tab - allows selecting two player search options.
 *
 * @author Barry Becker
 */
public class SearchOptionsPanel extends JPanel
                         implements ItemListener {

    /** search algorithm radio button group */
    private JRadioButton[] strategyButtons_;
    private SearchStrategyType algorithm_;

    private BruteSearchOptionsPanel bruteOptionsPanel_;
    private MonteCarloOptionsPanel monteCarloOptionsPanel_;
    private BestMovesOptionsPanel bestMovesOptionsPanel_;

    private SearchOptions searchOptions_;
    private boolean initialized = false;

    /** constructor */
    public SearchOptionsPanel(SearchOptions options) {

        searchOptions_ = options;
        setName(GameContext.getLabel("ALGORITHM"));
        initialize();
    }

    protected void initialize() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                GameContext.getLabel("PERFORMANCE_OPTIONS")));

        JLabel label = new JLabel( "     " );  // initial space
        label.setAlignmentX( Component.LEFT_ALIGNMENT );
        add(label);

        // radio buttons for which search algorithm to use
        JLabel algorithmLabel = new JLabel( GameContext.getLabel("SELECT_SEARCH_ALGORITHM") );
        algorithmLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
        add(algorithmLabel);

        add(createStrategyRadioButtons());

        bruteOptionsPanel_ = new BruteSearchOptionsPanel(searchOptions_.getBruteSearchOptions());
        monteCarloOptionsPanel_ = new MonteCarloOptionsPanel(searchOptions_.getMonteCarloSearchOptions());
        bestMovesOptionsPanel_ = new BestMovesOptionsPanel(searchOptions_.getBestMovesSearchOptions());

        add(bruteOptionsPanel_);
        add(monteCarloOptionsPanel_);
        add(bestMovesOptionsPanel_);
        showOptionsBasedOnAlgorithm();
        initialized = true;
    }


    public SearchOptions getOptions() {
        bruteOptionsPanel_.updateBruteOptionsOptions();
        monteCarloOptionsPanel_.updateMonteCarloOptionsOptions();
        bestMovesOptionsPanel_.updateBestMovesOptions();

        searchOptions_.setSearchStrategyMethod(getSelectedStrategy(searchOptions_.getSearchStrategyMethod()));
        //options.setShowGameTree(gameTreeCheckbox_.isSelected() );
        return searchOptions_;
    }

    /**
     * @return Radio buttons for selecting the strategy.
     */
    private JPanel createStrategyRadioButtons() {
        JPanel p = new JPanel();
        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );


        ButtonGroup buttonGroup = new ButtonGroup();
        int numStrategies = SearchStrategyType.values().length;
        strategyButtons_ = new JRadioButton[numStrategies];

        algorithm_ = searchOptions_.getSearchStrategyMethod();
        for (int i=0; i<numStrategies; i++) {
            SearchStrategyType alg = SearchStrategyType.values()[i];
            strategyButtons_[i] = new JRadioButton(alg.getLabel());
            strategyButtons_[i].setToolTipText(alg.getTooltip());
            strategyButtons_[i].addItemListener(this);
            p.add( new RadioButtonPanel( strategyButtons_[i], buttonGroup, algorithm_ == alg ));
        }
        return p;
    }

    /**
     * Invoked when a radio button has changed its selection state.
     */
    @Override
    public void itemStateChanged( ItemEvent e ) {
        //super.itemStateChanged(e);
        algorithm_ = getSelectedStrategy(searchOptions_.getSearchStrategyMethod());
        if (initialized)   {
            showOptionsBasedOnAlgorithm();
        }
    }

    private void showOptionsBasedOnAlgorithm() {

        boolean bruteForceStrategy = algorithm_.hasAttribute(SearchAttribute.BRUTE_FORCE);
        bruteOptionsPanel_.setVisible(bruteForceStrategy);
        monteCarloOptionsPanel_.setVisible(!bruteForceStrategy);
    }

    private SearchStrategyType getSelectedStrategy(SearchStrategyType defaultStrategy) {
        int numStrategies = SearchStrategyType.values().length;
        for (int i=0; i<numStrategies; i++) {
            if (strategyButtons_[i].isSelected()) {
                return SearchStrategyType.values()[i];
            }
        }
        return defaultStrategy;
    }

    public void ok() {
        getOptions();
    }

}

