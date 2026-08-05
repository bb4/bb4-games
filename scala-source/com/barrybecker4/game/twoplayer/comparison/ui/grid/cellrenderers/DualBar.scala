// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.grid.cellrenderers

import java.awt.{Color, Graphics2D}

/**
 * A two part bar showing a result for when each side starts the game.
 *
 * @author Barry Becker
 */
class DualBar(barColor: Color) extends SegmentedBar {

  private var color = barColor
  private var segments: Array[Double] = null
  private var labels: Array[String] = null

  def setBarSegments(barSegments: Array[Double], barLabels: Array[String]): Unit = {
    segments = barSegments
    labels = barLabels
  }

  override def drawBar(g2: Graphics2D): Unit = {
    drawDualBar(segments, labels, color, g2)
  }
}
