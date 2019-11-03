/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.ui.rendering;

import com.barrybecker4.ui.util.ColorMap;

import java.awt.*;

/**
 * A colormap for coloring the groups according to how healthy they are.
 * Blue will be healthy, while red will be near dead.
 *
 * @author Barry Becker
 */
public class GoColorMap extends ColorMap {

    private static final double[] myValues_ = {-1.1, -1.0, -0.2, 0.1, -0.05,
                                               0.0,
                                               0.05, 0.1, 0.2, 1.0, 1.1};
    /** base transparency value.*/
    private static final int CM_TRANS = 50;

    /** this colormap is used to show a spectrum of colors representing a group's health status.*/
    private static final Color[] myColors_ = {
        new Color( 200, 0, 0, CM_TRANS + 40 ),  // min. start of low range.
            new Color( 255, 20, 0, CM_TRANS ),
            new Color( 250, 130, 0, CM_TRANS ),
            new Color( 250, 255, 0, CM_TRANS ),
            new Color( 200, 200, 90, CM_TRANS ),
        new Color( 220, 220, 220, 0 ),           // middle
            new Color( 30, 220, 20, CM_TRANS ),  // high values range.
            new Color( 0, 255, 0, CM_TRANS ),
            new Color( 0, 255, 255, CM_TRANS ),
            new Color( 0, 0, 255, CM_TRANS ),
        new Color( 150, 0, 250, CM_TRANS + 40 )};     // max



    /**
     * our own custom colormap for visualizing values in Go.
     */
    public GoColorMap()
    {
        super(myValues_, myColors_);
    }

}
