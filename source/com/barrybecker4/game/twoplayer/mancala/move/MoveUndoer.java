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
     * placed and restoring them to their original bin.
     * It can be a bit tricky for compound moves - ones it which the player goes again because
     * their last seeded bin was their store. In these cases, the moves must be undone in the reverse order.
     * Furthermore some moves capture stones in other bins, those must also be restored and deducted from the
     * appropriate player storage.
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
     * Captures are restored from the appropriate players storage.
     * @param move the move to restore captures for.
     */
    private void restoreCaptures(MancalaMove move) {

        Captures captures = move.getCaptures();
        if (captures.isEmpty()) return;

        MancalaBin oppositeBin = null;
        MancalaBin playerHome = board.getHomeBin(move.isPlayer1());
        MancalaBin opponentHome = board.getHomeBin(!move.isPlayer1());

        Set<Location> keys = move.getCaptures().keySet();

        // first restore the players captures
        for (Location loc : keys) {
            MancalaBin bin = board.getBin(loc);
            if (bin.isOwnedByPlayer1() == move.isPlayer1()) {
                bin.increment();
                playerHome.increment(-1);
                oppositeBin = board.getBin(board.getOppositeLocation(loc));
            }
        }
        // next restore the opponent's captures. This can only happen on the last move of the game.
        for (Location loc : keys) {

            MancalaBin bin = board.getBin(loc);
            if (bin.isOwnedByPlayer1() == move.isPlayer1()) continue;

            byte numCaptures = move.getCaptures().get(loc);

            if (bin.equals(oppositeBin)) {
                oppositeBin.increment(numCaptures);
                playerHome.increment(-numCaptures);
            }
            else {
                bin.increment(numCaptures);
                opponentHome.increment(-numCaptures);
            }
        }
    }

    /**
     * March around and pickup the seeds placed by the move
     * @param m the move for which the seeing will be undone.
     */
    private void unseedStones(MancalaMove m) {
        // pick up seed stones
        byte numStones = m.getNumStonesSeeded();
        Location currentLocation = m.getFromLocation();
        board.getNthLocation(currentLocation, -numStones);
        MancalaBin startBin = board.getBin(currentLocation);
        startBin.increment(numStones);

        for (int i = 0; i < numStones; i++) {
            currentLocation = board.getNextLocation(currentLocation);
            MancalaBin nextBin = board.getBin(currentLocation);
            if (!(nextBin.isHome() && m.isPlayer1() != nextBin.isOwnedByPlayer1())) {
                nextBin.increment(-1);
            }
        }
    }
}
