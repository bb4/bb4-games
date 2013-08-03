// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.galactic;

import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer;

import java.util.List;

/**
 * Used to determine a players score at the end of a game.
 * The number of planets is the main determinant, but the number of ships can be used to break ties
 * @author Barry Becker
 */
public class Scorer {

    private static final double PLANET_WEIGHT = 1.0;
    private static final double SHIP_WEIGHT = 0.000000001;

    /**
     * Get the score for the specified player
     * @param player the player to determine score for
     * @return the players score at the end of the game
     */
    public double score(GalacticPlayer player) {

        List playerPlanets = Galaxy.getPlanets(player);
        return PLANET_WEIGHT * playerPlanets.size()
             + SHIP_WEIGHT * (double) player.getTotalNumShips();
    }
}
