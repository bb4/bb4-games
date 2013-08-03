// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.ui.infopanel;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.ui.panel.GeneralInfoPanel;

import javax.swing.border.TitledBorder;
import javax.swing.JPanel;
import java.awt.*;
import java.text.MessageFormat;


/**
 * Show general information about poker the game.
 *
 * @author Barry Becker
 */
class PokerGeneralInfoPanel extends GeneralInfoPanel {

    private JPanel commandPanel_;

    /**
     * Constructor
     */
    PokerGeneralInfoPanel(Player player, JPanel commandPanel) {
        super(player);
        commandPanel_ = commandPanel;
        setCommandPanelTitle(player);
    }

    /**
     * Set the appropriate text and color for the player label.
     */
    @Override
    public void setPlayerLabel(Player player) {

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

    private void setCommandPanelTitle(Player player) {
        Object[] args = {player.getName()};
        String title = MessageFormat.format(GameContext.getLabel("MAKE_YOUR_MOVE"), args);

        TitledBorder b = (TitledBorder)commandPanel_.getBorder();
        b.setTitle(title);
    }

    /**
     * implements the GameChangedListener interface.
     * This method called whenever a move has been made.
     */
    @Override
    public void update(GameController controller) {

        setPlayerLabel(controller.getCurrentPlayer());
        Move lastMove =  controller.getLastMove();
        if (lastMove != null)  {
            moveNumLabel_.setText( (controller.getNumMoves() + 2) + " " );
        }
        else {
            moveNumLabel_.setText( 1 + " " );
        }
    }

}