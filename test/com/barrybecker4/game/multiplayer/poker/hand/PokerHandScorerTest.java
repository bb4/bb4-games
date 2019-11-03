/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.hand;

import com.barrybecker4.game.card.Rank;
import junit.framework.TestCase;

import java.util.Arrays;

import static com.barrybecker4.game.multiplayer.poker.hand.PokerHandTstUtil.*;


/**
 * programming challenge to test which poker hands are better
 * see http://www.programming-challenges.com/pg.php?page=downloadproblem&probid=110202&format=html
 *
 * author Barry Becker
 */
@SuppressWarnings("ClassWithTooManyMethods")
public class PokerHandScorerTest extends TestCase {

    PokerHandScorer scorer = new PokerHandScorer();

    enum CompareType {LARGER, SMALLER, SAME}

    public void testAceHighBeatsKingHighOrdered() {
        compareHands(createHand("2H 3D 5S 9C KD"), createHand("2C 3H 4S 8C AH"), CompareType.SMALLER);
    }

    public void testAceHighBeatsKingHighUnrdered() {
        compareHands(createHand("2H KD 3D 5S 9C "), createHand("2C 3H 4S AH 8C"), CompareType.SMALLER);
    }

    public void testThreeOfAKindBeatsTwoPair() {
        compareHands(createHand("2H 5D 5S 5C 3D"), createHand("KC AH 4S 4C AH"), CompareType.LARGER);
    }

    public void testPairBeatsHighCard() {
        compareHands(createHand("2H 4S 4C 2D 4H"), createHand("2S 8S AS QS 3S"), CompareType.LARGER);
    }

    /** sorted, this compares  K 9 5 3 2 with  K 8 4 3 2. The First hand is higher because 9 beats 8  */
    public void testHighOfHeartsBeatsKingOfDiamonds() {
        compareHands(createHand("2H 3D 5S 9C KD"), createHand("2C 3H 4S 8C KH"), CompareType.LARGER);
    }

    /** sorted, this compares  K Q 9 6 5  with  K 9 5 3 2. The First hand is higher because Q beats 9  */
    public void testKingOfDiamondsLessThanKingOfHeartsWithSecondaryCardsLower() {
        compareHands(createHand("6H QD 5S 9C KD"), createHand("2D 3H 5C 9S KH"), CompareType.LARGER);
    }

    public void testPairOf7sLessThanPairOfJacks() {
        compareHands(createHand("7C 7H 5S 9C KD"), createHand("JC 3H JH 8C KH"), CompareType.SMALLER);
    }

    public void testPairOf9sBiggerThanKingHigh() {
        compareHands(createHand("9C 9H 5S 9C KD"), createHand("3C 4H 3H 8C KH"), CompareType.LARGER);
    }

    public void testPairOf9sBiggerThanPairOf4s() {
        compareHands(createHand("9C 9H 5S 3C 2D"), createHand("4C 4H AH 8C KH"), CompareType.LARGER);
    }

    public void testPairOf7sSmallerThanPairOf8s() {
        compareHands(createHand("7C 7H JS 9C KD"), createHand("8C 8H 3H 5C 4H"), CompareType.SMALLER);
    }

    /** verify that it is possible, in rare circumstances, for two different hands to be ranked the same */
    public void testTwoDifferentHandsWithSameValue() {
        compareHands(createHand("9H 8D 7S 6C 5D"), createHand("9C 8H 7S 6C 5H"), CompareType.SAME);
    }

    /**
     * Compare two hands with pair of aces. Secondary cards are Q 9 3 and Q J 4 respectively.
     * Second hand wins because J beats 9.
     */
    public void testSamePairUsesHighSecondaryCardToResolve() {
        compareHands(createHand("9H AS AD QC 3D"), createHand("AC AH QS 4C JD"), CompareType.SMALLER);
    }

    /** The second full house is larger because J beats 3.  */
    public void testRankOfThreeInFullHouseUsedFirst() {
        compareHands(createHand("KH 3S KD 3C 3D"), createHand("4C JH JS 4D JD"), CompareType.SMALLER);
    }

    // impossible without wild cards
    public void testTwoInFullHouseUsedIfThreeTie() {
        compareHands(createHand("10H JS 10D 10C JD"), createHand("AC AH 10S 10C 10D"), CompareType.SMALLER);
    }

    public void testSecondPairUsedToBreakTie() {
        compareHands(createHand("10H JS 10D 3C JD"), createHand("AC AH 10S 10C AS"), CompareType.SMALLER);
    }

    public void testStraightHigherThanTriple() {
        compareHands(createHand("9H 10S JD QC KD"), createHand("AC AH 10S 9C AS"), CompareType.LARGER);
    }

    public void testTwoPairHigherThanAceHigh() {
        compareHands(createHand("AH 8C 7H 6C 5H"), createHand("7D 7C 4C 3C 3S"), CompareType.SMALLER);
    }


    public void testHasStraightWhenNone() {
        HandScore score = scorer.getScore(createHand("2H 4S 4C 2D 4H"));
        assertNotSame("Unexpectedly had a straight", HandType.STRAIGHT, score.getType());
    }

    public void testHasNormalStraight() {
        HandScore score = scorer.getScore(createHand("3H 4S 5C 6D 7H"));
        assertEquals("Straight not found", HandType.STRAIGHT, score.getType());
    }

    public void testHasNormalStraightTwoLow() {
        HandScore score = scorer.getScore(createHand("2H 3S 4C 5D 6H"));
        assertEquals("Straight not found", HandType.STRAIGHT, score.getType());
    }

    public void testHasAceHighStraight() {
        assertTrue(scorer.hasStraight(Arrays.asList(Rank.ACE, Rank.KING, Rank.QUEEN, Rank.JACK, Rank.TEN)));
    }

    public void testHasAceLowStraight() {
        assertTrue(scorer.hasStraight(Arrays.asList(Rank.FIVE, Rank.FOUR, Rank.THREE, Rank.DEUCE, Rank.ACE)));
    }

    /** this should not happen */
    public void testHasAceOutOfOrderStraight() {
        assertFalse(scorer.hasStraight(Arrays.asList(Rank.ACE, Rank.FIVE, Rank.FOUR, Rank.THREE, Rank.DEUCE)));
    }

    public void testHasMiddleStraight() {
        assertTrue(scorer.hasStraight(Arrays.asList(Rank.TEN, Rank.NINE, Rank.EIGHT, Rank.SEVEN, Rank.SIX)));
    }

    public void testNoStraight() {
        assertFalse(scorer.hasStraight(Arrays.asList(Rank.JACK, Rank.NINE, Rank.EIGHT, Rank.SEVEN, Rank.SIX)));
    }

    /** should not happen because should be sorted */
    public void testOutOfOrderStraight() {
        assertFalse(scorer.hasStraight(Arrays.asList(Rank.NINE, Rank.JACK, Rank.EIGHT, Rank.SEVEN, Rank.SIX)));
    }

    /** this could never happen because the only time the ACE would be last is if 5, 4, 3, 2 were the other ranks
    public void testHasAceLowNoStraight() {
        assertFalse(scorer.hasStraight(Arrays.asList(Rank.SIX, Rank.FOUR, Rank.THREE, Rank.DEUCE, Rank.ACE)));
    }   */

    public void testNotFlush() {
        assertFalse(scorer.hasFlush(createHand("AH 3S 4C 5D 6H")));
    }

    public void testNotFlushButAllRed() {
        assertFalse(scorer.hasFlush(createHand("AH 3D 4D 5D 6H")));
    }

    public void testHasBlackFlush() {
        assertTrue(scorer.hasFlush(createHand("AS 3S 4S 9S 6S")));
    }

    public void testHasRedFlush() {
        assertTrue(scorer.hasFlush(createHand("AH 3H 4H 9H 6H")));
    }

    public void testStraightFlushHasFlush() {
        assertTrue(scorer.hasFlush(createHand("AS 3S 4S 5S 6S")));
    }


    /**
     * @param hand1 first hand
     * @param hand2 second hand
     * @param type expectation for hand1 compared to hand2. if smaller then hand1 expected to be smaller than hand2.
     */
    @SuppressWarnings("HardCodedStringLiteral")
    private void compareHands(Hand hand1, Hand hand2, CompareType type) {

        HandScore score1 = scorer.getScore(hand1);
        HandScore score2 = scorer.getScore(hand2);
        int result = score1.compareTo(score2);
        String h1 =  hand1 + " score=" + score1 + " type="+ score1.getType();
        String h2 =  hand2 + " score=" + score2 + " type="+ score2.getType();

        switch (type) {
         case LARGER:
             assertTrue("Unexpected " + h1 + " unexpectedly less than or equals to " + h2, result > 0);
             break;
         case SMALLER:
             assertTrue("Unexpected " + h1 + " unexpectedly greater than or equal to " + h2,  result < 0);
             break;
        case SAME :
             assertTrue("Unexpected " + h1 + " unexpectedly not equal to " + h2, result == 0);
             break;
        }
    }
}
