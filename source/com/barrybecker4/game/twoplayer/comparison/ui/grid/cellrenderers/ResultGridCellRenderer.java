// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.grid.cellrenderers;

import com.barrybecker4.game.twoplayer.comparison.model.PerformanceResultsPair;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;


/**
 * Draws all the segmented bars representing the results of two games played between
 * two players. The first bar segment of each bar represents the first player playing first,
 * and the second segment is for when the second player plays first.
 *
 * @author Barry Becker
 */
public class ResultGridCellRenderer extends JPanel
                                    implements TableCellRenderer {

    private static final Color TIME_BAR_COLOR = new Color(170, 140, 100);
    private static final Color NUM_MOVES_BAR_COLOR = new Color(90, 110, 70);

    // the following must sum to one.
    private static final double WON_BAR_PROPORTION = 0.8;
    private static final double TIME_BAR_PROPORTION = 0.1;
    private static final double NUM_MOVES_BAR_PROPORTION = 0.1;

    PerformanceResultsPair perfResults;

    private WonBar wonBar;
    private DualBar timeBar;
    private DualBar numMovesBar;

    /**
     * Constructor
     */
    public ResultGridCellRenderer() {
        setLayout(new GridBagLayout());
        setMinimumSize(new Dimension(100, 100));

        wonBar = new WonBar();
        timeBar = new DualBar(TIME_BAR_COLOR);
        numMovesBar = new DualBar(NUM_MOVES_BAR_COLOR);

        add(wonBar, createConstraints(0, WON_BAR_PROPORTION));
        add(timeBar, createConstraints(1, TIME_BAR_PROPORTION));
        add(numMovesBar, createConstraints(2, NUM_MOVES_BAR_PROPORTION));
    }

    private GridBagConstraints createConstraints(int gridy, double weighty) {
        GridBagConstraints constrs = new GridBagConstraints();
        constrs.gridx = 0;
        constrs.gridy = gridy;
        constrs.weightx = 1.0;
        constrs.weighty = weighty;
        constrs.fill = GridBagConstraints.BOTH;
        return constrs;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table,
                              Object value, boolean isSelected, boolean hasFocus,
                              int row, int col) {
        setData(table.getModel(), row, col);
        return this;
    }


    /** sets the data for bars as well as the labels and tooltip */
    private void setData(TableModel tableModel, int row, int col) {

        perfResults = (PerformanceResultsPair) tableModel.getValueAt(row, col);
        setToolTipText(perfResults.toHtmlString());

        wonBar.setOutcomes(perfResults.getOutcomes(), perfResults.getFinalImages());
        timeBar.setBarSegments(perfResults.getNormalizedTimes(), perfResults.getTimesFormatted());
        numMovesBar.setBarSegments(perfResults.getNormalizedNumMoves(), perfResults.getNumMoves());

        // This does not work for some reason.
        //wonBar.setToolTipText(perfResults.getWinnerText());
        //timeBar.setToolTipText("time");
        //numMovesBar.setToolTipText("numMoves");
    }

}
