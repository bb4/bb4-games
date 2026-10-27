/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui

import com.barrybecker4.game.multiplayer.set.Card
import com.barrybecker4.game.multiplayer.set.ui.render.CardRenderer
import com.barrybecker4.ui.themes.BarryTheme

import java.awt.{Dimension, Graphics, Graphics2D}
import java.awt.event.{MouseEvent, MouseMotionListener}
import java.awt.geom.Point2D
import java.util.{ArrayList, List as JList}
import javax.swing.JPanel
import scala.compiletime.uninitialized

class SolutionPanel(sets: JList[Card], viewer: SetGameViewer)
  extends JPanel with MouseMotionListener:

  assert(sets != null)
  private var currentlyHighlightedSet_ : JList[Card] = uninitialized
  addMouseMotionListener(this)

  private def getCanvasWidth: Int = getWidth - 2 * CardRenderer.LEFT_MARGIN
  private def getNumColumns: Int = 3

  private def calcCardDimension(numCols: Int): Dimension =
    val cardWidth = getCanvasWidth / numCols
    Dimension(cardWidth, (cardWidth * CardRenderer.CARD_HEIGHT_RAT).toInt)

  override def getPreferredSize: Dimension = Dimension(300, 800)

  private def getSet(i: Int): JList[Card] =
    val set = new ArrayList[Card](getNumColumns)
    val numCardsInSet = getNumColumns
    val index = i * numCardsInSet
    for j <- 0 until numCardsInSet do set.add(sets.get(index + j))
    set

  private def findSetOver(x: Int, y: Int): JList[Card] =
    val numCols = getNumColumns
    val cardDim = calcCardDimension(numCols)
    val cardHeight = cardDim.getHeight.toInt
    var selectedIndex = -1
    var row = 0
    while row < sets.size() / numCols do
      val rowPos = row * cardHeight + CardRenderer.TOP_MARGIN
      if y > rowPos && y <= rowPos + cardDim.getHeight then
        selectedIndex = row
        row = sets.size()
      else row += 1
    if selectedIndex == -1 then null else getSet(selectedIndex)

  override def mouseDragged(e: MouseEvent): Unit = {}

  override def mouseMoved(e: MouseEvent): Unit =
    val set = findSetOver(e.getX, e.getY)
    if currentlyHighlightedSet_ == null && set == null then return
    val changed =
      currentlyHighlightedSet_ == null || set == null || !SolutionPanel.sameSet(set, currentlyHighlightedSet_)
    if !changed then return
    if currentlyHighlightedSet_ != null then highlight(currentlyHighlightedSet_, false)
    if set != null then
      currentlyHighlightedSet_ = set
      highlight(currentlyHighlightedSet_, true)
    else
      currentlyHighlightedSet_ = null
    repaint()

  private def highlight(set: JList[Card], highlightOn: Boolean): Unit =
    if set == null then return
    val it = set.iterator()
    while it.hasNext do it.next().setHighlighted(highlightOn)
    viewer.refresh()

  def closed(): Unit = highlight(currentlyHighlightedSet_, false)

  override def paintComponent(g: Graphics): Unit =
    super.paintComponents(g)
    g.clearRect(0, 0, getWidth, getHeight)
    g.setColor(SolutionPanel.BACKGROUND_COLOR)
    g.fillRect(0, 0, getWidth, getHeight)
    val numCols = getNumColumns
    val cardDim = calcCardDimension(numCols)
    val cardWidth = cardDim.getWidth.toInt
    val cardHeight = cardDim.getHeight.toInt
    var i = 0
    while i < sets.size() do
      val row = i / numCols
      val col = i % numCols
      val colPos = col * cardWidth + CardRenderer.LEFT_MARGIN
      val rowPos = row * cardHeight + CardRenderer.TOP_MARGIN
      CardRenderer.render(g.asInstanceOf[Graphics2D], sets.get(i),
        new Point2D.Float(colPos.toFloat, rowPos.toFloat), cardWidth, cardHeight, false)
      i += 1

object SolutionPanel:
  private val BACKGROUND_COLOR = BarryTheme.UI_COLOR_SECONDARY3

  private def sameSet(s1: JList[Card], s2: JList[Card]): Boolean =
    (s1.get(0) eq s2.get(0)) && (s1.get(1) eq s2.get(1)) && (s1.get(2) eq s2.get(2))
