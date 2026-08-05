// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.configuration

import com.barrybecker4.game.common.GameContext
import com.barrybecker4.game.twoplayer.common.search.options.SearchOptions
import com.barrybecker4.game.twoplayer.common.ui.dialogs.searchoptions.SearchOptionsPanel
import com.barrybecker4.game.twoplayer.comparison.model.config.SearchOptionsConfig
import com.barrybecker4.ui.components.{GradientButton, TextInput}
import com.barrybecker4.ui.dialogs.OptionsDialog

import javax.swing._
import java.awt._
import java.awt.event.ActionEvent

/**
 * Allow for editing player search options.
 *
 * @author Barry Becker
 */
class SearchOptionsDialog(parent: Component, private val existingOptions: SearchOptionsConfig = null)
  extends OptionsDialog(parent) {

  private var okButton: GradientButton = null
  private var nameField: TextInput = null
  private var searchOptionsPanel: SearchOptionsPanel = null

  showContent()

  override def getTitle: String =
    GameContext.getLabel("EDIT_PLAYER_OPTIONS")

  def getSearchOptionsConfig: SearchOptionsConfig =
    new SearchOptionsConfig(nameField.getValue, searchOptionsPanel.getOptions)

  override def createDialogContent: JComponent = {
    val mainPanel = new JPanel()
    mainPanel.setLayout(new BorderLayout())

    val title = if (existingOptions != null) existingOptions.getName else ""
    val searchOptions =
      if (existingOptions != null) existingOptions.getSearchOptions else new SearchOptions()

    nameField = new TextInput("Configuration Name:", title, 30)
    nameField.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5))

    searchOptionsPanel = new SearchOptionsPanel(searchOptions)

    mainPanel.add(nameField, BorderLayout.NORTH)
    mainPanel.add(searchOptionsPanel, BorderLayout.CENTER)
    mainPanel.add(createButtonsPanel, BorderLayout.SOUTH)
    mainPanel
  }

  override def createButtonsPanel: JPanel = {
    val buttonsPanel = new JPanel(new FlowLayout())

    okButton = new GradientButton()
    initBottomButton(okButton, GameContext.getLabel("OK"), GameContext.getLabel("ACCEPT_PLAYER_OPTIONS"))
    initBottomButton(cancelButton, GameContext.getLabel("CANCEL"), GameContext.getLabel("CANCEL_EDITS"))

    buttonsPanel.add(okButton)
    buttonsPanel.add(cancelButton)

    buttonsPanel
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    super.actionPerformed(e)
    val source = e.getSource
    if (source eq okButton) {
      searchOptionsPanel.ok()
      dispose()
    }
  }
}
