/** Copyright by Barry G. Becker, 2000-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.ui.table.BasicTableModel;
import com.barrybecker4.ui.table.ColorCellEditor;
import com.barrybecker4.ui.table.ColorCellRenderer;
import com.barrybecker4.ui.table.TableBase;
import com.barrybecker4.ui.table.TableColumnMeta;
import scala.collection.JavaConverters;
import javax.swing.table.TableModel;


/**
 * Shows a list of the remaining players stats at the end of the game.
 * None of the cells are editable.
 *
 * @author Barry Becker
 */
public abstract class SummaryTable extends TableBase  {

    protected static final int NAME_INDEX = 0;
    protected static final int COLOR_INDEX = 1;

    protected static final String NAME = GameContext.getLabel("NAME");
    protected static final String COLOR = GameContext.getLabel("COLOR");


    /**
     * constructor
     * @param players to initialize the rows in the table with.
     */
    protected SummaryTable(PlayerList players, String[] columnNames) {
        super(JavaConverters.asScalaIteratorConverter(players.iterator()).asScala().toSeq(), columnNames);
    }

    @Override
    public void updateColumnMeta(TableColumnMeta[] columnMeta) {

        TableColumnMeta colorMeta = columnMeta[COLOR_INDEX];
        colorMeta.setCellRenderer(new ColorCellRenderer());
        colorMeta.setCellEditor(new ColorCellEditor(GameContext.getLabel("SELECT_PLAYER_COLOR")));
        colorMeta.setPreferredWidth(25);
        colorMeta.setMinWidth(20);
        colorMeta.setMaxWidth(25);

        columnMeta[NAME_INDEX].setPreferredWidth(100);
    }

    @Override
    public TableModel createTableModel(String[] columnNames)  {
        return new BasicTableModel(columnNames, 0, false);
    }

    protected BasicTableModel getPlayerModel()
    {
        return (BasicTableModel)table().getModel();
    }
}
