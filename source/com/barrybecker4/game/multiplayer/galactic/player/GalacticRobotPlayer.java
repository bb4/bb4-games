/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.player;


import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.multiplayer.galactic.Galaxy;
import com.barrybecker4.game.multiplayer.galactic.Order;
import com.barrybecker4.game.multiplayer.galactic.Planet;
import com.barrybecker4.game.multiplayer.galactic.PlanetComparator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a Robot Admiral commanding an intergalactic fleet of starships.
 * These Robot Admirals have there own unique strategy for playing.
 * Abstract base class for other robot player types.
 *
 * @author Barry Becker
 */
public abstract class GalacticRobotPlayer extends GalacticPlayer
{

    private enum RobotType {CRAZY_ROBOT, METHODICAL_ROBOT}

    GalacticRobotPlayer(String name, Planet homePlanet, Color color) {
        super(name, homePlanet, color, false);
    }

    /**
     * @return the current list of this Robot's orders.
     */
    public abstract List<Order> makeOrders(Galaxy galaxy, int numYearsRemaining);

    /**
     * send atacks to numAttacks closest planets not owned by this robot player.
     * @param origin planet from which the attack fleet will originate
     * @param numAttacks number of attacks
     * @return list of orders
     */
    List<Order> getOrders(Planet origin, int numAttacks, int numShipsToLeaveBehind, int numYearsRemaining)
    {
        List<Order> orders = new ArrayList<>();

        int numShipsToSend = origin.getNumShips() - numShipsToLeaveBehind;

        List<Planet> planets = Galaxy.getPlanets();
        // we must set a comparator to sort all the planets relative to.
        Collections.sort(planets, new PlanetComparator(origin));

        // find the numAttack closest planets
        List<Planet> closestEnemies = new ArrayList<>();
        Iterator it = planets.iterator();
        int ct = 0;
        while (it.hasNext() && ct<numAttacks) {
            Planet p = (Planet)it.next();

            if (p.getOwner() != origin.getOwner()) {
                closestEnemies.add(p);
                ct++;
            }
        }

        // now create the orders that will send numShipsToSend/numAttacks ships to each of these planets
        int attackFleetSize = numShipsToSend/numAttacks;
        it = closestEnemies.iterator();
        while (it.hasNext()) {
            Planet target = (Planet)it.next();
            Order order = new Order(origin, target, attackFleetSize);

            // only add the order if there is enough time remaining to reach that planet.
            if (order.getTimeNeeded() < numYearsRemaining)  {
                origin.deductShips(attackFleetSize);
                orders.add(order);
            }
        }

        GameContext.log(1, "num orders=" + orders.size());
        return orders;
    }


    /**
     *
     * @return a random robot player
     */
    public static GalacticRobotPlayer getRandomRobotPlayer(String name, Planet homePlanet, Color color)
    {
        int r = (int)(RobotType.values().length * Math.random());
        return getRobotPlayer(RobotType.values()[r], name, homePlanet, color, null);
    }


    private static int seq_ = 0;

    /**
     *
     * @return  robot players in round robin order (not randomly)
     */
    public static GalacticRobotPlayer getSequencedRobotPlayer(String name, Planet homePlanet, Color color)
    {

        int r = seq_++ % RobotType.values().length;
        return getRobotPlayer(RobotType.values()[r], name, homePlanet, color, null);
    }

    /**
     *
     * @return  robot players in round robin order (not randomly)
     */
    public static GalacticRobotPlayer getSequencedRobotPlayer(String name, Planet homePlanet,
                                                              Color color, ImageIcon icon)
    {

        int r = seq_++ % RobotType.values().length;
        return getRobotPlayer(RobotType.values()[r], name, homePlanet, color, icon);
    }


    private static GalacticRobotPlayer getRobotPlayer(RobotType type, String name, Planet homePlanet,
                                                      Color color, ImageIcon icon)
    {
         switch (type) {
            case CRAZY_ROBOT: return new CrazyRobotPlayer(name, homePlanet, color, icon);
            case METHODICAL_ROBOT: return new MethodicalRobotPlayer(name, homePlanet, color, icon);
        }
        return null;
    }
}



