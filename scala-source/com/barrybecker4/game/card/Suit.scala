/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.card

import java.util.{HashMap, Map as JMap}

/** Card suits listed in increasing order of value. Extends java.lang.Enum for Java interop. */
enum Suit extends java.lang.Enum[Suit]:
  case SPADES, CLUBS, DIAMONDS, HEARTS

  def getSymbol: String = this match
    case Suit.SPADES => "S"
    case Suit.CLUBS => "C"
    case Suit.DIAMONDS => "D"
    case Suit.HEARTS => "H"
end Suit

object SuitLookup:
  private val suitFromSymbol: JMap[String, Suit] =
    val m = new HashMap[String, Suit]()
    Suit.values.foreach(s => m.put(s.getSymbol, s))
    m

  def getSuitForSymbol(symbol: String): Suit | Null = suitFromSymbol.get(symbol)
