/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions;
import com.barrybecker4.game.twoplayer.common.search.transposition.HashKey;
import com.barrybecker4.game.twoplayer.common.search.transposition.ZobristHash;
import com.barrybecker4.optimization.parameter.ParameterArray;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;

/**
 * For searching two player games
 *
 * @author Barry Becker
 */
public abstract class TwoPlayerSearchable extends AbstractSearchable {

    protected final TwoPlayerBoard board_;
    protected final PlayerList players_;

    /** helps to find the best moves. */
    protected final BestMoveFinder bestMoveFinder_;

    /** Used to generate hashkeys. */
    protected final ZobristHash hash;


    /**
     * Constructor.
     */
    public TwoPlayerSearchable(final TwoPlayerBoard board,  PlayerList players) {

        super(board.getMoveList());
        board_ = board;
        players_ = players;

        hash = new ZobristHash(board_);
        bestMoveFinder_ = new BestMoveFinder(getSearchOptions().getBestMovesSearchOptions());
    }

    /**
     * Copy constructor.
     */
    public TwoPlayerSearchable(TwoPlayerSearchable searchable) {

        this(searchable.getBoard().copy(), (PlayerList)searchable.players_.clone());
    }

    @Override
    public TwoPlayerBoard getBoard() {
        return board_;
    }

    /**
     * Make the specified move.
     * It is actually ok if the same player moves twice in the case where we are looking for urgent moves.
     * @param move the move to play.
     */
    @Override
    public void makeInternalMove(TwoPlayerMove move) {

        getBoard().makeMove(move);

        if (move.isPassOrResignation())  {
            hash.applyPassingMove();
        } else {
            Location loc = move.getToLocation();
            hash.applyMove(loc, getBoard().getStateIndex(getBoard().getPosition(loc)));
        }
    }

    /**
     * takes back the most recent move.
     * @param move move to undo
     */
    @Override
    public void undoInternalMove( TwoPlayerMove move) {
        TwoPlayerMove lastMove = (TwoPlayerMove)moveList_.getLastMove();
        assert move.equals(lastMove) : "The move we are trying to undo ("+ move +") in list="
                + moveList_+" was not equal to the last move ("+lastMove+"). all move=" + getBoard().getMoveList();

        Location loc = move.getToLocation();

        if (!move.isPassingMove()) {
            hash.applyMove(loc, getBoard().getStateIndex(getBoard().getPosition(loc)));
        }

        getBoard().undoMove();
    }

    /**
     * Evaluates from player 1's perspective
     * @return an integer value for the worth of the move.
     *  must be between -SearchStrategy.WINNING_VALUE and SearchStrategy.WINNING_VALUE.
     */
    @Override
    public abstract int worth( TwoPlayerMove lastMove, ParameterArray weights);


    @Override
    public SearchOptions getSearchOptions() {
        return ((TwoPlayerPlayerOptions) getCurrentPlayer().getOptions()).getSearchOptions();
    }


    /**
     * Given a move, determine whether the game is over.
     * If recordWin is true, then the variables for player1/2HasWon can get set.
     *  sometimes, like when we are looking ahead we do not want to set these.
     * @param move the move to check. If null then return true. This is typically the last move played.
     * @param recordWin if true then the controller state will record wins
     */
    @Override
    public boolean done( TwoPlayerMove move, boolean recordWin ) {

        // the game can't be over if no moves have been made yet.
        if (moveList_.getNumMoves() == 0) {
            return false;
        }
        if (players_.anyPlayerWon()) {
            GameContext.log(0, "Game over because one of the players has won."); // NON_NLS
            return true;
        }
        if (moveList_.getNumMoves() > 0 && move == null) {
            Player currentPlayer = getCurrentPlayer();
            GameContext.log(0, "Game is over because there are no more moves for player " + currentPlayer); // NON_NLS
            if (recordWin) {
                currentPlayer.setWon(true);
            }
            return true;
        }

        int absValue = Math.abs(move.getValue());
        boolean won = (absValue >= SearchStrategy.WINNING_VALUE);

        if ( won && recordWin ) {
            if ( move.getValue() >= SearchStrategy.WINNING_VALUE )
                players_.getPlayer1().setWon(true);
            else
                players_.getPlayer2().setWon(true);
        }
        boolean maxMovesExceeded = moveList_.getNumMoves() >= getBoard().getMaxNumMoves();

        return (maxMovesExceeded || won);
    }

    /**
     * @return the player who's turn it is to move next.
     */
    private Player getCurrentPlayer()  {
        TwoPlayerMove move =  (TwoPlayerMove) moveList_.getLastMove();
        return (move==null || !move.isPlayer1()) ?  players_.getPlayer1() : players_.getPlayer2();
    }

    /**
     * @return true if the specified move caused one or more opponent pieces to become jeopardized
     */
    @Override
    public boolean inJeopardy( TwoPlayerMove move, ParameterArray weights) {
        return false;
    }

    /**
     * @return get a hash key that represents this board state (with negligibly small chance of conflict)
     */
    @Override
    public HashKey getHashKey() {
        return hash.getKey();
    }
}