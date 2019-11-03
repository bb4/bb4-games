/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.ui.dialog;

import com.barrybecker4.game.common.GameViewModel;
import com.barrybecker4.game.common.online.ui.OnlineGameManagerPanel;
import com.barrybecker4.game.multiplayer.common.ui.MultiPlayerNewGameDialog;
import com.barrybecker4.game.multiplayer.common.ui.PlayerTable;
import com.barrybecker4.game.multiplayer.trivial.online.ui.OnlineTrivialManagerPanel;

import java.awt.*;

/**
 * @author Barry Becker
 */
public class TrivialNewGameDialog extends MultiPlayerNewGameDialog {

    public TrivialNewGameDialog(Component parent, GameViewModel viewer ) {
        super( parent, viewer );
    }

    @Override
    protected PlayerTable createPlayerTable() {
        return  new TrivialPlayerTable(controller_.getPlayers());
    }

    @Override
    protected OnlineGameManagerPanel createPlayOnlinePanel() {
        return new OnlineTrivialManagerPanel(viewer_, this);
    }

}

