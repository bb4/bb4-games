/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.board.analysis;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.path.Path;
import com.barrybecker4.game.twoplayer.blockade.board.path.PathList;
import com.barrybecker4.game.twoplayer.blockade.board.path.PlayerPathLengths;
import com.barrybecker4.game.twoplayer.blockade.board.Homes;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Analyzes the blockade board.
 *
 * @author Barry Becker
 */
public class BoardAnalyzer {

    private BlockadeBoard board;
    private ShortestPathFinder pathFinder;

    /**
     * Constructor.
     */
    public BoardAnalyzer(BlockadeBoard board) {
        this.board = board;
        pathFinder = new ShortestPathFinder(board);
    }

    /**
     * @param player1 the last player to make a move.
     * @return all the opponent's shortest paths to specified players home bases.
     */
    public PathList findAllOpponentShortestPaths(boolean player1) {

        int numShortestPaths = Homes.NUM_HOMES * Homes.NUM_HOMES;
        PathList opponentPaths = new PathList();
        Set<BlockadeBoardPosition> hsPawns = new LinkedHashSet<>();
        for ( int row = 1; row <= board.getNumRows(); row++ ) {
            for ( int col = 1; col <= board.getNumCols(); col++ ) {
                BlockadeBoardPosition pos = board.getPosition( row, col );
                if ( pos.isOccupied() && pos.getPiece().isOwnedByPlayer1() != player1 ) {
                    hsPawns.add(pos);
                    assert (hsPawns.size() <= Homes.NUM_HOMES) : "Error: too many opponent pieces: " + hsPawns ;
                    PathList paths = findShortestPaths(pos);
                    GameContext.log(2, "about to add " + paths.size() + " more paths to "
                            + opponentPaths.size() + " maxAllowed=" + numShortestPaths);
                    for (Path p: paths) {
                        opponentPaths.add(p);
                    }
                }
            }
        }
        //assert (opponentPaths.size() == numShortestPaths) : "Too few opponent paths:" + opponentPaths;
        return opponentPaths;
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

        return pathFinder.findShortestPaths(position);
    }

    /**
     * find all the paths from each player's pawn to each opponent base.
     */
    public PlayerPathLengths findPlayerPathLengths() {
        PlayerPathLengths playerPaths = new PlayerPathLengths();

        for ( int row = 1; row <= board.getNumRows(); row++ ) {
            for ( int col = 1; col <= board.getNumCols(); col++ ) {
                BlockadeBoardPosition pos = board.getPosition( row, col );
                if ( pos.isOccupied() ) {
                    GamePiece piece = pos.getPiece();

                    // should reuse cached path if still valid.
                    PathList paths = board.findShortestPaths(pos);

                    playerPaths.getPathLengthsForPlayer(piece.isOwnedByPlayer1()).updatePathLengths(paths);
                }
            }
        }
        return playerPaths;
    }
}
