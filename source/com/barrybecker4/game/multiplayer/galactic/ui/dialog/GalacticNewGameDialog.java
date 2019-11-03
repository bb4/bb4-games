/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.dialog;

import com.barrybecker4.game.common.GameViewModel;
import com.barrybecker4.game.common.ui.dialogs.GameOptionsDialog;
import com.barrybecker4.game.multiplayer.common.online.ui.MultiPlayerOnlineGameTablesTable;
import com.barrybecker4.game.multiplayer.common.ui.MultiPlayerNewGameDialog;
import com.barrybecker4.game.multiplayer.common.ui.PlayerTable;

import java.awt.*;

public class GalacticNewGameDialog extends MultiPlayerNewGameDialog {

    public GalacticNewGameDialog( Component parent, GameViewModel viewer ) {
        super( parent, viewer );
    }

    @Override
    protected PlayerTable createPlayerTable() {
        return  new GalacticPlayerTable( controller_.getPlayers());
    }

    protected MultiPlayerOnlineGameTablesTable createOnlineGamesTable(String name) {
        return null;
    }

    protected GameOptionsDialog createNewGameTableDialog() {
        return null;
    }
}


