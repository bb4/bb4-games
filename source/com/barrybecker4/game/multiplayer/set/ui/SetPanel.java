/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.GameViewModel;
import com.barrybecker4.game.common.ui.dialogs.GameOptionsDialog;
import com.barrybecker4.game.common.ui.dialogs.NewGameDialog;
import com.barrybecker4.game.common.ui.panel.GameChangedEvent;
import com.barrybecker4.game.common.ui.panel.GameInfoPanel;
import com.barrybecker4.game.common.ui.panel.GamePanel;
import com.barrybecker4.game.common.ui.panel.GameToolBar;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.multiplayer.set.SetController;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main panel for set game
 *
 * It contains a dockable toolbar and a canvas for showing the cards.
 * new game, add card, solve, and help.
 *  @see SetToolBar
 *
 *  @author Barry Becker
 */
public class SetPanel extends GamePanel
                      implements ActionListener {

    public SetPanel()
    {}

    @Override
    public String getTitle() {
        return "Set Game";
    }


    @Override
    protected GameToolBar createToolbar() {
         return new SetToolBar(BG_TEXTURE, this);
    }

    @Override
    protected GameBoardViewer createBoardViewer() {
        return new SetGameViewer();
    }

    @Override
    protected NewGameDialog createNewGameDialog(Component parent, GameViewModel viewer ) {
        return new SetNewGameDialog(parent, viewer );
    }

    @Override
    protected GameOptionsDialog createOptionsDialog(Component parent, GameController controller )
    {
        return new SetOptionsDialog( parent, controller );
    }

    @Override
    protected GameInfoPanel createInfoPanel(GameController controller)
    {
        return new SetInfoPanel( controller);
    }

    /**
     * implements the GameChangedListener interface.
     * This method called whenever a move has been made.
     */
    @Override
    public void gameChanged( GameChangedEvent gce ) {
        // do nothing for this.
    }

    /**
     * Display a help dialog.
     * This dialog should tell about the game and give instructions on how to play.
     */
    @Override
    protected void showHelpDialog() {
        String name = getTitle();
        String comments = GameContext.getLabel("SET_COMMENTS");
        String helpMsg = GameContext.getLabel("SET_OVERVIEW");
        showHelpDialog(name, comments, helpMsg);
    }

    /**
     * handle button click actions.
     * If you add your own custom buttons, you should override this, but be sure the first line is
     * <P>
     * super.actionPerformed(e);
     */
    @Override
    public void actionPerformed( ActionEvent e ) {
        Object source = e.getSource();

        SetToolBar setToolBar = ((SetToolBar)toolBar_);
        SetController c = ((SetController)boardViewer_.getController());

        if ( source == setToolBar.getAddButton()) {
            c.addCards(1);
        }
        else if ( source == setToolBar.getRemoveButton()) {
            c.removeCard();

        }
        else if ( source == setToolBar.getSolveButton()) {
             //JOptionPane.showMessageDialog(this, "Solution Requested");
            SolutionDialog solutionDialog = new SolutionDialog(null, (SetController) boardViewer_.getController());
            solutionDialog.setVisible(true); //showDialog();
            solutionDialog.pack();
        }

        setToolBar.getRemoveButton().setEnabled(c.canRemoveCards());
        setToolBar.getAddButton().setEnabled(c.hasCardsToAdd());

        super.actionPerformed( e );

        boardViewer_.repaint();
    }

}
