/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui.render

import com.barrybecker4.game.common.board.IBoard
import com.barrybecker4.game.common.player.{Player, PlayerList}
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer
import com.barrybecker4.game.multiplayer.common.ui.MultiGameBoardRenderer
import com.barrybecker4.game.multiplayer.set.{Card, SetBoard}

import java.awt.{Dimension, Graphics, Graphics2D}
import java.awt.geom.Point2D

class SetGameRenderer private () extends MultiGameBoardRenderer:

  private def getCanvasWidth(panelWidth: Int): Int =
    panelWidth - 2 * CardRenderer.LEFT_MARGIN

  private def getNumColumns(panelWidth: Int, panelHeight: Int, numCards: Int): Int =
    val rat = getCanvasWidth(panelWidth).toFloat / (panelHeight - 2 * CardRenderer.TOP_MARGIN)
    var numColumns =
      if rat < 0.05 then 1
      else if rat < 0.15 then 2
      else if rat < 0.3 then 3
      else if rat < 0.6 then 4
      else if rat < 0.9 then 5
      else if rat < 1.2 then 6
      else if rat < 2.0 then 7
      else if rat < 3.4 then 10
      else 20
    numColumns *= (1 + numCards / 41)
    numColumns

  private def calcCardDimension(numCols: Int, panelWidth: Int): Dimension =
    val cardWidth = getCanvasWidth(panelWidth) / numCols
    Dimension(cardWidth, (cardWidth * CardRenderer.CARD_HEIGHT_RAT).toInt)

  def findCardOver(board: IBoard[?], x: Int, y: Int, panelWidth: Int, panelHeight: Int): Card =
    val b = board.asInstanceOf[SetBoard]
    val numCards = b.getNumCardsShowing
    val numCols = getNumColumns(panelWidth, panelHeight, numCards)
    val cardDim = calcCardDimension(numCols, panelWidth)
    val cardWidth = cardDim.getWidth.toInt
    val cardHeight = cardDim.getHeight.toInt
    var selectedIndex = -1
    var i = 0
    while i < numCards do
      val row = i / numCols
      val col = i % numCols
      val colPos = col * cardWidth + CardRenderer.LEFT_MARGIN
      val rowPos = row * cardHeight + CardRenderer.TOP_MARGIN
      if x > colPos && x <= colPos + cardDim.getWidth &&
         y > rowPos && y <= rowPos + cardDim.getHeight then
        selectedIndex = i
        i = numCards
      else i += 1
    if selectedIndex == -1 then null else b.getDeck.get(selectedIndex)

  override def render(
      g: Graphics, currentPlayer: Player, players: PlayerList,
      board: IBoard[?], panelWidth: Int, panelHeight: Int
  ): Unit =
    val b = board.asInstanceOf[SetBoard]
    val numCards = b.getNumCardsShowing
    g.clearRect(0, 0, panelWidth, panelHeight)
    g.setColor(getBackground)
    g.fillRect(0, 0, panelWidth, panelHeight)
    val numCols = getNumColumns(panelWidth, panelHeight, numCards)
    val cardDim = calcCardDimension(numCols, panelWidth)
    val cardWidth = cardDim.getWidth.toInt
    val cardHeight = cardDim.getHeight.toInt
    var i = 0
    while i < numCards do
      val row = i / numCols
      val col = i % numCols
      val colPos = col * cardWidth + CardRenderer.LEFT_MARGIN
      val rowPos = row * cardHeight + CardRenderer.TOP_MARGIN
      CardRenderer.render(g.asInstanceOf[Graphics2D], b.getDeck.get(i),
        new Point2D.Float(colPos.toFloat, rowPos.toFloat), cardWidth, cardHeight, false)
      i += 1

object SetGameRenderer:
  private var renderer_ : GameBoardRenderer = null

  def getRenderer: GameBoardRenderer =
    if renderer_ == null then renderer_ = SetGameRenderer()
    renderer_
