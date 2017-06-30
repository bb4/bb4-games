// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.set.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.ui.panel.GeneralInfoPanel;
import com.barrybecker4.game.common.ui.panel.InfoLabel;
import com.barrybecker4.game.common.ui.panel.RowEntryPanel;
import com.barrybecker4.game.common.ui.panel.SectionPanel;
import com.barrybecker4.game.multiplayer.set.SetBoard;

import javax.swing.*;


/**
 *  Show information and statistics about the game.
 *
 *  @author Barry Becker
 */
class SetGeneralInfoPanel extends GeneralInfoPanel {

    private JLabel numSetsOnBoardLabel_;
    private JLabel numCardsRemainingLabel_;


    /**
     * Constructor
     */
    SetGeneralInfoPanel(Player player) {
        SectionPanel.styleSectionPanel(this, GameContext.getLabel("GENERAL_INFO"));

        JLabel numSetsOnBoardText = new InfoLabel(GameContext.getLabel("NUMBER_OF_SETS_ON_BOARD"));
        numSetsOnBoardLabel_ = new InfoLabel( " " );

        JLabel numCardsRemainingText = new InfoLabel( GameContext.getLabel("NUMBER_OF_CARDS_REMAINING"));
        numCardsRemainingLabel_ = new InfoLabel( " " );
        numCardsRemainingLabel_.setHorizontalAlignment(JLabel.LEFT);

        add(new RowEntryPanel(numSetsOnBoardText, numSetsOnBoardLabel_));
        add(new RowEntryPanel(numCardsRemainingText, numCardsRemainingLabel_));

        add(Box.createGlue());
    }

    @Override
    protected void setPlayerLabel(Player player) {
        assert false : "Did not expect this to be called";
    }

    /**
     * implements the GameChangedListener interface.
     * This method called whenever something on the board has changed.
     */
    @Override
    public void update(GameController controller) {

        SetBoard b = (SetBoard) controller.getBoard();
        numSetsOnBoardLabel_.setText( b.getNumSetsOnBoard() + " " );

        int cardsInDeck = b.getDeck().size() - b.getNumCardsShowing();
        numCardsRemainingLabel_.setText( cardsInDeck + " " );
    }
}