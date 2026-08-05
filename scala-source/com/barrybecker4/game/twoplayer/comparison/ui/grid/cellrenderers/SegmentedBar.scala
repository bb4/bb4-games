// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.grid.cellrenderers

import javax.swing.JPanel
import java.awt.{Color, GradientPaint, Graphics, Graphics2D}
import java.awt.geom.Point2D

/**
 * Base segmented bar. Shows info about the two sides that played each other
 * for one of the trials. One segment for each of the players that started first.
 *
 * @author Barry Becker
 */
abstract class SegmentedBar extends JPanel {

  override def paint(g: Graphics): Unit = {
    super.paint(g)
    val g2 = g.asInstanceOf[Graphics2D]
    drawBackGround(g2)
    drawBar(g2)
  }

  private def drawBackGround(g2: Graphics2D): Unit = {
    g2.setColor(SegmentedBar.BG_COLOR)
    g2.fillRect(0, 0, getWidth, getHeight)
  }

  def drawBar(g2: Graphics2D): Unit

  def drawDualBar(
    normValues: Array[Double],
    labels: Array[String],
    barColor: Color,
    g2: Graphics2D
  ): Unit = {
    var n1Width = 0
    var n2Width = 0
    if (normValues != null) {
      n1Width = (normValues(0) * getWidth).toInt
      n2Width = (normValues(1) * getWidth).toInt
    }
    val height = getHeight

    g2.setPaint(createGradient(0, n1Width, barColor))
    g2.fillRect(0, 0, n1Width, height)

    g2.setPaint(createGradient(n1Width, n2Width, barColor))
    g2.fillRect(n1Width, 0, n2Width, height)

    g2.setColor(SegmentedBar.BORDER_COLOR)
    g2.drawRect(0, 0, n1Width, height)
    g2.drawRect(n1Width, 0, n2Width, height)

    if (height > 18) {
      g2.drawString(labels(0), SegmentedBar.TEXT_INSET_X, SegmentedBar.TEXT_INSET_Y)
      g2.drawString(labels(1), n1Width + SegmentedBar.TEXT_INSET_X, height - SegmentedBar.TEXT_INSET_Y / 2 + 1)
    }
  }

  def createGradient(offset: Double, width: Double, color: Color): GradientPaint = {
    val origin = new Point2D.Double(offset, 0.0)
    val end = new Point2D.Double(offset + width, 0.0)
    val startColor = color.brighter()
    new GradientPaint(origin, startColor, end, color)
  }
}

object SegmentedBar {
  val BG_COLOR: Color = new Color(220, 220, 221)
  val BORDER_COLOR: Color = new Color(20, 0, 0, 100)
  private val TEXT_INSET_X = 5
  private val TEXT_INSET_Y = 10
}
