// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.card

import java.util.Comparator

/** Used to sort a set of cards in ascending order. */
class CardComparator extends Comparator[Card]:
  override def compare(card1: Card, card2: Card): Int =
    if card1.rank == card2.rank then
      card1.suit.ordinal - card2.suit.ordinal
    else
      card1.rank.ordinal - card2.rank.ordinal
