/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.hex.ui;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.IRectangularBoard;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.common.ui.viewer.ViewerMouseListener;
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer;
import com.barrybecker4.game.twoplayer.hex.HexController;

import java.awt.event.MouseEvent;

/**
 *  Takes a HexController as input and displays the
 *  current state of the TicTactToe Game.
 *
 *  @author Barry Becker
 */
public class HexBoardViewer extends AbstractTwoPlayerBoardViewer {

    /**
      *  Constructor
      */
    public HexBoardViewer() {
    }

    @Override
    protected HexController createController() {
        return new HexController();
    }

    @Override
    protected GameBoardRenderer getBoardRenderer() {
        return HexBoardRenderer.getRenderer();
    }

    @Override
    protected ViewerMouseListener createViewerMouseListener() {
        return new HexViewerMouseListener(this);
    }


    /**
     * @return the tooltip for the panel given a mouse event.
     */
    @Override
    public String getToolTipText( MouseEvent e ) {
        if (get2PlayerController().isProcessing() || GameContext.getDebugMode() < 1)
            return "";  // avoids concurrent modification exception

        Location loc = getBoardRenderer().createLocation(e);
        StringBuilder sb = new StringBuilder( "<html><font=-3>" );  // NON_NLS

        BoardPosition space = ((IRectangularBoard)controller_.getBoard()).getPosition( loc );
        if ( space != null && GameContext.getDebugMode() > 0 ) {
            sb.append(space.getLocation());
        }
        else {
            sb.append( loc );
        }
        sb.append( "</font></html>" );
        return sb.toString();
    }
}
