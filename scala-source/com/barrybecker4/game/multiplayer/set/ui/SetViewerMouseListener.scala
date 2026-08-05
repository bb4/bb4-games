/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui

import com.barrybecker4.game.common.{GameContext, Move}
import com.barrybecker4.game.common.ui.viewer.ViewerMouseListener
import com.barrybecker4.game.multiplayer.set.{Card, SetBoard, SetController, SetPlayer}
import com.barrybecker4.game.multiplayer.set.ui.render.SetGameRenderer

import java.awt.event.MouseEvent
import java.util.List as JList
import javax.swing.JOptionPane
import scala.compiletime.uninitialized

class SetViewerMouseListener(viewer: SetGameViewer)
  extends ViewerMouseListener[Move, SetBoard](viewer):

  private var currentlyHighlightedCard_ : Card = uninitialized

  override def mousePressed(e: MouseEvent): Unit =
    val setViewer = viewer.asInstanceOf[SetGameViewer]
    if playerSelected() then
      val renderer = setViewer.getBoardRenderer.asInstanceOf[SetGameRenderer]
      val card = renderer.findCardOver(
        setViewer.getBoard, e.getX, e.getY, setViewer.getWidth, setViewer.getHeight)
      if card != null then
        card.toggleSelect()
        setViewer.repaint()
      notifyIfSetSelected()

  override def mouseMoved(e: MouseEvent): Unit =
    val setViewer = viewer.asInstanceOf[SetGameViewer]
    val renderer = setViewer.getBoardRenderer.asInstanceOf[SetGameRenderer]
    val card = renderer.findCardOver(
      setViewer.getBoard, e.getX, e.getY, setViewer.getWidth, setViewer.getHeight)
    val changed = !(card eq currentlyHighlightedCard_)

    if changed then
      if currentlyHighlightedCard_ != null then currentlyHighlightedCard_.setHighlighted(false)
      if card != null then
        currentlyHighlightedCard_ = card
        currentlyHighlightedCard_.setHighlighted(true)
      else
        currentlyHighlightedCard_ = null
      setViewer.repaint()

  private def playerSelected(): Boolean =
    if viewer.getController.getCurrentPlayer == null then
      JOptionPane.showMessageDialog(viewer,
        "Before you can select a set, you must specify a player on the right.")
      false
    else true

  private def notifyIfSetSelected(): Unit =
    val setViewer = viewer.asInstanceOf[SetGameViewer]
    val c = setViewer.getController.asInstanceOf[SetController]
    val selectedCards = setViewer.getSelectedCards
    if selectedCards.size() == 3 then
      val p = c.getCurrentPlayer.asInstanceOf[SetPlayer]
      if Card.isSet(selectedCards) then
        JOptionPane.showMessageDialog(viewer, GameContext.getLabel("FOUND_SET"))
        p.incrementNumSetsFound()
        c.removeCards(selectedCards)
        c.addCards(3)
      else
        JOptionPane.showMessageDialog(viewer, GameContext.getLabel("NOT_A_SET"))
        p.decrementNumSetsFound()
        c.gameChanged()
      deselectCards(selectedCards)
      c.setCurrentPlayer(null)
      viewer.repaint()

  private def deselectCards(cards: JList[Card]): Unit =
    val it = cards.iterator()
    while it.hasNext do it.next().setSelected(false)
