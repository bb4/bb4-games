/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.gametree;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.ui.components.GradientButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Set of buttons to control debug animation at the bottom fot he gameTreeDialog.
 * @author Barry Becker Date: Dec 24, 2006
 */
class GameTreeButtons extends JPanel implements ActionListener {

    private final JButton pauseButton_;
    private final JButton stepButton_;
    private final JButton continueButton_;
    private final JButton closeButton_;

    // the controller that is actually being played in the normal view.
    private TwoPlayerController mainController_ = null;
    private GameTreeDialog gameTreeDlg_;

    public GameTreeButtons(GameTreeDialog gameTreeDlg) {

        setLayout(new FlowLayout());

        gameTreeDlg_ = gameTreeDlg;

        pauseButton_ = addButton("Pause", "Pause processing", true);
        stepButton_ = addButton("Step", "Step forward through the search computation", false);
        continueButton_ = addButton("Continue", "Continue searching for the next move", false);
        closeButton_ = addButton("Close", "Hide the Game Tree Viewer", true);
    }

    public void setMainController(TwoPlayerController mainController) {
         mainController_ = mainController;
    }

    private JButton addButton( String label, String tooltip, boolean enabled) {
        JButton b = new GradientButton();
        b.setText( label );
        b.setToolTipText( tooltip );
        b.setEnabled(enabled);
        b.addActionListener( this );
        add(b);
        return b;
    }

    /**
     * called when the ok button is clicked.
     */
    private void pause() {
        // if we set the root to null, then it doesn't have to build the tree
        pauseButton_.setEnabled(false);
        continueButton_.setEnabled(true);
        stepButton_.setEnabled(true);

        mainController_.pause();
        gameTreeDlg_.showCurrentGameTree();
    }

    /**
     * called when the ok button is clicked.
     */
    private void step() {
        mainController_.getViewer().step();
        gameTreeDlg_.showCurrentGameTree();
    }

    /**
     * called when the ok button is clicked.
     */
    private void continueProcessing() {
        GameContext.log(1,  "continue" );
        pauseButton_.setEnabled(true);
        continueButton_.setEnabled(false);
        stepButton_.setEnabled(false);
        mainController_.getViewer().continueProcessing();
    }

    /**
     * called when one of the buttons at the bottom have been pressed.
     */
    @Override
    public void actionPerformed( ActionEvent e ) {
        Object source = e.getSource();
        if ( source.equals(pauseButton_) ) {
            pause();
        }
        else if ( source.equals(stepButton_) ) {
            step();
        }
        else if ( source.equals(continueButton_) ) {
            continueProcessing();
        }
        else if ( source.equals(closeButton_) ) {
            gameTreeDlg_.close();
        }
    }

}
