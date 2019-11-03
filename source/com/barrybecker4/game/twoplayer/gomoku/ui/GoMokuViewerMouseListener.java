/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.gomoku.ui;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.common.ui.viewer.ViewerMouseListener;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.gomoku.GoMokuBoard;
import com.barrybecker4.game.twoplayer.gomoku.GoMokuController;

import java.awt.event.MouseEvent;

/**
 *  Mouse handling for checkers game.
 *
 *  @author Barry Becker
 */
class GoMokuViewerMouseListener extends ViewerMouseListener<TwoPlayerMove, GoMokuBoard> {

    /**
     * Constructor.
     */
    GoMokuViewerMouseListener(GameBoardViewer<TwoPlayerMove, GoMokuBoard> viewer) {
        super(viewer);
    }


    @Override
    public void mousePressed( MouseEvent e ) {

        GoMokuBoardViewer viewer = (GoMokuBoardViewer) this.viewer;
        GoMokuController controller = (GoMokuController) viewer.getController();

        if (controller.isProcessing() || controller.isDone())   {
            return;
        }
        Location loc = getRenderer().createLocation(e);

        GoMokuBoard board = (GoMokuBoard) controller.getBoard();

        // if there is already a piece where the user clicked or its
        // out of bounds, then return without doing anything
        BoardPosition p = board.getPosition( loc);
        if ( (p == null) || !p.isUnoccupied() )
            return;

        TwoPlayerMove m =
            TwoPlayerMove.createMove( loc.row(), loc.col(), 0,
                                      new GamePiece(controller.isPlayer1sTurn()));

        viewer.continuePlay( m );
    }

}
