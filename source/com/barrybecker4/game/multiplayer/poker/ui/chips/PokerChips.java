// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.ui.chips;

import com.barrybecker4.common.math.Range;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A set of different sorts of poker chips.
 * This set of chips can be rendered as the stacks in from of the player or in the pot in the middle.
 * @author Barry Becker
 *
 */
public class PokerChips extends LinkedHashMap<PokerChip, Integer> {

    private static final Map<PokerChip, Range> THRESHOLDS = new LinkedHashMap<>();
    static {
        THRESHOLDS.put(PokerChip.FIVE_HUNDRED, new Range(500, 1000));
        THRESHOLDS.put(PokerChip.TWENTY_FIVE, new Range(200, 300));
        THRESHOLDS.put(PokerChip.TEN, new Range(70, 100));
        THRESHOLDS.put(PokerChip.FIVE, new Range(15, 20));
        THRESHOLDS.put(PokerChip.ONE, new Range(1, 1));
    }

    /**
     * Create a set of chips based on the specified amount.
     * @param amount dollar amount to convert into a set of chips.
     */
    public PokerChips(int amount) {
        initialize(amount);
    }

    /**
     * The integer array returned has an entry for each type of chip.
     * Only have chips of this type if max Thresh for the chiptype is exceeded.
     * min Thresh tells what amount to convert to these kinds of chips.
     * @param amount of cash to convert to chips
     */
    public void initialize(int amount) {

        int remainder = amount;

        for (PokerChip chipType : THRESHOLDS.keySet()) {
            assert (remainder >= 0) : "remainder "+remainder+" is negative";
            if (remainder >= THRESHOLDS.get(chipType).getMax()) {
                int amtToConvert = remainder - (remainder % (int)THRESHOLDS.get(chipType).getMin());
                int nChips = amtToConvert / chipType.getValue();
                remainder -=  nChips * chipType.getValue();
                put(chipType, nChips);
            }
        }
        assert (remainder == 0) : "remainder was " + remainder + ", not 0 as expected";
    }

}
