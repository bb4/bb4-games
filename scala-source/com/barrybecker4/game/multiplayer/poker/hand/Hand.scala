// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand

import com.barrybecker4.game.card.{Card, Deck}
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters.*

class Hand(private val hand: java.util.List[Card], private var faceUp: Boolean = false) {

  def this(cards: java.util.List[Card]) =
    this(cards, false)

  def this(deck: Deck, numCards: Int) = 
    this(Hand.dealCards(deck, numCards))

  def getCards: java.util.List[Card] = 
    java.util.ArrayList(hand)

  def setFaceUp(faceUp: Boolean): Unit = 
    this.faceUp = faceUp

  def isFaceUp: Boolean = faceUp

  def size: Int = hand.size

  override def toString: String = s"[$hand]"
}

object Hand {
  private def dealCards(deck: Deck, numCards: Int): java.util.List[Card] = {
    val hand = java.util.ArrayList[Card]()
    assert(numCards <= deck.size, "you can't deal more cards than you have in the deck")
    for (i <- 0 until numCards) {
      hand.add(deck.remove(0))
    }
    hand
  }
}
