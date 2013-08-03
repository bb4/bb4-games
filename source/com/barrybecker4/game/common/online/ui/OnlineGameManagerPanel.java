/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.online.ui;

import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.GameViewModel;
import com.barrybecker4.game.common.online.GameCommand;
import com.barrybecker4.game.common.online.OnlineChangeListener;
import com.barrybecker4.game.common.online.server.IServerConnection;
import com.barrybecker4.game.common.ui.dialogs.GameStartListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Manage the online game tables.
 * Allows a player to join exactly one virtual table and begin playing against other players online.
 * If the player creates a table, he sets the options for it.
 *
 * The server maintains the global state.
 * Any time something changes, the server broadcasts the global state to the online clients.
 *
 * @author Barry Becker
 */
public abstract class OnlineGameManagerPanel
                extends JPanel
                implements OnlineChangeListener, ActionListener {

    /** the options get set directly on the game controller that is passed in. */
    protected GameController controller_;

    /** typically the dialog that we live in. Called when table ready to play.   */
    protected GameStartListener gameStartedListener_;

    /** Constructor */
    protected OnlineGameManagerPanel(GameViewModel viewer, GameStartListener dlg) {

        controller_ = viewer.getController();
        gameStartedListener_ = dlg;

        IServerConnection connection = controller_.getServerConnection();
        assert (connection != null) :
                "You should not create this dlg without first verifying that online play is available.";
        connection.addOnlineChangeListener(this);

        enableEvents( AWTEvent.WINDOW_EVENT_MASK );

        initGUI();
    }

    void initGUI() {

        JPanel playOnlinePanel = createPlayOnlinePanel();
        add( playOnlinePanel );
    }


    public abstract void closing();

    @Override
    public abstract boolean handleServerUpdate(GameCommand cmd);

    /**
     * Subclasses need to provide a more interesting implementation of this if they
     * want to support online play.
     */
    protected JPanel createPlayOnlinePanel() {
        JPanel playOnlinePanel = new JPanel();
        playOnlinePanel.setLayout( new BoxLayout( playOnlinePanel, BoxLayout.Y_AXIS ) );

        JPanel p = new JPanel();
        p.setLayout( new BorderLayout() );
        p.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),
                     "Play Online" ) );
        p.setMaximumSize( new Dimension( 400, 60 ) );

        JLabel label = new JLabel("Join an Existing Table or Create a New one.");
        label.setAlignmentX( Component.LEFT_ALIGNMENT );
        p.add( label );

        playOnlinePanel.add( p );

        return playOnlinePanel;
    }

}
