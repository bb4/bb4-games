/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.dialog;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.multiplayer.common.ui.PlayerLabel;
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.dialogs.OptionsDialog;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.List;


/**
 * Show a summary of the final results.
 * The winner(s) is the player with all the chips.
 *
 * @author Barry Becker
 */
public class RoundOverDialog extends OptionsDialog {

    private GradientButton closeButton_;

    private List<PokerPlayer> winners_;
    private int winnings_;
    private JLabel winLabel_;


    /**
     * constructor - create the tree dialog.
     * @param parent frame to display relative to
     */
    public RoundOverDialog(Component parent, List<PokerPlayer> winners, int winnings ) {
        super( parent );
        winners_ = winners;
        winnings_ = winnings;
        showContent();
    }

    @Override
    public JComponent createDialogContent() {
        setResizable( true );
        JPanel mainPanel =  new JPanel();
        mainPanel.setLayout( new BorderLayout() );
        mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
                                                               BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JPanel buttonsPanel = createButtonsPanel();
        JPanel instructions = createInstructionsPanel();

        mainPanel.add(instructions, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createInstructionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        winLabel_ = new JLabel();
        initWonMessage();

        for (PokerPlayer winner : winners_) {
            PlayerLabel playerLabel = new PlayerLabel();
            playerLabel.setPlayer(winner);
            panel.add(playerLabel, BorderLayout.NORTH);
        }
        panel.add(winLabel_, BorderLayout.CENTER);
        return panel;
    }

    private void initWonMessage() {
        NumberFormat cf = GameContext.getCurrencyFormat();
        String cash = cf.format(winnings_);
        winLabel_.setText("won " + cash + " from the pot!");
        //JLabel amountToCall = new JLabel("To call, you need to add " + cf.format(callAmount_));
    }

    @Override
    public String getTitle() {
       return "Round Over";
    }

    @Override
    public JPanel createButtonsPanel(){
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        closeButton_ = new GradientButton();
        initBottomButton(closeButton_, GameContext.getLabel("CLOSE"), GameContext.getLabel("CLOSE_TIP"));

        buttonsPanel.add(closeButton_);
        return buttonsPanel;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == closeButton_) {
            this.setVisible(false);
        }
    }

}

