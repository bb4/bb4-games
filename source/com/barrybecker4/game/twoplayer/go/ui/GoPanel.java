/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.GameViewModel;
import com.barrybecker4.game.common.ui.dialogs.NewGameDialog;
import com.barrybecker4.game.common.ui.panel.GameInfoPanel;
import com.barrybecker4.game.common.ui.panel.GameToolBar;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPanel;
import com.barrybecker4.game.twoplayer.common.ui.gametree.GameTreeDialog;
import com.barrybecker4.game.twoplayer.go.ui.dialogs.GoNewGameDialog;
import com.barrybecker4.game.twoplayer.go.ui.gametree.GoTreeCellRenderer;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 *  This class defines the main UI for the Go game panel.
 *
 *  @author Barry Becker
 */
public final class GoPanel extends TwoPlayerPanel {

    /**
     * Construct the panel.
     */
    public GoPanel() {}

    /**
     * @return the title for the applet/application window
     */
    @Override
    public String getTitle() {
        return GameContext.getLabel("GO");
    }

    /**
     * creates a board viewer of the appropriate type given a game controller
     * @return the game board viewer
     */
    @Override
    protected GameBoardViewer createBoardViewer() {
        return new GoBoardViewer();
    }

    @Override
    protected NewGameDialog createNewGameDialog(Component parent, GameViewModel viewer )  {
        return new GoNewGameDialog( parent, viewer );
    }

    @Override
    protected GameInfoPanel createInfoPanel(GameController controller) {
        return new GoInfoPanel( controller );
    }


    @Override
    protected GameTreeDialog createGameTreeDialog() {
        AbstractTwoPlayerBoardViewer v =(AbstractTwoPlayerBoardViewer)createBoardViewer();
        v.setViewOnly( true ); // we don't want it to receive click events
        return new GameTreeDialog( null, v, new GoTreeCellRenderer());
    }

    @Override
    protected GameToolBar createToolbar() {
         return new GoToolBar(BG_TEXTURE, this);
    }

    /**
     * Display the help dialog to give instructions
     */
    @Override
    protected void showHelpDialog() {
        String name = getTitle();
        String comments = GameContext.getLabel("GO_COMMENTS");
        String overview = GameContext.getLabel("GO_OVERVIEW");
        showHelpDialog( name, comments, overview );
    }

    /**
     * handle the pass button and the regular ones too.
     */
    @Override
    public void actionPerformed( ActionEvent e ) {
        Object source = e.getSource();
        if ( source == ((GoToolBar)toolBar_).getPassButton() ) {
            ((GoBoardViewer) boardViewer_).pass();
        }
        else if ( source == ((GoToolBar)toolBar_).getResignButton() ) {
            ((GoBoardViewer) boardViewer_).resign();
        }
        super.actionPerformed( e );
    }
}



