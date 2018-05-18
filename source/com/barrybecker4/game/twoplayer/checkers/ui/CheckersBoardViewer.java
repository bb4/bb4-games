/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers.ui;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.IRectangularBoard;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.common.ui.viewer.ViewerMouseListener;
import com.barrybecker4.game.twoplayer.checkers.CheckersController;
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer;

import java.awt.event.MouseEvent;

/**
 *  This class takes a CheckersController as input and displays the
 *  Current state of the Checkers Game. The CheckersController contains a CheckersBoard
 *  which describes this state.
 *
 *  @author Barry Becker
 */
public class CheckersBoardViewer extends AbstractTwoPlayerBoardViewer {

    /**
     *  Construct the viewer
     */
    public CheckersBoardViewer() {
    }


    @Override
    protected GameController createController() {
        return new CheckersController();
    }

    @Override
    protected GameBoardRenderer getBoardRenderer() {
        return CheckersBoardRenderer.getRenderer();
    }

    @Override
    protected ViewerMouseListener createViewerMouseListener() {
        return new CheckersViewerMouseListener(this);
    }


    /**
     * @return the tooltip for the panel given a mouse event
     */
    @Override
    public String getToolTipText( MouseEvent e ) {

        Location loc = getBoardRenderer().createLocation(e);
        StringBuilder sb = new StringBuilder( "<html><font=-3>" );

        BoardPosition space = ((IRectangularBoard) controller.getBoard()).getPosition( loc );
        if ( space != null && space.isOccupied() && GameContext.getDebugMode() > 0 ) {
            sb.append( loc );
            sb.append("<br>");
            sb.append(space.toString());
        }
        sb.append( "</font></html>" );
        return sb.toString();
    }
}
