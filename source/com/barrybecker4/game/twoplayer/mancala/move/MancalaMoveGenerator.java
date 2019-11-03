/* Copyright by Barry G. Becker, 2014-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.move;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.MoveList;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.BestMoveFinder;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;
import com.barrybecker4.game.twoplayer.mancala.MancalaSearchable;
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard;
import com.barrybecker4.optimization.parameter.ParameterArray;

import java.util.List;

import static com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE;

/**
 * Responsible for determining a set of reasonable next moves.
 *
 * @author Barry Becker
 */
public final class MancalaMoveGenerator {

    /**
     * @return all reasonably good next moves.
     */
    public final MoveList<MancalaMove> generateMoves(
            MancalaSearchable searchable, TwoPlayerMove lastMove, ParameterArray weights) {

        boolean player1 = (lastMove == null) || !lastMove.isPlayer1();
        return generateMovesForPlayer(searchable, player1, weights);
    }

    private MoveList<MancalaMove> generateMovesForPlayer(
            MancalaSearchable searchable, boolean player1, ParameterArray weights) {

        MoveList<MancalaMove> moveList = new MoveList<>();
        MancalaBoard board = searchable.getBoard();
        List<Location> candidateStarts = board.getCandidateStartLocations(player1);

        for (Location startLoc : candidateStarts)  {

            MancalaMove move = MancalaMove.createMove(player1, startLoc, 0, board.getBin(startLoc));

            // if the last move is in a players home bin, then they need to make a follow up move (potentially > 1).
            if (board.moveAgainAfterMove(move)) {
                MoveList<MancalaMove> compoundMoves = generateMovesWithFollowUpForPlayer(move, board.copy());
                for (MancalaMove compoundMove : compoundMoves) {
                    determineMoveScore(searchable, weights, compoundMove);
                    moveList.add( compoundMove );
                }
            }
            else {
                determineMoveScore(searchable, weights, move);
                moveList.add( move );
            }
        }
        BestMoveFinder<MancalaMove> finder =
                new BestMoveFinder<>(searchable.getSearchOptions().getBestMovesSearchOptions());
        return finder.getBestMoves(moveList);
    }

    private void determineMoveScore(MancalaSearchable searchable, ParameterArray weights, MancalaMove move) {
        // this will actually set the captures on the move if any
        //System.out.println("about to make move " + move);
        searchable.makeInternalMove( move );
        move.setValue(searchable.worth(move, weights));
        // now revert the board
        //System.out.println("about to undo move " + move);
        //System.out.println("on board = " + searchable.getBoard());
        searchable.undoInternalMove( move );
    }

    /**
     * @return list of compound moves based on an initial move that requires at least one follow up.
     */
    private MoveList<MancalaMove> generateMovesWithFollowUpForPlayer(MancalaMove baseMove, MancalaBoard board) {

        MoveList<MancalaMove> moveList = new MoveList<>();
        board.makeMove(baseMove);
        List<Location> candidateStarts = board.getCandidateStartLocations(baseMove.isPlayer1());

        for (Location startLoc : candidateStarts)  {

            MancalaMove followUp = MancalaMove.createMove(baseMove.isPlayer1(), startLoc, 0, board.getBin(startLoc));

            // there may be follow ups to the follow up.
            if (board.moveAgainAfterMove(followUp) && candidateStarts.size() > 1) {
                MoveList<MancalaMove> followUps = generateMovesWithFollowUpForPlayer(followUp, board.copy());
                for (MancalaMove m : followUps) {
                    MancalaMove newMove = baseMove.copy();
                    newMove.setFollowUpMove(m);
                    moveList.add(newMove);
                }
            }
            else {
                MancalaMove newMove = baseMove.copy();
                board.makeMove(followUp);
                newMove.setFollowUpMove(followUp);
                moveList.add(newMove);
            }
        }

        //System.out.println("compound moves = " + moveList);
        return moveList;
    }

    /**
     * @return a list of urgent moves (i.e positions that can result in a win for either player.
     */
    public MoveList<MancalaMove> generateUrgentMoves(
            MancalaSearchable searchable, MancalaMove lastMove, ParameterArray weights) {
        // no urgent moves at start of game.
        if (lastMove == null)  {
            return new MoveList<>();
        }
        MoveList<MancalaMove> allMoves = findMovesForBothPlayers(searchable, lastMove, weights);

        // now keep only those that result in a win or loss.
        MoveList<MancalaMove> urgentMoves = new MoveList<>();
        boolean currentPlayer = !lastMove.isPlayer1();

        for (MancalaMove move : allMoves) {
            // if it is not a winning move, or we already have it, then skip
            if ( Math.abs(move.getValue()) >= WINNING_VALUE  && !contains(move, urgentMoves)) {
                move.setUrgent(true);
                move.setPlayer1(currentPlayer);
                move.setPiece(new GamePiece(currentPlayer));
                urgentMoves.add(move);
            }
        }
        return urgentMoves;
    }

    /**
     * Consider both our moves and opponent moves.
     * @return Set of all next moves.
     */
    private MoveList<MancalaMove> findMovesForBothPlayers(
            MancalaSearchable searchable, MancalaMove lastMove, ParameterArray weights) {
        MoveList<MancalaMove> allMoves = new MoveList<>();

        MoveList<MancalaMove> moves = generateMoves(searchable, lastMove, weights);
        allMoves.addAll(moves);

        // unlike go, I don't know if we need this
        TwoPlayerMove oppLastMove = lastMove.copy();
        oppLastMove.setPlayer1(!lastMove.isPlayer1());
        MoveList<MancalaMove> opponentMoves =
                generateMoves(searchable, oppLastMove, weights);
        for (MancalaMove move : opponentMoves){
            allMoves.add(move);
        }

        return allMoves;
    }

    /**
     * This version of contains does not consider the player when determining containment.
     * @return true of the {@link MoveList} contains the specified move.
     */
    private boolean contains(MancalaMove move, MoveList<MancalaMove> moves) {
        for (MancalaMove m : moves) {
            Location moveLocation = m.getToLocation();
            if (moveLocation.equals(move.getToLocation())) {
                return true;
            }
        }
        return false;
    }
}
