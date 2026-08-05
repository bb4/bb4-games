/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.hand

enum HandType(val label: String, val odds: Float) extends java.lang.Enum[HandType] {
  case HIGH_CARD extends HandType("High Card", 1)
  case PAIR extends HandType("Pair", 1.37f)
  case TWO_PAIR extends HandType("Two Pair", 20)
  case THREE_OF_A_KIND extends HandType("Three of a Kind", 40)
  case STRAIGHT extends HandType("Straight", 254)
  case FLUSH extends HandType("Flush", 508)
  case FULL_HOUSE extends HandType("Full House", 693)
  case FOUR_OF_A_KIND extends HandType("Four of a Kind", 4164)
  case STRAIGHT_FLUSH extends HandType("Straight Flush", 72192)
  case ROYAL_FLUSH extends HandType("Royal Flush", 649740)
  case FIVE_OF_A_KIND extends HandType("Five of a Kind", 749740)

  override def toString: String = label
}
