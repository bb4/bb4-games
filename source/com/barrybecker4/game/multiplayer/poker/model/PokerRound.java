/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.model;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.player.PlayerList;
import com.barrybecker4.game.multiplayer.poker.hand.Hand;
import com.barrybecker4.game.multiplayer.poker.hand.HandScore;
import com.barrybecker4.game.multiplayer.poker.hand.PokerHandScorer;
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Everything that happened during one round of the poker game.
 * This should include the amount that each player has contributed to the pot.
 * Allows for undo potentially.
 *
 * @see PokerTable
 * @author Barry Becker
 */
public class PokerRound extends Move {

    private int pot_;

    /**
     * Constructor.
     */
    public PokerRound() {}

    public void addToPot(int amount) {
        assert(amount > 0) : "You must add a positive amount";
        pot_ += amount;
    }


    public int getPotValue() {
        return pot_;
    }

    public void clearPot() {
        pot_ = 0;
    }

    /**
     * collect the antes
     */
    public void anteUp(PlayerList players, int anteAmount) {
        // get players to ante up, if they have not already
        if (getPotValue() == 0) {
            for (Player p : players) {
                PokerPlayer player = (PokerPlayer) p.getActualPlayer();

                // if a player does not have enough money to ante up, he is out of the game
                player.contributeToPot(this, anteAmount);
            }
        }
    }

    /**
     * @return the maximum contribution made by any player so far this round
     */
    public int getCurrentMaxContribution(PlayerList players) {
       int max = Integer.MIN_VALUE;
        for (Player p : players) {
            PokerPlayer player = (PokerPlayer) p.getActualPlayer();
            if (player.getContribution() > max) {
                max = player.getContribution();
            }
        }
        return max;
    }

    /**
     * @return the min number of chips of any player
     */
    public int getAllInAmount(PlayerList players) {
        // loop through the players and return the min number of chips of any player
        int min = Integer.MAX_VALUE;
        for (Player p : players) {
            PokerPlayer player = (PokerPlayer) p.getActualPlayer();
            if (!player.hasFolded() && ((player.getCash() + player.getContribution()) < min)) {
                min = player.getCash() + player.getContribution();
            }
        }
        return min;
    }

    /**
     * the round is over if there is only one player left who has not folded, or
     * everyone has had a chance to call.
     * @return true of the round is over
     */
    public boolean roundOver(PlayerList players, int playerIndex) {

        if (allButOneFolded(players))  {
            return true;
        }

        // special case of no one raising
        int contrib = getCurrentMaxContribution(players);
        GameContext.log(0, "in roundover check max contrib=" + contrib);

        for (Player pp : players) {
            PokerPlayer p = (PokerPlayer) pp.getActualPlayer();
            if (!p.hasFolded()) {
                assert(p.getContribution() <= contrib) :
                       "contrib was supposed to be the max, but " + p + " contradicats that.";
                if (p.getContribution() != contrib) {
                    return false;
                }
            }
        }

        return ((playerIndex >= getNumNonFoldedPlayers(players)));
    }


    /**
     * Determine the winner of the round.
     * In rare cases there could be ties. That is why a list of players is returned instead of just one player.
     * @return the player with the best poker hand for this round
     */
    public List<PokerPlayer> determineWinners(PlayerList players) {
        List<PokerPlayer> winners = new ArrayList<PokerPlayer>();
        int first = 0;

        while (((PokerPlayer) players.get(first).getActualPlayer()).hasFolded() && first < players.size()) {
            first++;
        }
        if (((PokerPlayer)players.get(first)).hasFolded())
            GameContext.log(0, "All players have folded. That was dumb. The winner will be random.");

        Hand bestHand = ((PokerPlayer) players.get(first)).getHand();
        PokerHandScorer scorer = new PokerHandScorer();
        HandScore bestScore = scorer.getScore(bestHand);

        // find the best hand
        for (int i = first + 1; i < players.size(); i++) {
            PokerPlayer player = (PokerPlayer) players.get(i).getActualPlayer();
            HandScore score = scorer.getScore(player.getHand());
            if (!player.hasFolded() && score.compareTo(bestScore) > 0) {
                bestScore = score;
            }
        }

        // find all players with a hand that good (rare that there will be more than one
        for (int i = first; i < players.size(); i++) {
            PokerPlayer player = (PokerPlayer) players.get(i).getActualPlayer();
            if (!player.hasFolded() && scorer.getScore(player.getHand()).compareTo(bestScore) == 0) {
                winners.add(player);
            }
        }

        GameContext.log(0, "The winning players were " + winners);
        return winners;
    }

    private boolean allButOneFolded(PlayerList players) {

        int numNotFolded = 0;
        for (final Player pp : players) {
            PokerPlayer player = (PokerPlayer) pp.getActualPlayer();
            if (!player.hasFolded()) {
                numNotFolded++;
            }
        }
        return (numNotFolded == 1);
    }

    /**
     * a player is not counted as active if he is "out of the game".
     * @return  number of active players.
     */
    private int getNumNonFoldedPlayers(PlayerList players) {
        int count = 0;
        for (final Player p : players) {
            PokerPlayer player = (PokerPlayer) p.getActualPlayer();
            if (!player.isOutOfGame())
               count++;
        }
        return count;
    }

}



