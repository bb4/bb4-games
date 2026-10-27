/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.card

import java.io.Serializable

/** Represents a standard playing card. */
class Card(val rank: Rank, val suit: Suit) extends Serializable:

  /**
   * Create a card instance from a string.
   * @param cardToken string representation of the card (e.g. "JD", or "10H")
   */
  def this(cardToken: String) =
    this(
      RankLookup.getRankForSymbol(cardToken.substring(0, cardToken.length - 1)).asInstanceOf[Rank],
      SuitLookup.getSuitForSymbol(cardToken.substring(cardToken.length - 1)).asInstanceOf[Suit]
    )
    assert(cardToken.length < 4)

  override def toString: String = s"$rank of $suit"
