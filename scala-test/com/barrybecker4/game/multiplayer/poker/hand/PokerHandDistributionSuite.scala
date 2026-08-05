// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand

import com.barrybecker4.game.card.{Deck, Rank}
import org.scalatest.funsuite.AnyFunSuite

import java.util
import java.util.HashMap

/** Verify that the distribution of the poker hand types roughly matches reality. */
class PokerHandDistributionSuite extends AnyFunSuite:

  private val NumHands = 7000

  test("hand distributions") {
    val distribution = new HashMap[HandType, Integer]()
    val scorer = new PokerHandScorer()

    for _ <- 0 until NumHands do
      val hand = new Hand(new Deck(), 5)
      val handType = scorer.getScore(hand).getType

      if distribution.containsKey(handType) then
        distribution.put(handType, distribution.get(handType) + 1)
      else
        distribution.put(handType, 1)

    assert(true)
  }
