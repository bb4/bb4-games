/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.ui.dialog;

import com.barrybecker4.game.multiplayer.common.ui.ActionDialog;
import com.barrybecker4.game.multiplayer.trivial.TrivialAction;
import com.barrybecker4.game.multiplayer.trivial.TrivialController;
import com.barrybecker4.game.multiplayer.trivial.player.TrivialPlayer;
import com.barrybecker4.ui.components.GradientButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Allow the user to specify a trivial action: keep hidden or reveal.
 * @author Barry Becker
 */
public final class RevealDialog extends ActionDialog {

    /** selected when the user desires to reveal his value.*/
    private JRadioButton revealButton_;
    private GradientButton okButton_;

    /**
     * constructor - create the tree dialog.
     * @param pc TrivialController
     */
    public RevealDialog(TrivialController pc, Component parent) {
        super(pc, parent);
    }

    @Override
    protected JPanel createPersonalInfoPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Value = "+ ((TrivialPlayer)player_).getValue());
        p.add(label, BorderLayout.CENTER);
        return p;
    }


    @Override
    protected JPanel createGameInstructionsPanel() {

        JRadioButton keepHiddenButton = new JRadioButton("Keep value hidden");
        keepHiddenButton.setSelected(true);

        revealButton_ = new JRadioButton("Reveal value");

        ButtonGroup group = new ButtonGroup();
        group.add(keepHiddenButton);
        group.add(revealButton_);

        JPanel panel = new JPanel(new BorderLayout());
        JPanel choicesPanel = new JPanel( new GridLayout(1, 2) );
        choicesPanel.add( keepHiddenButton );
        choicesPanel.add( revealButton_ );

        panel.add(choicesPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Create the OK/Cancel buttons that go at the bottom.
     */
    @Override
    public JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        okButton_ = new GradientButton();
        initBottomButton( okButton_, "OK", "Click when you have made your choice" );
        buttonsPanel.add(okButton_);

        return buttonsPanel;
    }

    @Override
    public String getTitle() {
        return "Hide or Reveal?";
    }

    /**
     * called when one of the buttons at the bottom have been pressed.
     */
    @Override
    public void actionPerformed( ActionEvent e ) {
        Object source = e.getSource();
        TrivialAction.Name actionName = TrivialAction.Name.KEEP_HIDDEN;
        if (source == okButton_) {
            if (revealButton_.isSelected()) {
                actionName = TrivialAction.Name.REVEAL;
                ((TrivialPlayer)player_).revealValue();
            }
            this.setVisible(false);
        }

        (player_).setAction(new TrivialAction(player_.getName(), actionName));
    }
}

