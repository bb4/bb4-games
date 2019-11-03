/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.GameViewModel;
import com.barrybecker4.game.common.ui.dialogs.GameOptionsDialog;
import com.barrybecker4.game.common.ui.dialogs.NewGameDialog;
import com.barrybecker4.game.common.ui.panel.GameInfoPanel;
import com.barrybecker4.game.common.ui.panel.GamePanel;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.multiplayer.galactic.ui.dialog.GalacticNewGameDialog;
import com.barrybecker4.game.multiplayer.galactic.ui.dialog.GalacticOptionsDialog;

import java.awt.*;

/**
 *  This class defines the main UI for the Galactic Empire game applet.
 *  It can be run as an applet or application.
 *
 *  @author Barry Becker
 */
public class GalacticPanel extends GamePanel  {

    /**
     *  Construct the panel.
     */
    public GalacticPanel()  {}


    @Override
    public String getTitle()
    {
        return  GameContext.getLabel("GALACTIC_TITLE");
    }


    @Override
    protected GameBoardViewer createBoardViewer() {
        return new GalaxyViewer();
    }

    @Override
    protected NewGameDialog createNewGameDialog(Component parent, GameViewModel viewer ) {
        return new GalacticNewGameDialog( parent, viewer );
    }

    @Override
    protected GameOptionsDialog createOptionsDialog(Component parent, GameController controller ) {
        return new GalacticOptionsDialog( parent, controller );
    }

    @Override
    protected GameInfoPanel createInfoPanel(GameController controller) {

        return new GalacticInfoPanel( controller);
    }

    // Display the help dialog to give instructions
    @Override
    protected void showHelpDialog() {
        String name = getTitle();
        String comments = GameContext.getLabel("GALACTIC_TITLE");
        String overview = GameContext.getLabel("GALACTIC_OVERVIEW");
        showHelpDialog( name, comments, overview );
    }

}



