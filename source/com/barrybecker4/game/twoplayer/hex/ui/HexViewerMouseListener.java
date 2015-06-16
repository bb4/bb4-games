/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.hex.ui;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.common.ui.viewer.ViewerMouseListener;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.gomoku.GoMokuBoard;
import com.barrybecker4.game.twoplayer.gomoku.GoMokuController;
import com.barrybecker4.game.twoplayer.gomoku.ui.GoMokuBoardViewer;
import com.barrybecker4.game.twoplayer.hex.HexBoard;
import com.barrybecker4.game.twoplayer.hex.HexController;

import java.awt.event.MouseEvent;

/**
 *  Mouse handling for checkers game.
 *
 *  @author Barry Becker
 */
class HexViewerMouseListener extends ViewerMouseListener<TwoPlayerMove, HexBoard> {

    /**
     * Constructor.
     */
    HexViewerMouseListener(GameBoardViewer<TwoPlayerMove, HexBoard> viewer) {
        super(viewer);
    }


    @Override
    public void mousePressed( MouseEvent e ) {

        HexBoardViewer viewer = (HexBoardViewer) viewer_;
        HexController controller = (HexController) viewer.getController();

        if (controller.isProcessing() || controller.isDone())   {
            return;
        }
        Location loc = getRenderer().createLocation(e);

        HexBoard board = controller.getBoard();

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