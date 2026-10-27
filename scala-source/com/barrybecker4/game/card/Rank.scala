/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.game.card

import java.util.{HashMap, Map as JMap}

/** Extends java.lang.Enum so Java static imports of cases keep working during the port. */
enum Rank extends java.lang.Enum[Rank]:
  case DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE

  def getSymbol: String = this match
    case Rank.DEUCE => "2"
    case Rank.THREE => "3"
    case Rank.FOUR => "4"
    case Rank.FIVE => "5"
    case Rank.SIX => "6"
    case Rank.SEVEN => "7"
    case Rank.EIGHT => "8"
    case Rank.NINE => "9"
    case Rank.TEN => "10"
    case Rank.JACK => "J"
    case Rank.QUEEN => "Q"
    case Rank.KING => "K"
    case Rank.ACE => "A"
end Rank

object RankLookup:
  private val rankFromSymbol: JMap[String, Rank] =
    val m = new HashMap[String, Rank]()
    Rank.values.foreach(r => m.put(r.getSymbol, r))
    m

  def getRankForSymbol(symbol: String): Rank | Null = rankFromSymbol.get(symbol)
