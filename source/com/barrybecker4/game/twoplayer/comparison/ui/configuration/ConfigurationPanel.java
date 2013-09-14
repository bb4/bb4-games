// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.configuration;

import com.barrybecker4.game.twoplayer.comparison.model.SearchOptionsConfig;
import com.barrybecker4.game.twoplayer.comparison.model.SearchOptionsConfigList;
import com.barrybecker4.game.twoplayer.comparison.model.data.ConfigurationListEnum;
import com.barrybecker4.ui.components.GradientButton;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Allow the user to maintain their current orders and add new ones.
 *
 * @author Barry Becker
 */
public final class ConfigurationPanel extends JPanel
                              implements ActionListener, ListSelectionListener {

    private static final ConfigurationListEnum DEFAULT_CONFIGURATIONS = ConfigurationListEnum.DEFAULT_CONFIGS;

    private GradientButton addConfigButton_;
    private GradientButton editConfigButton_;
    private GradientButton removeConfigButton_;

    private JComboBox configDropList;

    private ConfigurationsTable configTable_;
    private JScrollPane scrollPane_;


    /**
     * constructor - create the tree dialog.
     */
    public ConfigurationPanel() {
        init();
    }

    private void init() {

        this.setLayout(new BorderLayout());

        JPanel addremoveButtonsPanel = createButtonsPanel();

        JPanel titlePanel = new JPanel(new BorderLayout());
                titlePanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        JPanel dropListElement = createDropListElement();

        titlePanel.add(dropListElement, BorderLayout.WEST);
        titlePanel.add(addremoveButtonsPanel, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);

        scrollPane_ = new JScrollPane();

        initializeConfigTable();

        scrollPane_.setPreferredSize(new Dimension(360,120));
        scrollPane_.setBorder(
                BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
                scrollPane_.getBorder()));
        add(scrollPane_, BorderLayout.CENTER);
    }

    private JPanel createDropListElement() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel instr = new JLabel(" Select a set of configurations to test:  ");
        configDropList = createConfigDropDown();
        panel.add(instr, BorderLayout.CENTER);
        panel.add(configDropList, BorderLayout.EAST);
        return panel;
    }

    private JComboBox createConfigDropDown() {

        ComboBoxModel<ConfigurationListEnum> model =
                new DefaultComboBoxModel<ConfigurationListEnum>(ConfigurationListEnum.values());
        model.setSelectedItem(DEFAULT_CONFIGURATIONS);
        JComboBox dropList = new JComboBox<ConfigurationListEnum>(model);
        dropList.addActionListener(this);
        return dropList;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel();

        addConfigButton_ = new GradientButton("New");
        addConfigButton_.setToolTipText("add a new entry in the table");
        addConfigButton_.addActionListener(this);
        panel.add(addConfigButton_, BorderLayout.WEST);

        editConfigButton_ = new GradientButton("Edit");
        editConfigButton_.setToolTipText("edit an existing entry in the table");
        editConfigButton_.addActionListener(this);
        editConfigButton_.setEnabled(false);
        panel.add(editConfigButton_, BorderLayout.CENTER);

        removeConfigButton_ = new GradientButton("Remove");
        removeConfigButton_.setToolTipText("remove an entry from the table");
        removeConfigButton_.addActionListener(this);
        removeConfigButton_.setEnabled(false);
        panel.add(removeConfigButton_, BorderLayout.EAST);

        return panel;
    }

    /**
     * @return  the options list in the table
     */
    public SearchOptionsConfigList getConfigurations() {
        return configTable_.getSearchOptions();
    }

    /**
     * Handle add/edit/remove button click, or droplist selection.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();

        if (source == addConfigButton_) {
            addConfiguration();
        }
        else if (source == editConfigButton_) {

            editConfiguration(configTable_.getSelectedRow());
        }
        else if (source == removeConfigButton_) {

            configTable_.removeRow(configTable_.getSelectedRow());
            removeConfigButton_.setEnabled(false);
        }
        else if (source == configDropList) {
             initializeConfigTable();
        }
        else {
            assert false : "Unexpected source =" + source;
        }
    }

    /**
     * Create the table based on the current config droplist selection and set the new table in the scrollpane view
     */
    private void initializeConfigTable() {

        SearchOptionsConfigList configList =
                ((ConfigurationListEnum)configDropList.getSelectedItem()).getConfigList();
        configTable_ = new ConfigurationsTable(configList);
        configTable_.addListSelectionListener(this);
        scrollPane_.setViewportView(configTable_.getTable());
    }

    /**
     * add another row to the end of the table.
     */
    private void addConfiguration()  {

        SearchOptionsDialog optionsDialog = new SearchOptionsDialog(this);
        boolean canceled = optionsDialog.showDialog();

        if ( !canceled ) {
            SearchOptionsConfig options = optionsDialog.getSearchOptionsConfig();
            if (options != null)
                configTable_.addRow(options);
        }
    }

    /**
     * add another row to the end of the table.
     */
    private void editConfiguration(int row)  {

        SearchOptionsConfig initOptions = getConfigurations().get(row);
        SearchOptionsDialog optionsDialog = new SearchOptionsDialog(this, initOptions);
        boolean canceled = optionsDialog.showDialog();

        if ( !canceled ) {
            SearchOptionsConfig options = optionsDialog.getSearchOptionsConfig();
            if (options != null) {
                configTable_.updateRow(row, options);
            }
        }
    }

    /** called when one of the rows in the grid is selected */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        editConfigButton_.setEnabled(true);
        removeConfigButton_.setEnabled(true);
    }
}

