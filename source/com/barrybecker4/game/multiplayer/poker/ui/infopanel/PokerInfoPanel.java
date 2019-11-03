/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.infopanel;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.ui.panel.GameChangedEvent;
import com.barrybecker4.game.common.ui.panel.GameChangedListener;
import com.barrybecker4.game.common.ui.panel.GameInfoPanel;
import com.barrybecker4.game.common.ui.panel.GeneralInfoPanel;
import com.barrybecker4.game.common.ui.panel.SectionPanel;
import com.barrybecker4.game.multiplayer.poker.model.PokerAction;
import com.barrybecker4.game.multiplayer.poker.PokerController;
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer;
import com.barrybecker4.game.multiplayer.poker.ui.dialog.BettingDialog;
import com.barrybecker4.ui.components.GradientButton;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

/**
 * Show information and statistics about the game.
 *
 * @author Barry Becker
 */
public class PokerInfoPanel extends GameInfoPanel
                     implements GameChangedListener, ActionListener {

    private JButton commandButton_;
    private JPanel commandPanel_;


    /**
     * Constructor
     */
    public PokerInfoPanel( GameController controller ) {
        super(controller);
    }

    /**
     * The custom panel shows game specific info. In this case, the command button.
     * if all the players are robots, don't even show this panel.
     */
    @Override
    protected void createSubPanels() {
        JPanel customInfoPanel = createCustomInfoPanel();
        generalInfoPanel_ = createGeneralInfoPanel(controller_.getCurrentPlayer());
        add(generalInfoPanel_);

        if (!controller_.getPlayers().allPlayersComputer())  {
            add(customInfoPanel);
        }

        add( createChipLegendPanel());
    }

    /**
     * This panel shows information that is specific to the game type.
     * For Poker, we have a button that allows the current player to enter his commands
     */
    @Override
    protected JPanel createCustomInfoPanel() {
        commandPanel_ = new SectionPanel("");
        setCommandPanelTitle();

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
        return new PokerGeneralInfoPanel(player, commandPanel_);
    }

    /**
     * This panel shows a discrete color legend for the poker chip values
     */
    JPanel createChipLegendPanel() {
        JPanel legendPanel = new ChipLegendPanel();
        SectionPanel.styleSectionPanel(legendPanel, "Chip Values");
        return legendPanel;
    }


    private void setCommandPanelTitle() {
        Object[] args = {controller_.getCurrentPlayer().getName()};
        String title = MessageFormat.format(GameContext.getLabel("MAKE_YOUR_MOVE"), args);

        TitledBorder b = (TitledBorder)commandPanel_.getBorder();
        b.setTitle(title);
    }

    /**
     * The Command button was pressed.
     * open the dialog to get the players command.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == commandButton_) {
            PokerController pc = (PokerController)controller_;
            if (!pc.getCurrentPlayer().isHuman()) {
                JOptionPane.showMessageDialog(this, "It's not your turn", "Warning", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            gameChanged(null); // update the current player in the label

           // open the command dialog to get the players commands
           PokerPlayer currentPlayer = (PokerPlayer) pc.getCurrentPlayer().getActualPlayer();

           // if the current player has folded, then advance to the next player.
           if (currentPlayer.hasFolded())  {
               pc.advanceToNextPlayer();
           }

           BettingDialog bettingDialog = new BettingDialog(pc, getParent());

           boolean canceled = bettingDialog.showDialog();
           if ( !canceled ) {
               PokerAction action = (PokerAction)currentPlayer.getAction(pc);
               applyPokerAction(action, currentPlayer);

               pc.advanceToNextPlayer();
           }
        }
    }

    /**  apply the players action : fold, check, call, raise */
    private void applyPokerAction(PokerAction action, PokerPlayer currentPlayer) {

         PokerController pc = (PokerController)controller_;
         int callAmount = pc.getCurrentMaxContribution() - currentPlayer.getContribution();

         switch (action.getActionName()) {
             case FOLD :
                 currentPlayer.setFold(true);
                 break;
             case CALL :
                 if (callAmount <= currentPlayer.getCash())  {
                     currentPlayer.contributeToPot(pc.getRound(), callAmount);
                 } else {
                     currentPlayer.setFold(true);
                     // if this happens it was probably because someone was allowed
                     // to raise by more than the all in amount.
                     assert false:"callAmount=" + callAmount +" currentPlayer cash="+currentPlayer.getCash();
                 }
                 break;
             case RAISE :
                 currentPlayer.contributeToPot(pc.getRound(), callAmount);
                 int raise = action.getRaiseAmount();
                 currentPlayer.contributeToPot(pc.getRound(), raise);
                 break;
        }
        if (controller_.isOnlinePlayAvailable())  {
            controller_.getServerConnection().playerActionPerformed(action);
        }
    }

    /**
     * implements the GameChangedListener interface.
     * This method called whenever a move has been made.
     */
    @Override
    public void gameChanged( GameChangedEvent gce ) {
        if ( controller_ == null )  {
            return;
        }

        generalInfoPanel_.update(controller_);

        // don't allow any more actions when the game is done.
        commandButton_.setEnabled(!controller_.isDone());
    }

}
