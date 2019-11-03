/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic;

import java.util.Comparator;

/**
 * Sort planets by how far they are from a comparison planet.
 *
 * @author Barry Becker
 */
public class PlanetComparator implements Comparator<Planet> {

    private Planet comparisonPlanet_;

    public PlanetComparator(Planet p) {
        comparisonPlanet_ = p;
        assert(comparisonPlanet_ != null):
                "you must specify a comparison planet.";
    }


    @Override
    public int compare(Planet p1, Planet p2) {

        double p1Dist = p1.getDistanceFrom(comparisonPlanet_);
        double p2Dist = p2.getDistanceFrom(comparisonPlanet_);

        if (p1Dist < p2Dist ) {
            return -1;
        }
        else if ( p1Dist > p2Dist )  {
            return 1;
        }
        else  {
            return 0;
        }
    }

}
