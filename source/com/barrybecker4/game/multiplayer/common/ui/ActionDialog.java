/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.ui;

import com.barrybecker4.game.multiplayer.common.MultiGameController;
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer;
import com.barrybecker4.ui.dialogs.OptionsDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Allow the user to specify an action - like which planets to attack
 * @author Barry Becker
 */
public abstract class ActionDialog extends OptionsDialog {

    protected MultiGamePlayer player_;
    protected MultiGameController controller_;

    /**
     * Constructor - create the tree dialog.
     * @param gc game controller
     */
    protected ActionDialog(MultiGameController gc, Component parent) {
        controller_ = gc;
        player_ = (MultiGamePlayer) controller_.getCurrentPlayer().getActualPlayer();

        Point p = parent.getLocationOnScreen();
        // offset the dlg so the board is visible as a reference
        setLocation((int)(p.getX() + 0.7 * getParent().getWidth()),
                                 (int)(p.getY() + getParent().getHeight()/3.0));
        showContent();
    }

    /**
     * ui initialization of the tree control.
     */
    @Override
    public JComponent createDialogContent() {
        setResizable( true );
        JPanel mainPanel = new JPanel();

        mainPanel.setLayout( new BorderLayout() );
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel personalInfoPanel = createPersonalInfoPanel();
        JPanel buttonsPanel = createButtonsPanel();

        JPanel instructions = createInstructionsPanel();

        mainPanel.add(personalInfoPanel , BorderLayout.NORTH);
        mainPanel.add(instructions, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    protected abstract JPanel createPersonalInfoPanel();


    private JPanel createInstructionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(5,5,5,5)));
        PlayerLabel playerLabel_ = new PlayerLabel();
        playerLabel_.setPlayer(player_);

        JPanel gameSpecificInstructions = createGameInstructionsPanel();

        panel.add(playerLabel_, BorderLayout.NORTH);
        panel.add(gameSpecificInstructions, BorderLayout.CENTER);

        return panel;
    }

    protected abstract JPanel createGameInstructionsPanel();


    /**
     *  create the OK/Cancel buttons that go at the bottom.
     */
    @Override
    public abstract JPanel createButtonsPanel();
}

