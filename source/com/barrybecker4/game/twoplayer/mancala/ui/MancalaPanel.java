/* Copyright by Barry G. Becker, 20014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.GameViewModel;
import com.barrybecker4.game.common.ui.dialogs.NewGameDialog;
import com.barrybecker4.game.common.ui.panel.GameInfoPanel;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerInfoPanel;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPanel;

import java.awt.Component;

/**
 *  This class defines the main UI for the Mancala game applet.
 *  It can be run as an applet or application.
 *
 *  @author Barry Becker
 */
public class MancalaPanel extends TwoPlayerPanel {

    /**
     *  Construct the panel.
     */
    public MancalaPanel() {}


    @Override
    public String getTitle() {
        return  GameContext.getLabel("MANCALA_TITLE");
    }

    @Override
    protected GameBoardViewer createBoardViewer() {
        return new MancalaBoardViewer();
    }

    @Override
    protected NewGameDialog createNewGameDialog(Component parent, GameViewModel viewer ) {
        return new MancalaNewGameDialog( parent, viewer );
    }

    @Override
    protected GameInfoPanel createInfoPanel(GameController controller) {
        return new TwoPlayerInfoPanel( controller );
    }

    // Display the help dialog to give instructions
    @Override
    protected void showHelpDialog() {
        String name = getTitle();
        String comments = GameContext.getLabel("MANCALA_TITLE");
        String overview = GameContext.getLabel("MANCALA_OVERVIEW");
        showHelpDialog( name, comments, overview );
    }

}



