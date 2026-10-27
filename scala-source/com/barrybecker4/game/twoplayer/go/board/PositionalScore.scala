/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board

import com.barrybecker4.common.format.FormatUtil

/**
 * For debugging purposes we want to keep more detail on what composes the overall score.
 * @author Barry Becker
 */
class PositionalScore private {

  private var deadStoneScore: Double = 0
  private var eyeSpaceScore: Double = 0
  private var badShapeScore: Double = 0
  private var posScore: Double = 0
  private var healthScore: Double = 0

  /** Loosely based on badShapeScore, posScore, and healthScore */
  private var positionScore_ : Double = 0

  /** whether or not it has been incremented yet. */
  private var incremented_ : Boolean = false

  def getPositionScore: Double = positionScore_

  def getDeadStoneScore: Double = deadStoneScore
  def getEyeSpaceScore: Double = eyeSpaceScore
  def getBadShapeScore: Double = badShapeScore
  def getPosScore: Double = posScore
  def getHealthScore: Double = healthScore

  def incrementBy(score: PositionalScore): Unit = {
    positionScore_ += score.getPositionScore
    deadStoneScore += score.deadStoneScore
    eyeSpaceScore += score.eyeSpaceScore
    badShapeScore += score.badShapeScore
    posScore += score.posScore
    healthScore += score.healthScore
    incremented_ = true
  }

  /**
   * Don't call this after incrementing, but you must call once before incrementing.
   */
  private def calcPositionScore(): Unit = {
    assert(!incremented_)
    positionScore_ = deadStoneScore + eyeSpaceScore + healthScore + posScore + badShapeScore
  }

  def getDescription(worth: Double, captureScore: Double, territoryDelta: Double, scaleFactor: Double): String = {
    val buf = new StringBuilder(s"<html>Breakdown for <b>value</b> = ${FormatUtil.formatNumber(worth)}")
    buf.append("<br>")
    buf.append(s"captureScore=${format(captureScore)}<br>")
    buf.append(s"territoryDelta=${format(territoryDelta)}<br>")
    buf.append(s"scaleFactor=${format(scaleFactor)}<br>")
    buf.append(toString(true))
    buf.append("</html>")
    buf.toString
  }

  override def toString: String = toString(false)

  def toString(htmlForm: Boolean): String = {
    val buf = new StringBuilder()
    val sep = if (htmlForm) "<br>" else "\t"
    buf.append(s"  Overall positionScore=${format(positionScore_)}$sep")
    buf.append(s"  deadStoneScore=${format(deadStoneScore)}$sep")
    buf.append(s"  eyeSpaceScore=${format(eyeSpaceScore)}$sep")
    buf.append(s"  badShapeScore=${format(badShapeScore)}$sep")
    buf.append(s"  posScore=${format(posScore)}$sep")
    buf.append(s"  healthScore=${format(healthScore)}")
    buf.append("\n")
    buf.toString
  }

  private def format(num: Double): String = FormatUtil.formatNumber(num)
}

object PositionalScore {
  def createEyePointScore(deadStoneScore: Double, eyeSpaceScore: Double): PositionalScore = {
    val score = new PositionalScore()
    score.deadStoneScore = deadStoneScore
    score.eyeSpaceScore = eyeSpaceScore
    score.calcPositionScore()
    score
  }

  def createOccupiedScore(badShapeScore: Double, posScore: Double, healthScore: Double): PositionalScore = {
    val score = new PositionalScore()
    score.badShapeScore = badShapeScore
    score.healthScore = healthScore
    score.posScore = posScore
    score.calcPositionScore()
    score
  }

  /** Create a score accumulator starting at 0 */
  def createZeroScore(): PositionalScore = new PositionalScore()
}
