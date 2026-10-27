// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.set

import com.barrybecker4.game.common.GameContext
import java.util.{ArrayList, Collections}
import Card.AttributeValue

class Deck extends ArrayList[Card](Deck.protoDeck) {
  Collections.shuffle(this)
}

object Deck {
  private val protoDeck = {
    val deck = ArrayList[Card]()
    for (color <- Card.AttributeValue.values) {
      for (shape <- AttributeValue.values) {
        for (number <- AttributeValue.values) {
          for (texture <- AttributeValue.values) {
            deck.add(Card(color, shape, number, texture))
          }
        }
      }
    }
    deck
  }

  def main(args: Array[String]): Unit = 
    GameContext.log(0, s"deck=${Deck()}")
}
