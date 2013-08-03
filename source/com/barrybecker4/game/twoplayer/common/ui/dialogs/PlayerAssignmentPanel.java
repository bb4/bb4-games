// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.common.ui.dialogs;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.dialogs.OptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Use this modal dialog to initialize the two players that
 * are needed in order to start play in a 2 player game.
 * They are either human players or computer players.
 *
 * @author Barry Becker
 */
public class PlayerAssignmentPanel extends JPanel
                                   implements ActionListener {

    // radio buttons for selecting 1st and 2nd players
    private JRadioButton human1Button_;
    private JRadioButton computer1Button_;
    private JRadioButton human2Button_;
    private JRadioButton computer2Button_;

    private GradientButton editOptions1Button_;
    private GradientButton editOptions2Button_;

    private TwoPlayerController controller;
    private Component parent;

    /**
     * constructor
     */
    protected PlayerAssignmentPanel(TwoPlayerController controller, Component parent) {
        this.controller = controller;
        this.parent = parent;
        initialize();
    }

    protected void initialize() {

        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        final String human = GameContext.getLabel("HUMAN");
        final String computer = GameContext.getLabel("COMPUTER");
        final String editWeights = GameContext.getLabel("EDIT_PLAYER_OPTIONS");
        panel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                        GameContext.getLabel("PLAYER_ASSIGNMENT")));

        Player p1 = controller.getPlayers().getPlayer1();
        Player p2 = controller.getPlayers().getPlayer2();

        human1Button_ = new JRadioButton( human, p1.isHuman() );
        computer1Button_ = new JRadioButton( computer, !p2.isHuman() );
        editOptions1Button_ = new GradientButton(editWeights);
        editOptions1Button_.setEnabled(!p1.isHuman());
        JPanel firstP =
                createPlayerEntry( getPlayer1Label(), human1Button_, computer1Button_, editOptions1Button_);

        human2Button_ = new JRadioButton( human, p2.isHuman());
        computer2Button_ = new JRadioButton( computer, !p2.isHuman() );
        editOptions2Button_ = new GradientButton( editWeights );
        editOptions2Button_.setEnabled( !p2.isHuman() );
        JPanel secondP =
                createPlayerEntry( getPlayer2Label(), human2Button_, computer2Button_, editOptions2Button_);

        firstP.setAlignmentX( Component.LEFT_ALIGNMENT );
        secondP.setAlignmentX( Component.LEFT_ALIGNMENT );
        panel.add(firstP);
        panel.add(secondP);

        add(panel, BorderLayout.CENTER );
        add(new Panel(), BorderLayout.EAST);
    }


    protected String getPlayer1Label() {
        return GameContext.getLabel("FIRST_PLAYER" ) + OptionsDialog.COLON;
    }

    protected String getPlayer2Label() {
        return GameContext.getLabel("SECOND_PLAYER") + OptionsDialog.COLON;
    }

    private JPanel createPlayerEntry( String message,
                                      JRadioButton humanButton,
                                      JRadioButton computerButton,
                                      GradientButton editWtsButton ) {
        JPanel p = new JPanel();
        p.setLayout( new BoxLayout( p, BoxLayout.X_AXIS ) );

        JLabel label = new JLabel( message );
        label.setMinimumSize(new Dimension(140, 18));
        p.add( label );
        humanButton.addActionListener( this );
        computerButton.addActionListener( this );
        editWtsButton.addActionListener( this );

        ButtonGroup group = new ButtonGroup();
        group.add( humanButton );
        group.add( computerButton );
        computerButton.addActionListener( this );
        humanButton.addActionListener( this );

        humanButton.setAlignmentX( Component.LEFT_ALIGNMENT );
        computerButton.setAlignmentX( Component.LEFT_ALIGNMENT );
        editWtsButton.setAlignmentX( Component.RIGHT_ALIGNMENT );

        p.add( humanButton );
        p.add( computerButton );
        p.add( new JPanel()); // filler
        p.add( editWtsButton );
        return p;
    }

    protected void ok() {
        PlayerList players = controller.getPlayers();
        players.getPlayer1().setHuman( human1Button_.isSelected() );
        players.getPlayer2().setHuman( human2Button_.isSelected() );
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        Object source = e.getSource();

        if (source == computer1Button_ ) {
            editOptions1Button_.setEnabled(true);
        }
        else if ( source == computer2Button_ ) {
            editOptions2Button_.setEnabled(true);
        } else if ( source == human1Button_ ) {
            editOptions1Button_.setEnabled(false);
        }
        else if ( source == human2Button_ ) {
            editOptions2Button_.setEnabled(false);
        }
        else if ( source == editOptions1Button_) {
            showEditWeightsDialog(true);
        }
        else if ( source == editOptions2Button_) {
            showEditWeightsDialog(false);
        }
    }

    /**
     *  @return return true if canceled
     */
    private boolean showEditWeightsDialog(boolean showForPlayer1) {

        PlayerOptionsDialog editWtsDlg = new PlayerOptionsDialog(parent, controller, showForPlayer1 );

        editWtsDlg.setLocationRelativeTo(parent);
        editWtsDlg.setModal(true);
        editWtsDlg.setVisible(true);
        return false;
    }

    public void setBothComputerPlayers() {
        boolean checked = true;
        computer1Button_.setSelected(checked);
        computer2Button_.setSelected(checked);
        editOptions1Button_.setEnabled(!checked);
        editOptions2Button_.setEnabled(!checked);
        human1Button_.setEnabled(!checked);
        computer1Button_.setEnabled(!checked);
        human2Button_.setEnabled(!checked);
        computer2Button_.setEnabled(!checked);
    }
}