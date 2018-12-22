/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.ui;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.common.ui.viewer.GameBoardViewer;
import com.barrybecker4.game.common.ui.viewer.ViewerMouseListener;
import com.barrybecker4.game.twoplayer.blockade.BlockadeController;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove;
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;

/**
 *  Mouse handling for blockade game.
 *
 *  @author Barry Becker
 */
class BlockadeViewerMouseListener extends ViewerMouseListener<BlockadeMove, BlockadeBoard> {

    private BlockadeMove currentMove;

    /** becomes true if the player has placed his pawn on an opponent base. */
    private boolean hasWon;

    /** this becomes true when the player needs to place a wall instead of a piece during his turn.  */
    private boolean wallPlacingMode;

    private WallPlacer wallPlacer;


    /**
     * Constructor.
     */
    public BlockadeViewerMouseListener(GameBoardViewer<BlockadeMove, BlockadeBoard> viewer) {
        super(viewer);
        reset();
        wallPlacer = new WallPlacer((BlockadeBoard) viewer.getBoard());
    }

    @Override
    public void reset() {
        currentMove = null;
        hasWon = false;
        wallPlacingMode = false;
    }

    @Override
    public void mousePressed( MouseEvent e )
    {
        BlockadeBoardViewer viewer = (BlockadeBoardViewer) this.viewer;
        if (viewer.get2PlayerController().isProcessing() || wallPlacingMode)
            return;

        Board board = viewer.getBoard();
        Location loc = getRenderer().createLocation(e);
        BoardPosition position = board.getPosition( loc );

        // if there is no piece, or out of bounds, then return without doing anything
        if ( (position == null) || (position.isUnoccupied()) ) {
            return;
        }
        GamePiece piece = position.getPiece();
        if ( viewer.get2PlayerController().isPlayer1sTurn() != piece.isOwnedByPlayer1() )
            return; // wrong players piece

        getRenderer().setDraggedPiece(position);
    }

    /**
     * When the mouse is released either the piece or a wall is being placed
     * depending on the value of wallPlacingMode.
     */
    @Override
    public void mouseReleased( MouseEvent event )
    {
        // compute the coordinates of the position that we dropped the piece on.
        BlockadeBoardViewer viewer = (BlockadeBoardViewer) this.viewer;
        Location loc = getRenderer().createLocation(event);

        if (!wallPlacingMode)  {
            boolean placed = placePiece( loc );
            if (!placed) {
                getRenderer().setDraggedPiece(null);
            }
            if (hasWon) {
                viewer.continuePlay(currentMove);
            }
            return;
        }

        boolean wallPlaced = placeWall(loc, currentMove);
        if (!wallPlaced) {
            return;
        }

        viewer.continuePlay(currentMove);
    }


    /**
     * implements the MouseMotionListener interface.
     */
    @Override
    public void mouseDragged( MouseEvent e )
    {
        GameBoardRenderer renderer = getRenderer();
        Location loc = renderer.createLocation(e);

        if ( renderer.getDraggedShowPiece() != null ) {
            renderer.getDraggedShowPiece().setLocation( loc );
        }
        viewer.repaint();
    }

    /**
     * If we are in wallPlacingMode, then we show the wall being dragged around.
     * When the player clicks the wall is irrevocably placed.
     * @param e mouse event
     */
    @Override
    public void mouseMoved( MouseEvent e )
    {
        BlockadeBoardViewer viewer = (BlockadeBoardViewer) this.viewer;
        if (wallPlacingMode)  {

            // show the hovering wall
            BlockadeBoard board = (BlockadeBoard)viewer.getBoard();
            // now show it in the new location
            Location loc = getRenderer().createLocation(e);
            if (board.getPosition(loc)==null) {
                return;  // out of bounds
            }

            BlockadeBoardPosition[] positions =
                    wallPlacer.getCellLocations(e.getX(),  e.getY(), loc, getRenderer().getCellSize());

            ((BlockadeBoardRenderer)getRenderer()).setDraggedWall(
                    new BlockadeWall(positions[0], positions[1]));

            viewer.repaint();
        }
    }


    /**
     * Place the piece after dropping it two spaces from where it was.
     * @param loc location where the piece was placed.
     * @return true if a piece is successfully moved.
     */
    private boolean placePiece(Location loc) {
        if ( getRenderer().getDraggedPiece() == null )   {
            return false; // nothing being dragged
        }

        BlockadeController controller = (BlockadeController) viewer.getController();
        BlockadeBoard board = controller.getBoard();
        // get the original position.
        BlockadeBoardPosition position =
                board.getPosition(getRenderer().getDraggedPiece().getLocation());

        // valid or not, don't show the dragged piece after releasing the mouse.
        getRenderer().setDraggedPiece(null);

        BlockadeMove m = checkAndGetValidMove(position, loc);
        if (m == null) {
            return false;
        }

        // make sure that the piece shows while we decide where to place the wall.
        currentMove = m;
        GameContext.log(1, "legal human move :" + m.toString());
        position.getPiece().setTransparency((short) 0);
        boolean isPlayer1 = position.getPiece().isOwnedByPlayer1();
        BlockadeBoardPosition newPosition =
                board.getPosition(currentMove.getToRow(), currentMove.getToCol());
        newPosition.setPiece(position.getPiece());
        position.setPiece(null);
        viewer.refresh();

        if (newPosition.isHomeBase( !isPlayer1 )) {
            hasWon = true;
            assert(isPlayer1 == (controller.getCurrentPlayer() == controller.getPlayers().getFirstPlayer()));
            controller.getCurrentPlayer().setWon(true);
        }
        else {
            // piece moved! now a wall needs to be placed.
            wallPlacingMode = true;
        }
        return true;
    }

    /**
     * @param origPosition place dragged from
     * @param placedLocation location dragged to.
     * @return the valid move else and error is shown and null is returned.
     */
    private BlockadeMove checkAndGetValidMove(BlockadeBoardPosition origPosition, Location placedLocation) {
        BlockadeController controller = (BlockadeController) viewer.getController();
        BlockadeBoard board = controller.getBoard();
        List<BlockadeMove> possibleMoveList = controller.getPossibleMoveList(origPosition);

        BlockadeBoardPosition destpos = board.getPosition( placedLocation );
        if (customCheckFails(destpos)) {
            JOptionPane.showMessageDialog(viewer, GameContext.getLabel("ILLEGAL_MOVE"));
            return null;
        }
        // verify that the move is valid before allowing it to be made.
        Iterator it = possibleMoveList.iterator();
        boolean found = false;

        BlockadeMove m = null;
        while ( it.hasNext() && !found ) {
            m = (BlockadeMove) it.next();
            if ( (m.getToRow() == destpos.getRow()) && (m.getToCol() == destpos.getCol()) )
                found = true;
        }

        if ( !found ) {
            return null; // it was not valid
        }
        return m;
    }


    /**
     * If there is a piece at the destination already, or destination is out of bounds,
     * then return without doing anything.
     * @param destpos position to move to.
     * @return true if this is not a valid move.
     */
    private boolean customCheckFails(BlockadeBoardPosition destpos) {
        return  ( (destpos == null) || (destpos.isOccupied() && !destpos.isHomeBase()) );
    }

    /**
     * Place the wall if it is a legal placement.
     * We need to give a warning message if they try to place a wall at a position that overlaps
     * or intersects another wall, or if the wall prevents one of the pawns from reaching an
     * opponent home.
     *
     * @param loc location where the wall was placed.
     * @return true if a wall is successfully placed.
     */
    private boolean placeWall(Location loc, BlockadeMove m) {
        BlockadeBoardRenderer bbRenderer = (BlockadeBoardRenderer) getRenderer();
        BlockadeWall draggedWall = bbRenderer.getDraggedWall();
        if (draggedWall == null)
            return false;

        // first check to see if its a legal placement
        BlockadeBoard board = (BlockadeBoard) viewer.getBoard();

        // check wall intersection and overlaps.
        //assert(draggedWall_ != null);
        String sError = board.checkLegalWallPlacement(draggedWall, loc);

        if (sError != null) {
            JOptionPane.showMessageDialog(viewer, sError);
            bbRenderer.setDraggedWall(null);
            return false;
        }
        else {
            // wall placed successfully.
            wallPlacingMode = false;
            m.setWall(draggedWall);
            bbRenderer.setDraggedWall(null);
            return true;
        }
    }
}
