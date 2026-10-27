/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group

import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.group.{GoGroup, IGoGroup}
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionSet}

/**
 * Determine the absolute health of a group independent of the health of neighboring groups.
 * @author Barry Becker
 */
class RelativeHealthCalculator(val group_ : IGoGroup, val analyzerMap_ : GroupAnalyzerMap) {

  /**
   * Calculate the relative health of a group.
   * This method must be called only after calculateAbsoluteHealth has be done for all groups.
   * Good health is positive for a black group.
   * This measure of the group's health should be much more accurate than the absolute health
   * because it takes into account the relative health of neighboring groups.
   * If the health of an opponent bordering group is in worse shape
   * than our own then we get a boost since we can probably kill that group first.
   *
   * @return the overall health of the group.
   */
  def calculateRelativeHealth(board: GoBoard, absoluteHealth: Float): Float = {
    boostRelativeHealthBasedOnWeakNbr(board, absoluteHealth)
  }

  /**
   * If there is a weakest group, then boost ourselves relative to it.
   * it may be a positive or negative boost to our health depending on its relative strength.
   * @return relative health
   */
  private def boostRelativeHealthBasedOnWeakNbr(board: GoBoard, absoluteHealth: Float): Float = {
    var relativeHealth = absoluteHealth
    val groupStones = group_.getStones
    val finder = new WeakestGroupFinder(board, analyzerMap_)
    val weakestGroup = finder.findWeakestGroup(groupStones)

    if (weakestGroup != null) {
      val proportionWithEnemyNbrs = findProportionWithEnemyNbrs(groupStones)

      val diff = absoluteHealth + analyzerMap_.getAnalyzer(weakestGroup).getAbsoluteHealth

      relativeHealth =
        Math.min(1.0, Math.max(-1.0, absoluteHealth + diff * proportionWithEnemyNbrs)).toFloat
    }
    groupStones.unvisitPositions()
    relativeHealth
  }

  /**
   * What proportion of the groups stones are close to enemy groups?
   * this gives us an indication of how surrounded we are.
   * If we are very surrounded then we give a big boost for being stronger or weaker than a nbr.
   * If we are not very surrounded then we don't give much of a boost because there are other
   * ways to make life (i.e. run out/away).
   * @return proportion of our group stones with enemy neighbors.
   */
  private def findProportionWithEnemyNbrs(groupStones: GoBoardPositionSet): Double = {
    var numWithEnemyNbrs = 0
    for (p <- groupStones.toArray(new Array[Object](0))) {
      val stone = p.asInstanceOf[GoBoardPosition]
      if (stone.isVisited) {
        numWithEnemyNbrs += 1
        stone.setVisited(false)
      }
    }
    numWithEnemyNbrs.toDouble / (groupStones.size.toDouble + 2)
  }
}
