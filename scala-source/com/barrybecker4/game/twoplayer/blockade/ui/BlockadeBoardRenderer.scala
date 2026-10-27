/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.blockade.ui

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.board.Board
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer
import com.barrybecker4.game.twoplayer.blockade.board.{BlockadeBoard, BlockadeBoardPosition}
import com.barrybecker4.game.twoplayer.blockade.board.move.wall.BlockadeWall
import com.barrybecker4.game.twoplayer.common.ui.{TwoPlayerBoardRenderer, TwoPlayerPieceRenderer}

import java.awt.{Graphics, Graphics2D}
import scala.compiletime.uninitialized
import scala.jdk.CollectionConverters.*

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 *
 * @author Barry Becker
 */
class BlockadeBoardRenderer private () extends TwoPlayerBoardRenderer:
  pieceRenderer_ = BlockadePieceRenderer.getRenderer

  /** wall that gets dragged around until the player places it. */
  private var draggedWall_ : BlockadeWall = uninitialized

  def setDraggedWall(draggedWall: BlockadeWall): Unit =
    draggedWall_ = draggedWall

  def getDraggedWall: BlockadeWall = draggedWall_

  override def getPreferredCellSize: Int = 24

  override def drawBackground(
      g: Graphics, b: Board[?], startPos: Int, rightEdgePos: Int, bottomEdgePos: Int,
      panelWidth: Int, panelHeight: Int
  ): Unit =
    super.drawBackground(g, b, startPos, rightEdgePos, bottomEdgePos, panelWidth, panelHeight)
    val bb = b.asInstanceOf[BlockadeBoard]
    drawHomeBases(g, bb, player1 = true)
    drawHomeBases(g, bb, player1 = false)

  /** Draw the home bases for the specified player. */
  private def drawHomeBases(g: Graphics, board: BlockadeBoard, player1: Boolean): Unit =
    val homes = board.getPlayerHomes(player1)
    val cellSize = this.getCellSize
    val offset = Math.round(cellSize.toFloat / 4.0f)
    val renderer = pieceRenderer_.asInstanceOf[TwoPlayerPieceRenderer]
    g.setColor(if player1 then renderer.getPlayer1Color() else renderer.getPlayer2Color())
    for home <- homes do
      g.drawOval(
        getMargin + (home.getCol - 1) * cellSize + offset,
        getMargin + (home.getRow - 1) * cellSize + offset,
        2 * offset, 2 * offset)

  override def drawMarkers(board: Board[?], players: PlayerList, g2: Graphics2D): Unit =
    drawWalls(g2, board.asInstanceOf[BlockadeBoard])
    drawShortestPaths(g2, board.asInstanceOf[BlockadeBoard])

  private def drawWalls(g2: Graphics2D, board: BlockadeBoard): Unit =
    for i <- 1 to board.getNumRows do
      for j <- 1 to board.getNumCols do
        val pos = board.getPosition(i, j)
        BlockadePieceRenderer.renderWallAtPosition(g2, pos, cellSize, getMargin)
    if draggedWall_ != null then
      drawDraggedWall(g2, cellSize)

  private def drawShortestPaths(g2: Graphics2D, board: BlockadeBoard): Unit =
    var numPieces = 0
    for i <- 1 to board.getNumRows do
      for j <- 1 to board.getNumCols do
        val pos = board.getPosition(i, j)
        if pos.isOccupied then
          pieceRenderer_.render(g2, pos, cellSize, getMargin, board)
          if GameContext.getDebugMode > 0 then
            PathRenderer.drawShortestPaths(g2, pos, board, cellSize)
          numPieces += 1
    assert(numPieces >= 3, s"unexpected number of pieces: $numPieces")

  /**
   * Draw the dragged wall if there is one. The wall snaps to valid positions as the user drags it.
   */
  private def drawDraggedWall(g2: Graphics2D, cellSize: Int): Unit =
    val hsPositions = draggedWall_.getPositions
    for pos <- hsPositions.asScala do
      val vertical = draggedWall_.getIsVertical
      val cWall = if vertical then pos.getEastWall else pos.getSouthWall
      if vertical then pos.setEastWall(draggedWall_)
      else pos.setSouthWall(draggedWall_)
      BlockadePieceRenderer.renderWallAtPosition(g2, pos, cellSize, getMargin)
      if vertical then pos.setEastWall(cWall)
      else pos.setSouthWall(cWall)

object BlockadeBoardRenderer:
  private var renderer_ : GameBoardRenderer = uninitialized

  def getRenderer: GameBoardRenderer =
    if renderer_ == null then
      renderer_ = new BlockadeBoardRenderer()
    renderer_
