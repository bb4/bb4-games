/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.online.ui;

import com.barrybecker4.game.common.GameViewModel;
import com.barrybecker4.game.common.ui.dialogs.GameOptionsDialog;
import com.barrybecker4.game.common.ui.dialogs.GameStartListener;
import com.barrybecker4.game.multiplayer.common.online.ui.MultiPlayerOnlineGameTablesTable;
import com.barrybecker4.game.multiplayer.common.online.ui.MultiPlayerOnlineManagerPanel;
import com.barrybecker4.game.multiplayer.trivial.ui.dialog.TrivialOptionsDialog;
import com.barrybecker4.ui.table.TableButtonListener;

/**
 * Manage online player games.
 * Player can join no more than one table at a time.
 *
 * @author Barry Becker
 */
public class OnlineTrivialManagerPanel extends MultiPlayerOnlineManagerPanel {

    private static final long serialVersionUID = 1;

    public OnlineTrivialManagerPanel(GameViewModel viewer, GameStartListener dlg) {
        super(viewer, dlg);
    }

    @Override
    protected MultiPlayerOnlineGameTablesTable createOnlineGamesTable(TableButtonListener tableButtonListener) {
        return new TrivialOnlineGameTablesTable(tableButtonListener);
    }

    /**
     * You are free to set your own options for the table that you are creating.
     */
    @Override
    protected GameOptionsDialog createNewGameTableDialog() {
        return new TrivialOptionsDialog(null, controller_);
    }

}
