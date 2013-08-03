// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.galactic.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.ui.panel.GeneralInfoPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.MessageFormat;


/**
 *  Show information and statistics about the game.
 *  Also allow the player to enter their commands for the turn.
 *
 *  @author Barry Becker
 */
class GalacticGeneralInfoPanel extends GeneralInfoPanel {

    private JPanel commandPanel_;

    /**
     * Constructor
     */
    GalacticGeneralInfoPanel(Player player, JPanel commandPanel) {
        super(player);
        commandPanel_ = commandPanel;
        setCommandPanelTitle(player);
    }

    /**
     * set the appropriate text and color for the player label.
     */
    @Override
    protected void setPlayerLabel(Player player) {

        String playerName = player.getName();
        playerLabel_.setText(' ' + playerName + ' ');

        Color pColor = player.getColor();

        playerLabel_.setBorder(getPlayerLabelBorder(pColor));

        if (commandPanel_ != null) {
            commandPanel_.setForeground(pColor);
            setCommandPanelTitle(player);
        }
        this.repaint();
    }

    @Override
    public void update(GameController controller) {
        setPlayerLabel(controller.getCurrentPlayer());
        Move lastMove =  controller.getLastMove();
        if (lastMove != null)  {
            moveNumLabel_.setText( (controller.getPlayers().getNumPlayers() + 2) + " " );
        }
        else {
            moveNumLabel_.setText( 1 + " " );
        }
    }

    private void setCommandPanelTitle(Player player) {
        Object[] args = {player.getName()};
        String title = MessageFormat.format(GameContext.getLabel("GIVE_YOUR_ORDERS"), args);

        TitledBorder b = (TitledBorder)commandPanel_.getBorder();
        b.setTitle(title);
    }
}