// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand

import com.barrybecker4.game.card.{Card, Rank, Suit}
import java.util.List
import HandType.*

class PokerHandScorer {

  def getScore(hand: Hand): HandScore = {
    val grouping = RankGrouping(hand.getCards)
    val ranks = grouping.getRanks

    val hasStraightVal = hasStraight(ranks)
    val hasFlushVal = hasFlush(hand)

    val handType = determineType(grouping, hasStraightVal, hasFlushVal)

    HandScore(handType, ranks)
  }

  private def determineType(grouping: RankGrouping, hasStraightVal: Boolean, hasFlushVal: Boolean): HandType = {
    if (grouping.matchesCounts(5)) FIVE_OF_A_KIND
    else if (hasStraightVal && hasFlushVal) STRAIGHT_FLUSH
    else if (grouping.matchesCounts(4, 1)) FOUR_OF_A_KIND
    else if (grouping.matchesCounts(3, 2)) FULL_HOUSE
    else if (hasFlushVal) FLUSH
    else if (hasStraightVal) STRAIGHT
    else if (grouping.matchesCounts(3, 1, 1)) THREE_OF_A_KIND
    else if (grouping.matchesCounts(2, 2, 1)) TWO_PAIR
    else if (grouping.matchesCounts(2, 1, 1, 1)) PAIR
    else HIGH_CARD
  }

  def hasFlush(hand: Hand): Boolean = {
    val cards = hand.getCards
    val suit = cards.get(0).suit
    (0 until cards.size).forall(i => cards.get(i).suit == suit)
  }

  def hasStraight(ranks: List[Rank]): Boolean = {
    ranks.size == 5 &&
      (ranks.get(4) == Rank.ACE || (ranks.get(0).ordinal() - ranks.get(4).ordinal()) == 4)
  }
}
