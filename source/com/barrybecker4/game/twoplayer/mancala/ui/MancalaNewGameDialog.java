/** Copyright by Barry G. Becker, 20014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.ui;

import com.barrybecker4.game.common.GameViewModel;
import com.barrybecker4.game.twoplayer.common.ui.dialogs.TwoPlayerNewGameDialog;

import java.awt.Component;
import java.awt.event.ActionListener;

public class MancalaNewGameDialog extends TwoPlayerNewGameDialog
                                implements ActionListener {

    public MancalaNewGameDialog(Component parent, GameViewModel viewer) {
        super( parent, viewer );
    }

}

