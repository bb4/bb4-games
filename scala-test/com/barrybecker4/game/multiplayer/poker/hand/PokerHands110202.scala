/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.hand

import com.barrybecker4.game.card.{Card, Rank}
import org.scalatest.funsuite.AnyFunSuite

import java.io.{IOException, InputStream}
import java.util
import java.util.{ArrayList, StringTokenizer}

/**
 * Programming challenge to test which poker hands are better.
 * When running, enter 2 poker hands to be compared. There are exactly 5 cards in a hand.
 */
object PokerHands110202:

  private val MaxLg = 255

  def readLine(stream: InputStream): String =
    val lin = new Array[Byte](MaxLg)
    var lg = 0
    var car = -1

    try
      while lg < MaxLg do
        car = stream.read()
        if car < 0 || car == '\n' then
          // fall through to return
          ()
        else
          lin(lg) = car.toByte
          lg += 1
    catch
      case e: IOException =>
        e.printStackTrace()
        return null

    if car < 0 && lg == 0 then
      null
    else
      new String(lin, 0, lg)

  def evaluateLine(line: String): Unit =
    if line == null || line.length < 2 then return

    val blackCards = new ArrayList[Card](5)
    val whiteCards = new ArrayList[Card](5)
    val tokenizer = new StringTokenizer(line, " ")

    var ct = 0
    while tokenizer.hasMoreElements do
      val cardToken = tokenizer.nextElement().asInstanceOf[String]
      if ct < 5 then
        blackCards.add(new Card(cardToken))
      else if ct < 10 then
        whiteCards.add(new Card(cardToken))
      ct += 1

    val blackHand = new Hand(blackCards)
    val whiteHand = new Hand(whiteCards)
    val scorer = new PokerHandScorer()
    val blackWin = scorer.getScore(blackHand).compareTo(scorer.getScore(whiteHand))

    if blackWin > 0 then
      println("Black wins.")
    else if blackWin < 0 then
      println("White wins.")
    else
      println("Tie.")

  @throws[IOException]
  def evaluate(stream: InputStream): Unit =
    var line = readLine(stream)
    while line != null do
      evaluateLine(line)
      line = readLine(stream)

  def main(args: Array[String]): Unit =
    try
      evaluate(System.in)
    catch
      case e: IOException =>
        e.printStackTrace()
