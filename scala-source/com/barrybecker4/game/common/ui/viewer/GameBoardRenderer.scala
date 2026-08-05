/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.ui.viewer

import com.barrybecker4.common.geometry.{ByteLocation, Location}
import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.{Board, BoardPosition, IBoard}
import com.barrybecker4.game.common.player.{Player, PlayerList}
import com.barrybecker4.ui.themes.BarryTheme
import com.barrybecker4.ui.util.GUIUtil
import java.awt.{BasicStroke, Color, Dimension, Font, Graphics, Graphics2D, Stroke}
import java.awt.event.MouseEvent

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
abstract class GameBoardRenderer {

  /** the size of a game board cell where the pieces go */
  var cellSize: Int = scala.compiletime.uninitialized

  /** to move pieces you drag them (if the move is valid) */
  private var draggedPiece_ : BoardPosition = scala.compiletime.uninitialized

  /** this copy of the dragged piece is only for show   */
  private var draggedShowPiece_ : BoardPosition = scala.compiletime.uninitialized

  /**
   * singleton class for rendering the game pieces
   * we use a separate piece rendering class to avoid having ui in the piece class itself.
   * This allows us to more cleanly separate the client pieces from the server.
   * this must be initialized in the derived classes constructor.
   */
  var pieceRenderer_ : GamePieceRenderer = scala.compiletime.uninitialized

  var backgroundColor_ : Color = GameBoardRenderer.BACKGROUND_COLOR
  private var gridColor_ : Color = GameBoardRenderer.GRID_COLOR

  def getPieceRenderer: GamePieceRenderer = pieceRenderer_

  def setPieceRenderer(renderer: GamePieceRenderer): Unit =
    pieceRenderer_ = renderer

  def getSize(nrows: Int, ncols: Int): Dimension = {
    new Dimension(2 * getMargin + ncols * getCellSize,
      2 * getMargin + nrows * getCellSize)
  }

  def getPreferredSize(nrows: Int, ncols: Int): Dimension = {
    new Dimension(
      2 * getMargin + ncols * getPreferredCellSize,
      2 * getMargin + nrows * getPreferredCellSize)
  }

  /**
   * @param c the new color of the board.
   */
  def setBackground(c: Color): Unit = {
    backgroundColor_ = c
  }

  /**
   * @return the board color
   */
  def getBackground: Color = backgroundColor_

  /**
   * @param position The position containing the piece that is to be dragged
   */
  def setDraggedPiece(position: BoardPosition): Unit = {
    draggedPiece_ = position
    if (position != null) {
      draggedShowPiece_ = position.copy()
      draggedShowPiece_.getPiece.setTransparency(160.toShort)
    }
    else {
      draggedShowPiece_ = null
    }
  }

  def getDraggedPiece: BoardPosition = draggedPiece_

  /**
   * @param position position of fake overlay piece for show while dragging
   */
  def setDraggedShowPiece(position: BoardPosition): Unit = {
    draggedShowPiece_ = position
  }

  def getDraggedShowPiece: BoardPosition = draggedShowPiece_

  /**
   * Constructs a new Location given a MouseEvent
   *
   * @param e event containing the coordinates.
   * @return new location based on mouse position.
   */
  def createLocation(e: MouseEvent): Location = {
    val size = Math.max(1, getCellSize)
    val row = (e.getY - getMargin) / size + 1
    val col = (e.getX - getMargin) / size + 1
    new ByteLocation(row, col)
  }

  /**
   * @return  default cell size (override for specific games).
   */
  def getPreferredCellSize: Int = GameBoardRenderer.PREFERRED_CELL_SIZE

  /**
   * @return the space to the left and at the top of the board.
   */
  def getMargin: Int = GameBoardRenderer.BOARD_MARGIN

  /**
   * @return whether or not to draw the pieces on cell centers or vertices (like go or gomoku, but not like checkers).
   */
  def offsetGrid(): Boolean = false

  /**
   * @param c  the new color of the board's grid.
   */
  def setGridColor(c: Color): Unit = {
    gridColor_ = c
    GameContext.log(3, "setting gridColor to " + c)
  }

  /**
   * @return c  the new color of the board's grid.
   */
  def getGridColor: Color = gridColor_

  /**
   * @return  the size of a board position cell (must be square).
   */
  final def getCellSize: Int = cellSize

  /**
   * Clear the background so old pieces are erased.
   */
  def drawBackground(g: Graphics, b: Board[?], startPos: Int, rightEdgePos: Int, bottomEdgePos: Int,
                     panelWidth: Int, panelHeight: Int): Unit = {
    g.setColor(backgroundColor_)
    g.fillRect(0, 0, panelWidth, panelHeight)
  }

  /**
   * Compute the cell size based on the the dimensions of the viewer
   * The viewer window may be resized causing the cell size to change dynamically
   * @return the current cell size given the board and panel dimensions.
   */
  def calcCellSize(board: Board[?], panelWidth: Int, panelHeight: Int): Int = {
    val nrows = board.getNumRows
    val ncols = board.getNumCols

    val aspect = getBoardAspectRatio
    val panelAspect = panelWidth.toFloat / panelHeight.toFloat
    val boardAspect = aspect.toFloat * ncols / nrows

    val size = if (boardAspect < panelAspect)
      ((panelHeight - 2 * getMargin + 1) / nrows)
    else
      ((panelWidth - 2 * getMargin + 1) / (aspect * ncols)).toInt

    Math.max(size, GameBoardRenderer.MINIMUM_CELL_SIZE)
  }

  /**
   * Usually this is one, but some boards (like hex) have unusual shapes.
   * @return the ideal width / height ratio
   */
  def getBoardAspectRatio: Double = 1.0

  /**
   * Draw the gridlines over the background.
   */
  def drawGrid(g2: Graphics2D, startPos: Int, rightEdgePos: Int, bottomEdgePos: Int, start: Int,
               nrows1: Int, ncols1: Int, gridOffset: Int): Unit = {
    // draw the hatches which delineate the cells
    g2.setColor(getGridColor)

    for (i <- start to nrows1) {
      val ypos = getMargin + i * cellSize + gridOffset
      g2.drawLine(startPos, ypos, rightEdgePos, ypos)
    }
    for (i <- start to ncols1) {
      val xpos = getMargin + i * cellSize + gridOffset
      g2.drawLine(xpos, startPos, xpos, bottomEdgePos)
    }
  }

  /**
   * Draw some indication of where the last move was made.
   * The default is to show nothing.
   */
  def drawLastMoveMarker(g2: Graphics2D, currentPlayer: Player, board: Board[?]): Unit = {}

  /**
   * Draw the pieces and possibly other game markers for both players.
   */
  def drawMarkers(board: Board[?], players: PlayerList, g2: Graphics2D): Unit = {
    val nrows = board.getNumRows
    val ncols = board.getNumCols
    for (i <- 1 to nrows) {
      for (j <- 1 to ncols) {
        pieceRenderer_.render(g2, board.getPosition(i, j), cellSize, getMargin, board)
      }
    }
  }

  /**
   * This renders the current state of the Board to the screen.
   */
  def render(g: Graphics, currentPlayer: Player, players: PlayerList,
             board: IBoard[?], panelWidth: Int, panelHeight: Int): Unit = {

    val b = board.asInstanceOf[Board[?]]
    cellSize = calcCellSize(b, panelWidth, panelHeight)

    val g2 = g.asInstanceOf[Graphics2D]

    var gridOffset = 0
    var start = 0
    val nrows = b.getNumRows
    val ncols = b.getNumCols
    var nrows1 = nrows
    var ncols1 = ncols
    // if the grid is offset, it means the pieces will be shown at the vertices.
    if (offsetGrid()) {
      gridOffset = cellSize >> 1
      nrows1 = nrows - 1
      ncols1 = ncols - 1
    }

    val startPos = getMargin + gridOffset
    val rightEdgePos = getMargin + cellSize * ncols1 + gridOffset
    val bottomEdgePos = getMargin + cellSize * nrows1 + gridOffset

    drawBackground(g2, b, startPos, rightEdgePos, bottomEdgePos, panelWidth, panelHeight)

    drawGrid(g2, startPos, rightEdgePos, bottomEdgePos, start, nrows1, ncols1, gridOffset)

    g2.setFont(GameBoardRenderer.VIEWER_FONT)

    // now draw both player markers
    drawMarkers(b, players, g2)

    // if there is a piece being dragged, draw it
    if (draggedShowPiece_ != null) {
      draggedShowPiece_.getPiece.setTransparency(GameBoardRenderer.DRAG_TRANSPARENCY)
      pieceRenderer_.render(g2, draggedShowPiece_, cellSize, getMargin, b)
    }

    drawLastMoveMarker(g2, currentPlayer, b)
  }
}

object GameBoardRenderer {
  private val BACKGROUND_COLOR = BarryTheme.UI_COLOR_SECONDARY3
  private val GRID_COLOR = BarryTheme.UI_COLOR_SECONDARY1
  private val BOARD_MARGIN = 5
  private val PREFERRED_CELL_SIZE = 16

  private val VIEWER_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, 8)
  val LAST_MOVE_INDICATOR_COLOR = new Color(255, 120, 0)
  val LAST_MOVE_INDICATOR_STROKE: Stroke = new BasicStroke(3)

  /** don't allow the cells of the game board to get smaller than this */
  val MINIMUM_CELL_SIZE = 8
  private val DRAG_TRANSPARENCY: Short = 170
}
