/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.BoardPosition;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard;
import com.barrybecker4.game.twoplayer.blockade.board.move.BlockadeMove;
import com.barrybecker4.game.twoplayer.blockade.board.move.MoveGenerator;
import com.barrybecker4.game.twoplayer.blockade.board.path.PlayerPathLengths;
import com.barrybecker4.game.twoplayer.common.TwoPlayerSearchable;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;
import com.barrybecker4.optimization.parameter.ParameterArray;

/**
 * For searching the blockade tree.
 *
 * @author Barry Becker
 */
public class BlockadeSearchable extends TwoPlayerSearchable<BlockadeMove, BlockadeBoard> {

    /**
     *  Constructor.
     */
    public BlockadeSearchable(BlockadeBoard board, PlayerList players) {
        super(board, players);
    }

    private BlockadeSearchable(BlockadeSearchable searchable) {
        super(searchable);
    }

    @Override
    public BlockadeSearchable copy() {
        return new BlockadeSearchable(this);
    }

    @Override
    public BlockadeBoard getBoard() {
        return board_;
    }

    /**
     * The primary way of computing the score for Blockade is to
     * weight the difference of the 2 shortest minimum paths plus the
     * weighted difference of the 2 furthest minimum paths.
     * An alternative method might be to weight the sum of the shortest paths
     * and difference it with the weighted sum of the opponent shortest paths.
     * The minimum path for a piece is the distance to its closest enemy home position.
     *
     * @return the value of the current board position
     *   a positive value means that player1 has the advantage.
     *   A big negative value means a good move for p2.
     */
    @Override
    public int worth(BlockadeMove lastMove, ParameterArray weights ) {
        getProfiler().startCalcWorth();
        // if its a winning move then return the winning value
        boolean player1Moved = lastMove.isPlayer1();

        if (checkForWin(player1Moved)) {
            GameContext.log(1, "FOUND WIN!!!");
            return player1Moved ? SearchStrategy.WINNING_VALUE : -SearchStrategy.WINNING_VALUE;
        }

        PlayerPathLengths pathLengths = getBoard().findPlayerPathLengths();
        int worth = pathLengths.determineWorth(SearchStrategy.WINNING_VALUE, weights);
        getProfiler().stopCalcWorth();
        return worth;
    }


    /**
      * If a players pawn lands on an opponent home, the game is over.
      * @param player1 the player to check to see if won.
      * @return true if player has reached an opponent home. (for player1 or player2 depending on boolean player1 value)
      */
    private boolean checkForWin(boolean player1) {

        BoardPosition[] homes = getBoard().getPlayerHomes(!player1);
        for (BoardPosition home : homes) {
            GamePiece p = home.getPiece();
            if (p != null && p.isOwnedByPlayer1() == player1)
                return true;
        }
        return false;
    }

    /**
     * Generate all possible legal and reasonable next moves.
     * In blockade, there are a huge amount of possible next moves because of all the possible
     * wall placements. So restrict wall placements to those that hinder the enemy while not hindering you.
     * lastMove may be null if there was no last move.
     */
    @Override
    public MoveList<BlockadeMove> generateMoves(BlockadeMove lastMove, ParameterArray weights)  {
        getProfiler().startGenerateMoves();

        MoveGenerator generator = new MoveGenerator(weights, getBoard());
        MoveList<BlockadeMove> moveList  = generator.generateMoves(lastMove);

        MoveList<BlockadeMove> bestMoves =
            bestMoveFinder_.getBestMoves(moveList);

        getProfiler().stopGenerateMoves();
        return bestMoves;
    }

    /**
     * given a move, determine whether the game is over.
     * If recordWin is true, then the variables for player1/2HasWon can get set.
     * Sometimes, like when we are looking ahead we do not want to set these.
     * @param move the move to check. If null then return true.
     * @param recordWin if true then the controller state will record wins
     */
    @Override
    public boolean done(BlockadeMove move, boolean recordWin ) {

        if (getNumMoves() > 0 && move == null) {
            GameContext.log(0, "Game is over because there are no more moves.");
            return true;
        }

        boolean p1Won = checkForWin(true);
        boolean p2Won = checkForWin(false);

        if (recordWin) {
            if (p1Won) {
                players.getPlayer1().setWon(true);
            } else if (p2Won)  {
                players.getPlayer2().setWon(true);
            }
        }
        return (p1Won || p2Won);
    }

    /**
     * Quiescent search not yet implemented for Blockade
     * Probably we could return moves that result in a drastic change in value.
     *
     * @return list of urgent moves
     */
    @Override
    public MoveList<BlockadeMove> generateUrgentMoves(BlockadeMove lastMove, ParameterArray weights) {
        return new MoveList<>();
    }
}
