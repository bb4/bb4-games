/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.hex.ui;

import com.barrybecker4.game.common.GameViewModel;
import com.barrybecker4.game.twoplayer.common.ui.dialogs.TwoPlayerNewGameDialog;
import com.barrybecker4.game.twoplayer.gomoku.ui.GoMokuNewGameDialog;

import java.awt.*;

class HexNewGameDialog extends TwoPlayerNewGameDialog {

    public HexNewGameDialog(Component parent, GameViewModel viewer) {
        super( parent, viewer );
    }
}

