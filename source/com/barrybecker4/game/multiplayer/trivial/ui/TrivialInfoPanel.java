/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.ui.panel.GameChangedEvent;
import com.barrybecker4.game.common.ui.panel.GameChangedListener;
import com.barrybecker4.game.common.ui.panel.GameInfoPanel;
import com.barrybecker4.game.common.ui.panel.GeneralInfoPanel;
import com.barrybecker4.game.common.ui.panel.SectionPanel;
import com.barrybecker4.game.multiplayer.trivial.TrivialAction;
import com.barrybecker4.game.multiplayer.trivial.TrivialController;
import com.barrybecker4.game.multiplayer.trivial.player.TrivialPlayer;
import com.barrybecker4.game.multiplayer.trivial.ui.dialog.RevealDialog;
import com.barrybecker4.ui.components.GradientButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *  Show information and statistics about the game.
 *
 *  @author Barry Becker
 */
class TrivialInfoPanel extends GameInfoPanel
                       implements GameChangedListener, ActionListener {

    /** buttons to either give commands or pass*/
    private JButton commandButton_;
    private JPanel commandPanel_;

    /**
     * Constructor
     */
    TrivialInfoPanel( GameController controller ) {
        super(controller);
    }

    @Override
    protected void createSubPanels() {
        JPanel customPanel = createCustomInfoPanel();
        generalInfoPanel_ = createGeneralInfoPanel(controller_.getCurrentPlayer());
        add(generalInfoPanel_);

        // the custom panel shows game specific info. In this case, the command button.
        // if all the players are robots, don't even show this panel.
        if (!controller_.getPlayers().allPlayersComputer())   {
            add( customPanel );
        }
    }

    /**
     * This panel shows information that is specific to the game type.
     * For Trivial, we have a button that allows the current player to enter his commands
     */
    @Override
    protected JPanel createCustomInfoPanel() {
        commandPanel_ = new SectionPanel();

        // the command button
        JPanel bp = createPanel();
        bp.setBorder(createMarginBorder());

        commandButton_ = new GradientButton(GameContext.getLabel("ORDERS"));
        commandButton_.addActionListener(this);
        bp.add(commandButton_);

        commandPanel_.add(bp);
        return commandPanel_;
    }

    @Override
    protected GeneralInfoPanel createGeneralInfoPanel(Player player) {
        return new TrivialGeneralInfoPanel(player, commandPanel_);
    }

    /**
     * The Orders button was pressed.
     * open the Orders dialog to get the players directives for their move.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == commandButton_)  {
            TrivialController pc = (TrivialController)controller_;
            gameChanged(null); // update the current player in the label

           // open the command dialog to get the players commands
           Player p = pc.getCurrentPlayer();
           TrivialPlayer currentPlayer = (TrivialPlayer) p.getActualPlayer();

           RevealDialog bettingDialog = new RevealDialog(pc, getParent());

           boolean canceled = bettingDialog.showDialog();
           if ( !canceled ) {
               TrivialAction action = (TrivialAction) currentPlayer.getAction(pc);
               // apply the players action : to review their cards or not
               switch (action.getActionName()) {
                    case KEEP_HIDDEN :
                        break;
                    case REVEAL :
                        currentPlayer.revealValue();
                        break;
                }
                // tell the server that we have moved. All the surrogates need to then make their moves.
                if (controller_.isOnlinePlayAvailable())  {
                    controller_.getServerConnection().playerActionPerformed(action);
                }
                pc.advanceToNextPlayer();
           }
        }
    }

    /**
     * implements the GameChangedListener interface.
     * This method called whenever a move has been made.
     */
    @Override
    public void gameChanged( GameChangedEvent gce ) {
        if ( controller_ == null ) {
            return;
        }

        generalInfoPanel_.update(controller_);

        // disable if the game is done or the current player is a surrogate
        boolean enabled = !controller_.isDone() && !controller_.getCurrentPlayer().isSurrogate();
        commandButton_.setEnabled(enabled);
    }

}