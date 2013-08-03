/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameViewModel;
import com.barrybecker4.game.twoplayer.common.ui.dialogs.TwoPlayerNewGameDialog;

import java.awt.*;
import java.awt.event.ActionListener;

/**
 *  Any special options that are needed for Chess
 *
 *  @author Barry Becker
 */
public class ChessNewGameDialog extends TwoPlayerNewGameDialog
                                implements ActionListener {

    /** constructor  */
    public ChessNewGameDialog(Component parent, GameViewModel viewer ) {
        super(parent, viewer );
    }

    @Override
    public final String getTitle() {
        return GameContext.getLabel("CHESS_OPTIONS");
    }
}

