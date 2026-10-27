// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand

import com.barrybecker4.game.card.Rank.*
import org.scalatest.funsuite.AnyFunSuite

import java.util.Arrays

/** Programming challenge to test which poker hands are better. */
class HandScoreSuite extends AnyFunSuite:

  private val VeryHighScore = new HandScore(HandType.FULL_HOUSE, Arrays.asList(NINE, EIGHT))
  private val HighScore = new HandScore(HandType.THREE_OF_A_KIND, Arrays.asList(NINE))
  private val MediumScore = new HandScore(HandType.PAIR, Arrays.asList(KING))
  private val LowScore = new HandScore(HandType.HIGH_CARD, Arrays.asList(JACK))

  test("a low higher than very low") {
    assert(LowScore.compareTo(new HandScore(HandType.HIGH_CARD, Arrays.asList(TEN, NINE))) > 0)
  }

  test("a low lower than medium") {
    assert(LowScore.compareTo(MediumScore) < 0)
  }

  test("low equals low") {
    assert(LowScore.compareTo(LowScore) == 0)
  }

  test("high greater than medium") {
    assert(HighScore.compareTo(MediumScore) > 0)
  }

  test("very high greater than high") {
    assert(VeryHighScore.compareTo(HighScore) > 0)
  }

  test("low less than very high") {
    assert(LowScore.compareTo(VeryHighScore) < 0)
  }

  test("high equals high") {
    val threeOfAKind = new HandScore(HandType.THREE_OF_A_KIND, Arrays.asList(NINE, QUEEN, DEUCE))
    assert(HighScore.compareTo(threeOfAKind) == 0)
  }
