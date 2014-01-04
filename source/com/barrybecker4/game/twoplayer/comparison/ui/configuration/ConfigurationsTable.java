// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.configuration;

import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.comparison.model.config.SearchOptionsConfig;
import com.barrybecker4.game.twoplayer.comparison.model.config.SearchOptionsConfigList;
import com.barrybecker4.ui.table.BasicCellRenderer;
import com.barrybecker4.ui.table.BasicTableModel;
import com.barrybecker4.ui.table.TableBase;
import com.barrybecker4.ui.table.TableColumnMeta;

import javax.swing.table.TableModel;


/**
 * Contains a list of search option configurations .
 *
 * @author Barry Becker
 */
class ConfigurationsTable extends TableBase {

    private static final int NAME_INDEX = 0;
    private static final int ALGORITHM_INDEX = 1;
    private static final int BRUTE_OPTIONS_INDEX = 2;
    private static final int BEST_MOVE_OPTIONS_INDEX = 3;
    private static final int MONTE_CARLO_OPTIONS_INDEX = 4;
    // I don't want this extra column, but without it the object is not passed through
    private static final int INSTANCE_INDEX = 5;

    private static final String NAME = "Name";
    private static final String ALGORITHM = "Search Algorithm";
    private static final String BRUTE_OPTIONS = "Brute Force Options";
    private static final String BEST_MOVE_OPTIONS = "Best Move Options";
    private static final String MONTE_CARLO_OPTIONS = "Monte Carlo Options";
    private static final String INST = "";


    private static final String[] columnNames_ =  {
            NAME, ALGORITHM, BRUTE_OPTIONS, BEST_MOVE_OPTIONS, MONTE_CARLO_OPTIONS, INST
    };

    private static final String NAME_TIP = "Some descriptive name";
    private static final String ALGORITHM_TIP = "Name of the search algorithm";
    private static final String BRUTE_OPTIONS_TIP = "Brute force search options";
    private static final String BEST_MOVE_OPTIONS_TIP =
            "Options for how many of the better moves to select during search";
    private static final String MONTE_CARLO_OPTIONS_TIP =
            "Options for when the search algorithm involves a stochastic process";

    private static final String[] columnTips_ =  {
            NAME_TIP, ALGORITHM_TIP, BRUTE_OPTIONS_TIP, BEST_MOVE_OPTIONS_TIP, MONTE_CARLO_OPTIONS_TIP, ""};

    private static final int NUM_COLS = columnNames_.length;

    /**
     * Constructor
     * @param optionsList to initialize the rows in the table with. May be null.
     */
    public ConfigurationsTable(SearchOptionsConfigList optionsList)  {
        super(optionsList, columnNames_);
    }

    @Override
    protected void updateColumnMeta(TableColumnMeta[] columnMeta) {

        columnMeta[NAME_INDEX].setPreferredWidth(210);
        columnMeta[NAME_INDEX].setMaxWidth(310);
        columnMeta[NAME_INDEX].setCellRenderer(new BasicCellRenderer());
        columnMeta[ALGORITHM_INDEX].setPreferredWidth(110);
        columnMeta[ALGORITHM_INDEX].setMaxWidth(210);

        columnMeta[BRUTE_OPTIONS_INDEX].setCellRenderer(new BasicCellRenderer());
        columnMeta[BEST_MOVE_OPTIONS_INDEX].setCellRenderer(new BasicCellRenderer());
        columnMeta[MONTE_CARLO_OPTIONS_INDEX].setCellRenderer(new BasicCellRenderer());

        for (int i = 0; i < getNumColumns(); i++) {
            columnMeta[i].setTooltip(columnTips_[i]);
        }
    }

    @Override
    protected TableModel createTableModel(String[] columnNames) {
        return  new BasicTableModel(columnNames_, 0, false);
    }

    public void removeRow(int rowIndex) {
         getPlayerModel().removeRow(rowIndex);
    }

    /**
     * @return the search options represented by rows in the table
     */
    public SearchOptionsConfigList getSearchOptions() {

        TableModel model = getPlayerModel();
        int nRows = model.getRowCount();
        SearchOptionsConfigList searchOptions = new SearchOptionsConfigList(nRows);

        for (int i = 0; i < nRows; i++) {
            String options = (String)model.getValueAt(i, INSTANCE_INDEX-1);
        }
        for (int i = 0; i < nRows; i++) {
            SearchOptionsConfig options = ((SearchOptionsConfig) model.getValueAt(i, INSTANCE_INDEX));
            searchOptions.add( options );
        }

        return searchOptions;
    }

    private BasicTableModel getPlayerModel() {
        return (BasicTableModel)getModel();
    }

    /**
     * add a row based on a player object
     * @param opts to add
     */
    @Override
    public void addRow(Object opts) {

        SearchOptionsConfig optionsConfig = (SearchOptionsConfig) opts;
        SearchOptions sOptions = optionsConfig.getSearchOptions();

        Object d[] = new Object[NUM_COLS + 1];

        d[NAME_INDEX] = optionsConfig.getName();
        d[ALGORITHM_INDEX] = sOptions.getSearchStrategyMethod();
        d[BRUTE_OPTIONS_INDEX ] = sOptions.getBruteSearchOptions().toString();
        d[BEST_MOVE_OPTIONS_INDEX ] = sOptions.getBestMovesSearchOptions().toString();
        d[MONTE_CARLO_OPTIONS_INDEX ] = sOptions.getMonteCarloSearchOptions().toString();
        d[INSTANCE_INDEX] = optionsConfig;

        getPlayerModel().addRow(d);
    }

    /**
     * Edit a row based on a player object
     * @param optionsConfig to update
     */
    public void updateRow(int row, SearchOptionsConfig optionsConfig) {

        SearchOptions sOptions = optionsConfig.getSearchOptions();

        Object d[] = new Object[NUM_COLS + 1];

        d[NAME_INDEX] = optionsConfig.getName();
        d[ALGORITHM_INDEX] = sOptions.getSearchStrategyMethod();
        d[BRUTE_OPTIONS_INDEX ] = sOptions.getBruteSearchOptions().toString();
        d[BEST_MOVE_OPTIONS_INDEX ] = sOptions.getBestMovesSearchOptions().toString();
        d[MONTE_CARLO_OPTIONS_INDEX ] = sOptions.getMonteCarloSearchOptions().toString();
        d[INSTANCE_INDEX] = optionsConfig;

        getPlayerModel().removeRow(row);
        getPlayerModel().insertRow(row, d);
    }

}
