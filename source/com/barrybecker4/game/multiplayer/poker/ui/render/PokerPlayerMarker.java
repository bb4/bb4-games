/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui.render;

import com.barrybecker4.game.multiplayer.common.MultiPlayerMarker;
import com.barrybecker4.game.multiplayer.poker.model.PokerTable;
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer;

/**
 * Represents a Poker player in the viewer.
 * For the player we draw their picture or icon, their chips (or cash), various annotations and their cards.
 *
 * @see PokerTable
 * @author Barry Becker
 */
public class PokerPlayerMarker extends MultiPlayerMarker {

    public PokerPlayerMarker(PokerPlayer owner)  {
        super(owner);
    }
}



