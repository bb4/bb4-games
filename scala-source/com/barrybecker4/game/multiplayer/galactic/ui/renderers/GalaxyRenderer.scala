/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.renderers

import com.barrybecker4.common.geometry.Location
import com.barrybecker4.game.common.board.Board
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer
import com.barrybecker4.game.multiplayer.common.ui.MultiGameBoardRenderer
import com.barrybecker4.game.multiplayer.galactic.Order
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer

import java.awt.Graphics2D
import java.awt.geom.Point2D

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 *
 * @author Barry Becker
 */
class GalaxyRenderer private () extends MultiGameBoardRenderer:

  pieceRenderer_ = PlanetRenderer.getRenderer

  override def getPreferredCellSize: Int = 16

  override def drawMarkers(board: Board[?], players: PlayerList, g2: Graphics2D): Unit =
    val pit = players.iterator()
    while pit.hasNext do
      val player = pit.next().asInstanceOf[GalacticPlayer]
      val orderIt = player.getOrders.iterator()
      while orderIt.hasNext do
        val order = orderIt.next()
        val margin = getMargin

        val begin = order.getOrigin.getLocation
        val end = order.getCurrentLocation

        g2.setColor(order.getOwner.getColor)
        val beginX = (margin + cellSize * (begin.col - 0.5)).toInt
        val beginY = (margin + cellSize * begin.row - 0.5).toInt
        val endX = (margin + cellSize * (end.getX - 0.5)).toInt
        val endY = (margin + cellSize * (end.getY - 0.5)).toInt

        g2.drawLine(beginX, beginY, endX, endY)

        val rad = Math.round(Math.sqrt(order.getFleetSize)).toInt
        g2.drawOval((endX - rad / 2.0).toInt, (endY - rad / 2.0).toInt, rad, rad)

    super.drawMarkers(board, players, g2)

object GalaxyRenderer:
  private var renderer_ : GameBoardRenderer = null

  def getRenderer: GameBoardRenderer =
    if renderer_ == null then renderer_ = GalaxyRenderer()
    renderer_
