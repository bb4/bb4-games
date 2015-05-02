/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.pente.ui;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.common.ui.viewer.ViewerMouseListener;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.pente.PenteBoard;
import com.barrybecker4.game.twoplayer.pente.PenteController;

import java.awt.event.MouseEvent;

/**
 *  Mouse handling for checkers game.
 *
 *  @author Barry Becker
 */
class PenteViewerMouseListener extends ViewerMouseListener<TwoPlayerMove, PenteBoard> {

    /**
     * Constructor.
     */
    PenteViewerMouseListener(GameBoardViewer<TwoPlayerMove, PenteBoard> viewer) {
        super(viewer);
    }


    @Override
    public void mousePressed( MouseEvent e ) {

        PenteBoardViewer viewer = (PenteBoardViewer) viewer_;
        PenteController controller = (PenteController) viewer.getController();

        if (controller.isProcessing() || controller.isDone())   {
            return;
        }
        Location loc = getRenderer().createLocation(e);

        PenteBoard board = (PenteBoard) controller.getBoard();

        // if there is already a piece where the user clicked or its
        // out of bounds, then return without doing anything
        BoardPosition p = board.getPosition( loc);
        if ( (p == null) || !p.isUnoccupied() )
            return;

        TwoPlayerMove m =
            TwoPlayerMove.createMove( loc.getRow(), loc.getCol(), 0,
                                      new GamePiece(controller.isPlayer1sTurn()));

        viewer.continuePlay( m );
    }

}