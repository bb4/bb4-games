/** Copyright by Barry G. Becker, 2000-2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.online.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameViewModel;
import com.barrybecker4.game.common.online.GameCommand;
import com.barrybecker4.game.common.online.ui.OnlineGameManagerPanel;
import com.barrybecker4.game.common.ui.dialogs.GameOptionsDialog;
import com.barrybecker4.game.common.ui.dialogs.GameStartListener;
import com.barrybecker4.game.multiplayer.common.MultiGameOptions;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.table.TableButtonListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Manage multi-player online game tables.
 * Shows a list of the currently active "tables" to the user in a table.
 * Used only on the client to show list of active game tables.
 *
 * @author Barry Becker
 */
public abstract class MultiPlayerOnlineManagerPanel
                extends OnlineGameManagerPanel
                implements ActionListener, KeyListener, TableButtonListener {

    private static final String DEFAULT_NAME = "<name>";

    private NamePanel namePanel_;
    private GradientButton createTableButton_;
    private MultiPlayerTableManager tableManager;
    private GameOptionsDialog createGameTableDialog_;
    private String oldName_;

    /**
     * Constructor
     */
    protected MultiPlayerOnlineManagerPanel(GameViewModel viewer, GameStartListener dlg) {
        super(viewer, dlg);
    }

    /**
     * There is a button for creating a new online table at the top.
     *
     * The play online table as a row for each virtual table that an online player can join.
     * There is a join button next to each row in the table.
     * The player must never be seated at more than one table.
     * When the requisite number of players are at a table the game begins.
     * Join button, changes to "leave", after joining a table.
     * If you join a different table, you leave the last one that you joined.
     * A table (row) is removed if everyone leaves it.
     */
    @Override
    protected JPanel createPlayOnlinePanel()  {
        createGameTableDialog_ = createNewGameTableDialog();

        JPanel playOnlinePanel = new JPanel(new BorderLayout());
        playOnlinePanel.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),
                                                  GameContext.getLabel("ONLINE_DLG_TITLE")));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JPanel buttonsPanel = new JPanel(new BorderLayout());

        createTableButton_ = new GradientButton(GameContext.getLabel("CREATE_TABLE"));
        createTableButton_.setToolTipText( GameContext.getLabel("CREATE_TABLE_TIP") );
        createTableButton_.addActionListener(this);
        buttonsPanel.add(createTableButton_, BorderLayout.EAST);

        namePanel_ = new NamePanel();
        namePanel_.addKeyListener(this);

        JPanel bottomFill = new JPanel();
        bottomFill.setPreferredSize(new Dimension(100, 10));

        headerPanel.add(namePanel_, BorderLayout.CENTER);
        headerPanel.add(bottomFill, BorderLayout.SOUTH);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);
        playOnlinePanel.add(headerPanel, BorderLayout.NORTH);

        MultiPlayerOnlineGameTablesTable onlineGameTablesTable = createOnlineGamesTable(this);
        tableManager = new MultiPlayerTableManager(
                controller_.getServerConnection(), onlineGameTablesTable, gameStartedListener_);

        playOnlinePanel.setPreferredSize( new Dimension(600, 300) );
        playOnlinePanel.add( new JScrollPane(onlineGameTablesTable.getTable()) , BorderLayout.CENTER );

        if (controller_.getServerConnection().isConnected()) {
            controller_.getServerConnection().enterRoom();
        }
        return playOnlinePanel;
    }

    protected abstract MultiPlayerOnlineGameTablesTable createOnlineGamesTable(
            TableButtonListener tableButtonListener);

    /**
     * You are free to set your own options for the table that you are creating.
     */
    protected abstract GameOptionsDialog createNewGameTableDialog();

    /**
     * The server has sent out a message to all the clients.
     * @param cmd the command to handle.
     * @return true if handled.
     */
    @Override
    public boolean handleServerUpdate(GameCommand cmd) {
        return tableManager.handleServerUpdate(cmd);
    }

    /**
     * Implements actionListener.
     * The user has done something to change the table list
     * (e.g. added a new game).
     */
    @Override
    public void actionPerformed( ActionEvent e ) {
        Object source = e.getSource();

        if (namePanel_.nameChecksOut()) {
            if (source == createTableButton_) {
                createNewGameTable();
            }
        }
    }

    /**
     * Implements tableButtonListener.
     * User has joined a different table.
     */
    @Override
    public void tableButtonClicked( int row, int col, String id ) {

        if (namePanel_.nameChecksOut()) {
            tableManager.joinDifferentTable(row);
        }
    }

    /**
     * The create new table button at the top was clicked.
     * Creates a new row in the GameTableTable representing a table that players can come and sit at.
     */
    void createNewGameTable() {
        String currentName = namePanel_.getCurrentName();

        if (currentName.equals(DEFAULT_NAME)) {
            JOptionPane.showMessageDialog(this, "You need to select a name for yourself first.",
                                          "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // if the name has changed, make sure it is updated on the server
        if (!currentName.equals(oldName_)) {
            controller_.getServerConnection().nameChanged(oldName_, currentName);
            tableManager.setCurrentName(currentName);
            oldName_ = currentName;
        }

        boolean canceled = createGameTableDialog_.showDialog();

        if (!canceled)  {
            MultiGameOptions options = (MultiGameOptions)createGameTableDialog_.getOptions();
            tableManager.createNewGameTable(options);
        }
    }

    /**
     * called when the user closes the online game dialog.
     * We remove them from the active tables.
     */
    @Override
    public void closing() {
        String name = namePanel_.getCurrentName();
        System.out.println(name + " cancelled online dlg");
        //controller_.getServerConnection().leaveRoom(name);   // was commented
    }

    /**
     * Implement keyListener interface.
     * @param key key that was pressed
     */
    @Override
    public void keyTyped( KeyEvent key )  {}
    @Override
    public void keyPressed(KeyEvent key) {}
    @Override
    public void keyReleased(KeyEvent key) {
        char keyChar = key.getKeyChar();
        if ( keyChar == '\n' ) {
            String currentName = namePanel_.getCurrentName();
            controller_.getServerConnection().nameChanged(oldName_, currentName);
            tableManager.setCurrentName(currentName);
            oldName_ = currentName;
        }
    }
}
