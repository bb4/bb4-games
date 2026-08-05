/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui.dialog

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.multiplayer.galactic.{Galaxy, Order, Planet}
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer
import com.barrybecker4.ui.components.{GradientButton, NumberInput}
import com.barrybecker4.ui.dialogs.OptionsDialog

import java.awt.{BorderLayout, Component, Dimension}
import java.awt.event.{ActionEvent, ActionListener, ItemEvent, ItemListener}
import java.text.MessageFormat
import java.util.Map as JMap
import javax.swing._
import scala.compiletime.uninitialized

/**
 * Allow the user to specify a single order
 *
 * @author Barry Becker
 */
final class OrderDialog(
    player: GalacticPlayer,
    totalOutgoing: JMap[Planet, Integer],
    numYearsRemaining: Int
) extends OptionsDialog(null) with ActionListener with ItemListener:

  private var okButton: GradientButton = uninitialized
  private var originCombo: JComboBox[String] = uninitialized
  private var destinationCombo: JComboBox[String] = uninitialized
  private var availableShips: JLabel = uninitialized
  private var numShips: NumberInput = uninitialized

  showContent()

  override def createDialogContent: JComponent =
    val mainPanel = JPanel(true)
    setResizable(true)
    mainPanel.setLayout(BorderLayout())

    val buttonsPanel = createButtonsPanel

    val labelText = GameContext.getLabel("ORIGIN")
    originCombo = JComboBox[String]()
    originCombo.addItemListener(this)
    initPlanetSelect(originCombo, player)
    val originPanel = createComboInputPanel(labelText, originCombo)

    val destLabelText = GameContext.getLabel("DESTINATION")
    destinationCombo = JComboBox[String]()
    destinationCombo.addItemListener(this)
    val destPanel = createComboInputPanel(destLabelText, destinationCombo)
    initPlanetSelect(destinationCombo, null)
    availableShips = JLabel()

    showAvailableShips(getOrigin)

    val routePanel = JPanel(BorderLayout(), true)
    routePanel.setMinimumSize(Dimension(30, 60))
    routePanel.add(originPanel, BorderLayout.NORTH)
    routePanel.add(destPanel, BorderLayout.CENTER)
    routePanel.add(availableShips, BorderLayout.SOUTH)

    numShips = NumberInput(GameContext.getLabel("NUMBER_OF_SHIPS_TO_SEND"), OrderDialog.DEFAULT_FLEET_SIZE)

    mainPanel.add(routePanel, BorderLayout.NORTH)
    mainPanel.add(numShips, BorderLayout.CENTER)
    mainPanel.add(buttonsPanel, BorderLayout.SOUTH)

    mainPanel

  private def initPlanetSelect(combo: JComboBox[String], player: GalacticPlayer): Unit =
    val planets = Galaxy.getPlanets(player)
    val sPlanets = new Array[String](planets.size())
    for i <- 0 until planets.size() do
      sPlanets(i) = planets.get(i).getName.toString
    val comboModel = new DefaultComboBoxModel[String](sPlanets)
    combo.setOpaque(true)
    combo.setModel(comboModel)

  override def createButtonsPanel: JPanel =
    val buttonsPanel = JPanel(true)

    okButton = GradientButton()
    initBottomButton(okButton, GameContext.getLabel("OK"), GameContext.getLabel("PLACE_ORDER_TIP"))
    initBottomButton(cancelButton, GameContext.getLabel("CANCEL"), GameContext.getLabel("CANCEL"))

    buttonsPanel.add(okButton)
    buttonsPanel.add(cancelButton)

    buttonsPanel

  override def getTitle: String = GameContext.getLabel("MAKE_ORDER")

  private def showAvailableShips(planet: Planet): Unit =
    assert(planet != null)
    val availShips = planet.getNumShips - getOutgoingShips(planet)
    val arg = Array[AnyRef](planet.getName.toString, availShips.toString)
    val text = MessageFormat.format(GameContext.getLabel("AVAILABLE_SHIPS"), arg*)
    availableShips.setText(text)

  private def getOutgoingShips(planet: Planet): Int =
    val outgoingObj = totalOutgoing.get(planet)
    if outgoingObj != null then outgoingObj.intValue() else 0

  override def actionPerformed(e: ActionEvent): Unit =
    val source = e.getSource
    if source eq okButton then
      val order = getOrder
      if order == null then return
      val timeNeeded = order.getTimeNeeded
      if timeNeeded > numYearsRemaining then
        JOptionPane.showMessageDialog(
          this,
          "There are not enough years left (" + numYearsRemaining + ") in the game to reach that planet",
          "Information",
          JOptionPane.INFORMATION_MESSAGE)
      else
        setVisible(false)
    else if source eq cancelButton then
      cancel()
    else
      GameContext.log(0, "actionPerformed source=" + source + ". not cancel and not ok")

  override def itemStateChanged(e: ItemEvent): Unit =
    val source = e.getSource
    if source eq originCombo then
      showAvailableShips(getOrigin)
    repaint()

  def getOrder: Order =
    val origin = getOrigin
    val destination = getDestination
    val fleetSize = getFleetSize
    if fleetSize > (origin.getNumShips - getOutgoingShips(origin)) then
      JOptionPane.showMessageDialog(this, GameContext.getLabel("CANT_SEND_MORE_THAN_YOU_HAVE"))
      null
    else
      Order(origin, destination, fleetSize)

  private def getOrigin: Planet =
    Galaxy.getPlanet(originCombo.getSelectedItem.toString.charAt(0))

  private def getDestination: Planet =
    Galaxy.getPlanet(destinationCombo.getSelectedItem.toString.charAt(0))

  private def getFleetSize: Int = numShips.getIntValue

  private def createComboInputPanel(labelText: String, combo: JComboBox[String]): JPanel =
    val comboPanel = JPanel(true)
    comboPanel.setLayout(new BoxLayout(comboPanel, BoxLayout.X_AXIS))
    comboPanel.setAlignmentX(Component.LEFT_ALIGNMENT)
    val label = JLabel(labelText)
    comboPanel.add(label)
    comboPanel.add(JPanel(true))
    comboPanel.add(combo)
    comboPanel

object OrderDialog:
  private val DEFAULT_FLEET_SIZE = 10
