// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.common.ui.dialogs

import com.barrybecker4.game.common.{GameContext, GameWeights}
import com.barrybecker4.game.common.player.Player
import com.barrybecker4.game.twoplayer.common.{TwoPlayerController, TwoPlayerPlayerOptions}
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.ui.dialogs.searchoptions.SearchOptionsPanel
import com.barrybecker4.optimization.parameter.ParameterArray
import com.barrybecker4.ui.components.GradientButton
import com.barrybecker4.ui.dialogs.OptionsDialog

import javax.swing.{JComponent, JPanel, JTabbedPane}
import java.awt.event.ActionEvent
import java.awt.{BorderLayout, Component, FlowLayout}

/**
 * Allow for editing player options.
 * There are two tabs: search options and game weights.
 *
 * @author Barry Becker
 */
class PlayerOptionsDialog(
  parent: Component,
  private val controller: TwoPlayerController[?, ?],
  showForPlayer1: Boolean
) extends OptionsDialog(parent):

  private var okButton_ : GradientButton = null

  private var searchOptionsPanel: SearchOptionsPanel = null
  private var weightsPanel: EditWeightsPanel = null

  private val player: Player = controller.getPlayers.get(if showForPlayer1 then 0 else 1)

  private val gameWeights: GameWeights = controller.getComputerWeights()
  private val weights: ParameterArray =
    if showForPlayer1 then gameWeights.getPlayer1Weights else gameWeights.getPlayer2Weights

  showContent()

  override def getTitle: String =
    GameContext.getLabel("EDIT_PLAYER_OPTIONS")

  override def createDialogContent: JComponent =
    val mainPanel = new JPanel()
    mainPanel.setLayout(new BorderLayout())

    // contains tabs for search, and weights
    val tabbedPanel = new JTabbedPane()

    val searchOptions: SearchOptions[?, ?] =
      player.getOptions.asInstanceOf[TwoPlayerPlayerOptions[?, ?]].getSearchOptions

    searchOptionsPanel = new SearchOptionsPanel(searchOptions)
    weightsPanel = new EditWeightsPanel(weights, gameWeights)

    tabbedPanel.add(GameContext.getLabel("SEARCH_OPTIONS"), searchOptionsPanel)
    tabbedPanel.add(GameContext.getLabel("EDIT_WEIGHTS"), weightsPanel)

    mainPanel.add(tabbedPanel, BorderLayout.CENTER)
    mainPanel.add(createButtonsPanel, BorderLayout.SOUTH)

    mainPanel

  override def createButtonsPanel: JPanel =
    val buttonsPanel = new JPanel(new FlowLayout())

    okButton_ = new GradientButton()
    initBottomButton(okButton_, GameContext.getLabel("OK"), GameContext.getLabel("ACCEPT_PLAYER_OPTIONS"))
    initBottomButton(cancelButton, GameContext.getLabel("CANCEL"), GameContext.getLabel("CANCEL_EDITS"))

    buttonsPanel.add(okButton_)
    buttonsPanel.add(cancelButton)

    buttonsPanel

  private def ok(): Unit =
    searchOptionsPanel.ok()
    weightsPanel.ok()

  override def actionPerformed(e: ActionEvent): Unit =
    super.actionPerformed(e)
    val source = e.getSource()
    if source == okButton_ then
      ok()
      dispose()
