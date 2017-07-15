// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.common.ui;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.ui.panel.GeneralInfoPanel;
import com.barrybecker4.game.common.ui.panel.InfoLabel;
import com.barrybecker4.game.common.ui.panel.RowEntryPanel;
import com.barrybecker4.game.common.ui.panel.SectionPanel;
import com.barrybecker4.game.twoplayer.common.WinProbabilityCalculator;

import javax.swing.*;
import java.text.MessageFormat;

/**
 *  Show information and statistics about the game.
 *
 *  @author Barry Becker
 */
public class TwoPlayerGeneralInfoPanel extends GeneralInfoPanel {

    private JLabel chanceOfWinningLabel_;

    /**
     * Constructor
     */
    public TwoPlayerGeneralInfoPanel(Player player) {

        SectionPanel.styleSectionPanel(this, GameContext.getLabel("GENERAL_INFO"));

        JLabel turnLabel = new InfoLabel(GameContext.getLabel("PLAYER_TO_MOVE"));
        initPlayerLabel(player);

        JLabel moveNumTextLabel = new InfoLabel( GameContext.getLabel("CURRENT_MOVE_NUM"));
        moveNumTextLabel.setHorizontalAlignment(JLabel.LEFT);
        moveNumLabel = new JLabel("1");

        Object[] args = {player.getName()};
        String m = MessageFormat.format(GameContext.getLabel("CHANCE_OF_WINNING"), args );
        JLabel chanceOfWinningTextLabel = new InfoLabel( m );
        chanceOfWinningTextLabel.setHorizontalAlignment(JLabel.LEFT);
        chanceOfWinningLabel_ = new InfoLabel( "   " );
        //showRecommendedMove_ = new JCheckBox( "Show recommended move", false );

        add(new RowEntryPanel(turnLabel, playerLabel));
        add(new RowEntryPanel(moveNumTextLabel, moveNumLabel));
        add(new RowEntryPanel(chanceOfWinningTextLabel, chanceOfWinningLabel_));
        // add this back in when it is implemented
        //generalPanel.add( createRowEntryPanel(showRecommendedMove_) );
        add(Box.createGlue());
    }

    @Override
    protected void setPlayerLabel(Player player) {

        playerLabel.setText(' ' + player.getName() + ' ');
        playerLabel.setBorder(getPlayerLabelBorder(player.getColor()));
        repaint();
    }

    @Override
    public void update(GameController controller) {
        if ( controller.getLastMove() != null ) {
            setPlayerLabel(controller.getCurrentPlayer());
            moveNumLabel.setText( controller.getNumMoves() + " " );
            WinProbabilityCalculator calc = new WinProbabilityCalculator();
            String formattedPropability =
                    FormatUtil.formatNumber(calc.getChanceOfPlayer1Winning(controller.getMoveList().copy()));
            chanceOfWinningLabel_.setText(  formattedPropability + ' ' );
        }
    }

}