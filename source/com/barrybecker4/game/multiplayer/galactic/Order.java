/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * An order describes how a fleet of ships should be deployed.
 * ie where coming from, where going to, and how many ships.
 *
 * @author Barry Becker
 */
public class Order {

    private Planet origin;
    private Planet destination;
    private int fleetSize;
    private final Point2D currentLocation;
    private GalacticPlayer owner;
    private boolean hasArrived = false;

    /** distance traveled per year */
    private static final int NORMAL_SPEED = 2;

    private static final double INTERSECT_TOLERANCE = 0.2;

    /** constructor */
    public Order(Planet origin, Planet target, int fleetSize, Point2D loc) {
        commonInit(origin, target, fleetSize);
        currentLocation = loc;
    }

    /** constructor */
    public Order(Planet origin, Planet target, int fleetSize) {
       commonInit(origin, target, fleetSize);
       currentLocation = new Point2D.Double(origin.getLocation().col(), origin.getLocation().row());
    }

    private void commonInit(Planet origin, Planet target, int fleetSize) {
        this.origin = origin;
        assert(origin!=null);
        destination = target;
        this.fleetSize = fleetSize;

        // we need to store who the current owner is, because the owner of the planet of origin may change.
        owner = origin.getOwner();
        assert(owner !=null);
    }


    public Planet getOrigin()  {
        return origin;
    }

    public Planet getDestination() {
        return destination;
    }

    public int getFleetSize() {
        return fleetSize;
    }

    /**
     * @return the player who issued this order
     */
    public GalacticPlayer getOwner() {
        assert(owner != null);
        return owner;
    }

    private double getDistanceRemaining() {
        return destination.getDistanceFrom( getCurrentLocation() );
    }

    public double getTimeRemaining() {
        return getDistanceRemaining() / NORMAL_SPEED;
    }

    public boolean hasArrived() {
        return hasArrived;
    }

    /**
     * adjust the current location so it represents the position of the fleet a year later.
     */
    public void incrementYear() {
        Point2D oldLocation = new Point2D.Double(currentLocation.getX(), currentLocation.getY());

        Point2D v = getUnitDirection();
        v.setLocation(NORMAL_SPEED * v.getX(), NORMAL_SPEED * v.getY());

        currentLocation.setLocation( currentLocation.getX() + v.getX(),  currentLocation.getY() + v.getY());

        // if the destination planet lies on the line from where we were to where we are now,
        // then we overshot. set the currentLocation to the destination planet location.
        Line2D.Double line = new Line2D.Double(oldLocation, currentLocation);
        Location dLoc = destination.getLocation();
        if (line.intersects(dLoc.col(),  dLoc.row(), INTERSECT_TOLERANCE, INTERSECT_TOLERANCE)) {
            currentLocation.setLocation(dLoc.col(), dLoc.row());
            // the order never leaves once it has arrived. The order will be destroyed.
            hasArrived = true;
        }
    }

    public int getTimeNeeded() {
        return (int)(getDistanceRemaining() / NORMAL_SPEED + 1);
    }

    /**
     * @return a unit vector pointing in the current direction of movement.
     */
    private Point2D getUnitDirection() {
        Location dLoc = destination.getLocation();
        Point2D unitVec =
                new Point2D.Double(dLoc.col() - currentLocation.getX(), dLoc.row() - currentLocation.getY());
        double dist = unitVec.distance(new Point2D.Double(0, 0));
        unitVec.setLocation(unitVec.getX()/dist, unitVec.getY()/dist);
        return unitVec;
    }

    public Point2D getCurrentLocation() {
        return currentLocation;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder(50);
        sb.append("Target: ").append(destination).append('\n');  // NON-NLS
        sb.append("Fleet size: ").append(fleetSize).append('\n');  // NON-NLS
        sb.append("Location: ").append(currentLocation).append('\n'); // NON-NLS
        sb.append("Owner: ").append(owner).append('\n');  // NON-NLS
        return sb.toString();
    }
}



