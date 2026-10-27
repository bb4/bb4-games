/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.dialog

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.ui.panel.GameInfoPanel
import com.barrybecker4.game.multiplayer.galactic.Order
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer
import com.barrybecker4.ui.components.GradientButton
import com.barrybecker4.ui.dialogs.OptionsDialog

import java.awt.{BorderLayout, Component, Dimension}
import java.awt.event.ActionEvent
import java.text.MessageFormat
import java.util.List as JList
import javax.swing._
import scala.compiletime.uninitialized

/**
 * Allow the user to maintain their current orders and add new ones.
 *
 * @author Barry Becker
 */
final class OrdersDialog(parent: Component, player: GalacticPlayer, numYearsRemaining: Int)
  extends OptionsDialog(parent):

  private var addOrderButton: GradientButton = uninitialized
  private var removeOrderButton: GradientButton = uninitialized
  private val ordersTable: OrdersTable = OrdersTable(player.getOrders)
  private var okButton: GradientButton = uninitialized
  private val numPreExistingOrders: Int = ordersTable.getNumRows

  showContent()

  override def createDialogContent: JComponent =
    setResizable(true)

    val buttonsPanel = createButtonsPanel

    val titlePanel = JPanel(BorderLayout(), true)
    titlePanel.setBorder(GameInfoPanel.createMarginBorder())

    val title = MessageFormat.format(GameContext.getLabel("CURRENT_ORDERS"), player.getName)
    val titleLabel = JLabel(title)
    titleLabel.setForeground(player.getColor.darker())
    titleLabel.setIcon(player.getIcon)
    titlePanel.add(titleLabel, BorderLayout.WEST)

    val addremoveButtonsPanel = JPanel(true)

    addOrderButton = GradientButton(GameContext.getLabel("NEW_ORDER"))
    addOrderButton.addActionListener(this)
    addremoveButtonsPanel.add(addOrderButton, BorderLayout.CENTER)

    removeOrderButton = GradientButton(GameContext.getLabel("REMOVE_ORDER"))
    removeOrderButton.addActionListener(this)
    addremoveButtonsPanel.add(removeOrderButton, BorderLayout.EAST)

    titlePanel.add(addremoveButtonsPanel, BorderLayout.EAST)

    val mainPanel = JPanel(BorderLayout(), true)
    mainPanel.add(titlePanel, BorderLayout.NORTH)

    val scrollPane = JScrollPane(ordersTable.getTable)
    scrollPane.setPreferredSize(Dimension(360, 120))
    scrollPane.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(5, 5, 5, 5),
        scrollPane.getBorder))
    mainPanel.add(scrollPane, BorderLayout.CENTER)
    mainPanel.add(buttonsPanel, BorderLayout.SOUTH)

    mainPanel

  override def getTitle: String = GameContext.getLabel("CURRENT_ORDERS_TITLE")

  override def createButtonsPanel: JPanel =
    val buttonsPanel = JPanel(true)

    okButton = GradientButton()
    initBottomButton(okButton, GameContext.getLabel("OK"), GameContext.getLabel("PLACE_ORDER_TIP"))
    initBottomButton(cancelButton, GameContext.getLabel("CANCEL"), GameContext.getLabel("CANCEL"))

    buttonsPanel.add(okButton)
    buttonsPanel.add(cancelButton)

    buttonsPanel

  def getOrders: JList[Order] = ordersTable.getOrders

  override def actionPerformed(e: ActionEvent): Unit =
    val source = e.getSource

    if source eq okButton then
      setVisible(false)
    else if source eq cancelButton then
      cancel()
    else if source eq addOrderButton then
      addOrder()
    else if source eq removeOrderButton then
      if ordersTable.getNumRows > numPreExistingOrders then
        ordersTable.removeRow(ordersTable.getNumRows - 1)
      else
        JOptionPane.showMessageDialog(
          this, "Can't remove pre-existing orders", "Information", JOptionPane.INFORMATION_MESSAGE)

  private def addOrder(): Unit =
    val orderDialog = OrderDialog(player, ordersTable.getCurrentOutGoingShips, numYearsRemaining)
    orderDialog.setLocation(
      (getLocation.getX + 40).toInt,
      (getLocation.getY + 170).toInt)

    val canceled = orderDialog.showDialog
    if !canceled then
      val order = orderDialog.getOrder
      if order != null then
        ordersTable.addRow(order)
