// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.common.ui.dialogs

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.twoplayer.common.TwoPlayerController
import com.barrybecker4.ui.components.GradientButton
import com.barrybecker4.ui.dialogs.OptionsDialog

import javax.swing.{BorderFactory, BoxLayout, ButtonGroup, JLabel, JPanel, JRadioButton}
import java.awt.event.{ActionEvent, ActionListener}
import java.awt.{BorderLayout, Component, Dimension}

/**
 * Use this modal dialog to initialize the two players that
 * are needed in order to start play in a 2 player game.
 * They are either human players or computer players.
 *
 * @author Barry Becker
 */
class PlayerAssignmentPanel(
  private val controller: TwoPlayerController[?, ?],
  private val dialogParent: Component
) extends JPanel with ActionListener:

  // radio buttons for selecting 1st and 2nd players
  private var human1Button_ : JRadioButton = null
  private var computer1Button_ : JRadioButton = null
  private var human2Button_ : JRadioButton = null
  private var computer2Button_ : JRadioButton = null

  private var editOptions1Button_ : GradientButton = null
  private var editOptions2Button_ : GradientButton = null

  initialize()

  protected def initialize(): Unit =
    setLayout(new BorderLayout())

    val panel = new JPanel()
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))
    val human = GameContext.getLabel("HUMAN")
    val computer = GameContext.getLabel("COMPUTER")
    val editWeights = GameContext.getLabel("EDIT_PLAYER_OPTIONS")
    panel.setBorder(
      BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
        GameContext.getLabel("PLAYER_ASSIGNMENT")))

    val p1 = controller.getPlayers.getPlayer1
    val p2 = controller.getPlayers.getPlayer2

    human1Button_ = new JRadioButton(human, p1.isHuman)
    computer1Button_ = new JRadioButton(computer, !p2.isHuman)
    editOptions1Button_ = new GradientButton(editWeights)
    editOptions1Button_.setEnabled(!p1.isHuman)
    val firstP =
      createPlayerEntry(getPlayer1Label(), human1Button_, computer1Button_, editOptions1Button_)

    human2Button_ = new JRadioButton(human, p2.isHuman)
    computer2Button_ = new JRadioButton(computer, !p2.isHuman)
    editOptions2Button_ = new GradientButton(editWeights)
    editOptions2Button_.setEnabled(!p2.isHuman)
    val secondP =
      createPlayerEntry(getPlayer2Label(), human2Button_, computer2Button_, editOptions2Button_)

    firstP.setAlignmentX(Component.LEFT_ALIGNMENT)
    secondP.setAlignmentX(Component.LEFT_ALIGNMENT)
    panel.add(firstP)
    panel.add(secondP)

    add(panel, BorderLayout.CENTER)
    add(new JPanel(), BorderLayout.EAST)

  protected def getPlayer1Label(): String =
    GameContext.getLabel("FIRST_PLAYER") + OptionsDialog.COLON

  protected def getPlayer2Label(): String =
    GameContext.getLabel("SECOND_PLAYER") + OptionsDialog.COLON

  private def createPlayerEntry(message: String,
                                 humanButton: JRadioButton,
                                 computerButton: JRadioButton,
                                 editWtsButton: GradientButton): JPanel =
    val p = new JPanel()
    p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS))

    val label = new JLabel(message)
    label.setMinimumSize(new Dimension(140, 18))
    p.add(label)
    humanButton.addActionListener(this)
    computerButton.addActionListener(this)
    editWtsButton.addActionListener(this)

    val group = new ButtonGroup()
    group.add(humanButton)
    group.add(computerButton)
    computerButton.addActionListener(this)
    humanButton.addActionListener(this)

    humanButton.setAlignmentX(Component.LEFT_ALIGNMENT)
    computerButton.setAlignmentX(Component.LEFT_ALIGNMENT)
    editWtsButton.setAlignmentX(Component.RIGHT_ALIGNMENT)

    p.add(humanButton)
    p.add(computerButton)
    p.add(new JPanel()) // filler
    p.add(editWtsButton)
    p

  def ok(): Unit =
    val players = controller.getPlayers
    players.getPlayer1.setHuman(human1Button_.isSelected())
    players.getPlayer2.setHuman(human2Button_.isSelected())

  override def actionPerformed(e: ActionEvent): Unit =
    val source = e.getSource()

    if source == computer1Button_ then
      editOptions1Button_.setEnabled(true)
    else if source == computer2Button_ then
      editOptions2Button_.setEnabled(true)
    else if source == human1Button_ then
      editOptions1Button_.setEnabled(false)
    else if source == human2Button_ then
      editOptions2Button_.setEnabled(false)
    else if source == editOptions1Button_ then
      showEditWeightsDialog(true)
    else if source == editOptions2Button_ then
      showEditWeightsDialog(false)

  /**
   *  @return return true if canceled
   */
  private def showEditWeightsDialog(showForPlayer1: Boolean): Boolean =
    val editWtsDlg = new PlayerOptionsDialog(dialogParent, controller, showForPlayer1)

    editWtsDlg.setLocationRelativeTo(dialogParent)
    editWtsDlg.setModal(true)
    editWtsDlg.setVisible(true)
    false

  def setPlayersSelected(enable: Boolean): Unit =
    editOptions1Button_.setEnabled(enable)
    editOptions2Button_.setEnabled(enable)
    human1Button_.setEnabled(enable)
    computer1Button_.setEnabled(enable)
    human2Button_.setEnabled(enable)
    computer2Button_.setEnabled(enable)

  def setBothComputerPlayers(bothComputers: Boolean): Unit =
    computer1Button_.setSelected(bothComputers)
    computer2Button_.setSelected(bothComputers)
