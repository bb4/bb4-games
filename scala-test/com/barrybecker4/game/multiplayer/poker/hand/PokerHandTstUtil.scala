// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand

import com.barrybecker4.game.card.Card

import java.util.StringTokenizer

object PokerHandTstUtil:

  def createHand(line: String): Hand =
    val cards = new java.util.ArrayList[Card](5)
    val tokenizer = new StringTokenizer(line, " ")

    while tokenizer.hasMoreElements do
      val cardToken = tokenizer.nextElement().asInstanceOf[String]
      cards.add(new Card(cardToken))

    new Hand(cards)
