/* Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.ui

import com.barrybecker4.game.common.{GameContext, GameViewModel}
import com.barrybecker4.game.common.player.PlayerList
import com.barrybecker4.game.common.ui.dialogs.{GameStartListener, NewGameDialog}
import com.barrybecker4.game.common.ui.panel.GridBoardParamPanel
import com.barrybecker4.game.multiplayer.common.MultiGameOptions
import com.barrybecker4.ui.components.GradientButton

import javax.swing._
import javax.swing.event.{ListSelectionEvent, ListSelectionListener}
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}

/**
 * Manager players for new local multi-player game.
 *
 * @author Barry Becker
 */
abstract class MultiPlayerNewGameDialog(parent: Component, viewer: GameViewModel)
  extends NewGameDialog(parent, viewer)
  with ActionListener with ListSelectionListener with GameStartListener {

  // add / remove players
  private var addButton_ : GradientButton = null
  private var removeButton_ : GradientButton = null

  /** list of players in the local game. */
  private var playerTable_ : PlayerTable = null

  setResizable(true)

  /**
   * Lets you initialize all the players. Some subset of the players may be robots and not human.
   * @return a table of players
   */
  override def createPlayerAssignmentPanel(): JPanel = {
    val playerPanel = new JPanel(new BorderLayout())
    playerPanel.setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(),
        "Add Players for a new local game"))

    val headerPanel = new JPanel(new BorderLayout())
    val titleLabel = new JLabel(GameContext.getLabel("PLAYERS"))
    val buttonsPanel = new JPanel(new BorderLayout())

    addButton_ = new GradientButton(GameContext.getLabel("ADD"))
    addButton_.setToolTipText(GameContext.getLabel("ADD_TIP"))
    addButton_.addActionListener(this)
    removeButton_ = new GradientButton(GameContext.getLabel("REMOVE"))
    removeButton_.setToolTipText(GameContext.getLabel("REMOVE_PLAYER_TIP"))
    removeButton_.addActionListener(this)
    removeButton_.setEnabled(false)
    buttonsPanel.add(addButton_, BorderLayout.WEST)
    buttonsPanel.add(removeButton_, BorderLayout.EAST)

    headerPanel.add(titleLabel, BorderLayout.CENTER)
    headerPanel.add(buttonsPanel, BorderLayout.EAST)
    playerPanel.add(headerPanel, BorderLayout.NORTH)

    playerTable_ = createPlayerTable()
    playerTable_.addListSelectionListener(this)

    playerPanel.add(new JScrollPane(playerTable_.getTable), BorderLayout.CENTER)
    playerPanel.setPreferredSize(new Dimension(500, 300))
    playerPanel
  }

  /** sets the selected players and closes the dialog */
  override def startGame(players: PlayerList): Unit = {
    controller_.setPlayers(players)
    ok()
  }

  def createPlayerTable(): PlayerTable

  override def createBoardParamPanel(): GridBoardParamPanel =
    null

  override def ok(): Unit = {
    val selectedTab = tabbedPanel_.getSelectedComponent
    if (selectedTab eq playLocalPanel_) {
      controller_.setPlayers(playerTable_.getPlayers)
    }
    super.ok()
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    super.actionPerformed(e)
    val source = e.getSource

    if (source eq addButton_) {
      playerTable_.addRow()
    } else if (source eq removeButton_) {
      playerTable_.removeSelectedRows()
    }
    val options = controller_.getOptions.asInstanceOf[MultiGameOptions]
    addButton_.setEnabled(playerTable_.getModel.getRowCount < options.getMaxNumPlayers)
  }

  override def valueChanged(event: ListSelectionEvent): Unit = {
    val options = controller_.getOptions.asInstanceOf[MultiGameOptions]
    val enabled = playerTable_.getTable.getSelectedRowCount > 0 &&
      playerTable_.getModel.getRowCount > options.getMinNumPlayers
    removeButton_.setEnabled(enabled)
  }
}
