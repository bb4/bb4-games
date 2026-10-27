/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group

import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoStone

/**
 * Analyze the strength of stone relative to the group that it is in.
 *
 * @author Barry Becker
 */
class StoneInGroupAnalyzer(val group_ : IGoGroup) {

  private val DIFFERENCE_THRESHOLD = 0.6f
  private val MIN_LIFE_THRESH = 0.2f

  /**
   * @return true if the stone is much weaker than the group
   */
  def isStoneMuchWeakerThanGroup(stone: GoStone, absoluteHealth: Float): Boolean = {
    isStoneWeakerThanGroup(stone, DIFFERENCE_THRESHOLD, absoluteHealth)
  }

  /**
   * @return return true of the stone is greater than threshold weaker than the group.
   */
  private def isStoneWeakerThanGroup(stone: GoStone, threshold: Float, groupHealth: Float): Boolean = {
    val constrainedGroupHealth = getConstrainedGroupHealth(groupHealth)

    val stoneHealth = stone.getHealth
    val muchWeaker =
      if (stone.isOwnedByPlayer1) {
        assert(!group_.isOwnedByPlayer1)
        -constrainedGroupHealth - stoneHealth > threshold
      }
      else {
        assert(group_.isOwnedByPlayer1)
        constrainedGroupHealth + stoneHealth > threshold
      }

    muchWeaker
  }

  /**
   * for purposes of determining relative weakness. Don't allow the outer group to go out of its living range.
   * @param rawGroupHealth  original group health value
   * @return group health constrained to between +/- MIN_LIFE_THRESH
   */
  private def getConstrainedGroupHealth(rawGroupHealth: Float): Float = {
    var health = rawGroupHealth
    if (group_.isOwnedByPlayer1 && rawGroupHealth < MIN_LIFE_THRESH) {
      health = MIN_LIFE_THRESH
    } else if (!group_.isOwnedByPlayer1 && rawGroupHealth > -MIN_LIFE_THRESH) {
      health = -MIN_LIFE_THRESH
    }
    health
  }
}
