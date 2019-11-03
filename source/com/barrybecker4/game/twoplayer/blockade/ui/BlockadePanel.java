/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.ui;

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
 *  This class defines the main UI for the Blockade game applet.
 *  It can be run as an applet or application.
 *  see also the game Quoridor
 *
 *  @author Barry Becker
 */
public class BlockadePanel extends TwoPlayerPanel {

    @Override
    public String getTitle() {
        return GameContext.getLabel("BLOCKADE_TITLE");
    }

    @Override
    protected GameBoardViewer createBoardViewer() {
        return new BlockadeBoardViewer();
    }

    @Override
    protected NewGameDialog createNewGameDialog(Component parent, GameViewModel viewer ) {
        return new BlockadeNewGameDialog( parent, viewer );
    }

    @Override
    protected GameInfoPanel createInfoPanel(GameController controller) {
        return new TwoPlayerInfoPanel( controller );
    }

    /**
     *  Display the help dialog to give instructions
     */
    @Override
    protected void showHelpDialog() {
        String name = getTitle();
        String comments =GameContext.getLabel("BLOCKADE_COMMENTS");
        String overview = GameContext.getLabel("BLOCKADE_OVERVIEW");

        showHelpDialog( name, comments, overview );
    }

}



