/** Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.ui;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.common.ui.viewer.ViewerMouseListener;
import com.barrybecker4.game.twoplayer.mancala.MancalaController;
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBin;
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard;
import com.barrybecker4.game.twoplayer.mancala.move.MancalaMove;

import java.awt.event.MouseEvent;

/**
 *  Mouse handling for checkers game.
 *
 *  @author Barry Becker
 */
class MancalaViewerMouseListener extends ViewerMouseListener<MancalaMove, MancalaBoard> {

    /**
     * Constructor.
     */
    MancalaViewerMouseListener(GameBoardViewer<MancalaMove, MancalaBoard> viewer) {
        super(viewer);
    }

    /**
     * When the mouse is clicked on a bin, the stones in that bin should be
     * picked up and seeded counter-clockwise into other bins (skipping the opponent home bin).
     * @param e mouse event
     */
    @Override
    public void mousePressed( MouseEvent e ) {

        MancalaBoardViewer viewer = (MancalaBoardViewer) viewer_;
        MancalaController controller = (MancalaController) viewer.getController();

        if (controller.isProcessing() || controller.isDone())   {
            return;
        }
        Location loc = getRenderer().createLocation(e);

        MancalaBoard board = controller.getBoard();

        // If the player click on a home base, an opponent bin, or some invalid location, just return
        BoardPosition p = board.getPosition(loc);
        if ( (p == null) || p.isUnoccupied() )
            return;
        MancalaBin bin = (MancalaBin) p.getPiece();
        if (bin.isHome()
            || bin.getNumStones() == 0
            || bin.isOwnedByPlayer1() != controller.isPlayer1sTurn()) {
            return;
        }

        MancalaMove m =
            new MancalaMove(controller.isPlayer1sTurn(), loc, bin.getNumStones(), 0);

        viewer.refresh(); // needed to get initial move to show
        viewer.continuePlay( m );
    }

}