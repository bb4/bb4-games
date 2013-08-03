/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers.ui;

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
 *  This class defines the main UI for the Checkers game panel.
 *  It can be run as an applet or application.
 *
 *  @author Barry Becker
 */
public class CheckersPanel extends TwoPlayerPanel {

    /**
     * Construct the panel.
     */
    public CheckersPanel()
    {}

    @Override
    public String getTitle() {
        return GameContext.getLabel("CHECKERS_TITLE");
    }

    @Override
    protected GameBoardViewer createBoardViewer() {
        return new CheckersBoardViewer();
    }

    @Override
    protected NewGameDialog createNewGameDialog(Component parent, GameViewModel viewer ) {
        return new CheckersNewGameDialog( parent, viewer );
    }

    @Override
    protected GameInfoPanel createInfoPanel(GameController controller)
    {
        return new TwoPlayerInfoPanel( controller );
    }

    /**
     * Display the help dialog to give instructions.
     */
    @Override
    protected void showHelpDialog()
    {
        String name = getTitle();
        String comments = GameContext.getLabel("CHECKERS_COMMENTS");
        showHelpDialog( name, comments, GameContext.getLabel("CHECKERS_OVERVIEW" ));
    }

}



