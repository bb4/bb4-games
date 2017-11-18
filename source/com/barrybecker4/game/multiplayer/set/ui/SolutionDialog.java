/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.multiplayer.set.Card;
import com.barrybecker4.game.multiplayer.set.SetController;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.dialogs.OptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Barry Becker
 */
public class SolutionDialog extends OptionsDialog {
    private static final long serialVersionUID = 0L;

    private GradientButton okButton_ = new GradientButton();

    private SolutionPanel solutionPanel_;
    private SetController controller_;


    /**
     *  Constructor
     */
    public SolutionDialog(Component parent, SetController controller ) {
        super( parent);

        controller_ = controller;

        showContent();
    }

    @Override
    public JComponent createDialogContent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );

        JPanel buttonsPanel = createButtonsPanel();

         // list of sets. Each consecutive set of 3 cards in this list is a set.
        List<Card> sets_ = controller_.getSetsOnBoard();
        solutionPanel_ = new SolutionPanel(sets_, (SetGameViewer) controller_.getViewer());

        JPanel solutionsHolder = new JPanel();
        solutionsHolder.add(solutionPanel_);
        mainPanel.add(solutionsHolder, BorderLayout.CENTER );
        mainPanel.add( buttonsPanel, BorderLayout.SOUTH );


        return mainPanel;
    }

    @Override
    public String getTitle() {
        return GameContext.getLabel("SETS_ON_BOARD");
    }

    /** create the OK Cancel buttons that go at the botton  */
    @Override
    public JPanel createButtonsPanel() {

        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        initBottomButton( okButton_, GameContext.getLabel("OK"), GameContext.getLabel("OK") );
        buttonsPanel.add( okButton_ );

        return buttonsPanel;
    }


    /**
     * ok button pressed.
     */
    void ok() {
        solutionPanel_.closed();
        this.setVisible( false );
    }

    /**
     * called when a button has been pressed
     */
    @Override
    public void actionPerformed( ActionEvent e )  {
        Object source = e.getSource();
        if ( source == okButton_ ) {
            ok();
        }
    }

}