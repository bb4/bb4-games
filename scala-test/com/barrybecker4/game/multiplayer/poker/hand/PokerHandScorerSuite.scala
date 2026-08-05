/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.hand

import com.barrybecker4.game.card.Rank
import org.scalatest.funsuite.AnyFunSuite

import java.util.Arrays

/** Programming challenge to test which poker hands are better. */
class PokerHandScorerSuite extends AnyFunSuite:

  private enum CompareType:
    case Larger, Smaller, Same

  private val scorer = new PokerHandScorer()

  test("ace high beats king high ordered") {
    compareHands(PokerHandTstUtil.createHand("2H 3D 5S 9C KD"), PokerHandTstUtil.createHand("2C 3H 4S 8C AH"), CompareType.Smaller)
  }

  test("ace high beats king high unordered") {
    compareHands(PokerHandTstUtil.createHand("2H KD 3D 5S 9C "), PokerHandTstUtil.createHand("2C 3H 4S AH 8C"), CompareType.Smaller)
  }

  test("three of a kind beats two pair") {
    compareHands(PokerHandTstUtil.createHand("2H 5D 5S 5C 3D"), PokerHandTstUtil.createHand("KC AH 4S 4C AH"), CompareType.Larger)
  }

  test("pair beats high card") {
    compareHands(PokerHandTstUtil.createHand("2H 4S 4C 2D 4H"), PokerHandTstUtil.createHand("2S 8S AS QS 3S"), CompareType.Larger)
  }

  test("high of hearts beats king of diamonds") {
    compareHands(PokerHandTstUtil.createHand("2H 3D 5S 9C KD"), PokerHandTstUtil.createHand("2C 3H 4S 8C KH"), CompareType.Larger)
  }

  test("king of diamonds less than king of hearts with secondary cards lower") {
    compareHands(PokerHandTstUtil.createHand("6H QD 5S 9C KD"), PokerHandTstUtil.createHand("2D 3H 5C 9S KH"), CompareType.Larger)
  }

  test("pair of 7s less than pair of jacks") {
    compareHands(PokerHandTstUtil.createHand("7C 7H 5S 9C KD"), PokerHandTstUtil.createHand("JC 3H JH 8C KH"), CompareType.Smaller)
  }

  test("pair of 9s bigger than king high") {
    compareHands(PokerHandTstUtil.createHand("9C 9H 5S 9C KD"), PokerHandTstUtil.createHand("3C 4H 3H 8C KH"), CompareType.Larger)
  }

  test("pair of 9s bigger than pair of 4s") {
    compareHands(PokerHandTstUtil.createHand("9C 9H 5S 3C 2D"), PokerHandTstUtil.createHand("4C 4H AH 8C KH"), CompareType.Larger)
  }

  test("pair of 7s smaller than pair of 8s") {
    compareHands(PokerHandTstUtil.createHand("7C 7H JS 9C KD"), PokerHandTstUtil.createHand("8C 8H 3H 5C 4H"), CompareType.Smaller)
  }

  test("two different hands with same value") {
    compareHands(PokerHandTstUtil.createHand("9H 8D 7S 6C 5D"), PokerHandTstUtil.createHand("9C 8H 7S 6C 5H"), CompareType.Same)
  }

  test("same pair uses high secondary card to resolve") {
    compareHands(PokerHandTstUtil.createHand("9H AS AD QC 3D"), PokerHandTstUtil.createHand("AC AH QS 4C JD"), CompareType.Smaller)
  }

  test("rank of three in full house used first") {
    compareHands(PokerHandTstUtil.createHand("KH 3S KD 3C 3D"), PokerHandTstUtil.createHand("4C JH JS 4D JD"), CompareType.Smaller)
  }

  test("two in full house used if three tie") {
    compareHands(PokerHandTstUtil.createHand("10H JS 10D 10C JD"), PokerHandTstUtil.createHand("AC AH 10S 10C 10D"), CompareType.Smaller)
  }

  test("second pair used to break tie") {
    compareHands(PokerHandTstUtil.createHand("10H JS 10D 3C JD"), PokerHandTstUtil.createHand("AC AH 10S 10C AS"), CompareType.Smaller)
  }

  test("straight higher than triple") {
    compareHands(PokerHandTstUtil.createHand("9H 10S JD QC KD"), PokerHandTstUtil.createHand("AC AH 10S 9C AS"), CompareType.Larger)
  }

  test("two pair higher than ace high") {
    compareHands(PokerHandTstUtil.createHand("AH 8C 7H 6C 5H"), PokerHandTstUtil.createHand("7D 7C 4C 3C 3S"), CompareType.Smaller)
  }

  test("has straight when none") {
    val score = scorer.getScore(PokerHandTstUtil.createHand("2H 4S 4C 2D 4H"))
    assert(score.getType != HandType.STRAIGHT, "Unexpectedly had a straight")
  }

  test("has normal straight") {
    val score = scorer.getScore(PokerHandTstUtil.createHand("3H 4S 5C 6D 7H"))
    assert(score.getType == HandType.STRAIGHT, "Straight not found")
  }

  test("has normal straight two low") {
    val score = scorer.getScore(PokerHandTstUtil.createHand("2H 3S 4C 5D 6H"))
    assert(score.getType == HandType.STRAIGHT, "Straight not found")
  }

  test("has ace high straight") {
    assert(scorer.hasStraight(Arrays.asList(Rank.ACE, Rank.KING, Rank.QUEEN, Rank.JACK, Rank.TEN)))
  }

  test("has ace low straight") {
    assert(scorer.hasStraight(Arrays.asList(Rank.FIVE, Rank.FOUR, Rank.THREE, Rank.DEUCE, Rank.ACE)))
  }

  test("has ace out of order straight") {
    assert(!scorer.hasStraight(Arrays.asList(Rank.ACE, Rank.FIVE, Rank.FOUR, Rank.THREE, Rank.DEUCE)))
  }

  test("has middle straight") {
    assert(scorer.hasStraight(Arrays.asList(Rank.TEN, Rank.NINE, Rank.EIGHT, Rank.SEVEN, Rank.SIX)))
  }

  test("no straight") {
    assert(!scorer.hasStraight(Arrays.asList(Rank.JACK, Rank.NINE, Rank.EIGHT, Rank.SEVEN, Rank.SIX)))
  }

  test("out of order straight") {
    assert(!scorer.hasStraight(Arrays.asList(Rank.NINE, Rank.JACK, Rank.EIGHT, Rank.SEVEN, Rank.SIX)))
  }

  test("not flush") {
    assert(!scorer.hasFlush(PokerHandTstUtil.createHand("AH 3S 4C 5D 6H")))
  }

  test("not flush but all red") {
    assert(!scorer.hasFlush(PokerHandTstUtil.createHand("AH 3D 4D 5D 6H")))
  }

  test("has black flush") {
    assert(scorer.hasFlush(PokerHandTstUtil.createHand("AS 3S 4S 9S 6S")))
  }

  test("has red flush") {
    assert(scorer.hasFlush(PokerHandTstUtil.createHand("AH 3H 4H 9H 6H")))
  }

  test("straight flush has flush") {
    assert(scorer.hasFlush(PokerHandTstUtil.createHand("AS 3S 4S 5S 6S")))
  }

  private def compareHands(hand1: Hand, hand2: Hand, compareType: CompareType): Unit =
    val score1 = scorer.getScore(hand1)
    val score2 = scorer.getScore(hand2)
    val result = score1.compareTo(score2)
    val h1 = hand1.toString + " score=" + score1 + " type=" + score1.getType
    val h2 = hand2.toString + " score=" + score2 + " type=" + score2.getType

    compareType match
      case CompareType.Larger =>
        assert(result > 0, "Unexpected " + h1 + " unexpectedly less than or equals to " + h2)
      case CompareType.Smaller =>
        assert(result < 0, "Unexpected " + h1 + " unexpectedly greater than or equal to " + h2)
      case CompareType.Same =>
        assert(result == 0, "Unexpected " + h1 + " unexpectedly not equal to " + h2)
