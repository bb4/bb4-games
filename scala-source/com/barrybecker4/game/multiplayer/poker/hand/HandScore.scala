// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand

import com.barrybecker4.game.card.Rank
import java.util.List

class HandScore(private val `type`: HandType, private val ranks: List[Rank]) 
    extends Comparable[HandScore] {

  def getType: HandType = `type`

  override def compareTo(score: HandScore): Int = {
    if (this.`type`.ordinal() == score.`type`.ordinal()) {
      val numRanks = Math.min(this.ranks.size, score.ranks.size)
      var i = 0
      var thisRank: Rank = this.ranks.get(i)
      var thatRank: Rank = score.ranks.get(i)
      i += 1
      while (thisRank == thatRank && i < numRanks) {
        thisRank = this.ranks.get(i)
        thatRank = score.ranks.get(i)
        i += 1
      }

      if (i <= numRanks) thisRank.ordinal() - thatRank.ordinal() else 0
    } else {
      this.`type`.ordinal() - score.`type`.ordinal()
    }
  }
}
