// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.common.ui.panel;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 *  Show generic stuff like the current player's name and the number of moves made so far.
 *
 *  @author Barry Becker
 */
public abstract class GeneralInfoPanel extends JPanel {

    private static final Font PLAYER_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.BOLD, 12 );

    protected JLabel moveNumLabel_;
    protected JLabel playerLabel_;

    /**
     * Constructor
     */
    public GeneralInfoPanel(Player player) {

        SectionPanel.styleSectionPanel(this, GameContext.getLabel("GENERAL_INFO"));
        JLabel turnLabel = new InfoLabel(GameContext.getLabel("PLAYER_TO_MOVE"));
        initPlayerLabel(player);

        JLabel moveNumTextLabel = new InfoLabel( GameContext.getLabel("CURRENT_MOVE_NUM"));
        moveNumTextLabel.setHorizontalAlignment(JLabel.LEFT);
        moveNumLabel_ = new JLabel( "1" );

        add(new RowEntryPanel( turnLabel, playerLabel_ ) );
        add(new RowEntryPanel( moveNumTextLabel, moveNumLabel_ ) );

        // add this back in when it is implemented
        //generalPanel.add( new RowEntryPanel(showRecommendedMove_) );
        add( Box.createGlue() );
    }

    protected GeneralInfoPanel() {}

    protected void initPlayerLabel(Player player) {
        playerLabel_ = new JLabel();
        playerLabel_.setOpaque(true);
        playerLabel_.setFont(PLAYER_FONT);
        setPlayerLabel(player);
    }

    public void update(GameController controller) {
        setPlayerLabel(controller.getCurrentPlayer());
        if ( controller.getLastMove() != null ) {
            moveNumLabel_.setText( " " + controller.getNumMoves() );
        }
    }

    /**
     * set the appropriate text and color for the player label.
     */
    protected abstract void setPlayerLabel(Player player);

    protected Border getPlayerLabelBorder(Color pColor) {
        return BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0),
                                                  BorderFactory.createEtchedBorder(pColor, pColor.darker()));
    }
}