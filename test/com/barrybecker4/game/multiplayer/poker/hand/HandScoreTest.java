// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand;

import junit.framework.TestCase;

import java.util.Arrays;

import static com.barrybecker4.game.card.Rank.*;


/**
 * programming challenge to test which poker hands are better
 * see http://www.programming-challenges.com/pg.php?page=downloadproblem&probid=110202&format=html
 *
 * author Barry Becker
 */
@SuppressWarnings("ClassWithTooManyMethods")
public class HandScoreTest extends TestCase {

    private static final HandScore VERY_HIGH_SCORE = new HandScore(HandType.FULL_HOUSE, Arrays.asList(NINE, EIGHT));
    private static final HandScore HIGH_SCORE = new HandScore(HandType.THREE_OF_A_KIND, Arrays.asList(NINE));
    private static final HandScore MEDIUM_SCORE = new HandScore(HandType.PAIR, Arrays.asList(KING));
    private static final HandScore LOW_SCORE = new HandScore(HandType.HIGH_CARD, Arrays.asList(JACK));


    public void testALowHigherThanVeryLow() {
        assertTrue(LOW_SCORE.compareTo(new HandScore(HandType.HIGH_CARD, Arrays.asList(TEN, NINE))) > 0);
    }

    public void testALowLowerThanMedium() {
        assertTrue(LOW_SCORE.compareTo(MEDIUM_SCORE) < 0);
    }

    public void testLowEqualsLow() {
        assertTrue(LOW_SCORE.compareTo(LOW_SCORE) == 0);
    }

    public void testHighGreaterThanMedium() {
        assertTrue(HIGH_SCORE.compareTo(MEDIUM_SCORE) > 0);
    }

    public void testVeryHighGreaterThanHigh() {
        assertTrue(VERY_HIGH_SCORE.compareTo(HIGH_SCORE) > 0);
    }

    public void testLowLessThanVeryHigh() {
        assertTrue(LOW_SCORE.compareTo(VERY_HIGH_SCORE) < 0);
    }

    public void testHighEqualsHigh() {
        HandScore threeOfAKind = new HandScore(HandType.THREE_OF_A_KIND, Arrays.asList(NINE, QUEEN, DEUCE));
        assertTrue(HIGH_SCORE.compareTo(threeOfAKind) == 0);
    }

}
