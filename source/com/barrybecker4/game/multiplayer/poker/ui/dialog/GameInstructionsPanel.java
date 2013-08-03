// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.ui.dialog;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

/**
 * Allow the user to specify a poker action
 * @author Barry Becker
 */
final class GameInstructionsPanel extends JPanel {

    private PokerPlayer player;
    private int callAmount;


    /**
     * Constructor
     */
    public GameInstructionsPanel(PokerPlayer player, int callAmount ) {
        this.player = player;
        this.callAmount = callAmount;
        initUI();
    }

    private void initUI() {

        NumberFormat cf = GameContext.getCurrencyFormat();
        String cash = cf.format(player.getCash());

        JPanel instr = new JPanel();
        instr.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel currentCash = new JLabel("You currently have "+ cash);

        JLabel amountToCall = new JLabel("To call, you need to add " + cf.format(callAmount));

        this.setLayout(new BorderLayout());
        add(currentCash, BorderLayout.CENTER);
        if (callAmount > 0)  {
            add(amountToCall, BorderLayout.SOUTH);
        }
    }
}

