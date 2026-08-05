/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui.render

import com.barrybecker4.game.multiplayer.set.Card
import com.barrybecker4.game.multiplayer.set.ui.render.SymbolColors.ColorType

import java.awt._
import java.awt.geom.{Ellipse2D, GeneralPath, Point2D, Rectangle2D}

object CardRenderer:
  val LEFT_MARGIN = 5
  val TOP_MARGIN = 5
  val CARD_HEIGHT_RAT = 1.5f

  private val BACKGROUND_COLOR = new Color(250, 250, 255)
  private val BORDER_COLOR = new Color(45, 45, 55)
  private val HIGHLIGHTED_BORDER_COLOR = new Color(205, 205, 0)
  private val SELECTED_BORDER_COLOR = new Color(255, 255, 0)
  private val MARGIN_RAT = 0.02f
  private val SHAPE_BORDER_STROKE = new BasicStroke(4.0f)
  private val SHAPE_SIZE_FRAC = 0.82f
  private val THIRD_SHAPE_FRAC = 0.9f
  private val SHAPE_WIDTH_FRAC = 0.7f
  private val SHAPE_HEIGHT_FRAC = 0.25f
  private val ARC_RATIO = 0.12f
  private val symbolColors = new SymbolColors()

  private def getCardTexture(card: Card): Paint =
    card.texture() match
      case Card.AttributeValue.FIRST => BACKGROUND_COLOR
      case Card.AttributeValue.SECOND => symbolColors.getCardColor(card)
      case Card.AttributeValue.THIRD =>
        new GradientPaint(75, 75, BACKGROUND_COLOR, 80, 75,
          symbolColors.getColorForValue(card.color(), ColorType.HATCHED), true)

  private def getShape(card: Card, width: Int, height: Int): Shape =
    val topMargin = ((1.0 - SHAPE_SIZE_FRAC) * height).toInt
    val leftMargin = ((1.0 - SHAPE_SIZE_FRAC) * width).toInt
    val w = width * SHAPE_SIZE_FRAC
    val h = height * SHAPE_SIZE_FRAC
    card.shape() match
      case Card.AttributeValue.FIRST =>
        new Ellipse2D.Float(leftMargin.toFloat, topMargin.toFloat, w, h)
      case Card.AttributeValue.SECOND =>
        new Rectangle2D.Float(leftMargin.toFloat, topMargin.toFloat, w, h)
      case Card.AttributeValue.THIRD =>
        val hh = THIRD_SHAPE_FRAC * height
        val ww = THIRD_SHAPE_FRAC * width
        val leftStart = ((1.0 - THIRD_SHAPE_FRAC) * width).toFloat
        val path = new GeneralPath()
        val hd2 = (hh.toInt >> 1).toFloat
        val wd2 = (ww.toInt >> 1).toFloat
        path.moveTo(leftStart, hd2)
        path.lineTo(leftStart + wd2, 0)
        path.lineTo(leftStart + ww.toFloat, hd2)
        path.lineTo(leftStart + wd2, hh.toFloat)
        path.lineTo(leftStart, hd2)
        path

  private def getNumber(card: Card): Int =
    card.number() match
      case Card.AttributeValue.FIRST => 1
      case Card.AttributeValue.SECOND => 2
      case Card.AttributeValue.THIRD => 3

  def render(g2: Graphics2D, card: Card, position: Point2D, width: Int, height: Int, highlight: Boolean): Unit =
    val x = position.getX.toInt
    val y = position.getY.toInt
    val cardArc = (ARC_RATIO * width).toInt
    val margin = (MARGIN_RAT * width).toInt

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY)
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)

    if card.isSelected then g2.setColor(SELECTED_BORDER_COLOR)
    else if card.isHighlighted then g2.setColor(HIGHLIGHTED_BORDER_COLOR)
    else g2.setColor(BORDER_COLOR)

    g2.fillRoundRect(x + margin, y + margin, width - margin, height - margin, cardArc, cardArc)
    g2.setColor(BACKGROUND_COLOR)
    g2.fillRoundRect(x + 2 * margin, y + 2 * margin, width - 3 * margin, height - 3 * margin, cardArc, cardArc)
    g2.setColor(symbolColors.getCardColor(card))

    val shape = getShape(card, (width * SHAPE_WIDTH_FRAC).toInt, (height * SHAPE_HEIGHT_FRAC).toInt)
    val num = getNumber(card)
    val startXoffset = (((0.97 - SHAPE_WIDTH_FRAC) / 2.0) * width).toInt
    val startYoffset =
      ((3.0 - num) * height * 0.1).toInt + (((0.99 - 3.0 * SHAPE_HEIGHT_FRAC) / 2.0) * height).toInt
    val offset = ((height - 2 * margin) / 3.8).toInt

    g2.setStroke(SHAPE_BORDER_STROKE)
    g2.translate(x + startXoffset, y)
    var i = 0
    while i < num do
      g2.translate(0, startYoffset + i * offset)
      g2.setPaint(getCardTexture(card))
      g2.fill(shape)
      g2.setPaint(symbolColors.getBorderCardColor(card))
      g2.draw(shape)
      g2.translate(0, -(startYoffset + i * offset))
      i += 1
    g2.translate(-(x + startXoffset), -y)
