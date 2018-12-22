/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.AbstractGameProfiler;
import com.barrybecker4.game.common.GameProfiler;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.analysis.BoardAnalyzer;
import com.barrybecker4.game.twoplayer.blockade.board.analysis.PossibleMoveAnalyzer;
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove;
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall;
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.WallPlacementValidator;
import com.barrybecker4.game.twoplayer.blockade.board.path.PathList;
import com.barrybecker4.game.twoplayer.blockade.board.path.PlayerPathLengths;
import com.barrybecker4.game.twoplayer.common.TwoPlayerBoard;

import java.util.List;
import java.util.Set;

/**
 * Defines the structure of the blockade board and the pieces on it.
 * Each BlockadeBoardPosition can contain a piece and south and east walls.
 *
 * @author Barry Becker
 */
public class BlockadeBoard extends TwoPlayerBoard<BlockadeMove> {

    /** Home base positions for both players. */
    private Homes homes;

    private BoardAnalyzer boardAnalyzer;
    private WallPlacementValidator wallValidator;


    /**
     * Constructor.
     * @param numRows number of rows in the board grid.
     * @param numCols number of rows in the board grid.
     */
    public BlockadeBoard(int numRows, int numCols) {
        homes = new Homes();
        setSize(numRows, numCols);
        boardAnalyzer = new BoardAnalyzer(this);
        wallValidator = new WallPlacementValidator(this);
    }

    /** copy constructor */
    private BlockadeBoard(BlockadeBoard b) {
        super(b);
        boardAnalyzer = new BoardAnalyzer(this);
        homes = new Homes(b.homes);
    }

    @Override
    public BlockadeBoard copy() {
        return new BlockadeBoard(this);
    }

    @Override
    public BlockadeBoardPosition getPosition(int row, int col) {
        return (BlockadeBoardPosition) super.getPosition(row, col);
    }

    @Override
    public final BlockadeBoardPosition getPosition( Location loc ) {
        return (BlockadeBoardPosition) super.getPosition(loc.row(), loc.col());
    }

    /**
     * reset the board to its initial state.
     */
    @Override
    public void reset() {
        super.reset();

        if (homes == null) {
            homes = new Homes();
        }
        homes.init(getNumRows(), getNumCols());

        for (int i=0; i<Homes.NUM_HOMES; i++) {
            setPosition(homes.getPlayerHomes(true)[i]);
            setPosition(homes.getPlayerHomes(false)[i]);
        }
    }

    @Override
    protected BoardPosition getPositionPrototype() {
        return new BlockadeBoardPosition(1, 1);
    }

    /**
     * If the Blockade game has more than this many moves, then we assume it is a draw.
     * We make this number big, because in blockade it is impossible to have a draw.
     * I haven't proved it, but I think it is impossible for the number of moves to exceed
     * the rows*cols.
     * @return assumed maximum number of moves.
     */
    @Override
    public int getMaxNumMoves() {
        return Integer.MAX_VALUE;
    }

    /**
     * @return player1's home bases.
     */
    public BoardPosition[] getPlayerHomes(boolean player1) {
        return homes.getPlayerHomes(player1);
    }

    /**
     * Blockade pieces can move 1 or 2 spaces in any direction.
     * However, only in rare cases would you ever want to move only 1 space.
     * For example, move 1 space to land on a home base, or in preparation to jump an opponent piece.
     * They may jump over opponent pieces that are in the way (but they do not capture it).
     * The wall is ignored for the purposes of this method.
     *     Moves are only allowed if the candidate position is unoccupied (unless a home base) and if
     * it has not been visited already. The visited part is only significant when we are doing a traversal
     * such as when we are finding the shortest paths to home bases.
     * <pre>
     *       #     There are at most 12 moves from this position
     *     #*#    (some of course may be blocked by walls)
     *   #*O*#    The most common being marked with #'s.
     *     #*#
     *       #
     * </pre>
     *
     * We only add the one space moves if
     *   1. jumping 2 spaces in that direction would land on an opponent pawn,
     *   2. or moving one space moves lands on an opponent home base.
     *
     * @param position we are moving from
     * @param op1 true if opposing player is player1; false if player2.
     * @return a list of legal piece movements
     */
    public List<BlockadeMove> getPossibleMoveList(BlockadeBoardPosition position, boolean op1) {
        return new PossibleMoveAnalyzer(this).getPossibleMoveList(position, op1);
    }

    /**
     * @param player1 the last player to make a move.
     * @return all the opponent's shortest paths to your home bases.
     */
    public PathList findAllOpponentShortestPaths(boolean player1) {

        return boardAnalyzer.findAllOpponentShortestPaths(player1);
    }

    /**
     * Find the shortest paths from the specified position to opponent homes.
     * We use DefaultMutableTreeNodes to represent nodes in the path.
     * If the number of paths returned by this method is less than NUM_HOMES,
     * then there has been an illegal wall placement, since according to the rules
     * of the game there must always be paths from all pieces to all opponent homes.
     * If a pawn has reached an opponent home then the path magnitude is 0 and that player won.
     *
     * @param position position to check shortest paths for.
     * @return the NUM_HOMES shortest paths from toPosition.
     */
    public PathList findShortestPaths( BlockadeBoardPosition position )  {
        return boardAnalyzer.findShortestPaths(position);
    }

    /*
     * It is illegal to place a wall at a position that overlaps
     * or intersects another wall, or if the wall prevents one of the pawns from reaching an
     * opponent home.
     * @param wall to place. has not been placed yet.
     * @param location where the wall is to be placed.
     * @return an error string if the wall is not a legal placement on the board.
     */
    public String checkLegalWallPlacement(BlockadeWall wall, Location location) {
        return wallValidator.checkLegalWallPlacement(wall, location, boardAnalyzer);
    }

    /**
     * find all the paths from each player's pawn to each opponent base.
     * @return the lengths of all the paths from each player's pawn to each opponent base.
     */
    public PlayerPathLengths findPlayerPathLengths() {

        return boardAnalyzer.findPlayerPathLengths();
    }


    public void addWall(BlockadeWall wall) {
        showWall(wall, true);
    }

    public void removeWall(BlockadeWall wall) {
        showWall(wall, false);
    }

    /**
     * Shows or hides this wall on the game board.
     * @param show whether to show or hide the wall.
     */
    private void showWall(BlockadeWall wall, boolean show) {
        Set<BlockadeBoardPosition> positions = wall.getPositions();
        assert positions.isEmpty() || (positions.size() == 2) : "positions="+positions;

        for (BlockadeBoardPosition position : positions) {
            // since p may be from a different board, we need to make sure that we set the wall for this board.
            BlockadeBoardPosition otherPos = getPosition(position.getLocation());
            if (wall.isVertical()) {
                assert (!show || !otherPos.isEastBlocked()) : "East blocked already!";
                otherPos.setEastWall(show ? wall : null);
            } else {
                assert (!show || !otherPos.isSouthBlocked()) : "South blocked already!";
                otherPos.setSouthWall(show ? wall : null);
            }
        }
    }

    private AbstractGameProfiler getProfiler() {
        return GameProfiler.getInstance();
    }

    /**
     * Given a move specification, execute it on the board.
     * This places the players symbol at the position specified by move,
     * and then also places a wall somewhere.
     * @return true if the move was made successfully
     */
    @Override
    protected boolean makeInternalMove( BlockadeMove move ) {
        getProfiler().startMakeMove();
        BlockadeBoardPosition toPos = getPosition(move.getToLocation());
        // in the rare event that we capture an opponent on his base, remember it so it can be undone.
        if (toPos.isOccupiedHomeBase(!move.isPlayer1())) {
            move.capturedOpponentPawn = toPos.getPiece();
        }
        toPos.setPiece(move.getPiece());

        // we also need to place a wall.
        if (move.getWall() != null) {
            addWall(move.getWall());
        }
        getPosition(move.getFromRow(), move.getFromCol()).clear();
        getProfiler().stopMakeMove();
        return true;
    }

    /**
     * for Blockade, undoing a move means moving the piece back and
     * restoring any captures. It is very rare that there was a capture.
     * It can only happen on the final winning move.
     */
    @Override
    protected void undoInternalMove( BlockadeMove move ) {
        getProfiler().startUndoMove();
        BoardPosition startPos = getPosition(move.getFromRow(), move.getFromCol());
        startPos.setPiece( move.getPiece() );
        BlockadeBoardPosition toPos = getPosition(move.getToLocation());
        toPos.clear();
        if (move.capturedOpponentPawn != null) {
            toPos.setPiece(move.capturedOpponentPawn);
            move.capturedOpponentPawn = null;
        }

        // remove the wall that was placed by this move.
        if (move.getWall() != null) {
            removeWall(move.getWall());
        }
        getProfiler().stopUndoMove();
    }

    /**
     * Num different states.
     * There are 12 unique states for a position. 4 ways the walls can be arranged around the position.
     * @return number of different states this position can have.
     */
    @Override
    public int getNumPositionStates() {
        return 12;
    }

    /**
     * The index of the state for this position.
     * @return The index of the state for this position.
     */
    @Override
    public  int getStateIndex(BoardPosition pos) {
        return ((BlockadeBoardPosition) pos).getStateIndex();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(100);
        // print just the walls and pawns
        for ( int i = 1; i <= getNumRows(); i++ ) {
            for ( int j = 1; j <= getNumCols(); j++ ) {
                BlockadeBoardPosition pos = getPosition(i, j);
                if (pos.isOccupied())
                    buf.append(pos).append("\n");
                if (pos.getEastWall() != null)
                    buf.append("East wall at: ").append(i).append(' ').append(j).append('\n');
                if (pos.getSouthWall() != null)
                    buf.append("South wall at: ").append(i).append(' ').append(j).append('\n');
            }
        }
        return buf.toString();
    }
}
