// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.blockade.ui

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoard
import com.barrybecker4.game.twoplayer.blockade.board.BlockadeBoardPosition

/**
 *  Decides where the wall should be placed given the mouse position.
 *
 *  @author Barry Becker
 */
class WallPlacer(board: BlockadeBoard) {

  /**
   * If we are in wallPlacingMode, then we show the wall being dragged around.
   * When the player clicks the wall is irrevocably placed.
   * @return array of 2 board positions.
   */
  def getCellLocations(x: Int, y: Int, loc: Location, cellSize: Int): Array[BlockadeBoardPosition] = {
    val index = getWallIndexForPosition(x, y, loc, cellSize)

    var pos1: BlockadeBoardPosition = null
    var pos2: BlockadeBoardPosition = null

    index match {
      case 0 =>
        pos1 = board.getPosition(loc)
        pos2 = board.getPosition(loc.row + 1, loc.col)
      case 1 =>
        assert(board.getPosition(loc) != null)
        assert(board.getPosition(loc.row - 1, loc.col) != null)
        pos1 = board.getPosition(loc)
        pos2 = board.getPosition(loc.row - 1, loc.col)
      case 2 =>
        pos1 = board.getPosition(loc.row - 1, loc.col)
        pos2 = board.getPosition(loc.row - 1, loc.col + 1)
      case 3 =>
        pos1 = board.getPosition(loc.row - 1, loc.col)
        pos2 = board.getPosition(loc.row - 1, loc.col - 1)
      case 4 =>
        pos1 = board.getPosition(loc.row, loc.col - 1)
        pos2 = board.getPosition(loc.row - 1, loc.col - 1)
      case 5 =>
        pos1 = board.getPosition(loc.row, loc.col - 1)
        pos2 = board.getPosition(loc.row + 1, loc.col - 1)
      case 6 =>
        pos1 = board.getPosition(loc)
        pos2 = board.getPosition(loc.row, loc.col - 1)
      case 7 =>
        pos1 = board.getPosition(loc)
        pos2 = board.getPosition(loc.row, loc.col + 1)
      case _ =>
        assert(false, "bad index=" + index)
    }

    Array(pos1, pos2)
  }

  /**
   * returns an index corresponding to a triangular cell segment according to:
   * <pre>
   * \  3 | 2 /
   * 4 \  | /  1
   * -------------
   * 5 /  | \  0
   * /  6 | 7 \
   * </pre>
   * Perhaps Make this an enum with values NEE, NNE, NNW, NWW, SWW, etc
   * @param xp x position
   * @param yp y position
   * @return wall index corresponding to specified position.
   */
  private def getWallIndexForPosition(xp: Int, yp: Int, loc: Location, cellSize: Int): Int = {
    val numRows = board.getNumRows
    val numCols = board.getNumCols

    var x = xp.toFloat / cellSize - (xp / cellSize)
    var y = yp.toFloat / cellSize - (yp / cellSize)

    if (loc.col >= numCols) {
      x = Math.min(0.499f, x)
    } else if (loc.col <= 1) {
      x = Math.max(0.501f, x)
    }

    if (loc.row >= numRows) {
      y = Math.min(0.499f, y)
    } else if (loc.row <= 1) {
      y = Math.max(0.501f, y)
    }

    if (x <= 0.5f) {
      if (y <= 0.5f) { // upper left
        if (y > x) 4 else 3
      } else { // y > .5  // lower left
        if ((y - 0.5f) > (0.5f - x)) 6 else 5
      }
    } else { // x>.5
      if (y <= 0.5f) { // upper right
        if (y < (1.0f - x)) 2 else 1
      } else { // y > .5  // lower right
        if (y < x) 0 else 7
      }
    }
  }
}
