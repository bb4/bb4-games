// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand;

import com.barrybecker4.game.card.Rank;

import java.util.List;

/**
 * A comparable score for a hand. It's more meaningful to represent the score as an object than a number.
 * See Peter Norvig's Udacity class on Designing a compute program
 * https://www.udacity.com/course/viewer#!/c-cs212/l-48688918/m-48646686
 *
 * @author Barry Becker
 */
public class HandScore implements Comparable<HandScore> {

    private HandType type;
    private List<Rank>  ranks;

    /**
     * Constructor
     * @param type the type of poker hand. This is the most important factor.
     * @param ranks the unique card ranks in order of importance. This the secondary factor used to
     *              break ties between hands of the same type - although ties can still occur.
     *              "In order of importance" means the ranks are sorted by how they match up with the type of hand.
     *              For example, the ranks list for a full house like "4D 4H 4C QS QD" will be (4, Q) because the
     *              4 is more important that the queen because there are 3 of them.
     */
    public HandScore(HandType type, List<Rank> ranks) {
        this.type = type;
        this.ranks = ranks;
    }

    public HandType getType() {
        return type;
    }

    /**
     * The number of ranks do not need to match. This is convenient when creating a constant score
     * like new HandScore(HandType.PAIR, Rank.King) - meaning a pair of kings and you
     * @param score the poker hand score to compare to
     * @return a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(HandScore score) {
        if (this.type.ordinal() == score.type.ordinal()) {
            int numRanks = Math.min(this.ranks.size(), score.ranks.size());
            int i = 0;
            Rank thisRank;
            Rank thatRank;
            do
            {
                thisRank = this.ranks.get(i);
                thatRank = score.ranks.get(i);
                i++;
            } while (thisRank == thatRank && i < numRanks);

            return (i <= numRanks) ? thisRank.ordinal() - thatRank.ordinal() : 0;
        }
        return this.type.ordinal() - score.type.ordinal();
    }
}
