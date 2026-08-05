/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.ui

import com.barrybecker4.game.common.board.Board
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer

import java.awt.{Color, Graphics}

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
abstract class MultiGameBoardRenderer extends GameBoardRenderer {

  private val DEFAULT_GRID_COLOR = Color.GRAY
  private val DEFAULT_TABLE_COLOR = new Color(200, 180, 170)

  setGridColor(DEFAULT_GRID_COLOR)

  override def getPreferredCellSize: Int = 8

  private def getDefaultTableColor: Color = DEFAULT_TABLE_COLOR

  /**
   * whether or not to draw the pieces on cell centers or vertices (like go or gomoku, but not like checkers).
   */
  override def offsetGrid(): Boolean = true

  /**
   * Draw the background and a depiction of a circular game table.
   * Used only by games with a table (like poker)
   */
  def drawTable(g: Graphics, board: Board[?]): Unit = {
    g.setColor(getBackground)
    val width = board.getNumCols * getCellSize
    val height = board.getNumRows * getCellSize
    g.setColor(getDefaultTableColor)
    g.fillOval((0.05 * width).toInt, (0.05 * height).toInt, (0.9 * width).toInt, (0.9 * height).toInt)
  }
}
