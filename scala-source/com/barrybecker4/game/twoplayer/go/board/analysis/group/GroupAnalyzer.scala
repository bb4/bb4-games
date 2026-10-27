/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.go.GoController
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.elements.eye.GoEyeSet
import com.barrybecker4.game.twoplayer.go.board.elements.group.{GroupChangeListener, IGoGroup}
import com.barrybecker4.game.twoplayer.go.board.elements.position.{GoBoardPosition, GoStone}

/**
 * Analyzes a group to determine how alive it is, and also find other properties like eyes and liberties.
 *
 * @author Barry Becker
 */
class GroupAnalyzer(val group_ : IGoGroup, val analyzerMap_ : GroupAnalyzerMap) extends GroupChangeListener with Cloneable {

  private val stoneInGroupAnalyzer_ = new StoneInGroupAnalyzer(group_)
  private var relativeHealth_ = 0.0f
  private var absHealthCalculator_ = new AbsoluteHealthCalculator(group_, analyzerMap_)
  private var absoluteHealth_ = 0.0f

  group_.addChangeListener(this)

  def getGroup: IGoGroup = group_

  /**
   * Called when the group we are maintaining info about changes.
   * It changes by having stones added or removed.
   */
  override def groupChanged(): Unit = {
    invalidate()
  }

  /**
   * @return health score independent of neighboring groups.
   */
  def getAbsoluteHealth: Float = absoluteHealth_

  /**
   * We try to use the cached relative health value if we can.
   * @param board needed to calculate new value if not cached
   * @param useCachedValue if true, just return the cached value instead of checking for validity.
   *   Only do this if you are sure the value returned does not have to be perfectly accurate.
   * @return relative health
   */
  def getRelativeHealth(board: GoBoard, useCachedValue: Boolean): Float = {
    if (!GoController.USE_RELATIVE_GROUP_SCORING) {
      return getAbsoluteHealth
    }
    if (isValid || useCachedValue) {
      if (!isValid)
        GameContext.log(3, "using cached relative health when not valid")
      return getRelativeHealth
    }
    GameContext.log(0, "stale abs health. recalculating relative health")
    calculateRelativeHealth(board)
  }

  /**
   * @return health score dependent on strength of neighboring groups.
   */
  def getRelativeHealth: Float = relativeHealth_

  def invalidate(): Unit = {
    absHealthCalculator_.invalidate()
  }

  /**
   * @return true if the group has changed (structurally) in any way.
   */
  def isValid: Boolean = {
    absHealthCalculator_.isValid
  }

  /**
   * If nothing cached, this may not be accurate.
   * @return number of cached liberties.
   */
  def getNumLiberties(board: GoBoard): Int = {
    group_.getNumLiberties(board)
  }

  /**
   * @return set of eyes currently identified for this group.
   */
  def getEyes(board: GoBoard): GoEyeSet = {
    absHealthCalculator_.getEyes(board)
  }

  def calculateAbsoluteHealth(board: GoBoard): Float = {
    absoluteHealth_ = absHealthCalculator_.calculateAbsoluteHealth(board)
    absoluteHealth_
  }

  /**
   * @return true if the piece is an enemy of the set owner.
   *  If the difference in health between the stones is great, then they are not really enemies
   *  because one of them is dead.
   */
  def isTrueEnemy(pos: GoBoardPosition): Boolean = {
    assert(pos.isOccupied)
    val stone = pos.getPiece.asInstanceOf[GoStone]
    val muchWeaker = isStoneMuchWeakerThanGroup(stone)

    stone.isOwnedByPlayer1 != group_.isOwnedByPlayer1 && !muchWeaker
  }

  /**
   * used only for test. Remove when tested through AbsoluteGroupHealthCalc
   * @return eye potential
   */
  def getEyePotential: Float = {
    absHealthCalculator_.getEyePotential
  }

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
  def calculateRelativeHealth(board: GoBoard): Float = {
    if (!isValid) {
      calculateAbsoluteHealth(board)
    }

    val relativeCalculator = new RelativeHealthCalculator(group_, analyzerMap_)
    relativeHealth_ = relativeCalculator.calculateRelativeHealth(board, absoluteHealth_)

    relativeHealth_
  }

  /**
   * @return a deep copy of this GroupAnalyzer
   */
  override def clone(): Object = {
    val cloned = super.clone().asInstanceOf[GroupAnalyzer]
    cloned.absHealthCalculator_ = new AbsoluteHealthCalculator(group_, analyzerMap_)
    cloned
  }

  /**
   * @return true if the stone is much weaker than the group
   */
  private def isStoneMuchWeakerThanGroup(stone: GoStone): Boolean = {
    stoneInGroupAnalyzer_.isStoneMuchWeakerThanGroup(stone, getAbsoluteHealth)
  }

  /**
   * @return string form.
   */
  override def toString: String = {
    val newline = "\n"
    val sb = new StringBuilder()

    sb.append(group_.toString)
    val eyes = getEyes(null)
    if (eyes != null && !eyes.isEmpty)
      sb.append(eyes.toString).append(newline)
    sb.append("abs health=").append(FormatUtil.formatNumber(getAbsoluteHealth))
    sb.append(" rel health=").append(FormatUtil.formatNumber(getRelativeHealth))
    sb.append(" group Liberties=").append(getNumLiberties(null)).append('\n')
    sb.toString
  }
}
