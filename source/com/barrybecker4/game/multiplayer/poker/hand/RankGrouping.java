// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand;

import com.barrybecker4.game.card.Card;
import com.barrybecker4.game.card.Rank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.barrybecker4.game.card.Rank.*;

/**
 * Analyzes a poker hand and creates a map which has an entry for each card rank represented
 * in the hand and its associated count. Given that information its easy to determine things like
 * how many of a certain rank there is. Immutable.
 *
 * @author Barry Becker
 */
class RankGrouping {

    /** the one case where ace is treated as low instead of high in poker */
    private static final List<Rank> ACE_LOW_STRAIGHT = Arrays.asList(ACE, FIVE, FOUR, THREE, DEUCE);

    private List<RankGroup> groups;

    /**
     * Constructor.
     * @param hand the poker hand (not necessarily 5 cards)
     */
    RankGrouping(List<Card> hand) {
        groups = init(hand);
    }

    /**
     * Used to differentiate between hands of the same type - like two full house hands
     * or two hands that are both three of a kind.
     * There is one special case to be aware of however.
     * If the ranks are A, 5, 4, 3, 2, then A is low and 5, 4, 3, 2, A is returned.
     */
    public List<Rank> getRanks() {

        List<Rank> ranks = new ArrayList<Rank>();
        for (RankGroup group : groups) {
            ranks.add(group.rank);
        }
        if (ranks.equals(ACE_LOW_STRAIGHT)) {
            // move the ace to the end
            ranks.remove(0);
            ranks.add(ACE);
        }
        return ranks;
    }

    /**
     * Match against a specific list of counts which will map to a specific hand type.
     * For example, [3, 2] maps to a full house
     * [4, 1] maps to a four of a kind
     * [3, 1, 1] maps to three of a kind, etc
     * @param counts list of counts to match against.
     * @return true if matches the specified list of counts.
     */
    boolean matchesCounts(int... counts) {

        if (groups.size() != counts.length) return false;

        for (int i=0; i<counts.length; i++)
        {
            if (counts[i] != groups.get(i).count) {
                return false;
            }
        }

        return true;
    }

    /**
     * Builds the map which has an entry for each card rank represented in the hand and its associated count.
     * Assumes that the cards in the hands are sorted descending by rank.
     */
    private List<RankGroup> init(List<Card> hand) {

        Map<Rank, RankGroup> map = new HashMap<Rank, RankGroup>();

        for (Card card : hand) {
            RankGroup group = map.get(card.rank());
            if (group != null)  {
                group.count++;
            }
            else {
                map.put(card.rank(), new RankGroup(card.rank(), 1));
            }
        }

        List<RankGroup> groups = new ArrayList<RankGroup>(map.values());
        Collections.sort(groups);
        return groups;
    }

    private class RankGroup implements Comparable<RankGroup> {
        int count;
        Rank rank;

        /** constructor */
        RankGroup(Rank rank, int count) {
            this.rank = rank;
            this.count = count;
        }

        @Override
        public int compareTo(RankGroup group) {
            if (group.count == this.count) {
               return group.rank.ordinal() - this.rank.ordinal();
            }
            return group.count - this.count;
        }
    }
}
