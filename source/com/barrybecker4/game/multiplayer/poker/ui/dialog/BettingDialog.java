/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.dialog;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.multiplayer.common.ui.ActionDialog;
import com.barrybecker4.game.multiplayer.poker.model.PokerAction;
import com.barrybecker4.game.multiplayer.poker.PokerController;
import com.barrybecker4.game.multiplayer.poker.PokerOptions;
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer;
import com.barrybecker4.ui.components.GradientButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Allow the user to specify a poker action
 * @author Barry Becker
 */
public final class BettingDialog extends ActionDialog {

    private GradientButton foldButton;
    private GradientButton callButton;    // call or check
    private GradientButton raiseButton;

    private int callAmount;
    private int raiseAmount = 0;

    /**
     * Constructor - create the betting dialog.
     * @param pc pokerController
     */
    public BettingDialog(PokerController pc, Component parent) {
        super(pc, parent);
        callAmount = ((PokerPlayer)player_).getCallAmount(pc);
    }

    @Override
    protected JPanel createPersonalInfoPanel() {
        return new PokerHandViewer(((PokerPlayer)player_).getHand());
    }

    @Override
    protected JPanel createGameInstructionsPanel() {
        return new GameInstructionsPanel((PokerPlayer) player_, callAmount);
    }

    /**
     *  create the OK/Cancel buttons that go at the bottom.
     */
    @Override
    protected JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(true);

        foldButton = new GradientButton();
        initBottomButton(foldButton, GameContext.getLabel("FOLD"), GameContext.getLabel("FOLD_TIP") );

        callButton = new GradientButton();
        initBottomButton(callButton, GameContext.getLabel("CALL"), GameContext.getLabel("CALL_TIP") );

        raiseButton = new GradientButton();
        initBottomButton(raiseButton, GameContext.getLabel("RAISE"), GameContext.getLabel("RAISE_TIP") );

        buttonsPanel.add(foldButton);
        buttonsPanel.add(callButton);
        buttonsPanel.add(raiseButton);

        return buttonsPanel;
    }

    @Override
    public String getTitle() {
        return GameContext.getLabel("MAKE_YOUR_BET");
    }

    /**
     * called when one of the buttons at the bottom have been pressed.
     */
    @Override
    public void actionPerformed( ActionEvent event ) {
        Object source = event.getSource();
        PokerAction.Name actionName = null;
        if (source == foldButton) {
            actionName = PokerAction.Name.FOLD;
            ((PokerPlayer)player_).setFold(true);
            this.setVisible(false);
        }
        else if ( source == callButton) {
            actionName = PokerAction.Name.CALL;
            this.setVisible(false);
        }
        else if ( source == raiseButton) {
            actionName = PokerAction.Name.RAISE;
            showRaiseDialog();
        }
        else {
            assert false :"actionPerformed source="+source+". not recognized";
        }

        player_.setAction(new PokerAction(player_.getName(), actionName, raiseAmount));
    }

    void showRaiseDialog() {
        // open a dlg to get an order
        PokerController pc = (PokerController)controller_;
        PokerOptions options = (PokerOptions)controller_.getOptions();
        RaiseDialog raiseDialog =
                new RaiseDialog((PokerPlayer)player_, callAmount, pc.getAllInAmount(),
                                options.getMaxAbsoluteRaise(), options.getAnte());

        raiseDialog.setLocation((int)(this.getLocation().getX() + 40), (int)(this.getLocation().getY() +170));

        boolean canceled = raiseDialog.showDialog();

        if ( !canceled ) {
            raiseAmount = raiseDialog.getRaiseAmount();
            this.setVisible(false);
        }
    }
}

