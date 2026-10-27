package com.barrybecker4.game.twoplayer.hex.pathsearch

import com.barrybecker4.game.twoplayer.hex.HexBoard
import com.barrybecker4.search.AStarSearch
import com.barrybecker4.search.queue.HeapPriorityQueue
import scala.Option
import scala.collection.immutable.Seq
import scala.jdk.javaapi.CollectionConverters

/**
 * @author Barry Becker
 */
class ShortestPathSearch(state: HexBoard, player1: Boolean) 
    extends AStarSearch[HexState, HexTransition](
      HexSearchSpace(state, player1), 
      HeapPriorityQueue[HexState, HexTransition](256, null)
    ):

  def getShortestPathCost: Int =
    val path = solve
    var totalCost = 0

    if path.isEmpty then
      totalCost = 1000
    else
      val pathList = path.get
      import scala.jdk.javaapi.CollectionConverters.*
      val javaList: java.util.List[HexTransition] = asJava(pathList)
      import scala.jdk.CollectionConverters.*
      for transition <- javaList.asScala do
        totalCost += transition.getCost

    totalCost
