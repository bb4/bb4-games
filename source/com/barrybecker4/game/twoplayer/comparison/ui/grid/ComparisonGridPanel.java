// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.grid;

import com.barrybecker4.common.util.FileUtil;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.plugin.PluginManager;
import com.barrybecker4.game.common.ui.menu.GameMenuListener;
import com.barrybecker4.game.common.ui.panel.IGamePanel;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPanel;
import com.barrybecker4.game.twoplayer.comparison.execution.PerformanceRunner;
import com.barrybecker4.game.twoplayer.comparison.execution.PerformanceRunnerListener;
import com.barrybecker4.game.twoplayer.comparison.model.ResultsModel;
import com.barrybecker4.game.twoplayer.comparison.model.SearchOptionsConfigList;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.file.DirFileFilter;
import com.barrybecker4.ui.file.FileChooserUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


/**
 * Show grid of game trials with run button at top.
 *
 * @author Barry Becker
 */
public final class ComparisonGridPanel
             extends JPanel
             implements ActionListener, GameMenuListener, PerformanceRunnerListener {

    private static final String DEFAULT_SAVE_LOCATION = FileUtil.getHomeDir() + "temp/comparisonResults/";  // NON-NLS
    private GradientButton runButton_;
    private GradientButton resultsLocationButton_;
    private GradientButton saveResultsButton_;
    private JLabel resultFolderLabel_;
    private ComparisonGrid grid_;
    private JScrollPane scrollPane;
    private SearchOptionsConfigList optionsList;
    private String gameName;
    private ResultsModel finalResultsModel;

    /**
     * constructor - create the tree dialog.
     */
    public ComparisonGridPanel() {

        grid_ = ComparisonGrid.createInstance(new SearchOptionsConfigList());
        init();
    }

    public void setOptionsList(SearchOptionsConfigList optionsList) {

        grid_ = ComparisonGrid.createInstance(optionsList);
        this.optionsList = optionsList;
        runButton_.setEnabled(optionsList.size() > 0);
        scrollPane.setViewportView(grid_.getTable());
    }

    private void init() {

        this.setLayout(new BorderLayout());

        add(createTopControls(), BorderLayout.NORTH);

        scrollPane = createGridPane();
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createTopControls()    {
        JPanel topControlsPanel = new JPanel(new BorderLayout());
        runButton_ = new GradientButton("Run Comparisons");
        runButton_.addActionListener(this);
        runButton_.setEnabled(false);

        topControlsPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        topControlsPanel.add(runButton_, BorderLayout.WEST);

        saveResultsButton_ = new GradientButton("Save results to");
        resultFolderLabel_ = new JLabel(DEFAULT_SAVE_LOCATION);

        saveResultsButton_.addActionListener(this);
        saveResultsButton_.setEnabled(false);

        resultsLocationButton_ = new GradientButton("...");
        resultsLocationButton_.addActionListener(this);

        JPanel saveLocationPanel = new JPanel();
        saveLocationPanel.add(saveResultsButton_);
        saveLocationPanel.add(resultFolderLabel_);
        saveLocationPanel.add(resultsLocationButton_);
        topControlsPanel.add(saveLocationPanel, BorderLayout.CENTER);

        return topControlsPanel;
    }

    private JScrollPane createGridPane() {
        JScrollPane scrollPane = new JScrollPane(grid_.getTable());
        scrollPane.setPreferredSize(new Dimension(360, 120));
        scrollPane.setBorder(
                BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
                scrollPane.getBorder()));
        return scrollPane;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();

        if (source == runButton_) {
            final IGamePanel gamePanel = createGamePanel(gameName);

            PerformanceRunner runner =
                new PerformanceRunner((TwoPlayerPanel)gamePanel, optionsList, this);

            // when done, performanceRunsDone will be called.
            runner.doComparisonRuns();
        }
        else if (source == resultsLocationButton_) {
            updateSaveToLocation();
        }
        else if (source == saveResultsButton_) {
            assert finalResultsModel != null;
            finalResultsModel.saveModel(resultFolderLabel_.getText());

            String message = "Results written to " + resultFolderLabel_.getText();
            JOptionPane.showMessageDialog(null, message);
        }
    }

    private void updateSaveToLocation() {
        JFileChooser chooser = FileChooserUtil.getFileChooser(new DirFileFilter());

        File dir =  new File(resultFolderLabel_.getText());
        chooser.setCurrentDirectory(dir);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.showDialog(null, "Select this location");
        File file = chooser.getSelectedFile();
        if (file != null)  {
            resultFolderLabel_.setText(file.getAbsolutePath());
        }
    }

    @Override
    public void gameChanged(String gameName) {
        this.gameName = gameName;
    }

    public IGamePanel createGamePanel(String gameName) {

        // this will load the resources for the specified game.
        GameContext.loadResources(gameName);
        grid_.setGameName(gameName);
        repaint();

        return PluginManager.getInstance().getPlugin(gameName).getPanelInstance();
    }

    @Override
    public void paint(Graphics g) {

        grid_.updateRowHeight(scrollPane.getHeight());
        super.paint(g);
    }

    /**
     * Update the grid with the results and allow the user to save the results to the filesystem.
     * @param model results model
     */
    @Override
    public void performanceRunsDone(ResultsModel model) {
        finalResultsModel = model;
        grid_.updateWithResults(finalResultsModel);
        saveResultsButton_.setEnabled(true);
    }
}

