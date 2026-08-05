// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.ui.chips

import com.barrybecker4.math.Range
import java.util.{LinkedHashMap, Map}
import scala.jdk.CollectionConverters.*

class PokerChips(amount: Int) extends LinkedHashMap[PokerChip, Integer] {
  import PokerChips.*

  initialize(amount)

  def initialize(amount: Int): Unit = {
    var remainder = amount

    val it = THRESHOLDS.keySet().iterator()
    while (it.hasNext) {
      val chipType = it.next()
      assert(remainder >= 0, s"remainder $remainder is negative")
      if (remainder >= THRESHOLDS.get(chipType).max) {
        val amtToConvert = remainder - (remainder % THRESHOLDS.get(chipType).min.toInt)
        val nChips = amtToConvert / chipType.getValue
        remainder -= nChips * chipType.getValue
        put(chipType, nChips)
      }
    }
    assert(remainder == 0, s"remainder was $remainder, not 0 as expected")
  }
}

object PokerChips {
  private val THRESHOLDS: Map[PokerChip, Range] = {
    val map = LinkedHashMap[PokerChip, Range]()
    map.put(PokerChip.FIVE_HUNDRED, Range(500, 1000))
    map.put(PokerChip.TWENTY_FIVE, Range(200, 300))
    map.put(PokerChip.TEN, Range(70, 100))
    map.put(PokerChip.FIVE, Range(15, 20))
    map.put(PokerChip.ONE, Range(1, 1))
    map
  }
}
