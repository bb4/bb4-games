/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.ui;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.common.ui.viewer.ViewerMouseListener;
import com.barrybecker4.game.twoplayer.blockade.BlockadeController;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer;

import java.awt.event.MouseEvent;

/**
 *  This class takes a BlockadeController as input and displays the
 *  Current state of the Blockade Game. The BlockadeController contains a BlockadeBoard
 *  which describes this state.
 *
 *  @author Barry Becker
 */
class BlockadeBoardViewer extends AbstractTwoPlayerBoardViewer {
    /**
     * Construct the viewer.
     */
    public BlockadeBoardViewer() {}

    @Override
    protected GameController createController() {
        return new BlockadeController();
    }

    @Override
    protected GameBoardRenderer getBoardRenderer() {
        return BlockadeBoardRenderer.getRenderer();
    }

    @Override
    protected ViewerMouseListener createViewerMouseListener() {
        return new BlockadeViewerMouseListener(this);
    }


    /**
     * @return the tooltip for the panel given a mouse event.
     */
    @Override
    public String getToolTipText( MouseEvent e ) {
        Location loc = getBoardRenderer().createLocation(e);
        StringBuilder sb = new StringBuilder( "<html><font=-3>" );

        BlockadeBoardPosition space = ((BlockadeBoard) controller.getBoard()).getPosition( loc );
        if ( space != null && GameContext.getDebugMode() > 0 ) {
            sb.append(space.toString());
            sb.append(space.isVisited()?":Visited":"");
            sb.append((space.isHomeBase()?(space.isHomeBase(true)?" P1 Home":"p2 Home"):""));
        }
        sb.append( "</font></html>" );
        return sb.toString();
    }

}
