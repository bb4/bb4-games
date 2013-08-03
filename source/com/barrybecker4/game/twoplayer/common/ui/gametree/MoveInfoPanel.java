/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui.gametree;

import com.barrybecker4.ui.util.ColorMap;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.search.tree.SearchTreeNode;
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer;
import com.barrybecker4.ui.legend.ContinuousColorLegend;

import javax.swing.*;
import java.awt.*;

/**
 * Contains the move details and color legend underneath
 *
 * @author Barry Becker
 */
public final class MoveInfoPanel extends JPanel {


    MoveDetailsPanel moveDetails_;

    /**
     * Constructor
     */
    public MoveInfoPanel(ColorMap colormap) {

        moveDetails_ = new MoveDetailsPanel();

        ContinuousColorLegend colorLegend =
                new ContinuousColorLegend("Relative Score for Player", colormap, true);

        this.setLayout(new BorderLayout());
        this.add(moveDetails_, BorderLayout.CENTER);
        this.add(colorLegend, BorderLayout.SOUTH);
    }


    public void setText(AbstractTwoPlayerBoardViewer viewer, TwoPlayerMove m, SearchTreeNode lastNode) {
        moveDetails_.setText(viewer, m, lastNode);
    }
}

