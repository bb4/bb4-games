/* Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.hex.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.GameViewModel;
import com.barrybecker4.game.common.ui.dialogs.NewGameDialog;
import com.barrybecker4.game.common.ui.panel.GameInfoPanel;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerInfoPanel;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPanel;

import java.awt.*;

/**
 *  This class defines the main UI for the TicTacToe game applet or application.
 */
public class HexPanel extends TwoPlayerPanel {

    /**
     *  Construct the panel.
     */
    public HexPanel() {}


    @Override
    public String getTitle() {
        return GameContext.getLabel("HEX_TITLE");
    }

    @Override
    protected GameBoardViewer createBoardViewer() {
        return new HexBoardViewer();
    }

    @Override
    protected NewGameDialog createNewGameDialog(Component parent, GameViewModel viewer ) {
        return new HexNewGameDialog( parent, viewer );
    }

    @Override
    protected GameInfoPanel createInfoPanel(GameController controller) {
        return new TwoPlayerInfoPanel( controller );
    }

    /**
     * Display the help dialog to give instructions
     */
    @Override
    protected void showHelpDialog() {
        String name = getTitle();
        String comments = GameContext.getLabel("HEX_TITLE");
        String overview = GameContext.getLabel("HEX_OVERVIEW");
        showHelpDialog( name, comments, overview );
    }
}



