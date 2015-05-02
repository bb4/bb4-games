/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade;

import com.barrybecker4.ca.dj.jigo.sgf.SGFException;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.player.PlayerOptions;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition;
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove;
import com.barrybecker4.game.twoplayer.blockade.persistence.BlockadeGameExporter;
import com.barrybecker4.game.twoplayer.blockade.persistence.BlockadeGameImporter;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerOptions;
import com.barrybecker4.game.twoplayer.common.persistence.TwoPlayerGameExporter;
import com.barrybecker4.game.twoplayer.common.search.Searchable;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Defines for the computer how it should play blockade.
 *
 * Todo items
 *   - Restrict to N vertical and N horizontal walls, or allow not to place a wall.
 *     (perhaps only allow wall placements up to (xdim*ydim)/4 walls for each player)
 *
 * @author Barry Becker
 */
public class BlockadeController extends TwoPlayerController<BlockadeMove, BlockadeBoard> {

    /** the default Blockade board is 14 by 11 */
    private static final int NUM_ROWS = 14;
    private static final int NUM_COLS = 11;

    /**
     *  Construct the Blockade game controller.
     */
    public BlockadeController() {
        initializeData();
    }

    @Override
    protected BlockadeBoard createBoard() {
        return new BlockadeBoard(NUM_ROWS, NUM_COLS);
    }

    /**
     * this gets the Blockade specific weights.
     */
    @Override
    protected void initializeData() {
        weights_ = new BlockadeWeights();
    }

    @Override
    protected TwoPlayerOptions createOptions() {
        return new TwoPlayerOptions();
    }

    @Override
    protected PlayerOptions createPlayerOptions(String playerName, Color color) {
        return new BlockadePlayerOptions(playerName, color);
    }

    /**
     * The computer makes the first move in the game
     */
    @Override
    public void computerMovesFirst() {
        // determine the possible moves and choose one at random.
        MoveList<BlockadeMove> moveList = getSearchable().generateMoves(null, weights_.getPlayer1Weights());

        makeMove( moveList.getRandomMove() );
    }

    /**
     * Measure is determined by the score (amount of territory)
     * If called before the end of the game it just returns 0 - same as it does in the case of a tie.
     * @return some measure of how overwhelming the win was. May need to negate based on which player one.
     */
    @Override
    public int getStrengthOfWin() {

        if (!getPlayers().anyPlayerWon()) {
             return 0;
        }
        return getSearchable().worth(getLastMove(), weights_.getDefaultWeights());
    }

    /**
     * Save the current state of the blockade game to a file in SGF (4) format (standard game format).
     * This should some day be xml (xgf)
     */
    @Override
    public TwoPlayerGameExporter getExporter() {
        return new BlockadeGameExporter(this);
    }

    @Override
    public void restoreFromStream(InputStream iStream) throws IOException, SGFException {
        BlockadeGameImporter importer = new BlockadeGameImporter(this);
        importer.restoreFromStream(iStream);
    }

    /**
     * @param position location
     * @return a possible list of moves based on position passed in.
     */
    public List<BlockadeMove> getPossibleMoveList(BoardPosition position) {
        return (getBoard()).getPossibleMoveList(
                (BlockadeBoardPosition)position, !position.getPiece().isOwnedByPlayer1());
    }

    @Override
    public Searchable<BlockadeMove, BlockadeBoard> createSearchable(BlockadeBoard board, PlayerList players) {
        return new BlockadeSearchable(board, players);
    }
}
