// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.model

import com.barrybecker4.common.format.FormatUtil

import java.awt.Image

/**
 * Right now this just contains the name of the config and the
 * search options, but we may add the game weights too at some point.
 *
 * @author Barry Becker
 */
class PerformanceResultsPair(
  private var p1FirstResults: PerformanceResults,
  private var p2FirstResults: PerformanceResults
) {

  def this() = {
    this(new PerformanceResults(), new PerformanceResults())
    normalize(new ResultMaxTotals(1.0, 1))
  }

  def getP1FirstResults: PerformanceResults = p1FirstResults

  def getP2FirstResults: PerformanceResults = p2FirstResults

  def getOutcomes: Array[Outcome] =
    Array(p1FirstResults.getOutcome, p2FirstResults.getOutcome)

  def getFinalImages: Array[Image] =
    Array(p1FirstResults.getFinalImage, p2FirstResults.getFinalImage)

  def getTotalNumSeconds: Double =
    p1FirstResults.getNumSeconds + p2FirstResults.getNumSeconds

  def getTotalNumMoves: Int =
    p1FirstResults.getNumMoves + p2FirstResults.getNumMoves

  def normalize(maxTotals: ResultMaxTotals): Unit = {
    p1FirstResults.normalize(maxTotals)
    p2FirstResults.normalize(maxTotals)
  }

  def getNormalizedTimes: Array[Double] =
    Array(p1FirstResults.getNormalizedNumSeconds, p2FirstResults.getNormalizedNumSeconds)

  def getTimesFormatted: Array[String] =
    Array(p1FirstResults.getTimeFormatted, p2FirstResults.getTimeFormatted)

  def getNormalizedNumMoves: Array[Double] =
    Array(p1FirstResults.getNormalizedNumMoves, p2FirstResults.getNormalizedNumMoves)

  def getNumMoves: Array[String] =
    Array(
      FormatUtil.formatNumber(p1FirstResults.getNumMoves),
      FormatUtil.formatNumber(p2FirstResults.getNumMoves))

  def saveTo(path: String): Unit = {
    p1FirstResults.saveTo(path)
    p2FirstResults.saveTo(path)
  }

  def getWinnerText: String = {
    val text1 = "Player1 " + p1FirstResults.getOutcomeVerb(true) + " when he played first.\n"
    val text2 = "Player2 " + p2FirstResults.getOutcomeVerb(false) + " when he played first."
    text1 + text2
  }

  def toHtmlString: String = {
    val bldr = new StringBuilder("<html>")
    bldr.append(" Player1 first: ").append(p1FirstResults.toHtmlString(true)).append("<br>")
    bldr.append("Player2 first: ").append(p2FirstResults.toHtmlString(true))
    bldr.append("</html>")
    bldr.toString
  }

  override def toString: String = {
    val bldr = new StringBuilder("Results pair")
    bldr.append(" Player1 first: ").append(p1FirstResults.toString).append("\n")
    bldr.append(" Player2 first: ").append(p2FirstResults.toString)
    bldr.toString
  }
}
