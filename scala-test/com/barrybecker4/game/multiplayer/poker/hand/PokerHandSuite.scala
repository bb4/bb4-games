// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand

import com.barrybecker4.game.card.Deck
import org.scalatest.funsuite.AnyFunSuite

/** Programming challenge to test which poker hands are better. */
class PokerHandSuite extends AnyFunSuite:

  test("hands of different sizes") {
    val deck = new Deck()

    for i <- 5 until 9 do
      val hand = new Hand(deck, i)
      assert(hand.size == i, "Unexpected size of hand.")
  }
