// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.configuration;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.ui.dialogs.searchoptions.SearchOptionsPanel;
import com.barrybecker4.game.twoplayer.comparison.model.config.SearchOptionsConfig;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.components.TextInput;
import com.barrybecker4.ui.dialogs.OptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Allow for editing player search options.
 *
 * @author Barry Becker
 */
public class SearchOptionsDialog extends OptionsDialog {

    private GradientButton okButton;
    private TextInput nameField;
    private SearchOptionsConfig existingOptions;
    private SearchOptionsPanel searchOptionsPanel;


    /** Constructor  */
    public SearchOptionsDialog(Component parent) {
        super( parent );
        showContent();
    }

    /**
     * Constructor.
     * Use this version if we want to initialize with existing options
     */
    public SearchOptionsDialog(Component parent, SearchOptionsConfig options) {
        super( parent );
        existingOptions = options;
        showContent();
    }

    @Override
    public String getTitle() {
        return GameContext.getLabel("EDIT_PLAYER_OPTIONS");
    }

    public SearchOptionsConfig getSearchOptionsConfig() {
        return new SearchOptionsConfig(nameField.getValue(), searchOptionsPanel.getOptions());
    }

    @Override
    protected JComponent createDialogContent() {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );

        // all defaults initially.
        String title = (existingOptions != null) ? existingOptions.getName() : "";
        SearchOptions searchOptions =
                (existingOptions != null) ? existingOptions.getSearchOptions(): new SearchOptions();

        nameField = new TextInput("Configuration Name:", title, 30);
        nameField.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

        searchOptionsPanel = new SearchOptionsPanel(searchOptions);

        mainPanel.add(nameField, BorderLayout.NORTH);
        mainPanel.add( searchOptionsPanel, BorderLayout.CENTER);
        mainPanel.add(createButtonsPanel(), BorderLayout.SOUTH);
        return mainPanel;
    }

    @Override
    protected JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        okButton = new GradientButton();
        initBottomButton(okButton, GameContext.getLabel("OK"), GameContext.getLabel("ACCEPT_PLAYER_OPTIONS") );
        initBottomButton(cancelButton, GameContext.getLabel("CANCEL"), GameContext.getLabel("CANCEL_EDITS") );

        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);

        return buttonsPanel;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        super.actionPerformed(e);
        Object source = e.getSource();
        if ( source == okButton) {
            searchOptionsPanel.ok();
            dispose();
        }
    }
}

