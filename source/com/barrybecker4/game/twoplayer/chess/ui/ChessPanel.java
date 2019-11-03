/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess.ui;

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
 *  This class defines the main UI for the Chess game panel.
 *  It can be shown in an applet or application.
 *
 *  @author Barry Becker
 */
public class ChessPanel extends TwoPlayerPanel
{

    /**
     * Construct the panel.
     */
    public ChessPanel()
    {}

    @Override
    public String getTitle()
    {
        return GameContext.getLabel("CHESS_TITLE");
    }


    @Override
    protected GameBoardViewer createBoardViewer()
    {
        return new ChessBoardViewer();
    }

    @Override
    protected NewGameDialog createNewGameDialog(Component parent, GameViewModel viewer )
    {
        return new ChessNewGameDialog(parent, viewer );
    }

    @Override
    protected GameInfoPanel createInfoPanel(GameController controller)
    {
        return new TwoPlayerInfoPanel( controller );   // make ChessInfoPanel
    }

    // Display the help dialog to give instructions
    @Override
    protected void showHelpDialog()
    {
        String name = getTitle();
        String comments = GameContext.getLabel("CHESS_COMMENTS");
        String overview =GameContext.getLabel("CHESS_OVERVIEW");
        showHelpDialog( name, comments, overview );
    }

}



