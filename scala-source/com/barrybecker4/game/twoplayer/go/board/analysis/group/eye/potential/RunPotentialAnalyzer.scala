/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.go.board.analysis.group.eye.potential

import com.barrybecker4.common.geometry.{IntLocation, Location}
import com.barrybecker4.game.twoplayer.go.board.GoBoard
import com.barrybecker4.game.twoplayer.go.board.analysis.group.{GroupAnalyzer, GroupAnalyzerMap}
import com.barrybecker4.game.twoplayer.go.board.elements.position.GoBoardPosition
import com.barrybecker4.game.twoplayer.go.board.elements.string.IGoString

/**
 * Figure out the "eye potential" contribution from a horizontal or vertical run within a string.
 */
class RunPotentialAnalyzer(val groupString: IGoString, val board: GoBoard, val analyzerMap: GroupAnalyzerMap) {

  /**
   * Find the potential for one of the bbox's rows or columns.
   * @return eye potential for row and column at pos
   */
  def getRunPotential(position: Location, rowInc: Int, colInc: Int, maxRow: Int, maxCol: Int): Float = {
    var pos = new IntLocation(position)
    var runPotential = 0.0f
    val breadth = if (rowInc == 1) (maxRow - pos.row) else (maxCol - pos.col)
    val startSpace = board.getPosition(pos).asInstanceOf[GoBoardPosition]

    while (pos.col <= maxCol && pos.row <= maxRow) {
      var nextSpace = board.getPosition(pos).asInstanceOf[GoBoardPosition]
      val firstSpace = nextSpace
      var hasEnemy = false
      var runLength = 0
      val player1 = groupString.isOwnedByPlayer1
      
      while (inRun(pos, maxRow, maxCol, nextSpace, player1)) {
        if (containsEnemy(player1, nextSpace)) {
          hasEnemy = true
        }
        runLength += 1
        pos = pos.incrementOnCopy(rowInc, colInc)
        nextSpace = board.getPosition(pos).asInstanceOf[GoBoardPosition]
      }
      val bounded = isBounded(startSpace, nextSpace, firstSpace)
      runPotential += accrueRunPotential(rowInc, pos, breadth, firstSpace, hasEnemy, runLength, bounded)

      pos = pos.incrementOnCopy(rowInc, colInc)
    }

    runPotential
  }

  private def containsEnemy(ownedByPlayer1: Boolean, space: GoBoardPosition): Boolean = {
    val groupAnalyzer = analyzerMap.getAnalyzer(groupString.getGroup)
    space.isOccupied && space.getPiece.isOwnedByPlayer1 != ownedByPlayer1 && groupAnalyzer.isTrueEnemy(space)
  }

  private def inRun(pos: Location, maxRow: Int, maxCol: Int, space: GoBoardPosition, ownedByPlayer1: Boolean): Boolean = {
    (pos.col <= maxCol && pos.row <= maxRow
      && (space.isUnoccupied ||
      (space.isOccupied && space.getPiece.isOwnedByPlayer1 != ownedByPlayer1)))
  }

  private def isBounded(startSpace: GoBoardPosition, space: GoBoardPosition, firstSpace: GoBoardPosition): Boolean = {
    !(firstSpace.equals(startSpace)) && space != null && space.isOccupied
  }

  /**
   * Accumulate the potential for this run.
   * @return  accrued run potential.
   */
  private def accrueRunPotential(rowInc: Int, pos: Location, breadth: Int,
                                  firstSpace: GoBoardPosition, containsEnemy: Boolean,
                                  runLength: Int, bounded: Boolean): Float = {
    var runPotential = 0.0f

    if (!containsEnemy && runLength < breadth && runLength > 0) {
      val (firstPos, max, currentPos) =
        if (rowInc == 1) {
          (firstSpace.getRow, board.getNumRows, pos.row)
        } else {
          (firstSpace.getCol, board.getNumCols, pos.col)
        }

      runPotential = new Run(firstPos, currentPos, max, bounded).getPotential
    }

    runPotential
  }
}
