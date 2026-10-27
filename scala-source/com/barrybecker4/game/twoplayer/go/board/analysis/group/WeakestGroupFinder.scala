/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group

import com.barrybecker4.game.twoplayer.go.board.{GoBoard, GoProfiler}
import com.barrybecker4.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer
import com.barrybecker4.game.twoplayer.go.board.elements.group.{GoGroup, GoGroupSet}
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoBoardPositionSet}

import java.util.Set

/**
 * Finds the weakest group on the board relative to some other group.
 * @author Barry Becker
 */
class WeakestGroupFinder(val board: GoBoard, val analyzerMap: GroupAnalyzerMap) {

  /**
   * Given a set of same color stones, find the most alive enemy group bordering it.
   * A dead neighbor must not hide a living one. An enemy at perfect life (±1) is included.
   * @param groupStones the stones to find the strongest bordering enemy of.
   *            Note: we assume that the groupStones all have the same color.
   * @return the most alive bordering enemy group. Returns null if no group found.
   */
  def findWeakestGroup(groupStones: GoBoardPositionSet): GoGroup = {
    val isPlayer1 = groupStones.getOneMember.getPiece.isOwnedByPlayer1
    val enemyNbrGroups = getEnemyGroupNeighbors(groupStones, isPlayer1)

    var strongestGroup: GoGroup = null
    var strongestLife = Float.NegativeInfinity

    for (egroup <- enemyNbrGroups.toArray(new Array[Object](0))) {
      val enemyGroup = egroup.asInstanceOf[GoGroup]
      val h = analyzerMap.getAnalyzer(enemyGroup).getAbsoluteHealth
      val enemyLife = if (enemyGroup.isOwnedByPlayer1) h else -h
      if (enemyLife > strongestLife) {
        strongestLife = enemyLife
        strongestGroup = enemyGroup
      }
    }
    strongestGroup
  }

  /**
   * note: has intentional side effect of marking stones with enemy group nbrs as visited (within groupStones).
   * @param groupStones the set of stones in the group to find enemies of.
   * @return a HashSet of the groups that are enemies of this group
   */
  private def getEnemyGroupNeighbors(groupStones: GoBoardPositionSet, isPlayer1: Boolean): Set[?] = {
    GoProfiler.getInstance.startGetEnemyGroupNbrs()
    val enemyNbrs = new GoGroupSet()
    val nbrAnalyzer = new NeighborAnalyzer(board)

    for (stone <- groupStones.toArray(new Array[GoBoardPosition](0))) {
      val nbrs = nbrAnalyzer.findGroupNeighbors(stone, false)
      addEnemyNeighborsForStone(enemyNbrs, stone, nbrs, isPlayer1)
    }
    GoProfiler.getInstance.stopGetEnemyGroupNbrs()
    enemyNbrs
  }

  /**
   * if the stone has any enemy nbrs then mark it visited.
   * later we will count how many got visited.
   * this is a bit of a hack to determine how surrounded the group is by enemy groups.
   * @param enemyNbrs the enemy neighbors to add.
   * @param stone stone that enyNbrs are enemy of.
   * @param nbrs set of stones to add enemyNbrs to.
   */
  private def addEnemyNeighborsForStone(enemyNbrs: GoGroupSet, stone: GoBoardPosition,
                                        nbrs: GoBoardPositionSet, isPlayer1: Boolean): Unit = {

    for (possibleEnemy <- nbrs.toArray(new Array[GoBoardPosition](0))) {
      if (possibleEnemy.getPiece != null
        && possibleEnemy.getPiece.isOwnedByPlayer1 != isPlayer1
        && !possibleEnemy.isInEye) {
        stone.setVisited(true)
        assert(possibleEnemy.getGroup != null, s"Possible enemy, $possibleEnemy, had no group associated with it!")
        enemyNbrs.add(possibleEnemy.getGroup)
      }
    }
  }
}
