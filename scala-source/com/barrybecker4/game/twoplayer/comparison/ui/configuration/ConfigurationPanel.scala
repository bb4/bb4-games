// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.twoplayer.comparison.ui.configuration

import com.barrybecker4.game.twoplayer.comparison.model.config.{SearchOptionsConfig, SearchOptionsConfigList}
import com.barrybecker4.game.twoplayer.comparison.model.config.data.ConfigurationListEnum
import com.barrybecker4.ui.components.GradientButton

import javax.swing._
import javax.swing.event.{ListSelectionEvent, ListSelectionListener}
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}

/**
 * Allow the user to maintain their current orders and add new ones.
 *
 * @author Barry Becker
 */
final class ConfigurationPanel extends JPanel with ActionListener with ListSelectionListener {

  private var addConfigButton_ : GradientButton = null
  private var editConfigButton_ : GradientButton = null
  private var removeConfigButton_ : GradientButton = null

  private var configDropList: JComboBox[ConfigurationListEnum] = null

  private var configTable_ : ConfigurationsTable = null
  private var scrollPane_ : JScrollPane = null

  init()

  private def init(): Unit = {
    setLayout(new BorderLayout())

    val addremoveButtonsPanel = createButtonsPanel()

    val titlePanel = new JPanel(new BorderLayout())
    titlePanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3))

    val dropListElement = createDropListElement()

    titlePanel.add(dropListElement, BorderLayout.WEST)
    titlePanel.add(addremoveButtonsPanel, BorderLayout.EAST)

    add(titlePanel, BorderLayout.NORTH)

    scrollPane_ = new JScrollPane()

    initializeConfigTable()

    scrollPane_.setPreferredSize(new Dimension(360, 120))
    scrollPane_.setBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(5, 5, 5, 5),
        scrollPane_.getBorder))
    add(scrollPane_, BorderLayout.CENTER)
  }

  private def createDropListElement(): JPanel = {
    val panel = new JPanel(new BorderLayout())
    val instr = new JLabel(" Select a set of configurations to test:  ")
    configDropList = createConfigDropDown()
    panel.add(instr, BorderLayout.CENTER)
    panel.add(configDropList, BorderLayout.EAST)
    panel
  }

  private def createConfigDropDown(): JComboBox[ConfigurationListEnum] = {
    val model = new DefaultComboBoxModel[ConfigurationListEnum](ConfigurationListEnum.values)
    model.setSelectedItem(ConfigurationListEnum.DEFAULT_CONFIGS)
    val dropList = new JComboBox[ConfigurationListEnum](model)
    dropList.addActionListener(this)
    dropList
  }

  private def createButtonsPanel(): JPanel = {
    val panel = new JPanel()

    addConfigButton_ = new GradientButton("New")
    addConfigButton_.setToolTipText("add a new entry in the table")
    addConfigButton_.addActionListener(this)
    panel.add(addConfigButton_, BorderLayout.WEST)

    editConfigButton_ = new GradientButton("Edit")
    editConfigButton_.setToolTipText("edit an existing entry in the table")
    editConfigButton_.addActionListener(this)
    editConfigButton_.setEnabled(false)
    panel.add(editConfigButton_, BorderLayout.CENTER)

    removeConfigButton_ = new GradientButton("Remove")
    removeConfigButton_.setToolTipText("remove an entry from the table")
    removeConfigButton_.addActionListener(this)
    removeConfigButton_.setEnabled(false)
    panel.add(removeConfigButton_, BorderLayout.EAST)

    panel
  }

  def getConfigurations: SearchOptionsConfigList =
    configTable_.getSearchOptions

  override def actionPerformed(e: ActionEvent): Unit = {
    val source = e.getSource

    if (source eq addConfigButton_) {
      addConfiguration()
    } else if (source eq editConfigButton_) {
      editConfiguration(configTable_.getSelectedRow)
    } else if (source eq removeConfigButton_) {
      configTable_.removeRow(configTable_.getSelectedRow)
      removeConfigButton_.setEnabled(false)
    } else if (source eq configDropList) {
      initializeConfigTable()
    } else {
      assert(false, "Unexpected source =" + source)
    }
  }

  private def initializeConfigTable(): Unit = {
    val configList = configDropList.getSelectedItem.asInstanceOf[ConfigurationListEnum].getConfigList
    configTable_ = new ConfigurationsTable(configList)
    configTable_.addListSelectionListener(this)
    scrollPane_.setViewportView(configTable_.getTable)
  }

  private def addConfiguration(): Unit = {
    val optionsDialog = new SearchOptionsDialog(this)
    val canceled = optionsDialog.showDialog

    if (!canceled) {
      val options = optionsDialog.getSearchOptionsConfig
      if (options != null)
        configTable_.addConfiguration(options)
    }
  }

  private def editConfiguration(row: Int): Unit = {
    val initOptions = getConfigurations.get(row)
    val optionsDialog = new SearchOptionsDialog(this, initOptions)
    val canceled = optionsDialog.showDialog

    if (!canceled) {
      val options = optionsDialog.getSearchOptionsConfig
      if (options != null) {
        configTable_.updateRow(row, options)
      }
    }
  }

  override def valueChanged(e: ListSelectionEvent): Unit = {
    editConfigButton_.setEnabled(true)
    removeConfigButton_.setEnabled(true)
  }
}
