// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui;


import com.barrybecker4.game.twoplayer.comparison.ui.configuration.ConfigurationPanel;
import com.barrybecker4.game.twoplayer.comparison.ui.grid.ComparisonGridPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Allows comparing the performance of two player games played against each other
 * with different search strategies.
 * There are two main tabs
 *  1) configuration tab - allows you to define what search strategies will be used
 *  2) comparison table tab - Compares all the search strategies played off against each
 *    other using the specified two player game.
 *
 *  @author Barry Becker
 */
public class GameComparisonPanel extends JPanel
                                 implements ActionListener, ChangeListener {

    private JTabbedPane tabbedPane;
    private ConfigurationPanel comparisonConfigurationPanel;
    private ComparisonGridPanel comparisonGridPanel;

    /**
     * Constructor
     */
    public GameComparisonPanel() {
        initGui();
    }

    /**
     *  UIComponent initialization.
     */
    protected void initGui() {

        this.setLayout( new BorderLayout() );

        tabbedPane = new JTabbedPane();

        comparisonConfigurationPanel = new ConfigurationPanel();
        comparisonConfigurationPanel.setName("Configuration");

        comparisonGridPanel = new ComparisonGridPanel();
        comparisonGridPanel.setName("Comparison");

        tabbedPane.add(comparisonConfigurationPanel);
        tabbedPane.setToolTipTextAt(0,
                "Configure the search strategies to compete against each other");
        tabbedPane.addChangeListener(this);
        tabbedPane.add(comparisonGridPanel);
        tabbedPane.setToolTipTextAt( 0,
                "Shows the results of the two player game competing against itself with specified search strategies");

        this.add(tabbedPane, BorderLayout.CENTER );
    }

    public ComparisonGridPanel getGridPanel() {
        return comparisonGridPanel;
    }

    /**
     * handle button click actions.
     * If you add your own custom buttons, you should override this, but be sure the first line is
     * <P>
     * super.actionPerformed(e);
     */
    @Override
    public void actionPerformed( ActionEvent e ) {
        Object source = e.getSource();
        if ( source == null ) {
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {

        int index = tabbedPane.getSelectedIndex();
        if (index == 1) {
            comparisonGridPanel.setOptionsList(comparisonConfigurationPanel.getConfigurations());
        }
    }
}
