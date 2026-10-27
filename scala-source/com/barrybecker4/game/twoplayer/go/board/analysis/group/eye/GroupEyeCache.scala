/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group.eye

import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap
import com.barrybecker4.game.twoplayer.go.board.analysis.group.eye.potential.EyePotentialAnalyzer
import com.barrybecker4.game.twoplayer.go.board.elements.eye.{GoEyeSet, IGoEye}
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import scala.jdk.CollectionConverters._

/**
 * Keep a cache of the eyes in a group because its expensive to recompute them.
 * @author Barry Becker
 */
class GroupEyeCache(group: IGoGroup, analyzerMap: GroupAnalyzerMap) {

  private var eyes_ = new GoEyeSet()
  private var eyePotential_ = 0.0f
  private val eyeAnalyzer_ = new EyeSpaceAnalyzer(group, analyzerMap)
  private val potentialAnalyzer_ = new EyePotentialAnalyzer(group, analyzerMap)
  private var isValid_ = false

  /**
   * @return  set of eyes currently identified for this group.
   */
  def getEyes(board: GoBoard): GoEyeSet = {
    updateEyes(board)
    eyes_
  }

  /**
   * Compute how many eyes (connected internal blank areas) this group has.
   * The eyes are either false eyes or true (or big or territorial) eyes.
   * Also update eyePotential (a measure of how good the groups ability to make 2 eyes.
   * This method is expensive. That is why the 2 things it computes (eyes and eyePotential) are cached
   * on a per group basis. After this method runs, the cache is valid until something about the group changes.
   * @param board the game board. If null, we just use what is in the cache.
   */
  def updateEyes(board: GoBoard): Unit = {
    if (isValid_ || board == null) {
      return
    }

    eyeAnalyzer_.setBoard(board)
    potentialAnalyzer_.setBoard(board)
    eyes_ = eyeAnalyzer_.determineEyes()
    eyePotential_ = potentialAnalyzer_.calculateEyePotential()
    isValid_ = true
  }

  def invalidate(): Unit = {
    clearEyes()
  }

  /**
   * @return true if the group has changed (structurally) in any way.
   */
  def isValid: Boolean = isValid_

  /**
   * used only for test.
   * @return eye potential value
   */
  def getEyePotential: Float = eyePotential_

  /**
   * Determine approximately how many eyes the group has.
   * This is purposely a little vague, but if more than 2.0, then must be unconditionally alive.
   * The value that we count for each type of eye could be optimized.
   * @return approximation to the number of eyes in group.
   */
  def calcNumEyes(): Float = {
    var numEyes = 0.0f
    for (eye <- eyes_.asScala) {
      numEyes += eye.getStatus.getScore
    }
    numEyes
  }

  /**
   * clear the current eyes for the group (in preparation for recomputing them).
   */
  private def clearEyes(): Unit = {
    if (eyes_.isEmpty) return

    for (eye <- eyes_.asScala) {
      eye.clear()
    }
    eyes_.clear()
    isValid_ = false
  }
}
