// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.card

import org.scalatest.funsuite.AnyFunSuite
import java.util.Collections

class DeckSuite extends AnyFunSuite:

  test("sort shuffled deck") {
    val deck = Deck()
    Collections.sort(deck, CardComparator())
    val expected =
      "[DEUCE of SPADES, DEUCE of CLUBS, DEUCE of DIAMONDS, DEUCE of HEARTS, " +
        "THREE of SPADES, THREE of CLUBS, THREE of DIAMONDS, THREE of HEARTS, " +
        "FOUR of SPADES, FOUR of CLUBS, FOUR of DIAMONDS, FOUR of HEARTS, " +
        "FIVE of SPADES, FIVE of CLUBS, FIVE of DIAMONDS, FIVE of HEARTS, " +
        "SIX of SPADES, SIX of CLUBS, SIX of DIAMONDS, SIX of HEARTS, " +
        "SEVEN of SPADES, SEVEN of CLUBS, SEVEN of DIAMONDS, SEVEN of HEARTS, " +
        "EIGHT of SPADES, EIGHT of CLUBS, EIGHT of DIAMONDS, EIGHT of HEARTS, " +
        "NINE of SPADES, NINE of CLUBS, NINE of DIAMONDS, NINE of HEARTS, " +
        "TEN of SPADES, TEN of CLUBS, TEN of DIAMONDS, TEN of HEARTS, " +
        "JACK of SPADES, JACK of CLUBS, JACK of DIAMONDS, JACK of HEARTS, " +
        "QUEEN of SPADES, QUEEN of CLUBS, QUEEN of DIAMONDS, QUEEN of HEARTS, " +
        "KING of SPADES, KING of CLUBS, KING of DIAMONDS, KING of HEARTS, " +
        "ACE of SPADES, ACE of CLUBS, ACE of DIAMONDS, ACE of HEARTS]"
    assert(deck.toString == expected)
  }
