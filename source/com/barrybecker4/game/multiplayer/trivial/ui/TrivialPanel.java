/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.GameViewModel;
import com.barrybecker4.game.common.ui.dialogs.GameOptionsDialog;
import com.barrybecker4.game.common.ui.dialogs.NewGameDialog;
import com.barrybecker4.game.common.ui.panel.GameInfoPanel;
import com.barrybecker4.game.common.ui.panel.GamePanel;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.multiplayer.trivial.ui.dialog.TrivialNewGameDialog;
import com.barrybecker4.game.multiplayer.trivial.ui.dialog.TrivialOptionsDialog;

import java.awt.*;

/**
 *  This class defines the main UI for the Trivial game applet.
 *  It can be run as an applet or application.
 *
 *  @author Barry Becker
 */
public class TrivialPanel extends GamePanel {

    /**
     *  Construct the panel.
     */
    public TrivialPanel() {}


    @Override
    public String getTitle() {
        return  GameContext.getLabel("TRIVIAL_TITLE");
    }


    @Override
    protected GameBoardViewer createBoardViewer() {
        return new TrivialGameViewer();
    }

    @Override
    protected NewGameDialog createNewGameDialog(Component parent, GameViewModel viewer ) {
        return new TrivialNewGameDialog( parent, viewer );
    }

    @Override
    protected GameOptionsDialog createOptionsDialog(Component parent, GameController controller ) {
        return new TrivialOptionsDialog( parent, controller );
    }

    @Override
    protected GameInfoPanel createInfoPanel(GameController controller) {
        return new TrivialInfoPanel( controller);
    }

    // Display the help dialog to give instructions
    @Override
    protected void showHelpDialog()  {
        String name = getTitle();
        String comments = GameContext.getLabel("TRIVIAL_TITLE");
        String overview = GameContext.getLabel("TRIVIAL_OVERVIEW");
        showHelpDialog( name, comments, overview );
    }

}



