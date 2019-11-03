/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui;

import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.ui.panel.GameChangedEvent;
import com.barrybecker4.game.common.ui.panel.GameChangedListener;
import com.barrybecker4.game.common.ui.panel.GameInfoPanel;
import com.barrybecker4.game.common.ui.panel.GeneralInfoPanel;
import com.barrybecker4.game.common.ui.panel.SectionPanel;
import com.barrybecker4.game.multiplayer.set.SetController;
import com.barrybecker4.game.multiplayer.set.SetPlayer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;


/**
 *  Show information and statistics about the game.
 *
 *  @author Barry Becker
 */
class SetInfoPanel extends GameInfoPanel
                   implements GameChangedListener, ListSelectionListener {

    private SetSummaryTable playerTable;

    private JPanel playerPanel;


    /**
     * Constructor
     */
    SetInfoPanel( GameController controller ) {
        super(controller);
    }

    @Override
    protected void createSubPanels() {
        generalInfoPanel_ = createGeneralInfoPanel(controller_.getCurrentPlayer());
        add( generalInfoPanel_ );
        add( createCustomInfoPanel() );
    }

    @Override
    protected int getMinWidth() {
        return 250;
    }

    /**
     * This panel shows information that is specific to the game type.
     * For Set, we have a button that allows the current player to enter his commands
     */
    @Override
    protected JPanel createCustomInfoPanel() {
        JPanel pp = new SectionPanel("Players");

        playerPanel = createPanel();
        playerPanel.setLayout(new BorderLayout());
        playerPanel.setBorder(createMarginBorder());

        insertPlayerTable();

        pp.add(playerPanel);
        return pp;
    }

    @Override
    protected GeneralInfoPanel createGeneralInfoPanel(Player player) {
        return new SetGeneralInfoPanel(player);
    }

    private void insertPlayerTable() {

        playerPanel.removeAll();

        playerTable = new SetSummaryTable(controller_.getPlayers());
        playerTable.addListSelectionListener(this);

        playerPanel.add(playerTable.getTable(), BorderLayout.CENTER);
    }

    /**
     * restore to new game state.
     */
    @Override
    public void reset() {
        insertPlayerTable();
        invalidate();
        repaint();
    }

    /**
     * implements the GameChangedListener interface.
     * This method called whenever something on the board has changed.
     */
    @Override
    public void gameChanged( GameChangedEvent gce ) {
        if ( controller_ == null )  {
            return;
        }

        generalInfoPanel_.update(controller_);

        SetPlayer player = getSelectedPlayer();
        if (player != null) {
            int r = playerTable.getTable().getSelectedRow();
            playerTable.getTable().getModel().setValueAt("" + player.getNumSetsFound(), r, 2);
            playerTable.getTable().clearSelection();
        }
    }

    /**
     * @return null if no current player
     */
    private SetPlayer getSelectedPlayer() {
        SetController c = (SetController)controller_;
        int selectedPlayerIndex = playerTable.getTable().getSelectedRow();
        SetPlayer selectedPlayer = null;
        if (selectedPlayerIndex >= 0) {
            selectedPlayer = (SetPlayer) c.getPlayers().get(selectedPlayerIndex);
        }
        return selectedPlayer;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

        SetController c = (SetController)controller_;
        c.setCurrentPlayer(getSelectedPlayer());
    }
}
