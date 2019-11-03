/* Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
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

       MancalaMove currentMove = (MancalaMove)move;
       do {
           Location currentLocation = currentMove.getFromLocation();
           MancalaBin bin = board.getBin(currentLocation);
           int numStones = currentMove.getNumStonesSeeded();
           //assert (numStones == bin.getNumStones()) :    // failing
           //        "Unexpected number of stones to move. Expected " + bin.getNumStones() + " but had " + numStones;
           if (bin.isHome() || numStones == 0) {
               return false;
           }
           bin.takeStones();

           // march counter-clockwise around the board dropping stones into bins (but skipping the opponent home bin)
           int numStonesLeft = numStones;
           while (numStonesLeft > 0) {
                currentLocation = board.getNextLocation(currentLocation);
                MancalaBin nextBin = board.getBin(currentLocation);
                if (!(nextBin.isHome() && currentMove.isPlayer1() != nextBin.isOwnedByPlayer1())) {
                    nextBin.increment();
                    numStonesLeft--;
                }
           }
           currentMove.setCaptures(doCaptures(currentMove, currentLocation));
           currentMove = currentMove.getFollowUpMove();

       } while (currentMove != null);

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
        Location oppositeLoc = board.getOppositeLocation(currentLocation);
        MancalaBin oppositeBin = null;
        if (!lastBin.isHome()) {
            oppositeBin = board.getBin(oppositeLoc);
        }

        // if the last bin seeded
        //  - had no stones,
        //  - and was on the player's side,
        //  - and there is at least one opponent stone opposite,
        // capture that piece and all the stones from the opposite bin and put in your store
        if (!lastBin.isHome()
                && lastBin.getNumStones() == 1
                && lastBin.isOwnedByPlayer1() == move.isPlayer1()
                && oppositeBin.getNumStones() > 0)  {

            MancalaBin homeBin = board.getHomeBin(move.isPlayer1());
            captures.put(currentLocation, (byte)1);
            homeBin.increment(lastBin.takeStones());
            captures.put(oppositeLoc, oppositeBin.getNumStones());
            homeBin.increment(oppositeBin.takeStones());
        }

        // if no stones left on players side, opponent captures all remaining stones on his side
        if (board.isSideClear(move.isPlayer1())) {
            board.clearSide(!move.isPlayer1(), captures);
        }
        else if (board.isSideClear(!move.isPlayer1())) {
            board.clearSide(move.isPlayer1(), captures);
        }
        return captures;
    }

}
