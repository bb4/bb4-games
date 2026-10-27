// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.grid.cellrenderers

import com.barrybecker4.game.twoplayer.comparison.model.Outcome

import java.awt.{Color, Graphics2D, Image}

/**
 * The bar that shows which side won for each player who started fist.
 * Gray if tie.
 *
 * @author Barry Becker
 */
class WonBar extends SegmentedBar {

  private var outcomes: Array[Outcome] = null
  private var finalImages: Array[Image] = null

  def setOutcomes(outcomes: Array[Outcome], finalImages: Array[Image]): Unit = {
    this.outcomes = outcomes
    this.finalImages = finalImages
  }

  override def drawBar(g2: Graphics2D): Unit = {
    var side1Wincolor = SegmentedBar.BG_COLOR
    var side2Wincolor = SegmentedBar.BG_COLOR

    if (outcomes != null) {
      side1Wincolor = outcomes(0).getColor
      side2Wincolor = outcomes(1).getColor
    }
    val height = getHeight
    val width = getWidth / 2

    val imageSize = width - 2 * WonBar.IMAGE_INSET
    g2.setColor(side1Wincolor)
    g2.fillRect(0, 0, width, height)

    g2.setColor(side2Wincolor)
    g2.fillRect(width, 0, width, height)

    if (finalImages(0) != null)
      g2.drawImage(finalImages(0), WonBar.IMAGE_INSET, WonBar.IMAGE_INSET, imageSize, imageSize, null)
    if (finalImages(1) != null)
      g2.drawImage(finalImages(1), WonBar.IMAGE_INSET + width, WonBar.IMAGE_INSET, imageSize, imageSize, null)

    drawLabels(g2)
  }

  private def drawLabels(g2: Graphics2D): Unit = {
    g2.setColor(Color.BLACK)
    val width = getWidth / 2
    val lab1 = if (width > 70) "player1 first" else "p1 1st"
    val lab2 = if (width > 70) "player2 first" else "p2 1st"
    g2.drawString(lab1, WonBar.TEXT_INSET, WonBar.TEXT_INSET)
    g2.drawString(lab2, width + WonBar.TEXT_INSET, WonBar.TEXT_INSET)
  }
}

object WonBar {
  private val TEXT_INSET = 12
  private val IMAGE_INSET = 20
}
