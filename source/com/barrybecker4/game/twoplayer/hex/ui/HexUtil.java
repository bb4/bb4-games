// Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.hex.ui;

/**
 * Constants and static methods for working with hexagons
 */
public class HexUtil {

    private static final double DEG_TO_RAD =  Math.PI / 180.0;
    public static final double ROOT3 = Math.sqrt(3.0);
    public static final double ROOT3D2 = ROOT3/2.0;

    /**
     * Create an instance
     */
    private HexUtil() {}


    public static double rad(double angleInDegrees) {
        return angleInDegrees * DEG_TO_RAD;
    }
}
