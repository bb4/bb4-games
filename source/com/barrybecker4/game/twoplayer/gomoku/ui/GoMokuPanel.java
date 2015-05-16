/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.ui;

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
 *  This class defines the main UI for the GoMoku game applet.
 *  It can be run as an applet or application.
 *
 *  @author Barry Becker
 */
public class GoMokuPanel extends TwoPlayerPanel {

    /**
     *  Construct the panel.
     */
    public GoMokuPanel()
    {}


    @Override
    public String getTitle() {
        return  GameContext.getLabel("GO_MOKU_TITLE");
    }


    @Override
    protected GameBoardViewer createBoardViewer() {
        return new GoMokuBoardViewer();
    }

    @Override
    protected NewGameDialog createNewGameDialog(Component parent, GameViewModel viewer ) {
        return new GoMokuNewGameDialog( parent, viewer );
    }

    @Override
    protected GameInfoPanel createInfoPanel(GameController controller) {
        return new TwoPlayerInfoPanel( controller );
    }

    // Display the help dialog to give instructions
    @Override
    protected void showHelpDialog() {
        String name = getTitle();
        String comments = GameContext.getLabel("GO_MOKU_TITLE");
        String overview = GameContext.getLabel("GO_MOKU_OVERVIEW");
        showHelpDialog( name, comments, overview );
    }

}



