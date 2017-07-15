/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.dialogs;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.GameViewModel;
import com.barrybecker4.game.common.board.IBoard;
import com.barrybecker4.game.common.online.ui.OnlineGameManagerPanel;
import com.barrybecker4.game.common.ui.panel.GridBoardParamPanel;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.dialogs.OptionsDialog;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

/**
 * Use this modal dialog to let the user configure a new local game.
 * The have a choice of a new player vs player game or combinations of player vs computer or all computer.
 *
 * @author Barry Becker
 */
public abstract class NewGameDialog extends OptionsDialog implements ChangeListener {

    /** the options get set directly on the game controller that is passed in. */
    protected GameController controller_;

    /** contains potentially 2 tabs that shows options for creating a new game, or playing online */
    protected JTabbedPane tabbedPanel_;

    protected JPanel playLocalPanel_;
    private OnlineGameManagerPanel playOnlinePanel_;

    protected GridBoardParamPanel gridParamPanel_;

    protected GradientButton startButton_;

    /** the options get set directly on the game controller and viewer that are passed in  */
    protected final IBoard board_;
    protected final GameViewModel viewer_;


    /**
     *  constructor
     */
    protected NewGameDialog(Component parent, GameViewModel viewer) {
        super( parent );
        controller_ = viewer.getController();
        board_ = controller_.getBoard();
        viewer_ = viewer;
        showContent();
    }

    @Override
    protected JComponent createDialogContent() {
        JPanel mainPanel = new JPanel(true);
        mainPanel.setLayout( new BorderLayout() );

        playLocalPanel_ = createNewLocalGamePanel();

        JPanel buttonsPanel = createButtonsPanel();

        // add the tabs. Tabs because there may be local or remote games.
        tabbedPanel_ = new JTabbedPane();
        tabbedPanel_.add( GameContext.getLabel("NEW_GAME"), playLocalPanel_ );
        tabbedPanel_.setToolTipTextAt( 0, GameContext.getLabel("NEW_GAME_TIP") );
        tabbedPanel_.addChangeListener(this);

        mainPanel.add( tabbedPanel_, BorderLayout.CENTER );
        mainPanel.add( buttonsPanel, BorderLayout.SOUTH );

        return mainPanel;
    }

    protected OnlineGameManagerPanel createPlayOnlinePanel() {
        return null; // nothing if no online play supported
    }

    protected JPanel createNewLocalGamePanel() {
        JPanel playLocalPanel = new JPanel(true);
        playLocalPanel.setLayout( new BoxLayout( playLocalPanel, BoxLayout.Y_AXIS ) );
        JPanel playerPanel = createPlayerAssignmentPanel();
        gridParamPanel_ = createBoardParamPanel();
        JPanel customPanel = createCustomPanel();

        if (playerPanel != null) {
            playLocalPanel.add( playerPanel );
        }
        if (gridParamPanel_ != null)   {
            playLocalPanel.add( gridParamPanel_ );
        }
        if (customPanel != null )  {
            playLocalPanel.add( customPanel );
        }

        return playLocalPanel;
    }

    protected abstract JPanel createPlayerAssignmentPanel();


    @Override
    protected JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel( new FlowLayout(), true );

        startButton_ = new GradientButton();
        initBottomButton( startButton_, GameContext.getLabel("START_GAME"), GameContext.getLabel("START_GAME_TIP") );
        initBottomButton(cancelButton, GameContext.getLabel("CANCEL"), GameContext.getLabel("NGD_CANCEL_TIP") );

        buttonsPanel.add( startButton_ );
        buttonsPanel.add(cancelButton);

        return buttonsPanel;
    }

    /**
     * Subclasses use this to create their own custom options
     * Default is to have no custom panel.
     */
    protected JPanel createCustomPanel() {
        return null;
    }

    /**
     * panel which allows changing board specific properties.
     */
    protected abstract GridBoardParamPanel createBoardParamPanel();

    /**
     * Subclasses use this to create their own custom board configuration options
     * Default is to have no custom panel.
     */
    protected JPanel createCustomBoardConfigPanel() {
        return null;
    }

    @Override
    public String getTitle() {
        return GameContext.getLabel("NEW_GAME_DLG_TITLE");
    }

    protected void ok() {

        canceled_ = false;
        this.setVisible( false );
    }

    @Override
    public boolean showDialog() {

        boolean serverAvailable =  controller_.isOnlinePlayAvailable();
        if (serverAvailable) {
             if (playOnlinePanel_ == null) {
                 playOnlinePanel_ = createPlayOnlinePanel();
                 tabbedPanel_.add(playOnlinePanel_, 0);
                 tabbedPanel_.setTitleAt(0, "Play Online");
                 tabbedPanel_.setSelectedIndex(0);
                 pack();
             }
             tabbedPanel_.setEnabledAt(0, true);
        }
        else {
            if (playOnlinePanel_ != null) {
                tabbedPanel_.setEnabledAt(0, false);
            }
        }
        return super.showDialog();
    }

    /**
     * Called when one of the buttons at the bottom pressed
     */
    @Override
    public void actionPerformed( ActionEvent e )  {
        super.actionPerformed(e);
        Object source = e.getSource();

        if ( source == startButton_ ) {
            ok();
        }
    }

    /**
     * cancel button pressed
     */
    @Override
    protected void cancel() {
        // You are only allowed to participate in only games when the dialog is open.
        if (playOnlinePanel_ != null) {
            playOnlinePanel_.closing();
        }
        super.cancel();
    }

    /**
     * Called when the selected tab changes,
     * Or in the case of online play when the player has joined a table that is now ready to play.
     * I that case the dialog will close and play will begin.
     */
    @Override
    public void stateChanged( ChangeEvent event) {
        if (event.getSource() == tabbedPanel_) {
            startButton_.setVisible(tabbedPanel_.getSelectedComponent() != playOnlinePanel_);
        }
        //else if (e.getSource() == playOnlinePanel_) {
        //    this.setVisible(false);
        //}
    }

    /**
     * If the window gets closed, then the player has stood up from his table if online.
     */
    @Override
    protected void processWindowEvent( WindowEvent e ) {
        if ( e.getID() == WindowEvent.WINDOW_CLOSING ) {

            System.err.println("Window closing!");
            if (controller_.isOnlinePlayAvailable()) {
                GameContext.log(0, "Standing up from table.");
                playOnlinePanel_.closing();
            }
        }
        super.processWindowEvent( e );
    }
}