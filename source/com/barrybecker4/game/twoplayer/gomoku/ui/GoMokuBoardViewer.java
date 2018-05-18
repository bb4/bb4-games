/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.ui;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.IRectangularBoard;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.common.ui.viewer.ViewerMouseListener;
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer;
import com.barrybecker4.game.twoplayer.gomoku.GoMokuController;

import java.awt.event.MouseEvent;

/**
 *  Takes a GoMokuController as input and displays the
 *  current state of the GoMoku Game. The GoMokuController contains a GoMokuBoard
 *  which describes this state.
 *
 *  @author Barry Becker
 */
public class GoMokuBoardViewer extends AbstractTwoPlayerBoardViewer {

    public GoMokuBoardViewer() {}

    @Override
    protected GoMokuController createController() {
        return new GoMokuController();
    }

    @Override
    protected GameBoardRenderer getBoardRenderer() {
        return GoMokuBoardRenderer.getRenderer();
    }

    @Override
    protected ViewerMouseListener createViewerMouseListener() {
        return new GoMokuViewerMouseListener(this);
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

        BoardPosition space = ((IRectangularBoard) controller.getBoard()).getPosition( loc );
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
