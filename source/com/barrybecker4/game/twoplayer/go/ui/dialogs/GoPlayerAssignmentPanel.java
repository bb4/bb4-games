// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.go.ui.dialogs;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.ui.dialogs.PlayerAssignmentPanel;

import java.awt.*;

/**
 * Use this modal dialog to initialize the required game parameters that
 * are needed in order to start play in a 2 player game.
 *
 * @author Barry Becker
 */
public class GoPlayerAssignmentPanel extends PlayerAssignmentPanel {

    private static final String BLACK_IS = GameContext.getLabel("BLACK_IS");
    private static final String WHITE_IS = GameContext.getLabel("WHITE_IS");

    /**
     * constructor
     */
    protected GoPlayerAssignmentPanel(TwoPlayerController controller, Component parent) {
        super(controller, parent);
    }

    @Override
    protected String getPlayer1Label() {
        return BLACK_IS;
    }

    @Override
    protected String getPlayer2Label() {
        return WHITE_IS;
    }
}