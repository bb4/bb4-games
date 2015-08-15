/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.chess;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.common.player.PlayerOptions;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.TwoPlayerOptions;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

/**
 * Defines for the computer how it should play Chess.
 * Chess is very similar to Checkers so we derive from the CheckersController.
 *
 * Chess features to add:
 *  - show indicator of invalid move while dragging piece (before placed)
 *        Should show piece grayed our or transparent until in a valid position
 *       If you drop the piece in an invalid position, instead of showing an error message
 *      animate the piece back to its original position.
 * - exchange pawn for best piece when it reaches the other side.
 * - if you are in check, then don't allow moves other than those that get you out of check.
 * - game is over if no moves available (because of check mate usually).
 * - there is a tendency to get into an infinite cycle at the end of a computer vs computer game.
 * - castling.
 * - account for amount of king endangerment in worth.
 * - Checkers and Chess should probably have a common abstract base class, but I can't think of a good
 *   name for it, so currently Chess just derives from Checkers.
 *
 * @author Barry Becker
 */
public class ChessController extends TwoPlayerController<ChessMove, ChessBoard> {

    /**
     *  Constructor.
     */
    public ChessController() {
        initializeData();
    }

    @Override
    protected ChessBoard createBoard() {
        return new ChessBoard();
    }

    /**
     * this gets the Chess specific weights.
     */
    @Override
    protected void initializeData() {
        weights_ = new ChessWeights();
    }

    /**
     * The computer makes the first move in the game.
     */
    @Override
    public void computerMovesFirst() {

        // determine the possible moves and choose one at random.
        MoveList<ChessMove> moveList =
                getSearchable().generateMoves( null, weights_.getPlayer1Weights());

        makeMove( moveList.getRandomMove() );
        player1sTurn = false;
    }

    @Override
    protected TwoPlayerOptions createOptions() {
        return new TwoPlayerOptions();
    }

    @Override
    protected PlayerOptions createPlayerOptions(String playerName, Color color) {
        return new ChessPlayerOptions(playerName, color);
    }

    /**
     * remove any moves that put the king in jeopardy.
     * @param moveList list of moves to examine.
     */
    public void removeSelfCheckingMoves(List moveList) {
        ChessBoard b = getBoard();
        Iterator it = moveList.iterator();
        while (it.hasNext()) {
           ChessMove move = (ChessMove)it.next();
           if (b.causesSelfCheck(move)) {
                GameContext.log(2, "don't allow "+move+" because it puts the king in check." );
                it.remove();
           }
        }
    }

    @Override
    protected ChessSearchable createSearchable(ChessBoard board, PlayerList players) {
        return new ChessSearchable(board, players);
    }
}
