/** Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.mancala.move;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBin;
import com.barrybecker4.game.twoplayer.mancala.board.MancalaBoard;

/**
 * Responsible for making a mancala move on the board.
 *
 * @author Barry Becker
 */
public class MoveMaker extends MoveAction  {

    /**
     * Constructor
     * @param board the mancala board
     */
    public MoveMaker(MancalaBoard board) {
        super(board);
    }

   /**
     * Given a move specification, execute it on the board.
     * See rules: http://boardgames.about.com/cs/mancala/ht/play_mancala.htm
     * @param move the move to make, if possible.
     * @return false if the move is illegal.
     */
    public boolean makeMove(Move move) {

        MancalaMove m = (MancalaMove)move;
        do {
            Location currentLocation = m.getFromLocation();
            MancalaBin bin = board.getBin(currentLocation);
            if (bin.isHome() || bin.getNumStones() == 0) {
                m.setCaptures(new Captures());
                return false;
            }
            int numStones = bin.getStones();

            // march counter-clockwise around the board dropping stones into bins (but skipping the opponent home bin)
            for (int i = 0; i<numStones; i++) {
                currentLocation = board.getNextLocation(currentLocation);
                MancalaBin nextBin = board.getBin(currentLocation);
                if (!(nextBin.isHome() && m.isPlayer1() != nextBin.isOwnedByPlayer1())) {
                    nextBin.increment();
                }
            }
            m.setCaptures(doCaptures(m, currentLocation));
            m = m.getFollowUpMove();

        } while (m != null);

        return true;
    }

    /**
     * @param move the move that made the captures.
     * @param currentLocation location of final seeded bin.
     * @return map from location to number of captures made
     */
    private Captures doCaptures(MancalaMove move, Location currentLocation) {
        // handle some special cases. like capturing
        Captures captures = new Captures();
        MancalaBin lastBin = board.getBin(currentLocation);

        // if the last bin seeded had no stones, and was on the player's side, capture that piece
        // and all the stones from the opposite bin and put in your store
        if (!lastBin.isHome() && lastBin.getNumStones() == 1 && lastBin.isOwnedByPlayer1() == move.isPlayer1())  {

            Location oppositeLoc = board.getOppositeLocation(currentLocation);
            MancalaBin oppositeBin = board.getBin(oppositeLoc);
            MancalaBin homeBin = board.getHomeBin(move.isPlayer1());
            captures.put(oppositeLoc, lastBin.getNumStones());
            homeBin.increment(lastBin.getStones());
            homeBin.increment(oppositeBin.getStones());
        }

        // if no stones left on players side, opponent captures all remaining stone on his side
        if (board.isSideClear(move.isPlayer1())) {
            clearSide(!move.isPlayer1(), captures);
        }
        if (board.isSideClear(!move.isPlayer1())) {
            clearSide(move.isPlayer1(), captures);
        }
        return captures;
    }

    /**
     * Clear off the remaining stones on the specified players side and put them in his store.
     * @param player1 player's whose side to clear.
     */
    private void clearSide(boolean player1, Captures captures) {
        MancalaBin homeBin = board.getHomeBin(player1);

        Location currentLoc = board.getHomeLocation(!player1);
        for (int i=0; i < board.getNumCols() - 2; i++) {
            currentLoc = board.getNextLocation(currentLoc);
            MancalaBin bin = board.getBin(currentLoc);
            if (bin.getNumStones() > 0) {
                captures.put(currentLoc, bin.getNumStones());
            }
            homeBin.increment(bin.getStones());
        }
    }
}
