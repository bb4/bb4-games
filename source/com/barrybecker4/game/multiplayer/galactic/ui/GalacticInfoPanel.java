/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.ui.panel.GameChangedEvent;
import com.barrybecker4.game.common.ui.panel.GameChangedListener;
import com.barrybecker4.game.common.ui.panel.GameInfoPanel;
import com.barrybecker4.game.common.ui.panel.GeneralInfoPanel;
import com.barrybecker4.game.common.ui.panel.SectionPanel;
import com.barrybecker4.game.multiplayer.galactic.GalacticController;
import com.barrybecker4.game.multiplayer.galactic.Galaxy;
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer;
import com.barrybecker4.game.multiplayer.galactic.ui.dialog.OrdersDialog;
import com.barrybecker4.ui.components.GradientButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *  Show information and statistics about the game.
 *  Also allow the player to enter their commands for the turn.
 *
 *  @author Barry Becker
 */
class GalacticInfoPanel extends GameInfoPanel
                        implements GameChangedListener, ActionListener {

    /** buttons to either give commands or pass  */
    private JButton commandButton;
    private JButton passButton;
    private JPanel commandPanel;


    /**
     * Constructor
     */
    GalacticInfoPanel( GameController controller ) {
        super(controller);
    }

    @Override
    protected void createSubPanels() {
        JPanel customInfoPanel = createCustomInfoPanel();
        generalInfoPanel_ = createGeneralInfoPanel(controller_.getCurrentPlayer());
        add(generalInfoPanel_);

        // the custom panel shows game specific info. In this case the command button.
        // if all the players are robots, don't even show this panel.
        if (!controller_.getPlayers().allPlayersComputer())
            add(customInfoPanel);
    }


    @Override
    protected GeneralInfoPanel createGeneralInfoPanel(Player player) {
        return new GalacticGeneralInfoPanel(player, commandPanel);
    }

    /**
     * This panel shows information that is specific to the game type.
     * For Galactic Empire we have a button that allows the current player to enter his commands
     * Should split this out into a separate PlayerCommandPanel class.
     */
    @Override
    protected JPanel createCustomInfoPanel() {

        commandPanel = new SectionPanel();

        //setCommandPanelTitle(Player player)

        JPanel bp = createPanel();
        bp.setBorder(createMarginBorder());

        commandButton = new GradientButton(GameContext.getLabel("ORDERS"));
        commandButton.addActionListener(this);
        bp.add(commandButton);

        passButton = new GradientButton(GameContext.getLabel("PASS"));
        passButton.addActionListener(this);
        bp.add(passButton);

        commandPanel.add(bp);
        return commandPanel;
    }

    /**
     * The Orders button was pressed.
     * open the Orders dialog to get the players commands
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        GalacticController gc = (GalacticController)controller_;
        gameChanged(null); // update the current player in the label

        if (e.getSource() == commandButton) {

            // if the current player does not own any planets, then advance to the next player
            if (Galaxy.getPlanets((GalacticPlayer) gc.getCurrentPlayer()).size() == 0)  {
                gc.advanceToNextPlayer();
            }

            showOrdersDialog(gc);
        }
        else if (e.getSource() == passButton) {
           gc.advanceToNextPlayer();
        }
    }

    /**
     * Open the command dialog to get the players commands
     * @param gc the galactic controller
     */
    private void showOrdersDialog(GalacticController gc) {

        GalacticPlayer currentPlayer = (GalacticPlayer)gc.getCurrentPlayer();

        OrdersDialog ordersDialog =
                new OrdersDialog(null, currentPlayer, gc.getNumberOfYearsRemaining());
        Point p = getParent().getLocationOnScreen();

        // offset the dlg so the Galaxy grid is visible as a reference
        ordersDialog.setLocation((int)(p.getX()+0.7*getParent().getWidth()), (int)(p.getY()+getParent().getHeight()/3.0));

        boolean canceled = ordersDialog.showDialog();
        if ( !canceled ) { // newGame a game with the newly defined options
            currentPlayer.setOrders( ordersDialog.getOrders() );
            gc.advanceToNextPlayer();
        }
    }

    /**
     * implements the GameChangedListener interface.
     * This method called whenever a move has been made.
     */
    @Override
    public void gameChanged(GameChangedEvent gce) {
        if ( controller_ == null )  {
            return;
        }
        generalInfoPanel_.update(controller_);
    }

}
