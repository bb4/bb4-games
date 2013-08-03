/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.player;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.player.PlayerAction;
import com.barrybecker4.game.multiplayer.common.MultiGameController;
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer;
import com.barrybecker4.game.multiplayer.galactic.GalacticAction;
import com.barrybecker4.game.multiplayer.galactic.Galaxy;
import com.barrybecker4.game.multiplayer.galactic.Order;
import com.barrybecker4.game.multiplayer.galactic.Planet;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.ImageIcon;
import java.awt.Color;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents an Admiral commanding an intergalactic fleet of starships.
 *
 * @author Barry Becker
 */
public class GalacticPlayer extends MultiGamePlayer
{
    private static final String GALACTIC_IMAGE_DIR = GameContext.GAME_RESOURCE_ROOT + "multiplayer/galactic/ui/images/";

    // this player's home planet. (like earth is for humans)
    private Planet homePlanet_;

    // a list of outstanding Orders
    List<Order> orders_;

    ImageIcon icon_;
    String iconBaseName_;
    int iconIndex_;

    // ? have list of planets owned?

    public static final int DEFAULT_NUM_SHIPS = 100;


    /**
     * use this constructor if you already have an icon for the palyer.
     */
    GalacticPlayer(String name, Planet homePlanet, Color color, boolean isHuman, ImageIcon icon) {
        this(name, homePlanet, color, isHuman);
        icon_ = icon;
    }


    GalacticPlayer(String name, Planet homePlanet, Color color, boolean isHuman) {
        super(name, color, isHuman);
        homePlanet_ = homePlanet;
        homePlanet_.setOwner(this);
        orders_ = new LinkedList<Order>();
    }

    /**
     * Factory method for creating Galactic players of the appropriate type.
     * @return new galactic player
     */
    public static GalacticPlayer createGalacticPlayer(
              String name, Planet homePlanet, Color color, boolean isHuman) {
       if (isHuman)
           return new GalacticHumanPlayer(name, homePlanet, color);
        else
           return GalacticRobotPlayer.getSequencedRobotPlayer(name, homePlanet, color);
    }

    /**
     * Factory method for creating Galactic players of the appropriate type.
     * @return new galactic player
     */
    public static GalacticPlayer createGalacticPlayer(String name, Planet homePlanet, Color color,
                                                      boolean isHuman, ImageIcon icon) {
       if (isHuman)
           return new GalacticHumanPlayer(name, homePlanet, color, icon);
        else
           return GalacticRobotPlayer.getSequencedRobotPlayer(name, homePlanet, color, icon);
    }

    /**
     *
     * @param i index of player
     * @return  the default name for player i
     */
    public String getDefaultName(int i)  {
        Object[] args = {Integer.toString(i)};
        return MessageFormat.format(GameContext.getLabel("GALACTIC_DEFAULT_NAME"), args );
    }

    public Planet getHomePlanet()  {
        return homePlanet_;
    }

    public void setHomePlanet( Planet homePlanet ) {
        this.homePlanet_ = homePlanet;
    }

    public ImageIcon getIcon() {
        if (icon_ == null) {
            icon_ = GUIUtil.getIcon(GALACTIC_IMAGE_DIR + iconBaseName_ + (iconIndex_ + 1) + ".png");
        }
        return icon_;
    }

    /**
     * @param orders set the current list of orders for the player
     */
    public void setOrders(List<Order> orders)  {
        if (orders==null)
            return;
        orders_.clear();
        for (Order order : orders) {
            orders_.add(order);
        }
    }

    /**
     * A galactic action is a set of orders for directing ships to planets.
     */
    @Override
    public PlayerAction getAction(MultiGameController controller) {
        return new GalacticAction(getName(), getOrders());
    }

    /**
     * A galactic action is a set of orders for directing ships to planets.
     */
    @Override
    public void setAction(PlayerAction action) {
        setOrders(((GalacticAction) action).getOrders());
    }


    /**
     * @return get the current list of orders for the player
     */
    public List<Order> getOrders()  {
        GameContext.log(1,  "orders_="+orders_ );
        return orders_;
    }

    /**
     * The total ships is computed by summing the number of ships
     * at each of the player owned planets plus the number of ships that
     * are in transit.
     * @return total num ships under this players command
     */
    public int getTotalNumShips() {
        int totalNumShips = 0;
        for (Order order : orders_) {
            totalNumShips += order.getFleetSize();
        }
        List<Planet> ownedPlanets = Galaxy.getPlanets(this);
        for (Planet ownedPlanet : ownedPlanets) {

            totalNumShips += ownedPlanet.getNumShips();
        }
        return totalNumShips;
    }

    /**
     * @return  the total production capacity of all the planets owned by this player
     */
    public int getTotalProductionCapacity()  {
         int totalCapacity = 0;
        List<Planet> ownedPlanets = Galaxy.getPlanets(this);
        for (Planet ownedPlanet : ownedPlanets) {

            totalCapacity += ownedPlanet.getProductionCapacity();
        }
        return totalCapacity;
    }

    @Override
    protected String additionalInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Fleet size: "+getTotalNumShips());
        sb.append("Home planet: "+homePlanet_.getName());
        return sb.toString();
    }

}



