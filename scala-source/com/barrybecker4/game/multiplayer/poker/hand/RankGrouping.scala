// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.hand

import com.barrybecker4.game.card.{Card, Rank}
import java.util.{ArrayList, Collections, HashMap, List}
import scala.jdk.CollectionConverters.*

private class RankGrouping(hand: List[Card]) {
  import Rank.*

  private val ACE_LOW_STRAIGHT = java.util.Arrays.asList(ACE, FIVE, FOUR, THREE, DEUCE)

  private val groups: List[RankGroup] = init(hand)

  def getRanks: List[Rank] = {
    val ranks = ArrayList[Rank]()
    for (i <- 0 until groups.size) {
      ranks.add(groups.get(i).rank)
    }
    if (ranks == ACE_LOW_STRAIGHT) {
      ranks.remove(0)
      ranks.add(ACE)
    }
    ranks
  }

  def matchesCounts(counts: Int*): Boolean =
    groups.size == counts.length &&
      (0 until counts.length).forall(i => counts(i) == groups.get(i).count)

  private def init(hand: List[Card]): List[RankGroup] = {
    val map = HashMap[Rank, RankGroup]()

    for (i <- 0 until hand.size) {
      val card = hand.get(i)
      val group = map.get(card.rank)
      if (group != null) {
        group.count += 1
      } else {
        map.put(card.rank, RankGroup(card.rank, 1))
      }
    }

    val groups = ArrayList[RankGroup](map.values())
    Collections.sort(groups)
    groups
  }

  private case class RankGroup(rank: Rank, var count: Int) extends Comparable[RankGroup] {
    override def compareTo(group: RankGroup): Int = {
      if (group.count == this.count) {
        group.rank.ordinal() - this.rank.ordinal()
      } else {
        group.count - this.count
      }
    }
  }
}
