/** Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.ui;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.common.ui.viewer.ViewerMouseListener;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard;
import com.barrybecker4.game.twoplayer.mancala.MancalaController;

import java.awt.event.MouseEvent;

/**
 *  Mouse handling for checkers game.
 *
 *  @author Barry Becker
 */
class MancalaViewerMouseListener extends ViewerMouseListener {

    /**
     * Constructor.
     */
    MancalaViewerMouseListener(GameBoardViewer viewer) {
        super(viewer);
    }


    @Override
    public void mousePressed( MouseEvent e ) {

        MancalaBoardViewer viewer = (MancalaBoardViewer) viewer_;
        MancalaController controller = (MancalaController) viewer.getController();

        if (controller.isProcessing() || controller.isDone())   {
            return;
        }
        Location loc = getRenderer().createLocation(e);

        MancalaBoard board = (MancalaBoard) controller.getBoard();

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