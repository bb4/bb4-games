/** Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.checkers.ui;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.common.ui.viewer.ViewerMouseListener;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;

/**
 *  Mouse handling for checkers game.
 *
 *  @author Barry Becker
 */
public abstract class CheckersLikeViewerMouseListener<M extends TwoPlayerMove, B extends TwoPlayerBoard<M>>
        extends ViewerMouseListener<M, B> {

    /**
     * Constructor.
     */
    public CheckersLikeViewerMouseListener(GameBoardViewer<M, B> viewer) {
        super(viewer);
    }

    @Override
    public void mousePressed( MouseEvent e ) {
        TwoPlayerController<M, B> controller =
                (TwoPlayerController<M, B>) viewer_.getController();

        if (controller.isProcessing())
            return;
        Location loc = getRenderer().createLocation(e);

        B board = controller.getBoard();
        BoardPosition position = board.getPosition( loc );
        // if there is no piece or out of bounds, then return without doing anything
        if ( (position == null) || (position.isUnoccupied()) ) {
            return;
        }
        GamePiece piece = position.getPiece();
        if ( controller.isPlayer1sTurn() != piece.isOwnedByPlayer1() )
            return; // wrong players piece

        getRenderer().setDraggedPiece(position);
    }

    @Override
    public void mouseReleased( MouseEvent e ) {
        AbstractTwoPlayerBoardViewer<M, B> viewer =
                (AbstractTwoPlayerBoardViewer<M, B>) viewer_;
        GameController<M, B> controller = viewer.getController();

        // compute the coordinates of the position that we dropped the piece on.
        Location loc = getRenderer().createLocation(e);

        if ( getRenderer().getDraggedPiece() == null )
            return; // nothing being dragged

        Board board = controller.getBoard();
        // get the original position.
        BoardPosition position = board.getPosition( getRenderer().getDraggedPiece().getLocation());

        // valid or not, we won't show the dragged piece after releasing the mouse
        getRenderer().setDraggedPiece(null);

        BoardPosition destp = board.getPosition( loc );
        if (customCheckFails(position, destp)) {
            invalidMove();
            return;
        }

        List<M> possibleMoveList = getPossibleMoveList(position);

        // verify that the move is valid before allowing it to be made
        Iterator<M> it = possibleMoveList.iterator();
        boolean found = false;

        M move = null;
        while ( it.hasNext() && !found ) {
            move = it.next();
            if ( (move.getToRow() == destp.getRow()) && (move.getToCol() == destp.getCol()) )
                found = true;
        }

        if ( !found ) {
            invalidMove();
            return; // it was not valid
        }

        if (!viewer.continuePlay( move )) {
            // then game over
            viewer.showWinnerDialog();
        }
    }

    /**
     * if there is a piece at the destination already, or destination is out of bounds,
     * then return without doing anything.
     * @return true if custom check failed.
     */
    protected abstract boolean customCheckFails(BoardPosition position, BoardPosition destp);

    /**
     *  implements the MouseMotionListener interface
     */
    @Override
    public void mouseDragged( MouseEvent e ) {
        Location loc = getRenderer().createLocation(e);

        if ( getRenderer().getDraggedShowPiece() != null ) {
            getRenderer().getDraggedShowPiece().setLocation( loc );
        }
        viewer_.repaint(); viewer_.refresh();
    }

    /**
     * @param position place to consider possible moves from.
     * @return a list of all possible moves from the given position.
     */
    protected abstract List<M> getPossibleMoveList(BoardPosition position);


    private void invalidMove() {
        JOptionPane.showMessageDialog(viewer_, GameContext.getLabel("ILLEGAL_MOVE"));
        viewer_.refresh();
    }

}