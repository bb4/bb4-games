/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.set.ui

import com.barrybecker4.game.common.{GameController, Move}
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.common.ui.panel.{
  GameChangedEvent, GameInfoPanel, GeneralInfoPanel, SectionPanel
}
import com.barrybecker4.game.multiplayer.set.{SetController, SetPlayer}

import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.event.{ListSelectionEvent, ListSelectionListener}
import scala.compiletime.uninitialized

class SetInfoPanel(controller: GameController[?, ?]) extends GameInfoPanel(controller)
  with ListSelectionListener:

  private var playerTable: SetSummaryTable = uninitialized
  private var playerPanel: JPanel = uninitialized

  override def createSubPanels(): Unit =
    generalInfoPanel_ = createGeneralInfoPanel(controller_.getCurrentPlayer)
    add(generalInfoPanel_)
    add(createCustomInfoPanel())

  override def getMinWidth: Int = 250

  override def createCustomInfoPanel(): JPanel =
    val pp = SectionPanel("Players")
    playerPanel = createPanel()
    playerPanel.setLayout(BorderLayout())
    playerPanel.setBorder(GameInfoPanel.createMarginBorder())
    insertPlayerTable()
    pp.add(playerPanel)
    pp

  override def createGeneralInfoPanel(player: Player): GeneralInfoPanel =
    SetGeneralInfoPanel(player)

  private def insertPlayerTable(): Unit =
    playerPanel.removeAll()
    playerTable = SetSummaryTable(controller_.getPlayers)
    playerTable.addListSelectionListener(this)
    playerPanel.add(playerTable.getTable, BorderLayout.CENTER)

  override def reset(): Unit =
    insertPlayerTable()
    invalidate()
    repaint()

  override def gameChanged(gce: GameChangedEvent[Move]): Unit =
    if controller_ == null then return
    generalInfoPanel_.update(controller_)
    val player = getSelectedPlayer
    if player != null then
      val r = playerTable.getTable.getSelectedRow
      playerTable.getTable.getModel.setValueAt("" + player.getNumSetsFound, r, 2)
      playerTable.getTable.clearSelection()

  private def getSelectedPlayer: SetPlayer =
    val c = controller_.asInstanceOf[SetController]
    val selectedPlayerIndex = playerTable.getTable.getSelectedRow
    if selectedPlayerIndex >= 0 then
      c.getPlayers.get(selectedPlayerIndex).asInstanceOf[SetPlayer]
    else null

  override def valueChanged(e: ListSelectionEvent): Unit =
    val c = controller_.asInstanceOf[SetController]
    c.setCurrentPlayer(getSelectedPlayer)
