/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic;

import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 *  CaptureCounts the delta state change of everything that happened during one turn (year) of the game.
 *
 *  @see Galaxy
 *  @author Barry Becker
 */
public class GalacticTurn extends Move {

    /** a list of battle simulations */
    private List<BattleSimulation> battles_;


    /**
     *  Constructor. This should never be called directly
     *  use the factory method createMove instead.
     */
    private GalacticTurn() {}

    /**
     *  factory method for getting new moves.
     *  used to use recycled objects, but did not increase performance, so I removed it.
     */
    public static GalacticTurn createMove() {
        return new GalacticTurn();
    }

    /**
     * given an order and destPlanet create a battle sequence that can be played back in the ui.
     * @param order the directive from the admiral
     * @param destPlanet destination planet
     */
    public void addSimulation(Order order, Planet destPlanet) {
        BattleSimulation battle = new BattleSimulation(order, destPlanet);
        addSimulation(battle);
    }


    /**
     * given an order and destPlanet create a battle sequence that can be played back in the ui.
     * @param battle encapsulated battle simulation
     */
    public void addSimulation(BattleSimulation battle)  {
        if (battles_ == null) {
            battles_ = new ArrayList<>();
        }
        battles_.add(battle);
    }


    /**
     * private class representing a single melee round result
     */
    private static class Hit   {
        int numShipsDestroyed;
        Player playerHit;

        Hit(Player p, int numDestroyed) {
            playerHit = p;
            numShipsDestroyed = numDestroyed;
        }
    }

}



