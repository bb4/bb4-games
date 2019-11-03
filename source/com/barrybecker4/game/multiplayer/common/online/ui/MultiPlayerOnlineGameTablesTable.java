/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.online.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.online.OnlineGameTable;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.multiplayer.common.MultiGameOptions;
import com.barrybecker4.ui.table.BasicTableModel;
import com.barrybecker4.ui.table.TableBase;
import com.barrybecker4.ui.table.TableButton;
import com.barrybecker4.ui.table.TableButtonListener;
import com.barrybecker4.ui.table.TableColumnMeta;

import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A table that has a row for each virtual online game table.
 *
 * @author Barry Becker
 */
public abstract class MultiPlayerOnlineGameTablesTable extends TableBase  {

    protected static final int JOIN_INDEX = 0;
    protected static final int NUM_PLAYERS_INDEX = 1;
    protected static final int PLAYER_NAMES_INDEX = 2;
    protected static final int NUM_BASE_COLUMNS = 3;

    protected static final String JOIN = GameContext.getLabel("ACTION");
    protected static final String MIN_NUM_PLAYERS = GameContext.getLabel("MIN_NUM_PLAYERS");
    protected static final String PLAYER_NAMES = GameContext.getLabel("PLAYER_NAMES");

    private static final String JOIN_TIP = GameContext.getLabel("ACTION_TIP");
    private static final String MIN_NUM_PLAYERS_TIP = GameContext.getLabel("MIN_NUM_PLAYERS_TIP");
    protected static final String PLAYER_NAMES_TIP = GameContext.getLabel("PLAYER_NAMES_TIP");

    private static final String[] COLUMN_NAMES = {JOIN, MIN_NUM_PLAYERS, PLAYER_NAMES};

    private OnlineGameTable selectedTable_;
    private List<OnlineGameTable> tableList_;
    private static int counter_;

    private TableButtonListener tableButtonListener_;


    /**
     * constructor
     * @param tableButtonListener called when join button clicked.
     */
    public MultiPlayerOnlineGameTablesTable(TableButtonListener tableButtonListener) {
        this(COLUMN_NAMES, tableButtonListener);
    }

    protected MultiPlayerOnlineGameTablesTable(String[] colNames, TableButtonListener tableButtonListener) {

        initColumnMeta(colNames);

        assert(tableButtonListener != null);
        tableButtonListener_ = tableButtonListener;
        selectedTable_ = null;
        tableList_ = new ArrayList<>();

        initializeTable(null);
    }

    /**
     * init the table of tables.
     */
    @Override
    public void updateColumnMeta(TableColumnMeta[] columnMeta) {

        columnMeta[NUM_PLAYERS_INDEX].setTooltip(MIN_NUM_PLAYERS_TIP);

        // more space needed for the names list.
        columnMeta[PLAYER_NAMES_INDEX].setPreferredWidth(200);
        columnMeta[PLAYER_NAMES_INDEX].setTooltip(PLAYER_NAMES_TIP);

        TableColumnMeta actionCol = columnMeta[JOIN_INDEX];
        actionCol.setTooltip("Select which table you want to joint");

        TableButton joinCellEditor = new TableButton(GameContext.getLabel("JOIN"), "id");
        joinCellEditor.addTableButtonListener(tableButtonListener_);
        joinCellEditor.setToolTipText(JOIN_TIP);
        actionCol.setCellRenderer(joinCellEditor);
        actionCol.setCellEditor(joinCellEditor);
        actionCol.setPreferredWidth(70);
    }

    @Override
    public TableModel createTableModel(String[] columnNames) {
        return new BasicTableModel(columnNames, 0, true);
    }

    public OnlineGameTable getGameTable(int i) {
        return tableList_.get(i);
    }

    /**
     * @return the table that the player has chosen to sit at if any (at most 1.) return null is not sitting.
     */
    public OnlineGameTable getSelectedTable() {
        return selectedTable_;
    }

    public BasicTableModel getPlayerModel() {
        return (BasicTableModel)getModel();
    }

    public void removeRow(OnlineGameTable table) {

        tableList_.remove(table);
        removeRowFromModel(table);
        //GameContext.log(0, "selected row =" + (ct-1)
        // + " found="+ found +  "  modelRows="+ model.getRowCount() +"   tables="+ model.getDataVector());
    }

    private void removeRowFromModel(OnlineGameTable table) {
        boolean found = false;
        BasicTableModel model = getPlayerModel();
        int ct = 0;
        while (ct < model.getRowCount() && !found)  {
            String names = (String) model.getValueAt(ct, PLAYER_NAMES_INDEX);
            if (names.equals(table.getPlayerNames())) {
                model.removeRow(ct);
                found = true;
            }
            ct++;
        }
    }

    /**
     * clear out all the rows in the table.
     */
    public void removeAllRows() {

        BasicTableModel m = this.getPlayerModel();
        for (int i = m.getRowCount() -1; i >= 0; i--) {
            m.removeRow(i);
        }
        tableList_.clear();
    }

    /**
     * @param initialPlayerName starting name
     * @param options
     * @return the new online table to add as a new row.
     */
    public abstract OnlineGameTable createOnlineTable(String initialPlayerName, MultiGameOptions options);

    public abstract Player createPlayerForName(String playerName);

    @Override
    public void addRow(Object onlineTable) {
        addRow((OnlineGameTable) onlineTable, true);
    }

    /**
     * add a row based on a player object
     * @param onlineTable to add
     * @param localPlayerAtTable you cannot join a table you are already at.
     */
    public void addRow(OnlineGameTable onlineTable, boolean localPlayerAtTable) {

        getPlayerModel().addRow(getRowObject(onlineTable, localPlayerAtTable));
        tableList_.add(onlineTable);
        selectedTable_ = onlineTable;
    }

    /**
     * @return  the object array to create a row from.
     */
    protected abstract Object[] getRowObject(OnlineGameTable onlineTable, boolean localPlayerAtTable);

    /**
     * add another row to the end of the table.
     */
    public void addRow(String playersName, MultiGameOptions options) {
        OnlineGameTable onlineTable = createOnlineTable(playersName, options);
        addRow(onlineTable, true);
    }


    protected static synchronized String getUniqueName() {
          return "Table " + counter_++;
    }

     protected static Color getRandomColor() {

        int r = GameContext.random().nextInt(256);
        return new Color(r, 255 - r, GameContext.random().nextInt(256));
    }

    public String toString() {
        return tableList_.toString();
    }

}
