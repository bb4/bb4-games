/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.GameViewModel;
import com.barrybecker4.game.common.ui.dialogs.GameOptionsDialog;
import com.barrybecker4.game.common.ui.dialogs.NewGameDialog;
import com.barrybecker4.game.common.ui.panel.GameInfoPanel;
import com.barrybecker4.game.common.ui.panel.GamePanel;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.multiplayer.poker.ui.dialog.PokerNewGameDialog;
import com.barrybecker4.game.multiplayer.poker.ui.dialog.PokerOptionsDialog;
import com.barrybecker4.game.multiplayer.poker.ui.infopanel.PokerInfoPanel;

import java.awt.*;

/**
 *  This class defines the main UI for the Poker game applet.
 *  It can be run as an applet or application.
 *
 *  @author Barry Becker
 */
public class PokerPanel extends GamePanel {

    /**
     * Construct the panel. Needed for reflective creation.
     */
    public PokerPanel() {}

    @Override
    public String getTitle() {
        return  GameContext.getLabel("POKER_TITLE");
    }

    @Override
    protected GameBoardViewer createBoardViewer() {
        return new PokerGameViewer();
    }

    @Override
    protected NewGameDialog createNewGameDialog(Component parent, GameViewModel viewer ) {
        return new PokerNewGameDialog( parent, viewer );
    }

    @Override
    protected GameOptionsDialog createOptionsDialog(Component parent, GameController controller ) {
        return new PokerOptionsDialog( parent, controller );
    }

    @Override
    protected GameInfoPanel createInfoPanel(GameController controller) {
        return new PokerInfoPanel( controller);
    }

    // Display the help dialog to give instructions
    @Override
    protected void showHelpDialog()  {
        String name = getTitle();
        String comments = GameContext.getLabel("POKER_TITLE");
        String overview = GameContext.getLabel("POKER_OVERVIEW");
        showHelpDialog( name, comments, overview );
    }

}



