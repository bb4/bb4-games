/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.dialog;

import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer;
import com.barrybecker4.game.multiplayer.common.ui.SummaryTable;
import com.barrybecker4.game.multiplayer.common.ui.TallyDialog;
import com.barrybecker4.game.multiplayer.galactic.GalacticController;

import java.awt.Component;
import java.util.List;


/**
 * Show a summary of the final results.
 * We will show how many planets and how many ships each remaining player has.
 * The winner is the player with the most planets.
 * If there are more than one player with the same number of planets,
 * then the number of ships will be used to break ties.
 *
 * @author Barry Becker
 */
public final class GalacticTallyDialog extends TallyDialog {
    /**
     * constructor - create the tree dialog.
     * @param parent frame to display relative to
     * @param controller the controller
     */
    public GalacticTallyDialog(Component parent, GalacticController controller ) {
        super( parent, controller );
    }

    @Override
    protected SummaryTable createSummaryTable(PlayerList players) {
        return new GalacticSummaryTable(players);
    }

    /**
     * @param players players to find winner among
     * @return the player with the most planets (num ships used only as a tie breaker).
     */
    @Override
    public List<? extends MultiGamePlayer> findWinners(PlayerList players) {
        return controller_.determineWinners();
    }

}

