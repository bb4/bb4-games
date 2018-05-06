/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.ui.table.BasicTableModel;
import com.barrybecker4.ui.table.ColorCellEditor;
import com.barrybecker4.ui.table.ColorCellRenderer;
import com.barrybecker4.ui.table.TableBase;
import com.barrybecker4.ui.table.TableColumnMeta;
import scala.collection.Seq;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


/**
 * PlayerTable contains a list of players.
 * All of the cells are editable.
 * It is initialized with a list of Players and returns a list of Players.
 *
 * @author Barry Becker
 */
public abstract class PlayerTable extends TableBase {

    /** remember the deleted rows, so we can add them back when the user clicks add again  */
    private List<Vector> deletedRows;

    protected static final int NAME_INDEX = 0;
    protected static final int COLOR_INDEX = 1;
    protected static final int HUMAN_INDEX = 2;

    protected static final String NAME = GameContext.getLabel("NAME");
    protected static final String COLOR = GameContext.getLabel("COLOR");
    protected static final String HUMAN = GameContext.getLabel("HUMAN");


    /**
     * constructor
     * @param players to initialize the rows in the table with.
     */
    protected PlayerTable(PlayerList players, String[] columnNames) {
        super((Seq<Object>) players, columnNames);

        deletedRows = new ArrayList<>();
    }

    @Override
    public void updateColumnMeta(TableColumnMeta[] columnMeta) {

        columnMeta[NAME_INDEX].setPreferredWidth(130);
        columnMeta[HUMAN_INDEX].setPreferredWidth(80);
        TableColumnMeta colorColumnMeta = new TableColumnMeta(COLOR, null, 20, 40, 40);
        colorColumnMeta.setCellRenderer(new ColorCellRenderer());
        colorColumnMeta.setCellEditor(new ColorCellEditor(GameContext.getLabel("SELECT_PLAYER_COLOR")));
        columnMeta[COLOR_INDEX] = colorColumnMeta;
    }


    /**
     * @return  the players represented by rows in the table
     */
    public abstract PlayerList getPlayers();


    /**
     * add another row to the end of the table.
     */
    public void addRow() {
        if (deletedRows.isEmpty()) {
            Player player = createPlayer();
            addRow(player);
        }
        else
            getPlayerModel().addRow(deletedRows.remove(0));
    }

    protected abstract Player createPlayer();

    /**
     * remove the selected rows from the table
     */
    public void removeSelectedRows()  {
        int nSelected = table().getSelectedRowCount();
        int[] selectedRows = table().getSelectedRows();
        if (selectedRows.length == table().getRowCount()) {
            JOptionPane.showMessageDialog(null, "You are not allowed to delete all the players!");
            return;
        }
        BasicTableModel model = (BasicTableModel)getModel();
        for (int i=nSelected-1; i>=0; i--) {
            int selRow = selectedRows[i];
            GameContext.log(0, "adding this to delete list:"
                    + model.getDataVector().elementAt(selRow).getClass().getName());
            deletedRows.add((Vector)model.getDataVector().elementAt(selRow));
            model.removeRow(selRow);
        }
    }

    protected BasicTableModel getPlayerModel() {
        return (BasicTableModel) getModel();
    }

    @Override
    public TableModel createTableModel(String[] columnNames) {
        return new BasicTableModel(new String[][]{columnNames}, new Object[]{}, true);
    }

}
