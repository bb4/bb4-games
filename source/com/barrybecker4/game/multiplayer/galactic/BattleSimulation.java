/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic;

import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer;

import java.util.LinkedList;
import java.util.List;

/**
 *  CaptureCounts the delta state change of everything that happened during one turn of the game.
 *
 *  @see Galaxy
 *  @author Barry Becker
 */
public class BattleSimulation {

    private List<Player> hits_;
    private int numShipsAfterAttack_ = 0;
    private GalacticPlayer ownerAfterAttack_;
    //private GalacticPlayer ownerBeforeAttack_;

    private Order order_;
    private Planet destPlanet_;


    /**
     *  Constructor. This should never be called directly
     *  use the factory method createMove instead.
     */
    public BattleSimulation(Order order, Planet destPlanet) {
        order_ = order;
        destPlanet_ = destPlanet;
        createSimulation(order, destPlanet);
    }

    public GalacticPlayer getOwnerAfterAttack() {
        return ownerAfterAttack_;
    }

    public int getNumShipsAfterAttack() {
        return numShipsAfterAttack_;
    }

    public List getHitSequence() {
        return hits_;
    }

    /**
     * @return the planet on which the battle is occurring.
     */
    public Planet getPlanet() {
        return destPlanet_;
    }

    /**
     * @return the order that started it all.
     */
    public Order getOrder() {
        return order_;
    }

    /**
     * Given an order and destination planet, create a battle sequence that can be played back in the ui
     * @param order  describes the attack/reinforce order
     * @param destPlanet planet to which the armada is headed.
     */
    void createSimulation(Order order, Planet destPlanet) {
        hits_ = new LinkedList<>();

        if (order.getOwner()==destPlanet.getOwner()) {
            addReinforcements(order, destPlanet);
        }
        else {
            doBattleSequence(order, destPlanet);
        }
    }

    private void doBattleSequence(Order order, Planet destPlanet) {

        int numAttackShips = order.getFleetSize();
        int numDefendShips = destPlanet.getNumShips();
        Player attacker = order.getOwner();
        Player defender = destPlanet.getOwner();

        // create hit sequence
        while (numAttackShips>0 && numDefendShips>0) {
            // int total = numAttackShips + numDefendShips;

            double ratio = (0.5+ (double)numDefendShips/((double)(numAttackShips + numDefendShips)))/2.0;
            if (Math.random() > ratio) {
                numAttackShips--;
                hits_.add(attacker);
            }
            else {
                numDefendShips--;
                hits_.add(defender);
            }
        }
        if (numAttackShips == 0) {
            updateForDefenderVictory(destPlanet, numDefendShips);
        }
        else {
            assert (numDefendShips == 0);
            updateForAttackerVictory(order, numAttackShips);
        }
    }

    private void updateForDefenderVictory(Planet destPlanet, int numDefendShips) {
        ownerAfterAttack_ = destPlanet.getOwner();
        numShipsAfterAttack_ = numDefendShips;
    }

    private void updateForAttackerVictory(Order order, int numAttackShips) {
        ownerAfterAttack_ = order.getOwner();
        numShipsAfterAttack_ = numAttackShips;
    }

    /**
     * reinforcements have arrived at the specified destination planet..
     */
    private void addReinforcements(Order order, Planet destPlanet) {
        numShipsAfterAttack_ = order.getFleetSize() + destPlanet.getNumShips();
        ownerAfterAttack_ = destPlanet.getOwner();
    }


}



