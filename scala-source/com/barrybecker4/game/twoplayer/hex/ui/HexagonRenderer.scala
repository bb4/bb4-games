package com.barrybecker4.game.twoplayer.hex.ui

import java.awt.{Color, Graphics2D, Point, Polygon, Stroke}

object HexagonRenderer:

  def fillHexagon(g2: Graphics2D, point: Point, radius: Double, color: Color): Unit =
    drawHexagonAux(g2, point, radius, fill = true, color, null)

  def drawHexagon(g2: Graphics2D, point: Point, radius: Double, color: Color, stroke: Stroke): Unit =
    drawHexagonAux(g2, point, radius, fill = false, color, stroke)

  private def drawHexagonAux(g2: Graphics2D, point: Point, radius: Double,
                            fill: Boolean, color: Color, stroke: Stroke): Unit =
    val numPoints = 7
    val xpoints = new Array[Int](numPoints)
    val ypoints = new Array[Int](numPoints)

    for i <- 0 to 6 do
      val angStart = HexUtil.rad(30 + 60 * i)
      xpoints(i) = (point.getX + radius * Math.cos(angStart)).toInt
      ypoints(i) = (point.getY + radius * Math.sin(angStart)).toInt

    val poly = Polygon(xpoints, ypoints, numPoints)
    g2.setColor(color)

    if fill then
      g2.fillPolygon(poly)
    else
      g2.setStroke(stroke)
      g2.drawPolygon(poly)
