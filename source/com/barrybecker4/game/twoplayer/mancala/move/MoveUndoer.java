/** Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.move;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBin;
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard;

import java.util.Set;

/**
 * Responsible for undoing a mancala move on the board.
 *
 * @author Barry Becker
 */
public class MoveUndoer extends MoveAction {

    /**
     * Constructor
     * @param board the mancala board
     */
    public MoveUndoer(MancalaBoard board) {
        super(board);
    }

    /**
     * For mancala, undoing a move means picking up all the stones that were
     * placed and restoring them to their original bins.
     * It can be a bit tricky for compound moves - ones it which the player goes again because
     * their last seeded bin was their store. In these cases, the moves must be undone in the reverse order.
     * Furthermore some moves capture stones in other bins, those must also be restored and deducted from the
     * appropriate player storage.  Note: recursive.
     */
    public void undoMove(Move move) {

        MancalaMove m = (MancalaMove) move;
        if (m.getFollowUpMove() != null) {
            undoMove(m.getFollowUpMove());
        }
        restoreCaptures(m);
        unseedStones(m);
    }

    /**
     * Captures are restored from the appropriate player's storage.
     * @param move the move to restore captures for.
     */
    private void restoreCaptures(MancalaMove move) {

        Captures captures = move.getCaptures();
        if (captures.isEmpty()) return;

        Location oppositeBinLocation = null;
        MancalaBin playerHome = board.getHomeBin(move.isPlayer1());
        MancalaBin opponentHome = board.getHomeBin(!move.isPlayer1());

        Set<Location> keys = move.getCaptures().keySet();

        // first restore the player's captures
        for (Location loc : keys) {
            MancalaBin bin = board.getBin(loc);
            if (bin.isOwnedByPlayer1() == move.isPlayer1()) {
                bin.increment();
                if (playerHome.getNumStones() == 0) {
                    throw new IllegalStateException("error undoing move=" + move.toString());
                }
                playerHome.increment(-1);
                oppositeBinLocation = board.getOppositeLocation(loc);
            }
        }
        // next restore the opponent's captures.
        // Restoring opponent captures on a whole side can only happen on the last move of the round/game.
        for (Location loc : keys) {
            MancalaBin bin = board.getBin(loc);
            if (bin.isOwnedByPlayer1() == move.isPlayer1()) continue;

            byte numCaptures = move.getCaptures().get(loc);

            if (loc.equals(oppositeBinLocation)) {
                board.getBin(oppositeBinLocation).increment(numCaptures);
                playerHome.increment(-numCaptures);
            }
            else {
                bin.increment(numCaptures);
                assert (numCaptures < opponentHome.getNumStones()) : "We cannot restore " + numCaptures + " to "
                        + loc + " from the opponent home because it has only " + opponentHome.getNumStones();
                opponentHome.increment(-numCaptures);
            }
        }
    }

    /**
     * March around and pickup the seeds placed by the move
     * @param move the move for which the seeing will be undone.
     */
    private void unseedStones(MancalaMove move) {

        byte numStones = move.getNumStonesSeeded();
        Location currentLocation = move.getFromLocation();
        MancalaBin startBin = board.getBin(currentLocation);
        startBin.increment(numStones);
        int stonesToPickup = numStones;

        while (stonesToPickup > 0) {
            currentLocation = board.getNextLocation(currentLocation);
            MancalaBin nextBin = board.getBin(currentLocation);
            if (!(nextBin.isHome() && move.isPlayer1() != nextBin.isOwnedByPlayer1())) {
                assert nextBin.getNumStones() > 0 : "Cannot undo move " + move
                        + ". Cannot unseed " + currentLocation + " because it is already at 0.";
                nextBin.increment(-1);
                stonesToPickup--;
            }
        }
    }
}
