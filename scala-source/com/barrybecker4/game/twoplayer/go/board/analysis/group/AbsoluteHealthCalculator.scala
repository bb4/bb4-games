/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.go.board.{GoBoard, GoProfiler}
import com.barrybecker4.game.twoplayer.go.board.analysis.group.eye.{EyeHealthEvaluator, GroupEyeCache}
import com.barrybecker4.game.twoplayer.go.board.elements.eye.GoEyeSet
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.string.IGoString
import scala.jdk.CollectionConverters._

/**
 * Determine the absolute health of a group independent of the health of neighboring groups.
 * @author Barry Becker
 */
class AbsoluteHealthCalculator(val group_ : IGoGroup, val analyzerMap_ : GroupAnalyzerMap) {

  private var absoluteHealth_ = 0.0f
  private var cachedNumStonesInGroup_ = 0
  private val eyeCache_ = new GroupEyeCache(group_, analyzerMap_)

  /**
   * @return false if the group has changed (structurally) in any way.
   */
  def isValid: Boolean = eyeCache_.isValid

  def invalidate(): Unit = {
    eyeCache_.invalidate()
  }

  /**
   * used only for test.
   * @return eye potential
   */
  def getEyePotential: Float = eyeCache_.getEyePotential

  /**
   * Calculate the absolute health of a group.
   * All the stones in the group have the same health rating because the
   * group lives or dies as a unit.
   * (not entirely true - strings live or die as unit, but there is a relationship).
   * Good health of a black group is positive; white, negative.
   * The health is a function of the number of eyes (their type and status), liberties, and
   * the health of surrounding groups. If the health of an opponent bordering group
   * is in worse shape than our own then we get a boost since we can probably
   * kill that group first. See RelativeHealthCalculator.calculateRelativeHealth.
   * A perfect 1 (or -1) indicates unconditional life (or death).
   * This means that the group cannot be killed (or given life) no matter
   * how many times the opponent plays (see Dave Benson 1977).
   *  http://senseis.xmp.net/?BensonsAlgorithm
   *
   * @return the overall health of the group independent of nbr groups.
   */
  def calculateAbsoluteHealth(board: GoBoard): Float = {
    if (eyeCache_.isValid) {
      GameContext.log(1, "cache valid. Returning health=" + absoluteHealth_)
      return absoluteHealth_
    }

    val numLiberties = group_.getNumLiberties(board)

    val side = if (group_.isOwnedByPlayer1) 1.0f else -1.0f

    var numEyes = eyeCache_.calcNumEyes()
    val numStones = group_.getNumStones
    val lifeAnalyzer = new LifeAnalyzer(group_, board, analyzerMap_)
    val eyeEvaluator = new EyeHealthEvaluator(lifeAnalyzer)

    absoluteHealth_ = eyeEvaluator.determineHealth(side, numEyes, numLiberties, numStones)

    GoProfiler.getInstance.startUpdateEyes()
    eyeCache_.updateEyes(board)
    GoProfiler.getInstance.stopUpdateEyes()

    val eyePotential = eyeCache_.getEyePotential
    val revisedNumEyes = eyeCache_.calcNumEyes()
    numEyes = Math.max(eyePotential, revisedNumEyes)

    val health = eyeEvaluator.determineHealth(side, numEyes, numLiberties, numStones)

    absoluteHealth_ = health
    if (Math.abs(absoluteHealth_) > 1.0) {
      GameContext.log(0, s"Warning: health exceeded 1.0: health=$health numEyes=$numEyes")
      absoluteHealth_ = side
    }

    absoluteHealth_
  }

  /**
   * @return set of eyes currently identified for this group.
   */
  def getEyes(board: GoBoard): GoEyeSet = {
    if (!eyeCache_.isValid) {
      calculateAbsoluteHealth(board)
    }
    eyeCache_.getEyes(board)
  }

  /**
   * Calculate the number of stones in the group.
   * @return number of stones in the group.
   */
  def getNumStones: Int = {
    if (eyeCache_.isValid) {
      return cachedNumStonesInGroup_
    }
    var numStones = 0
    for (str <- group_.getMembers.asScala) {
      numStones += str.size
    }
    cachedNumStonesInGroup_ = numStones
    numStones
  }
}
