// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.card

import java.util
import java.util.Collections
import scala.jdk.CollectionConverters.*

/** Represents a standard deck of playing cards without jokers. */
class Deck extends util.ArrayList[Card](Deck.protoDeck):
  Collections.shuffle(this)

object Deck:
  private val protoDeck: util.List[Card] =
    (for
      suit <- Suit.values
      rank <- Rank.values
    yield Card(rank, suit)).toList.asJava
