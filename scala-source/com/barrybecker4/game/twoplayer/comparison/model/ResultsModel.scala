// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.model

import com.barrybecker4.common.util.FileUtil

/**
 * @author Barry Becker
 */
class ResultsModel(val size: Int) {

  /** matrix of performance results based on the grid of options to compare. */
  private val resultsGrid = Array.ofDim[PerformanceResultsPair](size, size)

  def setResults(i: Int, j: Int, results: PerformanceResultsPair): Unit = {
    resultsGrid(i)(j) = results
  }

  def getResults(i: Int, j: Int): PerformanceResultsPair =
    resultsGrid(i)(j)

  def getSize: Int = size

  def normalize(): Unit = {
    val maxTotalTime = findMaxTotalTimeSeconds
    val maxTotalMoves = findMaxTotalMoves
    val maxTotals = new ResultMaxTotals(maxTotalTime, maxTotalMoves)
    updateNormalizedValues(maxTotals)
  }

  private def updateNormalizedValues(maxTotals: ResultMaxTotals): Unit = {
    var i = 0
    while (i < size) {
      var j = 0
      while (j < size) {
        resultsGrid(i)(j).normalize(maxTotals)
        j += 1
      }
      i += 1
    }
  }

  private def findMaxTotalTimeSeconds: Double = {
    var maxTime = 0.0
    var i = 0
    while (i < size) {
      var j = 0
      while (j < size) {
        val time = resultsGrid(i)(j).getTotalNumSeconds
        if (time > maxTime) maxTime = time
        j += 1
      }
      i += 1
    }
    maxTime
  }

  private def findMaxTotalMoves: Int = {
    var maxNumMoves = 0
    var i = 0
    while (i < size) {
      var j = 0
      while (j < size) {
        val numMoves = resultsGrid(i)(j).getTotalNumMoves
        if (numMoves > maxNumMoves) maxNumMoves = numMoves
        j += 1
      }
      i += 1
    }
    maxNumMoves
  }

  def saveModel(path: String): Unit = {
    try {
      var i = 0
      while (i < size) {
        var j = 0
        while (j < size) {
          val resultsPair = resultsGrid(i)(j)
          val directoryName =
            if (i > j) resultsPair.getP2FirstResults.getDescription
            else resultsPair.getP1FirstResults.getDescription
          resultsPair.saveTo(path + FileUtil.FILE_SEPARATOR + directoryName)
          j += 1
        }
        i += 1
      }
    } catch {
      case e: java.io.IOException =>
        throw new IllegalStateException("Could not save the model", e)
    }
  }

  override def toString: String = {
    val bldr = new StringBuilder
    var i = 0
    while (i < size) {
      var j = 0
      while (j < size) {
        bldr.append(resultsGrid(i)(j).toString)
        bldr.append("\n")
        j += 1
      }
      bldr.append("\n")
      i += 1
    }
    bldr.toString
  }
}
