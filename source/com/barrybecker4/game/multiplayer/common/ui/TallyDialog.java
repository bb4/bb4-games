/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.multiplayer.common.MultiGameController;
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.dialogs.OptionsDialog;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Show a summary of the final results.
 * We will show how stats about each player.
 * The winner is determined by some metric applied to these stats.
 *
 * @author Barry Becker
 */
public abstract class TallyDialog extends OptionsDialog {

    protected MultiGameController controller_;

    private GradientButton okButton_;



    /**
     * constructor - create the tree dialog.
     * @param parent frame to display relative to
     * @param controller pass in game controller.
     */
    protected TallyDialog(Component parent, MultiGameController controller ) {
        super( parent );
        controller_ = controller;

        showContent();
    }

    @Override
    public String getTitle() {
        return GameContext.getLabel("TALLY_TITLE");
    }

    /**
     * ui initialization of the tree control.
     */
    @Override
    protected JComponent createDialogContent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        PlayerList players = controller_.getPlayers();
        String winningPlayers = winnersLabel(findWinners(players));

        // show a label at the top with who the winner(s) is
        JLabel winnersLabel = new JLabel();
        winnersLabel.setText("<html>" + GameContext.getLabel("GAME_OVER") + "<br>"
                + GameContext.getLabel("WINNER_IS") + "<br>" + winningPlayers + "</html>");
        winnersLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        mainPanel.add(winnersLabel, BorderLayout.NORTH);

        SummaryTable summaryTable_= createSummaryTable(players);
        JPanel tablePanel = new JPanel();
        tablePanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        tablePanel.add(new JScrollPane(summaryTable_.getTable()), BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(createButtonsPanel(), BorderLayout.SOUTH);

        return mainPanel;
    }

    private String winnersLabel(List<? extends MultiGamePlayer> winners) {
        StringBuilder bldr = new StringBuilder();
        for (MultiGamePlayer winner : winners) {
            bldr.append(winner.getName()).append(", ");
        }
        return bldr.substring(0, bldr.length()-2);
    }

    protected abstract SummaryTable createSummaryTable(PlayerList players);


    protected abstract List<? extends MultiGamePlayer> findWinners(PlayerList players);


    /**
     *  create the OK Cancel buttons that go at the bottom
     */
    @Override
    protected JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        okButton_ = new GradientButton();
        initBottomButton( okButton_, GameContext.getLabel("OK"), GameContext.getLabel("PLACE_ORDER_TIP") );
        //initBottomButton( cancelButton, GameContext.getLabel("CANCEL"), GameContext.getLabel("CANCEL") );

        buttonsPanel.add( okButton_ );
        //buttonsPanel.add( cancelButton );

        return buttonsPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == okButton_) {
            this.setVisible(false);
        }
    }

}

