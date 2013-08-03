/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerBoardRenderer;
import com.barrybecker4.game.twoplayer.common.ui.TwoPlayerPieceRenderer;

import java.awt.*;
import java.util.Set;

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
class BlockadeBoardRenderer extends TwoPlayerBoardRenderer {

    private static GameBoardRenderer renderer_;

    /** wall that gets dragged around until the player places it.   */
    private BlockadeWall draggedWall_;

    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    private BlockadeBoardRenderer() {
        pieceRenderer_ = BlockadePieceRenderer.getRenderer();
    }

    public static GameBoardRenderer getRenderer() {
        if (renderer_ == null)
            renderer_ = new BlockadeBoardRenderer();
        return renderer_;
    }

    public void setDraggedWall(BlockadeWall draggedWall) {
        draggedWall_ = draggedWall;
    }

    public BlockadeWall getDraggedWall() {
        return draggedWall_;
    }

    @Override
    protected int getPreferredCellSize() {
        return 24;
    }

    @Override
    protected void drawBackground( Graphics g, Board b, int startPos, int rightEdgePos, int bottomEdgePos,
                                   int panelWidth, int panelHeight) {
        super.drawBackground(g, b, startPos, rightEdgePos, bottomEdgePos, panelWidth, panelHeight);

        BlockadeBoard bb = (BlockadeBoard)b;
        drawHomeBases(g, bb, true);
        drawHomeBases(g, bb, false);
    }

    /**
     * Draw the home bases for the specified player.
     */
    private void drawHomeBases(Graphics g, BlockadeBoard board, boolean player1) {
        // draw the home bases
        BoardPosition[] homes = board.getPlayerHomes(player1);

        int cellSize = this.getCellSize();
        int offset = Math.round((float)cellSize / 4.0f);
        TwoPlayerPieceRenderer renderer = (TwoPlayerPieceRenderer) pieceRenderer_;
        g.setColor(player1? renderer.getPlayer1Color(): renderer.getPlayer2Color());

        for (BoardPosition home : homes) {
            g.drawOval(getMargin() + (home.getCol() - 1) * cellSize + offset,
                       getMargin() + (home.getRow() - 1) * cellSize + offset,
                       2 * offset, 2 * offset);
        }
    }

    @Override
    protected void drawMarkers( Board board, PlayerList players, Graphics2D g2 ) {
        drawWalls(g2, (BlockadeBoard)board);
        drawShortestPaths(g2, (BlockadeBoard)board);
    }

    private void drawWalls(Graphics2D g2, BlockadeBoard board) {

        for ( int i = 1; i <= board.getNumRows(); i++ )  {
            for ( int j = 1; j <= board.getNumCols(); j++ ) {
                BlockadeBoardPosition pos = board.getPosition( i, j );
                BlockadePieceRenderer.renderWallAtPosition(g2, pos, cellSize, getMargin());
            }
        }
        if ( draggedWall_ != null ) {
            drawDraggedWall(g2, cellSize);
        }
    }

    private void drawShortestPaths(Graphics2D g2, BlockadeBoard board) {
        int numPieces = 0;
        for ( int i = 1; i <= board.getNumRows(); i++ )  {
            for ( int j = 1; j <= board.getNumCols(); j++ ) {
                BlockadeBoardPosition pos = board.getPosition( i, j );
                if (pos.isOccupied())       {
                    pieceRenderer_.render(g2, pos, cellSize, getMargin(), board);
                    if (GameContext.getDebugMode() > 0)
                       PathRenderer.drawShortestPaths(g2, pos, board, cellSize);
                    numPieces++;
                }
            }
        }
        assert(numPieces >= 3) : "unexpected number of pieces: " + numPieces;
    }

    /**
     * Draw the dragged wall if there is one.  The wall snaps to valid positions as the use drags it.
     */
    private void drawDraggedWall(Graphics2D g2, int cellSize) {
        // first remember the walls currently there (if any) so they can be restored.
        Set<BlockadeBoardPosition> hsPositions = draggedWall_.getPositions();
        for (BlockadeBoardPosition pos : hsPositions) {
            boolean vertical = draggedWall_.isVertical();
            BlockadeWall cWall = vertical ? pos.getEastWall() : pos.getSouthWall();

            // temporarily set the dragged wall long enough to render it.
            if (vertical)
                pos.setEastWall(draggedWall_);
            else
                pos.setSouthWall(draggedWall_);

            BlockadePieceRenderer.renderWallAtPosition(g2, pos, cellSize, getMargin());

            // restore the actual wall
            if (vertical)
                pos.setEastWall(cWall);
            else
                pos.setSouthWall(cWall);
        }
    }

}

