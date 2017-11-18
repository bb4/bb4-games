/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.dialog;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.components.NumberInput;
import com.barrybecker4.ui.dialogs.OptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Allow the user to specify amount to raise the pot.
 *
 * @author Barry Becker
 */
final class RaiseDialog extends OptionsDialog {

    private PokerPlayer player_;
    private GradientButton okButton_;
    private NumberInput raiseAmount_;
    private int callAmount_;
    private int allInAmount_;
    private int maxRaiseAllowed_;
    private int ante_;

    private static final int DEFAULT_RAISE_AMOUNT = 5; // dollars


    /**
     * constructor - create the tree dialog.
     */
    public RaiseDialog(PokerPlayer player, int callAmount, int allInAmount, int maxRaiseAllowed, int ante) {
        setResizable( true );
        player_ = player;
        callAmount_ = callAmount;
        allInAmount_ = allInAmount;
        maxRaiseAllowed_ = maxRaiseAllowed;
        ante_ = ante;
        showContent();
    }


    /**
     * ui initialization of the tree control.
     */
    @Override
    public JComponent createDialogContent() {
        JPanel mainPanel =  new JPanel();
        mainPanel.setLayout( new BorderLayout() );

        JPanel primaryPanel = new JPanel();
        primaryPanel.setLayout( new BorderLayout() );

        primaryPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
                                                                   BorderFactory.createEmptyBorder(10,10,10,10)));

        JPanel buttonsPanel = createButtonsPanel();

        // add the form elements
        JPanel instructionsPanel = new JPanel(new BorderLayout());
        instructionsPanel.setMinimumSize(new Dimension(30, 60));

        JLabel instr1 = new JLabel("You currently have $"+player_.getCash());
        instructionsPanel.add(instr1, BorderLayout.NORTH);
        if (callAmount_ > 0) {
            JLabel instr2 = new JLabel("You first need to add $"+callAmount_+" to meet what others have added.");
            instructionsPanel.add(instr2, BorderLayout.CENTER);
        }
        if ((player_.getCash() > allInAmount_ - player_.getContribution()) && allInAmount_ < maxRaiseAllowed_) {

            JLabel instr3 = new JLabel("If you want, you can \"all in\" one of the players by raising $"+(allInAmount_ - callAmount_ - ante_));
            instructionsPanel.add(instr3, BorderLayout.SOUTH);
        }
        if (callAmount_ > 0) {
            raiseAmount_ = new NumberInput(GameContext.getLabel("AMOUNT_TO_RAISE1"), DEFAULT_RAISE_AMOUNT);
        }
        else {
            raiseAmount_ = new NumberInput(GameContext.getLabel("AMOUNT_TO_RAISE2"), DEFAULT_RAISE_AMOUNT);
        }

        primaryPanel.add(instructionsPanel, BorderLayout.NORTH);
        primaryPanel.add(raiseAmount_, BorderLayout.CENTER);
        mainPanel.add(primaryPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    /**
     *  create the OK/Cancel buttons that go at the bottom.
     */
    @Override
    public JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        okButton_ = new GradientButton();
        initBottomButton( okButton_, GameContext.getLabel("OK"), GameContext.getLabel("RAISE_BUTTON_TIP") );
        initBottomButton(cancelButton(), GameContext.getLabel("CANCEL"), GameContext.getLabel("CANCEL") );

        buttonsPanel.add( okButton_ );
        buttonsPanel.add(cancelButton());

        return buttonsPanel;
    }

    @Override
    public String getTitle() {
        return GameContext.getLabel("MAKE_RAISE");
    }

    /**
     * called when one of the buttons at the bottom have been pressed.
     */
    @Override
    public void actionPerformed( ActionEvent e ) {

        super.actionPerformed(e);
        Object source = e.getSource();
        if (source == okButton_) {
            int contrib = callAmount_ + getRaiseAmount();
            if (contrib > player_.getCash()) {
                JOptionPane.showMessageDialog(this, "You cannot raise by more money than you have!");
            }
            else if ((maxRaiseAllowed_ < allInAmount_) && (getRaiseAmount() > maxRaiseAllowed_))  {
                JOptionPane.showMessageDialog(this, "The maximum raise allowed is $"
                        + maxRaiseAllowed_ + ". You cannot raise by more than that.");
            }
            else if (contrib > allInAmount_ - player_.getContribution()) {
                JOptionPane.showMessageDialog(this, "You cannot raise by more money than the poorest player.");
            }
            else if (getRaiseAmount() > maxRaiseAllowed_) {
                JOptionPane.showMessageDialog(this, "The maximum raise allowed is $"
                        + maxRaiseAllowed_ + ". You cannot raise by more than that.");
            }
            else {
                this.setVisible(false);
            }
        }
        else {
             throw new IllegalStateException("actionPerformed source=" + source + ". not cancel and not ok");
        }
    }

    /**
     * @return retrieve the specified order.
     */
    public int getRaiseAmount() {
        return raiseAmount_.getIntValue();
    }

}

