/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group.eye.potential

import com.barrybecker4.common.geometry.{Box, ByteLocation}
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap
import com.barrybecker4.game.twoplayer.go.board.elements.group.IGoGroup
import com.barrybecker4.game.twoplayer.go.board.elements.string.IGoString
import scala.jdk.CollectionConverters._

/**
 * Figure out how likely (the potential) that a group can form two eyes.
 *
 * @author Barry Becker
 */
class EyePotentialAnalyzer(val group: IGoGroup, val analyzerMap: GroupAnalyzerMap) {

  private var board: GoBoard = scala.compiletime.uninitialized
  private var boundingBox: Box = scala.compiletime.uninitialized

  def setBoard(board: GoBoard): Unit = {
    this.board = board
    boundingBox = group.findBoundingBox()
  }

  /**
   * Expand the bbox by one in all directions.
   * if the bbox is within one space of the edge, extend it all the way to the edge.
   * loop through the rows and columns calculating distances from group stones
   * to the edge and to other stones.
   * if there is a (mostly living) enemy stone in the run, don't count the run.
   * @return eye potential - a measure of how easily this group can make 2 eyes
   *    (0 - 2; 2 meaning it already has 2 guaranteed eyes or can easily get them).
   */
  def calculateEyePotential(): Float = {
    assert(board != null, "The board must be set before calculating potential")
    val numRows = board.getNumRows
    val numCols = board.getNumCols
    boundingBox = boundingBox.expandGloballyBy(1, numRows, numCols)
    boundingBox = boundingBox.expandBordersToEdge(1, numRows, numCols)

    findTotalEyePotential()
  }

  /**
   * Make sure that every internal enemy stone is really an enemy and not just dead.
   * compare it with one of the group strings.
   * @return eyePotential - a measure of how easily this group can make 2 eyes (0 - 2; 2 meaning has 2 eyes).
   */
  private def findTotalEyePotential(): Float = {
    if (group.getMembers.isEmpty) return 0
    val groupString = group.getMembers.iterator().next()

    val rMin = boundingBox.getMinRow
    val rMax = boundingBox.getMaxRow
    val cMin = boundingBox.getMinCol
    val cMax = boundingBox.getMaxCol

    var totalPotential = 0.0f
    totalPotential += getTotalRowPotentials(groupString, rMin, rMax, cMin, cMax)
    totalPotential += getTotalColumnPotentials(groupString, rMin, rMax, cMin, cMax)

    Math.min(1.9, Math.sqrt(totalPotential) / 1.3).toFloat
  }

  /**
   * @return  total of all the row run potentials.
   */
  private def getTotalRowPotentials(groupString: IGoString, rMin: Int, rMax: Int, cMin: Int, cMax: Int): Float = {
    var totalPotential = 0.0f
    val runAnalyzer = new RunPotentialAnalyzer(groupString, board, analyzerMap)

    for (r <- rMin to rMax) {
      totalPotential += runAnalyzer.getRunPotential(new ByteLocation(r, cMin), 0, 1, rMax, cMax)
    }
    totalPotential
  }

  /**
   * @return total of all the column run potentials.
   */
  private def getTotalColumnPotentials(groupString: IGoString, rMin: Int, rMax: Int, cMin: Int, cMax: Int): Float = {
    var totalPotential = 0.0f
    val runAnalyzer = new RunPotentialAnalyzer(groupString, board, analyzerMap)

    for (c <- cMin to cMax) {
      totalPotential += runAnalyzer.getRunPotential(new ByteLocation(rMin, c), 1, 0, rMax, cMax)
    }
    totalPotential
  }
}
